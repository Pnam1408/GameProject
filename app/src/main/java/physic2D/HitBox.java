package physic2D;

import org.joml.Vector2f;

import components.Component;

public class HitBox extends Component {
    private float topOffset, bottomOffset, leftOffset, rightOffset;
    private float topPos, bottomPos, leftPos, rightPos;
    private Vector2f position;

    public HitBox(float top, float bottom, float left, float right) {
        this.topOffset = top;
        this.bottomOffset = bottom;
        this.leftOffset = left;
        this.rightOffset = right;
    }

    public void setPosition(Vector2f newPos) {
        this.position = new Vector2f(newPos);
        this.topPos = this.position.y + this.topOffset;
        this.bottomPos = this.position.y + this.bottomOffset;
        this.leftPos = this.position.x + this.leftOffset;
        this.rightPos = this.position.x + this.rightOffset;
    }

    @Override
    public void start() {
        setPosition(this.gameObject.transform.position);
    } 

    @Override
    public void physicUpdate(float dt) {
        this.gameObject.transform.position.x = this.position.x;
        this.gameObject.transform.position.y = this.position.y;
    }

    public Vector2f getPosition() {
        return this.position;
    }

    public float getTopPos() {
        return this.topPos;
    }

    public float getBottomPos() {
        return this.bottomPos;
    }

    public float getLeftPos() {
        return this.leftPos;
    }

    public float getRightPos() {
        return this.rightPos;
    }
    
}
