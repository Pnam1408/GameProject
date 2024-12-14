package engine;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.util.Arrays;

public class KeyListener {
    private static KeyListener instance;
    public boolean keyPressed[] = new boolean[GLFW_KEY_LAST + 1];
    public boolean keyBeginPress[] = new boolean[GLFW_KEY_LAST + 1];

    private KeyListener() {
    }

    public static void endFrame() {
        Arrays.fill(get().keyBeginPress, false);
    }

    public static KeyListener get() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }
        return instance;
    }

    public static void keyCallBack(long window, int key, int scancode, int action, int mods) {
        if (key <= GLFW_KEY_LAST && key >= 0) {
            if (action == GLFW_PRESS) {
                get().keyPressed[key] = true;
                get().keyBeginPress[key] = true;
            }
            else if (action == GLFW_RELEASE) {
                get().keyPressed[key] = false;
                get().keyBeginPress[key] = false;
            }
        }
    }

    public static boolean isPressed(int keyCode) {
        return get().keyPressed[keyCode];
    }


    // This function return true when the key is press the first time not when holding
    public static boolean keyBeginPress(int keyCode) {
        return get().keyBeginPress[keyCode];
    }
}
