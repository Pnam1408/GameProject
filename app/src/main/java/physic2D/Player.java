package physic2D;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

import org.joml.Vector2f;

import components.SpriteRenderer;
import components.StateMachine;
import engine.KeyListener;
import engine.Prefabs;
import engine.Window;
import util.AssetPool;
import util.JMath;

public class Player extends Actor {

    private final float changeSceneDelayTime = 0.1f;

    private final float gravity = 700.0f;

    private final float maxRunSpeed = 60.0f;
    private final float maxFallSpeed = -90.0f;
    private final float wallSlideMul = 0.2f;
    private final float runDecel = 300.0f;
    private final float runAccel = 500.0f;
    private final float airMul = 0.65f;

    private final float coyoteTime = 0.1f;
    private final float jumpBufferTime = 0.1f;
    private final float varJumpTime = 0.2f;
    private final float jumpSpeed = 60f;
    private final float jumpHBoost = 10f;
    private final float wallJumpHSpeed = 60.0f;
    private final float wallJumpForceTime = 0.15f;

    private final float dashSpeed = 120f;
    private final float dashDiagSpeed = (float) (dashSpeed / Math.sqrt(2));
    private final float dashTime = 0.15f;
    private final float dashCoolDown = 0.15f;
    private final float dashBufferTime = 0.1f;
    protected final int defaultDashNum = 1;

    private final int wallJumpCheckDistance = 3;
    private final int dashCornerCorrection = 4;
    private final int upwardCornerCorrection = 4;

    protected Vector2f velocity;
    private int moveX;
    private int moveY;
    private int forceMoveX;

    private Vector2f dashDir;
    private float dashBufferTimer;
    private float dashCoolDownTimer;
    protected float dashEffectTimer;
    protected int dashCount;

    private float coyoteTimer;
    private float jumpBufferTimer;
    private float forceMoveXTimer;
    private float varJumpTimer;
    private float varJumpSpeed;

    private float changeSceneDelayTimer;
    private boolean isDead = false;

    private boolean onGound;
    private boolean flipX;

    private transient StateMachine stateMachine;

    public Player() {
        super();
        this.velocity = new Vector2f();
        this.dashDir = new Vector2f();
        this.dashCoolDownTimer = 0.0f;
    }

    @Override
    public void start() {
        this.hitBox = this.gameObject.getComponent(HitBox.class);
        this.physic2D = Window.getScene().getPhysic2D();
        this.stateMachine = this.gameObject.getComponent(StateMachine.class);
    }

