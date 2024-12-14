package components;

import java.util.ArrayList;
import java.util.List;

import util.AssetPool;

public class AnimationState {
    public String title;
    public List<Frame> animationFrames = new ArrayList<>();

    private static Sprite defaultSprite = new Sprite();
    private transient float time = 0.0f;
    private transient int currentSprite = 0;
    private boolean doesLoop = true;

    public void refreshTextures() {
        for (Frame frame : animationFrames) {
            frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilepath()));
        }
    }

    public void addFrame(Sprite sprite, float frameTime) {
        animationFrames.add(new Frame(sprite, frameTime));
    }

    public void addFrames(List<Sprite> sprites, float frameTime) {
        for (Sprite sprite : sprites) {
            this.animationFrames.add(new Frame(sprite, frameTime));
        }
    }

    public void setLoop(boolean doesLoop) {
        this.doesLoop = doesLoop;
    }

    public void update(float dt) {
        if (currentSprite < animationFrames.size()) {
            time -= dt;
            if (time <= 0) {
                if (!(currentSprite == animationFrames.size() - 1 && !doesLoop)) {
                    currentSprite = (currentSprite + 1) % animationFrames.size();
                }
                time = animationFrames.get(currentSprite).frameTime;
            }
        }
    }

    public Sprite getCurrentSprite() {
        if (currentSprite < animationFrames.size()) {
            return animationFrames.get(currentSprite).sprite;
        }

        return defaultSprite;
    }

}
