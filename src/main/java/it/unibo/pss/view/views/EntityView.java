package it.unibo.pss.view.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private final Map<Integer, InterpolationData> interpolationMap = new HashMap<>();

	private static final long INTERP_DURATION_NANO = 100_000_000L;

	private static class InterpolationData {
		Point2D previous;
		Point2D current;
		long lastUpdateTime;

		InterpolationData(Point2D pos, long time) {
			this.previous = pos;
			this.current = pos;
			this.lastUpdateTime = time;
		}

		void update(Point2D newPos, long time) {
			if (!this.current.equals(newPos)) {
				this.previous = this.current;
				this.current = newPos;
				this.lastUpdateTime = time;
			}
		}
	}

	@Override
	public void render(GraphicsContext gc, ModelDTO modelDTO, PanZoomHandler camera, GeometryRenderer renderer) {
		World grid = modelDTO.getGrid();
		double canvasWidth = gc.getCanvas().getWidth();
		double canvasHeight = gc.getCanvas().getHeight();
		Point2D cameraOffset = CameraOffsetHandler.computeCameraOffset(renderer, camera, canvasWidth, canvasHeight,
				grid.getWidth(), grid.getHeight());

		List<BasicEntity> allEntities = new ArrayList<>();
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				for (BasicEntity entity : grid.getTile(x, y).getEntities()) {
					if (entity.isAlive()) {
						allEntities.add(entity);
					} else {
						interpolationMap.remove(entity.getId());
					}
				}
			}
		}

		// Batch render entities by z-index
		for (int currentZ = 0; currentZ <= 2; currentZ++) {
			for (BasicEntity entity : allEntities) {
				if (entity.getZIndex() != currentZ)
					continue;

				// compute the current grid position.
				Point2D currentGridPos = new Point2D(entity.getX(), entity.getY());
				long now = System.nanoTime();

				// retrieve or initialize interpolation data.
				InterpolationData interpData = interpolationMap.get(entity.getId());
				if (interpData == null) {
					interpData = new InterpolationData(currentGridPos, now);
					interpolationMap.put(entity.getId(), interpData);
				} else {
					interpData.update(currentGridPos, now);
				}

				// compute the interpolation factor
				double fraction = Math.min(1.0, (double) (now - interpData.lastUpdateTime) / INTERP_DURATION_NANO);

				// compute the pixel rectangles for the previous and current grid positions.
				Rectangle2D prevRect = renderer.computeTileRect((int) interpData.previous.getX(),
						(int) interpData.previous.getY(),
						cameraOffset.getX(), cameraOffset.getY(),
						camera.getScale());
				Rectangle2D currRect = renderer.computeTileRect((int) interpData.current.getX(),
						(int) interpData.current.getY(),
						cameraOffset.getX(), cameraOffset.getY(),
						camera.getScale());

				// linearly interpolate the top-left coordinates.
				double interpMinX = prevRect.getMinX() + fraction * (currRect.getMinX() - prevRect.getMinX());
				double interpMinY = prevRect.getMinY() + fraction * (currRect.getMinY() - prevRect.getMinY());
				Rectangle2D interpolatedRect = new Rectangle2D(interpMinX, interpMinY, currRect.getWidth(), currRect.getHeight());

				if (!CullingHandler.isRectVisible(interpolatedRect.getMinX(), interpolatedRect.getMinY(),
							interpolatedRect.getWidth(), interpolatedRect.getHeight(),
							canvasWidth, canvasHeight))
					continue;

				String actionKey = modelDTO.getEntityActions().getOrDefault(entity.getId(), "IDLE");
				Image sprite = spriteLoader.getEntitySprite(entity, actionKey);
				if (sprite != null) {
					double spriteSizeX = sprite.getWidth() * camera.getScale();
					double spriteSizeY = sprite.getHeight() * camera.getScale();
					double drawX = interpolatedRect.getMinX() + interpolatedRect.getWidth() / 2 - spriteSizeX / 2;
					double drawY = interpolatedRect.getMinY() + interpolatedRect.getHeight() - spriteSizeY;
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
