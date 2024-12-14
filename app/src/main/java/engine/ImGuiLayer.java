package engine;

import imgui.ImFontConfig;
import imgui.ImFontGlyphRangesBuilder;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;

import org.lwjgl.glfw.GLFW;

import editor.GameViewWindow;
import editor.MenuBar;
import editor.PropertiesWindow;
import editor.SceneHierarchyWindow;
import render.PickingTexture;
import scenes.Scene;

public class ImGuiLayer {

    private long glfwWindow;
    private final String glslVersion = "#version 330 core";

    // LWJGL3 renderer (SHOULD be initialized)
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();

    private GameViewWindow gameViewWindow;
    private PropertiesWindow propertiesWindow;
    private MenuBar menuBar;
    private SceneHierarchyWindow sceneHierarchyWindow;

    public ImGuiLayer(long glfwWindow, PickingTexture pickingTexture) {
        this.glfwWindow = glfwWindow;
        this.gameViewWindow = new GameViewWindow();
        this.propertiesWindow = new PropertiesWindow(pickingTexture);
        this.menuBar = new MenuBar();
        this.sceneHierarchyWindow = new SceneHierarchyWindow();
    }

    // Initialize Dear ImGui.
    public void initImGui() {
        ImGui.createContext();

        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename("imgui.ini");
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);

        // Fonts configuration
        final ImFontConfig fontConfig = new ImFontConfig();
        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder();

        rangesBuilder.addRanges(io.getFonts().getGlyphRangesVietnamese());
        final short[] glyphRanges = rangesBuilder.buildRanges();

        io.getFonts().addFontFromFileTTF("assets\\fonts\\Roboto-Medium.ttf", 20, glyphRanges);
        io.getFonts().build();

        fontConfig.destroy();

        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init(glslVersion);

    }

    public void update(double dt, Scene currentScene) {
        startFrame();
        setupDockSpace();

        currentScene.imgui();
        gameViewWindow.imgui();
        propertiesWindow.imgui();
        menuBar.imgui();
        sceneHierarchyWindow.imgui();

        ImGui.end();
        endFrame();
    }

    private void startFrame() {
        imGuiGl3.newFrame();
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    private void endFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        // Update and Render additional Platform Windows
        // (Platform functions may change the current OpenGL context, so we save/restore it to make it easier to paste this code elsewhere.
        //  For this specific demo app we could also call glfwMakeContextCurrent(window) directly)
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupCurrentContext = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupCurrentContext);
        }
    }


    public void destroyImGui() {
        imGuiGlfw.shutdown();
        imGuiGl3.shutdown();
        ImGui.destroyContext();
    }

    private void setupDockSpace() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);

        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
        ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
        ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("Dockspace Demo", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        // Dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"));
    }

    public PropertiesWindow getPropertiesWindow() {
        return this.propertiesWindow;
    }
}