    @Override
    public void physicUpdate(float dt) {
        if (isDead) {
            return;
        }

        // Direction input
        if (KeyListener.isPressed(GLFW_KEY_RIGHT)) {
            moveX = 1;
        }
        else if (KeyListener.isPressed(GLFW_KEY_LEFT)) {
            moveX = -1;
        }
        else {
            moveX = 0;
        }

        if (KeyListener.isPressed(GLFW_KEY_UP)) {
            moveY = 1;
        }
        else if (KeyListener.isPressed(GLFW_KEY_DOWN)) {
            moveY = -1;
        }
        else {
            moveY = 0;
        }

        dashDir.x = moveX;
        dashDir.y = moveY;

        if (forceMoveXTimer > 0) {
            forceMoveXTimer -= dt;
            moveX = forceMoveX;
        }

        // Get jump input
        if (KeyListener.keyBeginPress(GLFW_KEY_C)) {
            jumpBufferTimer = jumpBufferTime;
        } else if (jumpBufferTimer > 0) {
            jumpBufferTimer -= dt;
        }

        // Dash input
        if (KeyListener.keyBeginPress(GLFW_KEY_X)) {
            dashBufferTimer = dashBufferTime;
        } else if (dashBufferTimer > 0) {
            dashBufferTimer -= dt;
        }

        if (dashEffectTimer > 0) {  
            dashEffectTimer -= dt;
            dashCoolDownTimer -= dt;
        } else {

            if (moveX != 0) {
                flipX = (moveX > 0) ? false : true;
            }

            // Running
            float friction = (onGound) ? (runDecel) : (runDecel * airMul);
            float accel = (onGound) ? (runAccel) : (runAccel * airMul);

            if (Math.abs(velocity.x) > maxRunSpeed && Math.signum(velocity.x) == moveX) {
                velocity.x = JMath.approach(velocity.x, maxRunSpeed * moveX, friction * dt);
            } else {
                velocity.x = JMath.approach(velocity.x, maxRunSpeed * moveX, accel * dt);
            }

            // Goundcheck
            if (velocity.y <= 0) {
                if (physic2D.collideWithSolidCheck(this, new Vector2f(0, -1)) != null) {
                    onGound = true;
                } else {
                    onGound = false;
                }
            }
            else {
                onGound = false;
            }

            if (onGound) {
                coyoteTimer = coyoteTime;
                dashCount = defaultDashNum;
            } else if (coyoteTimer > 0) {
                coyoteTimer -= dt;
            }

            // Gravity
            if (!onGound) {
                // WallSlide
                if (physic2D.collideWithSolidCheck(this, new Vector2f(moveX, 0)) != null) {
                    velocity.y = JMath.approach(velocity.y, maxFallSpeed * wallSlideMul, gravity * dt);
                } else {
                    velocity.y = JMath.approach(velocity.y, maxFallSpeed, gravity * dt);
                }
            }

            // Variable length jump
            if (varJumpTimer > 0) {
                if (KeyListener.isPressed(GLFW_KEY_C)) {
                    velocity.y = Math.max(velocity.y, varJumpSpeed);
                    varJumpTimer -= dt;
                } else {
                    varJumpTimer = 0;
                }
            }

            if (jumpBufferTimer > 0) {
                if (coyoteTimer > 0) {
                    jump();
                }
                else if (wallJumpCheck(1)) {
                    wallJump(-1);
                }
                else if (wallJumpCheck(-1)) {
                    wallJump(1);
                }
            }

            // Animation
            if (!onGound) {
                if (physic2D.collideWithSolidCheck(this, new Vector2f(moveX, 0)) != null) {
                    if (!stateMachine.getCurrentState().title.equals("WallSlide")) {
                        stateMachine.trigger("startSlide");
                    }
                    flipX = (moveX > 0) ? true : false;
                } else {
                    if (!stateMachine.getCurrentState().title.equals("Jump")) {
                        stateMachine.trigger("startJumping");
                    }
                }
            }
            else {
                if (!stateMachine.getCurrentState().title.equals("Idle")) {
                    stateMachine.trigger("backToIdle");
                }
                if (moveX == 0) {
                    if (KeyListener.isPressed(GLFW_KEY_UP)) {
                        stateMachine.trigger("startLookup");
                    } else if (KeyListener.isPressed(GLFW_KEY_DOWN)) {
                        stateMachine.trigger("startLookdown");
                    }
                } else {
                    stateMachine.trigger("startRunning");
                }
            }

            if (dashBufferTimer > 0 && dashCount > 0 && dashCoolDownTimer <= 0) {
                dash();
            } else if (dashCoolDownTimer > 0) {
                dashCoolDownTimer -= dt;
            }
        }

        // Move according to velocity
        moveX(velocity.x * dt);
        moveY(velocity.y * dt);

        this.gameObject.getComponent(SpriteRenderer.class).setFlipX(flipX);
    }

    @Override
    public void editorUpdate(float dt) {

    }

    @Override
    public void update(float dt) {
        if (isDead) {
            if (changeSceneDelayTimer < 0.0f) {
                Window.restartLevel();
            } else {
                changeSceneDelayTimer -= dt;
            }
        }
    }

    @Override
    public void onCollideX() {
        // Dash corner correct
        if (dashEffectTimer > 0) {
            if (velocity.y == 0 && velocity.x != 0) {
                for (int i = 1; i <= dashCornerCorrection; i++) {
                    for (int j = 1; j >= -1; j -= 2) {
                        if (physic2D.collideWithSolidCheck(this, new Vector2f(Math.signum(velocity.x), i*j)) == null) {
                            this.moveY(i * j);
                            this.moveX(Math.signum(velocity.x));
                        }
                    }
                }
            }
        } else {
            this.velocity.x = 0;
        }
    }

