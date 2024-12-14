package scenes;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import org.joml.Vector2f;

import components.Component;
import components.SpriteSheet;
import engine.GameObject;
import engine.KeyListener;
import engine.Window;
import util.AssetPool;

public class MenuSceneInitializer extends SceneInitializer {

    private class menuControls extends Component {
        
        TextButton choosedButton;
        
        TextButton startButton;
        TextButton exitButton;
        TextButton continueButton;

        @Override
        public void start() {

            startButton = new TextButton("START", new Vector2f(65.0f, 34.0f));
            startButton.setButtonCallback(new ButtonCallback() {
                @Override
                public void pressed() {
                    Window.newGame();
                }
            });
            startButton.start();

            continueButton = new TextButton("CONTINUE", new Vector2f(65.0f, 24.0f));
            continueButton.setButtonCallback(new ButtonCallback() {
                @Override
                public void pressed() {
                    Window.continueLastSave();
                }
            });
            continueButton.start();

            exitButton = new TextButton("EXIT", new Vector2f(65.0f, 14.0f));
            exitButton.setButtonCallback(new ButtonCallback() {
                @Override
                public void pressed() {
                    Window.get().close();
                }
            });
            exitButton.start();

            choosedButton = startButton;
            startButton.setActive();

        }
    
        @Override
        public void update(float dt) {
            if (KeyListener.keyBeginPress(GLFW_KEY_C) || KeyListener.keyBeginPress(GLFW_KEY_ENTER)) {
                choosedButton.pressed();
            }
            if (KeyListener.keyBeginPress(GLFW_KEY_UP)) {
                if (choosedButton == continueButton) {
                    choosedButton.setInActive();
                    choosedButton = startButton;
                    choosedButton.setActive();
                }
                else if (choosedButton == exitButton) {
                    choosedButton.setInActive();
                    choosedButton = continueButton;
                    choosedButton.setActive();
                }
            } else if (KeyListener.keyBeginPress(GLFW_KEY_DOWN)) {
                if (choosedButton == startButton) {
                    choosedButton.setInActive();
                    choosedButton = continueButton;
                    choosedButton.setActive();
                }
                else if (choosedButton == continueButton) {
                    choosedButton.setInActive();
                    choosedButton = exitButton;
                    choosedButton.setActive();
                }
            }
        }
    }

    private GameObject menuObject;

    public MenuSceneInitializer(int level) {
        super(level);
    }

    @Override
    public void init(Scene scene) {
        menuObject = scene.createGameObject("Editor");
        menuObject.setNoSerialize();
        menuObject.addComponent(new menuControls());
        scene.addGameObjectToScene(menuObject);
        Window.getScene().getPhysic2D().pause();
    }

    @Override
    public void loadResourses(Scene scene) {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/fontfix.png", 
            new SpriteSheet(AssetPool.getTexture("assets/fontfix.png"),
             4, 6, 30*2, 0));
        AssetPool.addSpritesheet("assets/DoGhost- Asset Pack/DoGhost-SUMMER fix.png", 
            new SpriteSheet(AssetPool.getTexture("assets/DoGhost- Asset Pack/DoGhost-SUMMER fix.png"),
             8, 8, 10*13, 0));
        AssetPool.addSpritesheet("assets/DoGhost- Asset Pack/Hero animate and object fix.png", 
            new SpriteSheet(AssetPool.getTexture("assets/DoGhost- Asset Pack/Hero animate and object fix.png"),
             8, 8, 10*13, 0));
        AssetPool.addSpritesheet("assets/editor/gizmos.png",
            new SpriteSheet(AssetPool.getTexture("assets/editor/gizmos.png"), 24, 48, 3, 0));

        AssetPool.addSound("assets/sounds/jump.ogg", false);
        AssetPool.addSound("assets/sounds/dash_red_left.ogg", false);
        AssetPool.addSound("assets/sounds/powerUp.ogg", false);
        AssetPool.addSound("assets/sounds/hitHurt.ogg", false);
        AssetPool.addSound("assets/sounds/springboard-a-92438.ogg", false);

        AssetPool.addLevel(0, "levels/level0.txt");
        AssetPool.addLevel(1, "levels/level1.txt");
        AssetPool.addLevel(2, "levels/level2.txt");
        AssetPool.addLevel(3, "levels/level3.txt");
        AssetPool.addLevel(4, "levels/level4.txt");
        AssetPool.addLevel(5, "levels/level5.txt");
        AssetPool.addLevel(6, "levels/level6.txt");
        AssetPool.addLevel(7, "levels/level7.txt");
        AssetPool.addLevel(8, "levels/level8.txt");
        AssetPool.addLevel(9, "levels/level9.txt");
        AssetPool.addLevel(10, "levels/level10.txt");
        AssetPool.addLevel(11, "levels/level11.txt");
        AssetPool.addLevel(12, "levels/level12.txt");
        AssetPool.addLevel(13, "levels/level13.txt");
        AssetPool.addLevel(14, "levels/level14.txt");
        AssetPool.addLevel(15, "levels/level15.txt");
        AssetPool.addLevel(16, "levels/level16.txt");
        AssetPool.addLevel(17, "levels/level17.txt");
        AssetPool.addLevel(18, "levels/level18.txt");
        AssetPool.addLevel(19, "levels/level19.txt");
        // Test level
        AssetPool.addLevel(-1, "levels/levelTest.txt");
    }

    @Override
    public void imgui() {}

}


