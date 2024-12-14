package physic2D;

import org.joml.Vector2f;

import components.SpriteRenderer;
import components.StateMachine;
import engine.Window;

public class CrumbleBlock extends Solid {
    private final float lifeTime = 1.5f;
    private final float respawnTime = 1.5f;

    private boolean countdown = false;
    private float lifeTimer;
    private float respawnTimer;
    private transient StateMachine stateMachine;

    public CrumbleBlock() {
        lifeTimer = lifeTime;
        respawnTimer = respawnTime;
    }

    @Override
    public void start() {
        this.hitBox = this.gameObject.getComponent(HitBox.class);
        this.stateMachine = this.gameObject.getComponent(StateMachine.class);
        this.physic2D = Window.getScene().getPhysic2D();
    }

    @Override
    public void physicUpdate(float dt) {
        if (countdown) {
            lifeTimer -= dt;
            if (lifeTimer < 0) {
                this.gameObject.getComponent(SpriteRenderer.class).setInvisible();
                stateMachine.trigger("Respawn");
                countdown = false;
            }
        } else {
            Actor actor = physic2D.collideWithActorCheck(this.getHitBox(), new Vector2f(0.0f,1.0f));
            if (actor != null && actor instanceof Player) {
                countdown = true;
            }
        }
        if (lifeTimer < 0) {
            respawnTimer -= dt;
            if (respawnTimer < 0 && physic2D.collideWithActorCheck(this.getHitBox(), new Vector2f()) == null) {
                lifeTimer = lifeTime;
                respawnTimer = respawnTime;
                this.gameObject.getComponent(SpriteRenderer.class).setVisible();
            }
        }

        // Animation
        if (lifeTimer < 1.0f && lifeTimer >= 0.5f && !stateMachine.getCurrentState().title.equals("Break")) {
            stateMachine.trigger("Break");
        } else if (lifeTimer >= 0.0f && lifeTimer < 0.5f && !stateMachine.getCurrentState().title.equals("Collapse")) {
            stateMachine.trigger("Collapse");
        }
    }

    @Override
    public boolean collideAt(HitBox target, Vector2f newPositionOffset) {
        return  lifeTimer >= 0  && (this.hitBox.getLeftPos() < (target.getRightPos() + newPositionOffset.x)
                                &&  this.hitBox.getRightPos() > (target.getLeftPos() + newPositionOffset.x)
                                &&  this.hitBox.getTopPos() > (target.getBottomPos() + newPositionOffset.y)
                                &&  this.hitBox.getBottomPos() < (target.getTopPos() + newPositionOffset.y));
    }

    @Override
    public void onCollide(Actor actor, Vector2f newPositionOffset) {
        if (actor instanceof Player) {
            if (newPositionOffset.y < 0 || newPositionOffset.x != 0) {
                countdown = true;
            }
        }
    }
}
