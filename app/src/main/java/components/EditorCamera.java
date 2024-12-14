package components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_HOME;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

import org.joml.Vector2f;

import engine.Camera;
import engine.KeyListener;
import engine.MouseListener;

public class EditorCamera extends Component {
    public boolean drag = false;
    public boolean reset = false;

    public float dragDebounce = 0.032f;

    public float lerpTime = 0.0f;
    public float dragSensitivity = 0.2f;
    public float scrollSensitivity = 0.1f;

    public Camera editorCamera;
    private Vector2f clickOrigin;

    public EditorCamera(Camera editorCamera) {
        this.editorCamera = editorCamera;
        clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt) {

        // TODO: fix weird camera movement while keep holding mouse button
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT) && dragDebounce > 0) {
            this.clickOrigin = new Vector2f(MouseListener.getScreenX(), MouseListener.getScreenY());
            dragDebounce -= dt;
            return;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            Vector2f mousePos = new Vector2f(MouseListener.getScreenX(), MouseListener.getScreenY());
            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            editorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos, dt);
        }

        if (dragDebounce <= 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            dragDebounce = 0.1f;
        }

        if (MouseListener.getScrollY() != 0.0f) {
            float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY()) * scrollSensitivity, 1 / editorCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            editorCamera.addZoom(addValue);

        }

        if (KeyListener.isPressed(GLFW_KEY_HOME)) {
            reset = true;
        }

        if (reset) {
            editorCamera.position.lerp(new Vector2f(), lerpTime);
            editorCamera.setZoom(this.editorCamera.getZoom() +
                    ((1.0f - editorCamera.getZoom()) * lerpTime));
            this.lerpTime += 0.1f * dt;
            if (Math.abs(editorCamera.position.x) <= 1.0f &&
                    Math.abs(editorCamera.position.y) <= 1.0f) {
                this.lerpTime = 0.0f;
                editorCamera.position.set(0f, 0f);
                this.editorCamera.setZoom(1.0f);
                reset = false;
            }
        }

        editorCamera.adjustProjection();
    }
}
