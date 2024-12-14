package engine;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.util.Arrays;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LAST;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY;
    private boolean mouseButtonPressed[] = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
    private boolean mouseButtonBeginPressed[] = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
    private boolean isDragging;

    private int numButtonDown = 0;

    private Vector2f worldPos;
    private Vector2f lastWorldPos;
    private Vector2f screenPos;

    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static MouseListener get() {
        if (MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    public static void mousePosCallback(long window, double xPos, double yPos) {
        if (get().numButtonDown > 0) {
            get().isDragging = true;
        }

        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().lastWorldPos = get().worldPos;

        get().xPos = xPos;
        get().yPos = yPos;
        get().worldPos = calcWorld();
        get().screenPos = calcScreen();
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().numButtonDown++;
            get().mouseButtonPressed[button] = true;
            get().mouseButtonBeginPressed[button] = true;
        }
        else if (action == GLFW_RELEASE) {
            get().numButtonDown--;
            get().mouseButtonPressed[button] = false;
            get().mouseButtonBeginPressed[button] = false;
            get().isDragging = false;
        }
    }

    public static void mouseScrollCallback(long window, double scrollX, double scrollY) {
        get().scrollX = scrollX;
        get().scrollY = scrollY;
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastWorldPos = get().worldPos;
        Arrays.fill(get().mouseButtonBeginPressed, false);
    }

    public static float getX() {
        return (float)get().xPos;
    }

    public static float getY() {
        return (float)get().yPos;
    }

    public static float getDx() {
        return (float)(get().lastX - get().xPos);
    }

    public static float getDy() {
        return (float)(get().lastY - get().yPos);
    }

    public static float getScrollX() {
        return (float)get().scrollX;
    }

    public static float getScrollY() {
        return (float)get().scrollY;
    }

    public static float getWorldDx() {
        return (float)(get().lastWorldPos.x - get().worldPos.x);
    }

    public static float getWorldDy() {
        return (float)(get().lastWorldPos.y - get().worldPos.y);
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        return get().mouseButtonPressed[button];
    }

    public static boolean mouseButtonBeginPressed(int button) {
        return get().mouseButtonBeginPressed[button];
    }

    public static void setGameViewportPos(Vector2f gameViewportPos) {
        get().gameViewportPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize) {
        get().gameViewportSize.set(gameViewportSize);
    }

    public static Vector2f calcWorld() {
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (2.0f * (currentX / get().gameViewportSize.x)) - 1.0f;
        float currentY = (getY() - get().gameViewportPos.y);
        currentY = (2.0f * (1.0f - (currentY / get().gameViewportSize.y))) - 1;

        Camera camera = Window.getScene().camera();
        Vector4f tmp = new Vector4f(currentX, currentY, 0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));

        return new Vector2f(tmp.x, tmp.y);
    }

    public static Vector2f getWorld() {
        return get().worldPos;
    }

    public static float getWorldX() {
        return getWorld().x;
    }

    public static float getWorldY() {
        return getWorld().y;
    }

    public static Vector2f calcScreen() {
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / get().gameViewportSize.x) * 1920.0f;
        float currentY = getY() - get().gameViewportPos.y;
        currentY = 1080.0f - ((currentY / get().gameViewportSize.y) * 1080.0f);

        Vector2f screenPos = new Vector2f(currentX, currentY);
        return screenPos;
    }

    public static Vector2f getScreen() {
        return get().screenPos;
    }

    public static float getScreenX() {
        return getScreen().x;
    }

    public static float getScreenY() {
        return getScreen().y;
    }

    public static Vector2f screenToWorld(Vector2f screenCoords) {
        Vector2f normalizedScreenCords = new Vector2f(
                screenCoords.x / Window.getWidth(),
                screenCoords.y / Window.getHeight()
        );
        normalizedScreenCords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
        Camera camera = Window.getScene().camera();
        Vector4f tmp = new Vector4f(normalizedScreenCords.x, normalizedScreenCords.y,
                0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

    public static Vector2f worldToScreen(Vector2f worldCoords) {
        Camera camera = Window.getScene().camera();
        Vector4f ndcSpacePos = new Vector4f(worldCoords.x, worldCoords.y, 0, 1);
        Matrix4f view = new Matrix4f(camera.getViewMatrix());
        Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
        ndcSpacePos.mul(projection.mul(view));
        Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);
        windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        windowSpace.mul(new Vector2f(Window.getWidth(), Window.getHeight()));

        return windowSpace;
    }

}
