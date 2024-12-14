package scenes;

public abstract class SceneInitializer {
    private int level;

    public SceneInitializer(int level) {
        this.level = level;
    }

    public abstract void init(Scene scene);
    public abstract void loadResourses(Scene scene);
    public abstract void imgui();

    public int getLevel() {
        return this.level;
    }
}
