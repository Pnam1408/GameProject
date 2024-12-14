package components;

public class Dust extends Component {
    private float lifeTime;

    public Dust() {
        lifeTime = 0.3f;
    }

    @Override
    public void update(float dt) {
        lifeTime -= dt;
        if (lifeTime < 0) {
            this.gameObject.destroy();
        }
    }
}
