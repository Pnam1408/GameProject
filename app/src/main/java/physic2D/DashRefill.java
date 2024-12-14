package physic2D;

import components.SpriteRenderer;

public class DashRefill extends Actor {
    private final float cooldownTime = 2.0f;
    private float cooldownTimer;

    public DashRefill() {
        this.cooldownTimer = 0.0f;
    }

    @Override
    public void collideWithActor(Actor actor) {
        if (cooldownTimer <= 0 && actor instanceof Player) {
            Player player = (Player) actor;
            if (player.refilldash()) {
                this.cooldownTimer = cooldownTime;
                this.gameObject.getComponent(SpriteRenderer.class).setInvisible();
            }
        }
    }

    @Override
    public void physicUpdate(float dt) {
        if (cooldownTimer > 0) {
            cooldownTimer -= dt;
            if (cooldownTimer <= 0) {
                this.gameObject.getComponent(SpriteRenderer.class).setVisible();
            }
        }
    }
}
