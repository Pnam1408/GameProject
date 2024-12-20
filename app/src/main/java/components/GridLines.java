package components;

import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.Camera;
import engine.Window;
import render.DebugDraw;
import util.Settings;

public class GridLines extends Component {

    @Override
    public void editorUpdate(float dt) {
        Camera camera = Window.getScene().camera();

        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        int firstX = ((int)(cameraPos.x / Settings.GRID_WIDTH) - 1) * Settings.GRID_HEIGHT;
        int firstY = ((int)(cameraPos.y / Settings.GRID_HEIGHT) - 1) * Settings.GRID_HEIGHT;

        int numVtLines = (int)(projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) + 2;
        int numHzLines = (int)(projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 2;

        int height = (int)(projectionSize.y * camera.getZoom() + Settings.GRID_HEIGHT * 2);
        int width = (int)(projectionSize.x * camera.getZoom() + Settings.GRID_WIDTH * 2);

        int maxLines = Math.max(numVtLines, numHzLines);
        Vector3f color = new Vector3f(0.7f, 0.7f, 0.7f);
        
        for (int i = 0; i < maxLines; i++) {
            int x = firstX + (Settings.GRID_WIDTH * i);
            int y = firstY + (Settings.GRID_HEIGHT * i);

            if (i < numVtLines) {
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if (i < numHzLines) {
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }

    }
}
