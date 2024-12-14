package physic2D;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import engine.GameObject;

public class Physics2D {
    private float physicsTime = 0.0f;
    private float physicsTimeStep = (1.0f / 60.0f);

    private boolean isStop = false;

    private List<Actor> actors = new ArrayList<>();
    private List<Solid> solids = new ArrayList<>();
    private List<HitBox> hitBoxs = new ArrayList<>();

    public void update(float dt) {
        if (!isStop) {
            physicsTime += dt;
            if (physicsTime >= 0.0f) {
                physicsTime -= physicsTimeStep;
                for (Actor actor : actors) {
                    actor.physicUpdate(physicsTimeStep);
                }

                for (Solid solid : solids) {
                    solid.physicUpdate(physicsTimeStep);
                }

                for (HitBox hitBox : hitBoxs) {
                    hitBox.physicUpdate(physicsTimeStep);
                }
            }
        }
    }

    public void addGameObject(GameObject go) {
        if (go.getComponent(Actor.class) != null) {
            actors.add(go.getComponent(Actor.class));
        }
        else if (go.getComponent(Solid.class) != null) {
            solids.add(go.getComponent(Solid.class));
        }
        if (go.getComponent(HitBox.class) != null) {
            hitBoxs.add(go.getComponent(HitBox.class));
        }
    }

    public void destroyGameObject(GameObject go) {

    }

    public List<Solid> getAllSolid() {
        return this.solids;
    }

    public Solid collideWithSolidCheck(Actor actor, Vector2f newPositionOffset) {
        for (Solid solid : solids) {
            if (solid.collideAt(actor.getHitBox(), newPositionOffset)) {
                return solid;
            }
        }
        return null;
    }

    public void collideWithActorCheck(Actor actor1) {
        for (Actor actor2 : actors) {
            if (actor2.collideAt(actor1.getHitBox(), new Vector2f())) {
                actor2.collideWithActor(actor1);
            }
        }
    }

    public Actor collideWithActorCheck(HitBox obj, Vector2f positionOffset) {
        for (Actor actor : actors) {
            if (actor.collideAt(obj, positionOffset)) {
                return actor;
            }
        }
        return null;
    }

    public void pause() {
        this.isStop = true;
    }

    public void unpause() {
        this.isStop = false;
    }
}
