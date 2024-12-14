package components;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class DeadParticle extends Component {
    private boolean isRed = true;
    private float timer;

    private Vector2f velocity;
    private Vector2f remainder;

    public DeadParticle(Vector2f velocity) {
        this.velocity = velocity;
        this.remainder = new Vector2f();
        this.timer = 0.1f;
    }

    @Override
    public void update(float dt) {
        remainder.x += velocity.x * dt;
        remainder.y += velocity.y * dt;
        this.gameObject.transform.position.x += Math.round(remainder.x);
        this.gameObject.transform.position.y += Math.round(remainder.y);
        remainder.x -= Math.round(remainder.x);
        remainder.y -= Math.round(remainder.y);

        if (isRed) {
            this.gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
            isRed = false;
        } else {
            this.gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
            isRed = true;
        }

        if (timer < 0.065f) {
            this.gameObject.transform.scale = new Vector2f(2, 2);
        }

        timer -= dt;
    }
}
