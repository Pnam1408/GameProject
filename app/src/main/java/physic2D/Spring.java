package physic2D;

import org.joml.Vector2f;

import components.StateMachine;
import engine.Window;
import util.AssetPool;

public class Spring extends Actor {
    private final float activeTime = 0.1f;

    private Vector2f fixVelocity;
    private float timer;

    private transient StateMachine stateMachine;

    public Spring(Vector2f fixVelocity) {
        this.fixVelocity = fixVelocity;
    }

    @Override
    public void start() {
        this.timer = 0.0f;
        this.hitBox = this.gameObject.getComponent(HitBox.class);
        this.physic2D = Window.getScene().getPhysic2D();
        this.stateMachine = this.gameObject.getComponent(StateMachine.class);
    }

    @Override
    public void collideWithActor(Actor actor) {
        if (actor instanceof Player) {
            if (timer <= 0) {
                Player player = (Player) actor;
                player.refilldash();
                player.cancelDash();
                player.setVelocity(fixVelocity);

                AssetPool.getSound("assets/sounds/springboard-a-92438.ogg").play();
                stateMachine.trigger("Activate");
                timer = activeTime;
            }
        }
    }

    @Override
    public void physicUpdate(float dt) {
        if (timer > 0) {
            timer -= dt;
            if (timer < 0) {
                stateMachine.trigger("Deactivate");
            }
        }
    }
}
