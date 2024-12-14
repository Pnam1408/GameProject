package engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.*;

import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import render.DebugDraw;
import render.Framebuffer;
import render.PickingTexture;
import render.Renderer;
import render.Shader;
import scenes.EditorSceneInitializer;
import scenes.EndSceneInitializer;
import scenes.LevelSceneInitializer;
import scenes.MenuSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL30.*;

public class Window implements Observer {

    public static final int menuLevel = 0;
    public static final int editorlevel = 18;
    public static final int lastLevel = 19;

    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;
    private boolean runtimePlaying = false;

    private static Window window = null;

    private long audioContext;
    private long audioDevice;

    private static int currentLevel;
    private static Scene currentScene;

    public static final boolean RELEASE_BUILD = true;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "A game";
        EventSystem.addObservers(this);
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public static void changeScene(SceneInitializer sceneInitializer) {
        if (currentScene!= null) {
            currentScene.destroy();
        }

        if (!RELEASE_BUILD) {
            getImguiLayer().getPropertiesWindow().setActiveGameObject(null);
        }

        currentLevel = sceneInitializer.getLevel();

        currentScene = new Scene(sceneInitializer);

    //    currentScene.init();
    //    currentScene.load();
        currentScene.init();
        currentScene.load();
        currentScene.start();
    }

    public static Scene getScene() {
        return currentScene;
    }

    public void run() {
        System.out.println("Hello LWJGL" + Version.getVersion() + "!");

        init();
        loop();

        // Destroy audio context
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Set up an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if ( !glfwInit() ) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to creat GLFW Window");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallBack);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable V-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // Initialize the audio device
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
        
        if (!alCapabilities.OpenAL10) {
            assert false : "Audio library not supported.";
        }

        // This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer = new Framebuffer(1920, 1080);
        this.pickingTexture = new PickingTexture(1920, 1080);
        glViewport(0, 0, 1920, 1080);

        if (RELEASE_BUILD) {
            runtimePlaying = true;
            toMenu();
        } else {
            this.imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
            this.imGuiLayer.initImGui();
            changeScene(new EditorSceneInitializer(editorlevel));
        }
    }

    public void loop() {

        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            // Render pass 1. Render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0, 0, 1920, 1080);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            // Render pass 2. Render actual game
            DebugDraw.beginFrame();

            this.framebuffer.bind();
            
            glClearColor(61.0f/255.0f, 31.0f/255.0f, 77.0f/255.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {

                Renderer.bindShader(defaultShader);
                if (runtimePlaying) {
                    currentScene.update(dt);
                } else {
                    currentScene.editorUpdate(dt);
                }
                currentScene.render();
                DebugDraw.draw();
            }
            
            this.framebuffer.unbind();

            if (RELEASE_BUILD) {
                // NOTE: This is the most complicated piece for release builds. In release builds
                //       we want to just blit the framebuffer to the main window so we can see the game
                //
                //       In non-release builds, we usually draw the framebuffer to an ImGui component as an image.
                glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer.getFboID());
                glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
                glBlitFramebuffer(0, 0, framebuffer.width, framebuffer.height, 0, 0, this.width, this.height,
                        GL_COLOR_BUFFER_BIT, GL_NEAREST);
            } else {
                this.imGuiLayer.update(dt, currentScene);
            }

            KeyListener.endFrame();
            MouseListener.endFrame();

            glfwSwapBuffers(glfwWindow);

            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
        
    }

    public static int getHeight() {
        return get().height;
    }

    public static int getWidth() {
        return get().width;
    }

    public static Framebuffer getFramebuffer() {
        return get().framebuffer;
    }

    public static float getTargetAspectRatio() {
        return 16.0f / 9.0f;
    }

    public static ImGuiLayer getImguiLayer() {
        return get().imGuiLayer;
    }

    @Override
    public void onNotify(GameObject go, Event event) {
        switch (event.type) {
            case GameEngineStartPlay:
                this.runtimePlaying = true;
                currentScene.save();
                Window.changeScene(new LevelSceneInitializer(currentLevel));
                break;
            case GameEngineStopPlay:
                this.runtimePlaying = false;
                Window.changeScene(new EditorSceneInitializer(editorlevel));
                break;
            case LoadLevel:
                Window.changeScene(new EditorSceneInitializer(editorlevel));
                break;
            case SaveLevel:
                currentScene.save();
                break;
            default:
                break;
        }
    }

    public static int getCurrentLevel() {
        return currentLevel;
    }

    public void close() {
        glfwSetWindowShouldClose(glfwWindow, true);
    }

    public static void nextLevel() {
        if (currentLevel + 1 == lastLevel) {
            Window.changeScene(new EndSceneInitializer(lastLevel));
        } else {
            Window.changeScene(new LevelSceneInitializer(currentLevel + 1));
        }
        if (RELEASE_BUILD) {
            saveLevelIndex();
        }
    }

    public static void restartLevel() {
        Window.changeScene(new LevelSceneInitializer(currentLevel));
    }

    public static void newGame() {
        Window.changeScene(new LevelSceneInitializer(1));
        saveLevelIndex();
    }

    public static void continueLastSave() {
        int saveLevel = readSaveFile();
        if (saveLevel == 0) {
            Window.newGame();
        } else if (saveLevel == lastLevel) {
            Window.toEndScreen();
        } else {
            Window.changeScene(new LevelSceneInitializer(saveLevel));
        }
    }

    public static void toMenu() {
        Window.changeScene(new MenuSceneInitializer(0));
    }

    public static void toEndScreen() {
        Window.changeScene(new EndSceneInitializer(lastLevel));
    }

    public static void saveLevelIndex() {
        try {
            FileWriter myWriter = new FileWriter("save.txt");
            myWriter.write(String.valueOf(currentLevel));
            myWriter.close();
        } catch (IOException e) {
            System.out.println("Can not write save file");
            e.printStackTrace();
        }
    }

    private static int readSaveFile() {
        int level = 0;
        try {
            File myObj = new File("save.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            level = Integer.parseInt(data);
        }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Save file not found");
            e.printStackTrace();
        }
        return level;
    }
}

