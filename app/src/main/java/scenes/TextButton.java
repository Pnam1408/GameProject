package scenes;

import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector4f;

import components.SpriteRenderer;
import engine.GameObject;
import engine.Prefabs;
import engine.Window;

public class TextButton {
    List<GameObject> textObjs;
    ButtonCallback buttonCallback;
    boolean active;

    public TextButton(String text, Vector2f position) {
        textObjs = Prefabs.generateText(text, position);
    }

    public void start() {
        for (GameObject gameObject : textObjs) {
            Window.getScene().addGameObjectToScene(gameObject);
        }
        active = false;
    }

    public void setActive() {
        for (GameObject gameObject : textObjs) {
            gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        }
        active = true;
    }

    public void setInActive() {
        for (GameObject gameObject : textObjs) {
            gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
        }
        active = false;
    }

    public void setButtonCallback(ButtonCallback buttonCallback) {
        this.buttonCallback = buttonCallback;
    }

    public void pressed() {
        buttonCallback.pressed();
    }

    public void setInvisible() {
        for (GameObject gameObject : textObjs) {
            gameObject.getComponent(SpriteRenderer.class).setInvisible();
        }
    }

    public void setVisible() {
        for (GameObject gameObject : textObjs) {
            gameObject.getComponent(SpriteRenderer.class).setVisible();
        }
    }

}
