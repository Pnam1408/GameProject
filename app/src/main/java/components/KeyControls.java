package components;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.List;

import editor.PropertiesWindow;
import engine.GameObject;
import engine.KeyListener;
import engine.Window;
import util.Settings;

public class KeyControls extends Component {
    private float debounceTime = 0.2f;
    private float debounce = 0.0f;

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;

        PropertiesWindow propertiesWindow = Window.getImguiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();
        float distance = KeyListener.isPressed(GLFW_KEY_LEFT_SHIFT) ? 1.0f : 8.0f;

        if (KeyListener.isPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObject != null) {
            GameObject newObj = activeGameObject.copy();
            Window.getScene().addGameObjectToScene(newObj);
            newObj.transform.position.add(Settings.GRID_WIDTH, 0.0f);
            propertiesWindow.setActiveGameObject(newObj);
            if (newObj.getComponent(StateMachine.class) != null) {
                newObj.getComponent(StateMachine.class).refreshTextures();
            }
        } else if (KeyListener.isPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObjects.size() > 1) {
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject copy = go.copy();
                Window.getScene().addGameObjectToScene(copy);
                propertiesWindow.addActiveGameObject(copy);
                if (copy.getComponent(StateMachine.class) != null) {
                    copy.getComponent(StateMachine.class).refreshTextures();
                }
            }
        } else if (KeyListener.keyBeginPress(GLFW_KEY_DELETE)) {
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        } else if (KeyListener.isPressed(GLFW_KEY_PAGE_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.zIndex--;
            }
        } else if (KeyListener.isPressed(GLFW_KEY_PAGE_UP) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.zIndex++;
            }
        } else if (KeyListener.isPressed(GLFW_KEY_UP) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y += distance;
            }
        } else if (KeyListener.isPressed(GLFW_KEY_LEFT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x -= distance;
            }
        } else if (KeyListener.isPressed(GLFW_KEY_RIGHT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x += distance;
            }
        } else if (KeyListener.isPressed(GLFW_KEY_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y -= distance;
            }
        }
    }
}
