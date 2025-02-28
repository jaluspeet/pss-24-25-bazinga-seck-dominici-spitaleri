package it.unibo.pss.view.views;

import java.util.List;

import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.world.World;
import it.unibo.pss.view.geometry.GeometryRenderer;
import it.unibo.pss.view.handlers.CameraOffsetHandler;
import it.unibo.pss.view.handlers.CullingHandler;
import it.unibo.pss.view.handlers.PanZoomHandler;
import it.unibo.pss.view.sprites.EntitySpriteLoader;
import it.unibo.pss.view.views.StackView.Renderable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class EntityView implements Renderable {
	private final EntitySpriteLoader spriteLoader = new EntitySpriteLoader("/sprites/entity");

	@Override
	public void render(GraphicsContext gc, ModelDTO modelDTO, PanZoomHandler camera, GeometryRenderer renderer) {
		World grid = modelDTO.getGrid();
		Point2D cameraOffset = CameraOffsetHandler.computeCameraOffset(renderer, camera, gc.getCanvas().getWidth(), gc.getCanvas().getHeight(), grid.getWidth(), grid.getHeight());

		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				World.Tile tile = grid.getTile(x, y);

				List<BasicEntity> aliveEntities = tile.getEntities().stream()
					.filter(BasicEntity::isAlive)
					.toList();

				if (aliveEntities.isEmpty()) continue;

				Rectangle2D rect = renderer.computeTileRect(x, y, cameraOffset.getX(), cameraOffset.getY(), camera.getScale());
				if (!CullingHandler.isRectVisible(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), gc.getCanvas().getWidth(), gc.getCanvas().getHeight()))
					continue;

				for (BasicEntity entity : aliveEntities) {
					Image sprite = spriteLoader.getEntitySprite(entity);
					if (sprite != null) {
						double spriteSizeX = sprite.getWidth() * camera.getScale();
						double spriteSizeY = sprite.getHeight() * camera.getScale();
						gc.drawImage(sprite, rect.getMinX() + rect.getWidth() / 2 - spriteSizeX / 2, rect.getMinY() + rect.getHeight() / 2 - spriteSizeY / 2, spriteSizeX, spriteSizeY);
					}
				}
			}
		}
	}
}
