package it.unibo.pss.view.views;

import java.util.ArrayList;
import java.util.List;
import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.world.World;
import it.unibo.pss.view.geometry.GeometryRenderer;
import it.unibo.pss.view.handlers.CameraOffsetHandler;
import it.unibo.pss.view.handlers.CullingHandler;
import it.unibo.pss.view.handlers.PanZoomHandler;
import it.unibo.pss.view.sprites.EntitySpriteLoader;
import it.unibo.pss.view.sprites.SpriteCache;
import it.unibo.pss.view.views.StackView.Renderable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class EntityView implements Renderable, SpriteCache {
	private final EntitySpriteLoader spriteLoader = new EntitySpriteLoader("/entity");

	@Override
	public void render(GraphicsContext gc, ModelDTO modelDTO, PanZoomHandler camera, GeometryRenderer renderer) {
		World grid = modelDTO.getGrid();
		double canvasWidth = gc.getCanvas().getWidth();
		double canvasHeight = gc.getCanvas().getHeight();
		Point2D cameraOffset = CameraOffsetHandler.computeCameraOffset(renderer, camera, canvasWidth, canvasHeight, grid.getWidth(), grid.getHeight());

		// collect all alive entities
		List<BasicEntity> allEntities = new ArrayList<>();
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				for (BasicEntity entity : grid.getTile(x, y).getEntities()) {
					if (entity.isAlive()) {
						allEntities.add(entity);
					}
				}
			}
		}

		// batch render by z index
		for (int currentZ = 0; currentZ <= 2; currentZ++) {
			for (BasicEntity entity : allEntities) {
				if (entity.getZIndex() != currentZ) continue;

				Rectangle2D rect = renderer.computeTileRect(entity.getX(), entity.getY(), cameraOffset.getX(), cameraOffset.getY(), camera.getScale());
				if (!CullingHandler.isRectVisible(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), canvasWidth, canvasHeight))
					continue;

				String actionKey = modelDTO.getEntityActions().getOrDefault(entity.getId(), "IDLE");
				Image sprite = spriteLoader.getEntitySprite(entity, actionKey);
				if (sprite != null) {
					double spriteSizeX = sprite.getWidth() * camera.getScale();
					double spriteSizeY = sprite.getHeight() * camera.getScale();
					double drawX = rect.getMinX() + rect.getWidth() / 2 - spriteSizeX / 2;
					double drawY = rect.getMinY() + rect.getHeight() / 2 - spriteSizeY / 2;
					gc.drawImage(sprite, drawX, drawY, spriteSizeX, spriteSizeY);
				}
			}
		}
	}

	@Override
	public void reloadSprites() {
		spriteLoader.reload();
	}
}
