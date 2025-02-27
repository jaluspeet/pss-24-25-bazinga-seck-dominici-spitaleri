package it.unibo.pss.view.views;

import it.unibo.pss.view.geometry.GeometryRenderer;
import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.model.world.World;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.view.components.CullingUtil;
import it.unibo.pss.view.components.Camera;
import it.unibo.pss.view.components.Viewport.Renderable;
import it.unibo.pss.view.sprites.EntitySpriteLoader;
import it.unibo.pss.view.components.CameraUtil;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

// render the entities
public class EntityView implements Renderable {
	private final EntitySpriteLoader spriteLoader = new EntitySpriteLoader("/sprites/entity");

	// override the render method to render the entities
	@Override
	public void render(GraphicsContext gc, ModelDTO modelDTO, Camera camera, GeometryRenderer renderer) {
		World grid = modelDTO.getGrid();
		int gridCols = grid.getWidth();
		int gridRows = grid.getHeight();
		double canvasWidth = gc.getCanvas().getWidth();
		double canvasHeight = gc.getCanvas().getHeight();
		Point2D cameraOffset = CameraUtil.computeCameraOffset(renderer, camera, canvasWidth, canvasHeight, gridCols, gridRows);
		for (int x = 0; x < gridCols; x++) {
			for (int y = 0; y < gridRows; y++) {
				World.Tile tile = grid.getTile(x, y);
				if (tile.getEntities().isEmpty())
					continue;
				Rectangle2D rect = renderer.computeTileRect(x, y, cameraOffset.getX(), cameraOffset.getY(), camera.getScale());
				if (!CullingUtil.isRectVisible(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), canvasWidth, canvasHeight))
					continue;

				for (BasicEntity entity : tile.getEntities()) {
					if (!entity.isAlive()) {
						continue; // Skip dead entities
					}
					double centerX = rect.getMinX() + rect.getWidth() / 2;
					double centerY = rect.getMinY() + rect.getHeight() / 2;
					Image sprite = spriteLoader.getEntitySprite(entity);
					if (sprite != null) {
						double spriteWidth = sprite.getWidth() * camera.getScale();
						double spriteHeight = sprite.getHeight() * camera.getScale();
						gc.drawImage(sprite, centerX - spriteWidth / 2, centerY - spriteHeight / 2, spriteWidth, spriteHeight);
					}
				}
			}
		}
	}
}
