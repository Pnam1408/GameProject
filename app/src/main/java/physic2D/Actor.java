package physic2D;

import org.joml.Vector2f;

import components.Component;
import engine.Window;


public class Actor extends Component {
    protected transient Physics2D physic2D;
    private Vector2f remainder;
    protected transient HitBox hitBox;

    public Actor() {
        this.remainder = new Vector2f(0.0f, 0.0f);
    }

    @Override
    public void start() {
        this.hitBox = this.gameObject.getComponent(HitBox.class);
        this.physic2D = Window.getScene().getPhysic2D();
    }

    @Override
    public void physicUpdate(float dt) {

    }

    public void moveX(float amount) {
        remainder.x += amount;
        int move = Math.round(remainder.x);

        if (move != 0) {
            remainder.x -= move;
            int sign = (int) Math.signum(move);
            while (move != 0) {
                Solid solid = physic2D.collideWithSolidCheck(this, new Vector2f(sign, 0));
                if (solid != null) {
                    solid.onCollide(this, new Vector2f(sign, 0));
                    onCollideX();
                    return;
                }
                this.hitBox.setPosition(hitBox.getPosition().add(sign, 0));
                move -= sign;
                physic2D.collideWithActorCheck(this);
            }
        }
    }

    public void moveY(float amount) {
        remainder.y += amount;
        int move = Math.round(remainder.y);

        if (move != 0) {
            remainder.y -= move;
            int sign = (int) Math.signum(move);
            while (move != 0) {
                Solid solid = physic2D.collideWithSolidCheck(this, new Vector2f(0, sign));
                if (solid != null) {
                    solid.onCollide(this, new Vector2f(0, sign));
                    onCollideY();
                    return;
                }
                this.hitBox.setPosition(hitBox.getPosition().add(0, sign));
                move -= sign;
                physic2D.collideWithActorCheck(this);
            }
        }
    }

    public boolean isRiding(Solid solid) {
        if (this.hitBox.getBottomPos() == solid.getHitBox().getTopPos()) {
            return true;
        }
        return false;
    }

    public void onCollideX() {

    }

    public void onCollideY() {

    }

    public boolean collideAt(HitBox target, Vector2f newPositionOffset) {
        return (this.hitBox.getLeftPos() < (target.getRightPos() + newPositionOffset.x)
            &&  this.hitBox.getRightPos() > (target.getLeftPos() + newPositionOffset.x)
            &&  this.hitBox.getTopPos() > (target.getBottomPos() + newPositionOffset.y)
            &&  this.hitBox.getBottomPos() < (target.getTopPos() + newPositionOffset.y));
    }

    public HitBox getHitBox() {
        return this.hitBox;
    }

    public void collideWithActor(Actor actor) {
        
    }

}
