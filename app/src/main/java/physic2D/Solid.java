package physic2D;

import org.joml.Vector2f;

import components.Component;
import engine.Window;

public class Solid extends Component {
    protected transient Physics2D physic2D;
    protected transient HitBox hitBox;

    public Solid() {
        
    }

    public void start() {
        this.hitBox = this.gameObject.getComponent(HitBox.class);
        this.physic2D = Window.getScene().getPhysic2D();
    }

    public void physicUpdate(float dt) {

    }

    public void update(float dt) {

    }


    public boolean collideAt(HitBox target, Vector2f newPositionOffset) {
        return (this.hitBox.getLeftPos() < (target.getRightPos() + newPositionOffset.x)
            &&  this.hitBox.getRightPos() > (target.getLeftPos() + newPositionOffset.x)
            &&  this.hitBox.getTopPos() > (target.getBottomPos() + newPositionOffset.y)
            &&  this.hitBox.getBottomPos() < (target.getTopPos() + newPositionOffset.y));
    }

    public void onCollide(Actor actor, Vector2f newPositionOffset) {
        
    }

    public HitBox getHitBox() {
        return this.hitBox;
    }
}
