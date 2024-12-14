package components;


import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import org.joml.Vector2f;
import org.joml.Vector4f;

import editor.PropertiesWindow;
import engine.GameObject;
import engine.MouseListener;
import engine.Prefabs;
import engine.Window;

public class Gizmo extends Component {
    private Vector4f xAxisColor = new Vector4f(1f, 0.5f, 0.5f, 1f);
    private Vector4f xAxisHoverColor = new Vector4f(1f, 0f, 0f, 1f);
    private Vector4f yAxisColor = new Vector4f(0.5f, 1f, 0.5f, 1f);
    private Vector4f yAxisHoverColor = new Vector4f(0f, 1f, 0f, 1f);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    protected GameObject activeGameObject = null;
    private PropertiesWindow propertiesWindow;

    private Vector2f xAxisOffset = new Vector2f(1.0f, -4.0f);
    private Vector2f yAxisOffset = new Vector2f(-4.0f, 1.0f);

    private int gizmoWidth = 2;
    private int gizmoHeight = 6;

    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;
    protected boolean using = false;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 2, 6);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 2, 6);
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;
        
        Window.getScene().addGameObjectToScene(xAxisObject);
        Window.getScene().addGameObjectToScene(yAxisObject);
    }

    @Override
    public void start() {
        this.xAxisObject.transform.rotation = 90;
        this.yAxisObject.transform.rotation = 180;
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
        this.xAxisObject.setPickable(false);
        this.yAxisObject.setPickable(false);
    }

    @Override
    public void update(float dt) {
        if (using) {
            setInactive();
        }
    }

    @Override
    public void editorUpdate(float dt) {
        if (!using) return;
        
        this.activeGameObject = this.propertiesWindow.getActiveGameObject();
        if (this.activeGameObject != null) {
            this.setActive();

        } else {
            this.setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if ((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true;
            yAxisActive = false;
        } else if ((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            yAxisActive = true;
            xAxisActive = false;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        if (this.activeGameObject != null) {
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(this.xAxisOffset);
            this.yAxisObject.transform.position.add(this.yAxisOffset);
        }
    }

    public void setActive() {
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);
    }

    public void setInactive() {
        this.activeGameObject = null;
        this.xAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
        this.yAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
    }

    private boolean checkXHoverState() {
        Vector2f mousePos = MouseListener.getWorld();
        if (mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight / 2.0f) &&
                mousePos.x >= xAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y >= xAxisObject.transform.position.y - (gizmoHeight / 2.0f) &&
                mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth / 2.0f)) {
            xAxisSprite.setColor(xAxisHoverColor);
            return true;
        }

        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = MouseListener.getWorld();
        if (mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth / 2.0f) &&
                mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2.0f) &&
                mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight / 2.0f)) {
            yAxisSprite.setColor(yAxisHoverColor);
            return true;
        }

        yAxisSprite.setColor(yAxisColor);
        return false;
    }

    public void setUsing() {
        this.using = true;
    }

    public void setNotUsing() {
        this.using = false;
        this.setInactive();
    }
}