    @Override
    public void onCollideY() {
        // Dash corner correct
        if (dashEffectTimer > 0) {
            if (velocity.x == 0 && velocity.y != 0) {
                for (int i = 1; i <= dashCornerCorrection; i++) {
                    for (int j = 1; j >= -1; j -= 2) {
                        if (physic2D.collideWithSolidCheck(this, new Vector2f(i*j, Math.signum(velocity.y))) == null) {
                            this.moveX(i * j);
                            this.moveY(Math.signum(velocity.y));
                        }
                    }
                }
            }
        } else if (velocity.y > 0 && velocity.x == 0) {
            for (int i = 1; i <= upwardCornerCorrection; i++) {
                for (int j = 1; j >= -1; j -= 2) {
                    if (physic2D.collideWithSolidCheck(this, new Vector2f(i*j, Math.signum(velocity.y))) == null) {
                        this.moveX(i * j);
                        this.moveY(Math.signum(velocity.y));
                    }
                }
            }
        } else {
            this.velocity.y = 0;
        }
    }

    public void jump() {
        jumpBufferTimer = 0;
        coyoteTimer = 0;
        varJumpTimer = varJumpTime;
        velocity.y = jumpSpeed;
        velocity.x += jumpHBoost * moveX;
        varJumpSpeed = velocity.y;
        AssetPool.getSound("assets/sounds/jump.ogg").play();
        Prefabs.generateDust(this.gameObject.transform.position);
    }

    public void wallJump(int dir) {
        jumpBufferTimer = 0;
        varJumpTimer = varJumpTime;
        velocity.y = jumpSpeed;
        velocity.x = wallJumpHSpeed * dir;
        if (moveX != 0){
            forceMoveXTimer = wallJumpForceTime;
            forceMoveX = dir;
        }
        varJumpSpeed = velocity.y;
        AssetPool.getSound("assets/sounds/jump.ogg").play();
        Prefabs.generateDust(this.gameObject.transform.position);
    }

    public boolean wallJumpCheck(int dir) {
        for (int i = 1; i < wallJumpCheckDistance; i++) {
            if (physic2D.collideWithSolidCheck(this, new Vector2f(dir * i, 0)) != null) {
                return true;
            }
        }
        return false;
    }

    public void dash() {
        dashCount--;
        dashCoolDownTimer = dashCoolDown;
        dashEffectTimer = dashTime;


        if (dashDir.x != 0 && dashDir.y != 0) {
            velocity.y = dashDiagSpeed * dashDir.y;
            velocity.x = dashDiagSpeed * dashDir.x;
        } else if (dashDir.y != 0) {
            velocity.y = dashSpeed * dashDir.y;
            velocity.x = 0;
        } else if (dashDir.x == 0){
            dashDir.x = (flipX) ? -1 : 1;
            velocity.y = 0;
            velocity.x = dashSpeed * dashDir.x;
        } else {
            velocity.y = 0;
            velocity.x = dashSpeed * dashDir.x;
        }
        if (dashDir.x != 0) {
            flipX = (dashDir.x > 0) ? false : true;
        }
        stateMachine.trigger("startDashing");
        AssetPool.getSound("assets/sounds/dash_red_left.ogg").play();
    }

    public void die() {
        this.gameObject.getComponent(SpriteRenderer.class).setInvisible();
        this.isDead = true;
        this.changeSceneDelayTimer = changeSceneDelayTime;
        Prefabs.generateDeathParticles(this.gameObject.transform.position);
        AssetPool.getSound("assets/sounds/hitHurt.ogg").play();
    }

    public void win() {
        physic2D.pause();
        Window.nextLevel();
    }

    public boolean refilldash() {
        if (dashCount < defaultDashNum) {
            dashCount = defaultDashNum;
            AssetPool.getSound("assets/sounds/powerUp.ogg").play();
            return true;
        } else {
            return false;
        }
    }

    public void cancelDash() {
        dashEffectTimer = 0;
    }

    public void setVelocity(Vector2f newVelocity) {
        velocity.x = newVelocity.x;
        velocity.y = newVelocity.y;
    }

}
