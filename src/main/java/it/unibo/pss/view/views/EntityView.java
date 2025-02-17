package it.unibo.pss.view.views;

import it.unibo.pss.controller.model.ModelDTO;
import it.unibo.pss.model.world.World;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.view.components.CullingHandler;
import it.unibo.pss.view.components.Camera;
import it.unibo.pss.view.components.Viewport.Renderable;
import it.unibo.pss.view.renderer.GeometryRenderer;
import it.unibo.pss.view.sprites.EntitySpriteLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

/** Renders entities isometrically using sprites. */
public class EntityView implements Renderable {
	private static final int DOT_SIZE = 5;
	private final EntitySpriteLoader spriteLoader = new EntitySpriteLoader();

	@Override
	public void render(GraphicsContext gc, ModelDTO modelDTO, Camera camera, GeometryRenderer renderer) {
		World grid = modelDTO.getGrid();
		int gridCols = grid.getWidth();
		int gridRows = grid.getHeight();
		double canvasWidth = gc.getCanvas().getWidth();
		double canvasHeight = gc.getCanvas().getHeight();
		Point2D baseOffset = renderer.computeCenterOffset(canvasWidth, canvasHeight, gridCols, gridRows);
		Point2D cameraOffset = camera.applyCamera(baseOffset);
		for (int x = 0; x < gridCols; x++) {
			for (int y = 0; y < gridRows; y++) {
				World.Tile tile = grid.getTile(x, y);
				if (tile.getEntities().isEmpty())
					continue;
				Rectangle2D rect = renderer.computeTileRect(x, y, cameraOffset.getX(), cameraOffset.getY(), camera.getScale());
				if (!CullingHandler.isRectVisible(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), canvasWidth, canvasHeight))
					continue;
				for (BasicEntity entity : tile.getEntities()) {
					double centerX = rect.getMinX() + rect.getWidth() / 2;
					double centerY = rect.getMinY() + rect.getHeight() / 2;
					gc.setFill(spriteLoader.getEntityColor(entity));
					gc.fillOval(centerX - DOT_SIZE / 2.0, centerY - DOT_SIZE / 2.0, DOT_SIZE, DOT_SIZE);
				}
			}
		}
	}
}
