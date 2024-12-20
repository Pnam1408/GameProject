package components;

import org.joml.Vector2f;
import org.joml.Vector4f;

import editor.JImGui;
import render.Texture;

public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private Sprite sprite = new Sprite();
    private boolean flipX = false;
    private boolean flipY = false;

    private transient Transform lastTransform;
    private transient boolean isDirty = true;

//    public SpriteRenderer(Sprite sprite) {
//        this.sprite = sprite;
//        this.color = new Vector4f(1f,1f,1f,1f);
//        isDirty = true;
//    }
//    
//    public SpriteRenderer(Vector4f color) {
//        this.color = color;
//        this.sprite = new Sprite(null);
//        isDirty = true;
//    }


    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void editorUpdate(float dt) {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void imgui() {
        if (JImGui.colorPicker4("Color Picker", this.color)) {
            this.isDirty = true;
        }
    }

    public Vector4f getColor() {
        return this.color;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords() {
        Vector2f[] texCoords = sprite.getTexCoords().clone();

        if (flipX == true) {
            Vector2f tmp = texCoords[0];
            texCoords[0] = texCoords[3];
            texCoords[3] = tmp;
            tmp = texCoords[1];
            texCoords[1] = texCoords[2];
            texCoords[2] = tmp;
        }
        
        if (flipY == true) {
            Vector2f tmp = texCoords[0];
            texCoords[0] = texCoords[1];
            texCoords[1] = tmp;
            tmp = texCoords[3];
            texCoords[3] = texCoords[2];
            texCoords[2] = tmp;
        }

        return texCoords;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        isDirty = true;
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.isDirty = true;
            this.color.set(color);
        }
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
        this.isDirty = true;
    }

    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
        this.isDirty = true;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void setClean() {
        this.isDirty = false;
    }

    public void setDirty() {
        this.isDirty = true;
    }

    public void setTexture(Texture texture) {
        this.sprite.setTexture(texture);
    }

    public void setInvisible() {
        this.color = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
        this.isDirty = true;
    }

    public void setVisible() {
        this.color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.isDirty = true;
    }
}
