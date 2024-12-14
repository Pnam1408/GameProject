package scenes;

import org.joml.Vector2f;

import components.EditorCamera;
import components.GizmoSystem;
import components.GridLines;
import components.KeyControls;
import components.MouseControls;
import components.Sprite;
import components.SpriteSheet;
import engine.GameObject;
import engine.Prefabs;
import util.AssetPool;
import imgui.ImGui;
import imgui.ImVec2;

public class EditorSceneInitializer extends SceneInitializer {

    private GameObject editorObject;

    public EditorSceneInitializer(int level) {
        super(level);
    }

    @Override
    public void init(Scene scene) {
        SpriteSheet gizmos = AssetPool.getSpritesheet("assets/editor/gizmos.png");

        editorObject = scene.createGameObject("Editor");
        editorObject.setNoSerialize();
        editorObject.addComponent(new MouseControls());
        editorObject.addComponent(new KeyControls());
        editorObject.addComponent(new GridLines());
        editorObject.addComponent(new EditorCamera(scene.camera()));
        editorObject.addComponent(new GizmoSystem(gizmos));
        scene.addGameObjectToScene(editorObject);

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
    public void imgui() {
        SpriteSheet tiles = AssetPool.getSpritesheet("assets/DoGhost- Asset Pack/DoGhost-SUMMER fix.png");
        ImGui.begin("Editor Stuff");
        editorObject.imgui();
        ImGui.end();

        ImGui.begin("Test Window");

        if (ImGui.beginTabBar("WindowTabBar")) {
            if (ImGui.beginTabItem("Tiles")) {
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);
                
                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < tiles.size(); i++) {
                    Sprite sprite = tiles.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 4;
                    float spriteHeight = sprite.getHeight() * 4;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();
                
                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateTile(sprite);
                        editorObject.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();
                
                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < tiles.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Decal")) {
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);
                
                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < tiles.size(); i++) {
                    Sprite sprite = tiles.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 4;
                    float spriteHeight = sprite.getHeight() * 4;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();
                
                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, 8, 8);
                        object.transform.zIndex = -1;
                        editorObject.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();
                
                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < tiles.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Texts")) {
                SpriteSheet texts = AssetPool.getSpritesheet("assets/fontfix.png");
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);
                
                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < texts.size(); i++) {
                    Sprite sprite = texts.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 4;
                    float spriteHeight = sprite.getHeight() * 4;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();
                
                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, 4, 6);
                        editorObject.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();
                
                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < texts.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Entity")) {
                SpriteSheet spriteSheet = AssetPool.getSpritesheet("assets/DoGhost- Asset Pack/Hero animate and object fix.png");
                Sprite playerSprite = spriteSheet.getSprite(0);
                float spriteWidth = playerSprite.getWidth() * 4;
                float spriteHeight = playerSprite.getHeight() * 4;
                int id = playerSprite.getTexId();
                Vector2f[] texCoords = playerSprite.getTexCoords();

                ImGui.pushID(1);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject player = Prefabs.generatePlayer();
                    editorObject.getComponent(MouseControls.class).pickupObject(player);
                }
                ImGui.popID();
                ImGui.sameLine();

                Sprite boneSprite = spriteSheet.getSprite(125);
                spriteWidth = boneSprite.getWidth() * 4;
                spriteHeight = boneSprite.getHeight() * 4;
                id = boneSprite.getTexId();
                texCoords = boneSprite.getTexCoords();

                ImGui.pushID(2);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject bone = Prefabs.generateBone();
                    editorObject.getComponent(MouseControls.class).pickupObject(bone);
                }
                ImGui.popID();
                ImGui.sameLine();

                Sprite fruitSprite = spriteSheet.getSprite(57);
                spriteWidth = fruitSprite.getWidth() * 4;
                spriteHeight = fruitSprite.getHeight() * 4;
                id = fruitSprite.getTexId();
                texCoords = fruitSprite.getTexCoords();

                ImGui.pushID(3);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject fruit = Prefabs.generateDashRefill();
                    editorObject.getComponent(MouseControls.class).pickupObject(fruit);
                }
                ImGui.popID();
                ImGui.sameLine();

                Sprite bubbleSprite = spriteSheet.getSprite(45);
                spriteWidth = bubbleSprite.getWidth() * 4;
                spriteHeight = bubbleSprite.getHeight() * 4;
                id = bubbleSprite.getTexId();
                texCoords = bubbleSprite.getTexCoords();

                ImGui.pushID(4);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject bubble = Prefabs.generateCrumbleBlock();
                    editorObject.getComponent(MouseControls.class).pickupObject(bubble);
                }
                ImGui.popID();
                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Spike")) {
                if (ImGui.button("Up")) {
                    GameObject object = Prefabs.generateSpike(tiles.getSprite(102), 0);
                    editorObject.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.sameLine();
                if (ImGui.button("Right")) {
                    GameObject object = Prefabs.generateSpike(tiles.getSprite(103), 1);
                    editorObject.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.sameLine();
                if (ImGui.button("Down")) {
                    GameObject object = Prefabs.generateSpike(tiles.getSprite(102), 2);
                    editorObject.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.sameLine();
                if (ImGui.button("Left")) {
                    GameObject object = Prefabs.generateSpike(tiles.getSprite(103), 3);
                    editorObject.getComponent(MouseControls.class).pickupObject(object);
                }

                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Spring")) {
                if (ImGui.button("Up")) {
                    GameObject object = Prefabs.generateSpring(0);
                    editorObject.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.sameLine();
                if (ImGui.button("Right")) {
                    GameObject object = Prefabs.generateSpring(1);
                    editorObject.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.sameLine();
                if (ImGui.button("Down")) {
                    GameObject object = Prefabs.generateSpring(2);
                    editorObject.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.sameLine();
                if (ImGui.button("Left")) {
                    GameObject object = Prefabs.generateSpring(3);
                    editorObject.getComponent(MouseControls.class).pickupObject(object);
                }

                ImGui.endTabItem();
            }

            ImGui.endTabBar();

        }

        ImGui.end();
    }

}
