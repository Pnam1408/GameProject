package scenes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import components.Component;
import components.ComponentDeserializer;
import components.SpriteRenderer;
import components.StateMachine;
import components.Transform;
import engine.Camera;
import engine.GameObject;
import engine.GameObjectDeserializer;
import physic2D.Physics2D;
import render.Renderer;
import util.AssetPool;

public class Scene {
    private Renderer renderer;
    private Camera camera;
    private boolean isRunning;
    private List<GameObject> gameObjects;
    private SceneInitializer sceneInitializer;
    private Physics2D physics2d;
    private GameObject player;

    public Scene(SceneInitializer sceneInitializer) {
        this.sceneInitializer = sceneInitializer;
        this.physics2d = new Physics2D();
        this.renderer = new Renderer();
        this.isRunning = false;
        this.gameObjects = new ArrayList<>();
    }

    public void init() {
        this.camera = new Camera(new Vector2f(0f, 0f));
        this.sceneInitializer.loadResourses(this);
        this.sceneInitializer.init(this);
    }

    public void start() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.start();
            this.renderer.add(go);
        }

        isRunning = true;
    }

    public void editorUpdate(float dt) {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.editorUpdate(dt);

            if (go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2d.destroyGameObject(go);
                i--;
            }
        }
    }

    public void update(float dt) {

        this.physics2d.update(dt);
        
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.update(dt);

            if (go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2d.destroyGameObject(go);
                i--;
            }
        }
    };

    public void render() {
        this.renderer.render();
    };

    public Camera camera() {
        return this.camera;
    }

    public void imgui() {
        this.sceneInitializer.imgui();
    }

    public void save() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();

        try {
            String levelFilePath = AssetPool.getLevel(sceneInitializer.getLevel());
            FileWriter writer = new FileWriter(levelFilePath);
            List<GameObject> objToSerialize = new ArrayList<>();
            for (GameObject go : gameObjects) {
                if (go.doSerialization()) {
                    objToSerialize.add(go);
                }
            }
            writer.write(gson.toJson(objToSerialize));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();

        String inFile = "";
        String levelFilePath = AssetPool.getLevel(sceneInitializer.getLevel());
        try {
            inFile = new String(Files.readAllBytes(Paths.get(levelFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {
            int maxGoID = -1;
            int maxComID = -1;

            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i=0; i < objs.length; i++) {
                addGameObjectToScene(objs[i]);

                for (Component c : objs[i].getAllComponents()) {
                    if (c.getUid() > maxComID) {
                        maxComID = c.getUid();
                    }
                }
                if (objs[i].getUid() > maxGoID) {
                    maxGoID = objs[i].getUid();
                }
            }

            maxComID++;
            maxGoID++;
        }
        for (GameObject g : gameObjects) {
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
            if (g.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = g.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }
    }

    public void addGameObjectToScene(GameObject go) {
        physics2d.addGameObject(go);
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }

    public GameObject getGameObject(int gameObjectID) {
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectID)
                .findFirst();
        return result.orElse(null);
    }

    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public List<GameObject> getGameObjects() {
        return this.gameObjects;
    }

    public void destroy() {
        for(GameObject go : gameObjects) {
            go.destroy();
        }
        this.isRunning = false;
    }

    public Physics2D getPhysic2D() {
        return this.physics2d;
    }

    public GameObject getPlayer() {
        return this.player;
    }


}
