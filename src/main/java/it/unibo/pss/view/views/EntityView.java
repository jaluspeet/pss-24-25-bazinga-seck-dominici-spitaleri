package it.unibo.pss.view.views;

import it.unibo.pss.controller.model.ModelDTO;
import it.unibo.pss.model.world.World;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.view.components.CullingUtil;
import it.unibo.pss.view.components.Camera;
import it.unibo.pss.view.components.Viewport.Renderable;
import it.unibo.pss.view.renderer.GeometryRenderer;
import it.unibo.pss.view.sprites.EntitySpriteLoader;
import it.unibo.pss.view.components.CameraUtil;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/** Renders entities using their sprite textures. */
public class EntityView implements Renderable {
	private final EntitySpriteLoader spriteLoader = new EntitySpriteLoader("/sprites/entity");

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
					double centerX = rect.getMinX() + rect.getWidth() / 2;
					double centerY = rect.getMinY() + rect.getHeight() / 2;
					Image texture = spriteLoader.getEntityTexture(entity);
					if (texture != null) {
						double spriteWidth = texture.getWidth() * camera.getScale();
						double spriteHeight = texture.getHeight() * camera.getScale();
						gc.drawImage(texture, centerX - spriteWidth / 2, centerY - spriteHeight / 2, spriteWidth, spriteHeight);
					}
				}
			}
		}
	}
}
