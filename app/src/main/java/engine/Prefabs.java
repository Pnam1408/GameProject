package engine;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector4f;

import components.AnimationState;
import components.DeadParticle;
import components.Dust;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import components.StateMachine;
import physic2D.Bone;
import physic2D.CrumbleBlock;
import physic2D.DashRefill;
import physic2D.HitBox;
import physic2D.Player;
import physic2D.Spike;
import physic2D.Spring;
import physic2D.Tile;
import util.AssetPool;

public class Prefabs {
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = Window.getScene().createGameObject("Sprite_Object_Gen");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer spriteRenderer = new SpriteRenderer();
        spriteRenderer.setSprite(sprite);
        block.addComponent(spriteRenderer);

        return block;
    }

    public static GameObject generatePlayer() {
        SpriteSheet playerSheet = AssetPool.getSpritesheet("assets/DoGhost- Asset Pack/Hero animate and object fix.png");
        GameObject player = generateSpriteObject(playerSheet.getSprite(0), 8, 8);
        player.name = "Player";
        player.addComponent(new Player());
        player.addComponent(new HitBox(4, -4, -4, 4));

        float defaultFrameTime = 0.15f;

        AnimationState idle = new AnimationState();
        idle.title = "Idle";
        idle.addFrame(playerSheet.getSprite(0), defaultFrameTime);
        idle.setLoop(false);

        AnimationState lookup = new AnimationState();
        lookup.title = "Lookup";
        lookup.addFrame(playerSheet.getSprite(6), defaultFrameTime);
        lookup.setLoop(false);

        AnimationState lookdown = new AnimationState();
        lookdown.title = "Lookdown";
        lookdown.addFrame(playerSheet.getSprite(5), defaultFrameTime);
        lookdown.setLoop(false);

        AnimationState jump = new AnimationState();
        jump.title = "Jump";
        jump.addFrame(playerSheet.getSprite(2), defaultFrameTime);
        jump.setLoop(false);

        AnimationState wallSlide = new AnimationState();
        wallSlide.title = "WallSlide";
        wallSlide.addFrame(playerSheet.getSprite(4), defaultFrameTime);
        wallSlide.setLoop(false);

        AnimationState dash = new AnimationState();
        dash.title = "Dash";
        dash.addFrame(playerSheet.getSprite(1), defaultFrameTime);
        dash.setLoop(false);


        AnimationState run = new AnimationState();
        run.title = "Run";
        run.addFrame(playerSheet.getSprite(0), defaultFrameTime);
        run.addFrame(playerSheet.getSprite(1), defaultFrameTime);
        run.addFrame(playerSheet.getSprite(2), defaultFrameTime);
        run.addFrame(playerSheet.getSprite(3), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();

        stateMachine.addState(idle);
        stateMachine.addState(run);
        stateMachine.addState(jump);
        stateMachine.addState(lookdown);
        stateMachine.addState(lookup);
        stateMachine.addState(wallSlide);
        stateMachine.addState(dash);

        stateMachine.addState(idle.title, run.title, "startRunning");
        stateMachine.addState(lookup.title, run.title, "startRunning");
        stateMachine.addState(lookdown.title, run.title, "startRunning");
        stateMachine.addState(run.title, idle.title, "backToIdle");

        stateMachine.addState(jump.title, wallSlide.title, "startSlide");
        stateMachine.addState(dash.title, wallSlide.title, "startSlide");
        stateMachine.addState(run.title, wallSlide.title, "startSlide");
        stateMachine.addState(wallSlide.title, idle.title, "backToIdle");

        stateMachine.addState(idle.title, lookup.title, "startLookup");
        stateMachine.addState(lookup.title, idle.title, "backToIdle");

        stateMachine.addState(idle.title, lookdown.title, "startLookdown");
        stateMachine.addState(lookdown.title, idle.title, "backToIdle");

        stateMachine.addState(idle.title, jump.title, "startJumping");
        stateMachine.addState(lookup.title, jump.title, "startJumping");
        stateMachine.addState(lookdown.title, jump.title, "startJumping");
        stateMachine.addState(wallSlide.title, jump.title, "startJumping");
        stateMachine.addState(run.title, jump.title, "startJumping");
        stateMachine.addState(dash.title, jump.title, "startJumping");
        stateMachine.addState(jump.title, idle.title, "backToIdle");

        stateMachine.addState(idle.title, dash.title, "startDashing");
        stateMachine.addState(lookup.title, dash.title, "startDashing");
        stateMachine.addState(lookdown.title, dash.title, "startDashing");
        stateMachine.addState(wallSlide.title, dash.title, "startDashing");
        stateMachine.addState(run.title, dash.title, "startDashing");
        stateMachine.addState(jump.title, dash.title, "startDashing");
        stateMachine.addState(dash.title, idle.title, "backToIdle");

        stateMachine.setDefaultState(idle.title);

        player.addComponent(stateMachine);

        return player;
    }

    public static GameObject generateTile(Sprite sprite) {
        GameObject tile = generateSpriteObject(sprite, 8, 8);
        tile.name = "Tile";
        tile.addComponent(new Tile());
        tile.addComponent(new HitBox(4, -4, -4, 4));
        
        return tile;
    }

    public static GameObject generateSpike(Sprite sprite, int rot) {
        GameObject spike = generateSpriteObject(sprite, 8, 8);
        spike.name = "Spike";
        spike.addComponent(new Spike());
        switch (rot) {
            // Up
            case 0:
                spike.addComponent(new HitBox(0, -4, -4, 4));
                break;
            // Right
            case 1:
                spike.addComponent(new HitBox(4, -4, -4, 0));
                break;
            // Down
            case 2:
                spike.addComponent(new HitBox(4, 0, -4, 4));
                spike.getComponent(SpriteRenderer.class).setFlipY(true);
                break;
            // Left
            case 3:
                spike.addComponent(new HitBox(4, -4, 0, 4));
                spike.getComponent(SpriteRenderer.class).setFlipX(true);    
                break;
            default:
                break;
        }
        return spike;
    }

    public static GameObject generateSpring(int rot) {
        SpriteSheet spriteSheet = AssetPool.getSpritesheet("assets/DoGhost- Asset Pack/Hero animate and object fix.png");
        GameObject spring = generateSpriteObject(null, 8, 8);
        spring.addComponent(new HitBox(4, -4, -4, 4));

        AnimationState idle = new AnimationState();
        idle.title = "Idle";
        idle.setLoop(false);

        AnimationState activated = new AnimationState();
        activated.title = "Activated";
        activated.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(activated);
        stateMachine.addState(idle);

        stateMachine.addState(idle.title, activated.title, "Activate");
        stateMachine.addState(activated.title, idle.title, "Deactivate");

        stateMachine.setDefaultState(idle.title);

        spring.addComponent(stateMachine);
        switch (rot) {
            // Up
            case 0:
                spring.getComponent(SpriteRenderer.class).setSprite(spriteSheet.getSprite(65));
                spring.addComponent(new Spring(new Vector2f(0.0f, 200.0f)));        
                idle.addFrame(spriteSheet.getSprite(65), 1);
                activated.addFrame(spriteSheet.getSprite(66), 1);
                break;
            // Right
            case 1:
                spring.getComponent(SpriteRenderer.class).setSprite(spriteSheet.getSprite(75));
                spring.addComponent(new Spring(new Vector2f(150.0f, 120.0f)));        
                idle.addFrame(spriteSheet.getSprite(75), 1);
                activated.addFrame(spriteSheet.getSprite(76), 1);
                break;
            // Down
            case 2:
                spring.getComponent(SpriteRenderer.class).setSprite(spriteSheet.getSprite(67));
                spring.addComponent(new Spring(new Vector2f(0.0f, -150.0f)));        
                idle.addFrame(spriteSheet.getSprite(67), 1);
                activated.addFrame(spriteSheet.getSprite(68), 1);
                break;
            // Left
            case 3:
                spring.getComponent(SpriteRenderer.class).setSprite(spriteSheet.getSprite(78));
                spring.addComponent(new Spring(new Vector2f(-150.0f, 120.0f)));        
                idle.addFrame(spriteSheet.getSprite(78), 1);
                activated.addFrame(spriteSheet.getSprite(77), 1);
                break;
            default:
                break;
        }
        return spring;
    }

    public static GameObject generateBone() {
        SpriteSheet spriteSheet = AssetPool.getSpritesheet("assets/DoGhost- Asset Pack/Hero animate and object fix.png");
        GameObject bone = generateSpriteObject(spriteSheet.getSprite(125), 8, 8);
        bone.name = "Bone";
        bone.addComponent(new Bone());
        bone.addComponent(new HitBox(4, -4, -4, 4));
        return bone;
    }

    private static void generateDeathParticle(Vector2f position, Vector2f velocity) {
        GameObject deadPartical = generateSpriteObject(new Sprite(), 4, 4);
        deadPartical.getComponent(SpriteRenderer.class).setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        deadPartical.transform.position = new Vector2f(position);
        deadPartical.addComponent(new DeadParticle(velocity));
        Window.getScene().addGameObjectToScene(deadPartical);
    }

    public static void generateDeathParticles(Vector2f position) {
        float speed = 240.0f;
        float angleDeg = 0.0f;
        for (int i = 0; i < 8; i++) {
            angleDeg += 45.0f;
            Vector2f velocity = new Vector2f((float) (speed * Math.cos(Math.toRadians(angleDeg))),
                (float) (speed * Math.sin(Math.toRadians(angleDeg))));
            generateDeathParticle(position, velocity);
        }
    }

    public static GameObject generateDashRefill() {
        SpriteSheet spriteSheet = AssetPool.getSpritesheet("assets/DoGhost- Asset Pack/Hero animate and object fix.png");
        GameObject fruit = generateSpriteObject(spriteSheet.getSprite(57), 8, 8);
        fruit.name = "Bone";
        fruit.addComponent(new DashRefill());
        fruit.addComponent(new HitBox(4, -4, -4, 4));
        return fruit;
    }

    public static void generateDust(Vector2f position) {
        SpriteSheet spriteSheet = AssetPool.getSpritesheet("assets/DoGhost- Asset Pack/Hero animate and object fix.png");
        GameObject dust = generateSpriteObject(spriteSheet.getSprite(7), 8, 8);
        dust.transform.position = new Vector2f(position);
        dust.name = "Dust";
        dust.addComponent(new Dust());

        float frameTime = 0.1f;
        AnimationState dustAnimation = new AnimationState();
        dustAnimation.title = "DustAnimation";
        dustAnimation.addFrame(spriteSheet.getSprite(7), frameTime);
        dustAnimation.addFrame(spriteSheet.getSprite(8), frameTime);
        dustAnimation.addFrame(spriteSheet.getSprite(9), frameTime);
        dustAnimation.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(dustAnimation);
        stateMachine.setDefaultState(dustAnimation.title);

        dust.addComponent(stateMachine);
        Window.getScene().addGameObjectToScene(dust);
    }

    public static GameObject generateCrumbleBlock() {
        SpriteSheet spriteSheet = AssetPool.getSpritesheet("assets/DoGhost- Asset Pack/Hero animate and object fix.png");
        GameObject block = generateSpriteObject(spriteSheet.getSprite(45), 8, 8);
        block.name = "Bubble";
        block.addComponent(new CrumbleBlock());
        block.addComponent(new HitBox(4, -4, -4, 4));

        float frameTime = 0.5f;
        AnimationState idle = new AnimationState();
        idle.title = "Idle";
        idle.addFrame(spriteSheet.getSprite(45), frameTime);
        idle.setLoop(false);

        AnimationState breaking = new AnimationState();
        breaking.title = "Break";
        breaking.addFrame(spriteSheet.getSprite(46), frameTime);
        breaking.setLoop(false);

        AnimationState collapse = new AnimationState();
        collapse.title = "Collapse";
        collapse.addFrame(spriteSheet.getSprite(47), frameTime);
        collapse.setLoop(false);

        StateMachine stateMachine = new StateMachine();

        stateMachine.addState(idle);
        stateMachine.addState(breaking);
        stateMachine.addState(collapse);

        stateMachine.addState(idle.title, breaking.title, "Break");
        stateMachine.addState(breaking.title, collapse.title, "Collapse");
        stateMachine.addState(collapse.title, idle.title, "Respawn");

        stateMachine.setDefaultState(idle.title);
        block.addComponent(stateMachine);
        return block;
    }

    private static GameObject generateChar(char c) {
        SpriteSheet texts = AssetPool.getSpritesheet("assets/fontfix.png");
        int asciiCode = (int) c;
        int asciiCodeOfA = (int) 'A';
        GameObject object = Prefabs.generateSpriteObject(texts.getSprite(asciiCode - asciiCodeOfA), 4, 6);
        return object;
    }

    public static List<GameObject> generateText(String text, Vector2f position) {
        List<GameObject> textObjs = new ArrayList<GameObject>();
        String uppercaseText = text.toUpperCase();
        for (int i = 0; i < text.length(); i++) {
            GameObject charObject = generateChar(uppercaseText.charAt(i));
            charObject.transform.position = new Vector2f(position.x + i * 5, position.y);
            textObjs.add(charObject);
        }
        return textObjs;
    }
}
