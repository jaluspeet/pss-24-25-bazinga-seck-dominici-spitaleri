package it.unibo.pss.view.views;

import it.unibo.pss.controller.model.ModelDTO;
import it.unibo.pss.model.world.WorldGrid;
import it.unibo.pss.view.components.CullingHandler;
import it.unibo.pss.view.components.Camera;
import it.unibo.pss.view.components.Viewport.Renderable;
import it.unibo.pss.view.renderer.GeometryRenderer;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Renders entities isometrically as red dots (or texture as needed). */
public class EntityView implements Renderable {
	private static final int DOT_SIZE = 5;

	@Override
	public void render(GraphicsContext gc, ModelDTO modelDTO, Camera camera, GeometryRenderer renderer) {
		WorldGrid grid = modelDTO.getGrid();
		int gridCols = grid.getWidth();
		int gridRows = grid.getHeight();
		double canvasWidth = gc.getCanvas().getWidth();
		double canvasHeight = gc.getCanvas().getHeight();
		Point2D centerOffset = renderer.computeCenterOffset(canvasWidth, canvasHeight, gridCols, gridRows);
		gc.setFill(Color.RED);
		for (int x = 0; x < gridCols; x++) {
			for (int y = 0; y < gridRows; y++) {
				WorldGrid.Tile tile = grid.getTile(x, y);
				if (tile.getEntities().isEmpty()) continue;
				Rectangle2D rect = renderer.computeTileRect(x, y, centerOffset.getX(), centerOffset.getY(), camera.getScale());
				if (!CullingHandler.isRectVisible(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), canvasWidth, canvasHeight)) {
					continue;
				}
				double centerX = rect.getMinX() + rect.getWidth() / 2;
				double centerY = rect.getMinY() + rect.getHeight() / 2;
				gc.fillOval(centerX - DOT_SIZE / 2.0, centerY - DOT_SIZE / 2.0, DOT_SIZE, DOT_SIZE);
			}
		}
	}
}
