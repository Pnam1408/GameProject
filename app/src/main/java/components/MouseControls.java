package components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.HashSet;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import engine.GameObject;
import engine.KeyListener;
import engine.MouseListener;
import engine.Window;
import render.DebugDraw;
import render.PickingTexture;
import scenes.Scene;
import util.Settings;

public class MouseControls extends Component {
    GameObject holdingObject = null;
    Vector2f lastPlacePos = null;
    private float debounceTime = 0.2f;
    private float debounce = debounceTime;

    private boolean boxSelectSet = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();

    public void pickupObject(GameObject go) {
        if (this.holdingObject != null) {
            this.holdingObject.destroy();
        }
        this.holdingObject = go;
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
        this.holdingObject.setPickable(false);
        Window.getScene().addGameObjectToScene(go);
    }

    public void place() {
        GameObject newObj = holdingObject.copy();
        if (newObj.getComponent(StateMachine.class) != null) {
            newObj.getComponent(StateMachine.class).refreshTextures();
        }
        newObj.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 1));
        newObj.setPickable(true);
        Window.getScene().addGameObjectToScene(newObj);
        lastPlacePos = newObj.transform.position;
    }

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;
        PickingTexture pickingTexture = Window.getImguiLayer().getPropertiesWindow().getPickingTexture();
        Scene currentScene = Window.getScene();

        if (holdingObject != null) {
            Vector2f newPos = MouseListener.getWorld();
            newPos.x = (int) Math.floor(newPos.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH + Settings.GRID_WIDTH/2;
            newPos.y = (int) Math.floor(newPos.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT + Settings.GRID_HEIGHT/2;
            holdingObject.transform.position = newPos;
            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                if (!(MouseListener.getScreenX() < 0 || MouseListener.getScreenX() > 1920
                    || MouseListener.getScreenY() < 0 || MouseListener.getScreenY() > 1080)) {
                    if (lastPlacePos == null || !newPos.equals(lastPlacePos.x, lastPlacePos.y)) {
                        place();
                    }
                }
                else {
                    holdingObject.destroy();
                    holdingObject = null;
                    }
            }
            

            if (!MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                lastPlacePos = null;
            }

            if (KeyListener.isPressed(GLFW_KEY_ESCAPE)) {
                holdingObject.destroy();
                holdingObject = null;
            }
    
        } else if (!MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x, y);
            GameObject pickedObj = currentScene.getGameObject(gameObjectId);
            if (pickedObj != null && pickedObj.isPickable()) {
                Window.getImguiLayer().getPropertiesWindow().setActiveGameObject(pickedObj);
            } else if (pickedObj == null && !MouseListener.isDragging()) {
                Window.getImguiLayer().getPropertiesWindow().clearSelected();
            }
            this.debounce = 0.2f;
        } else if (MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            if (!boxSelectSet) {
                Window.getImguiLayer().getPropertiesWindow().clearSelected();
                boxSelectStart = MouseListener.getScreen();
                boxSelectSet = true;
            }
            boxSelectEnd = MouseListener.getScreen();
            Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
            Vector2f halfSize =
                    (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            DebugDraw.addBox2D(
                    (new Vector2f(boxSelectStartWorld)).add(halfSize),
                    new Vector2f(halfSize).mul(2.0f),
                    0.0f);
        } else if (boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int)boxSelectStart.x;
            int screenStartY = (int)boxSelectStart.y;
            int screenEndX = (int)boxSelectEnd.x;
            int screenEndY = (int)boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX) {
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }
            if (screenEndY < screenStartY) {
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }

            float[] gameObjectIds = pickingTexture.readPixels(
                    new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY)
            );
            Set<Integer> uniqueGameObjectIds = new HashSet<>();
            for (float objId : gameObjectIds) {
                uniqueGameObjectIds.add((int)objId);
            }

            for (Integer gameObjectId : uniqueGameObjectIds) {
                GameObject pickedObj = Window.getScene().getGameObject(gameObjectId);
                if (pickedObj != null && pickedObj.isPickable()) {
                    Window.getImguiLayer().getPropertiesWindow().addActiveGameObject(pickedObj);
                }
            }
        }
    }

}
