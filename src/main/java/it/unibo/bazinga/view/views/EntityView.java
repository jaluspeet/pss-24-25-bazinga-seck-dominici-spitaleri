package it.unibo.bazinga.view.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.unibo.bazinga.controller.observer.ModelDTO;
import it.unibo.bazinga.model.entity.BasicEntity;
import it.unibo.bazinga.model.world.World;
import it.unibo.bazinga.view.geometry.GeometryRenderer;
import it.unibo.bazinga.view.handlers.CameraOffsetHandler;
import it.unibo.bazinga.view.handlers.CullingHandler;
import it.unibo.bazinga.view.handlers.PanZoomHandler;
import it.unibo.bazinga.view.sprites.EntitySpriteLoader;
import it.unibo.bazinga.view.sprites.SpriteCache;
import it.unibo.bazinga.view.views.StackView.Renderable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * The EntityView class is responsible for rendering all entities in the game world.
 */
public class EntityView implements Renderable, SpriteCache {
	private final EntitySpriteLoader spriteLoader = new EntitySpriteLoader("/entity");
	private final Map<Integer, InterpolationData> interpolationMap = new HashMap<>();
	private static final long INTERP_DURATION_NANO = 100_000_000L;

	/**
	 * The InterpolationData class is used to store the previous and current grid positions of an entity,
	 * as well as the last update time.
	 */
	private static class InterpolationData {
		Point2D previous;
		Point2D current;
		long lastUpdateTime;

		/**
		 * Constructor for InterpolationData.
		 *
		 * @param pos the initial position.
		 * @param time the initial time.
		 */
		InterpolationData(Point2D pos, long time) {
			this.previous = pos;
			this.current = pos;
			this.lastUpdateTime = time;
		}

		/**
		 * Update the current position and time.
		 *
		 * @param newPos the new position.
		 * @param time the new time.
		 */
		void update(Point2D newPos, long time) {
			if (!this.current.equals(newPos)) {
				this.previous = this.current;
				this.current = newPos;
				this.lastUpdateTime = time;
			}
		}
	}

	/** 
	 * Override the render method to render all entities in the world grid.
	 *
	 * @param gc the graphics context.
	 * @param modelDTO the model data transfer object.
	 * @param camera the pan-zoom handler.
	 * @param renderer the geometry renderer.
	 * @param now the current timestamp.
	 */
	@Override
	public void render(GraphicsContext gc, ModelDTO modelDTO, PanZoomHandler camera, GeometryRenderer renderer, long now) {
		World grid = modelDTO.getGrid();
		double canvasWidth = gc.getCanvas().getWidth();
		double canvasHeight = gc.getCanvas().getHeight();
		Point2D cameraOffset = CameraOffsetHandler.computeCameraOffset(renderer, camera, canvasWidth, canvasHeight, grid.getWidth(), grid.getHeight());

		// Collect all entities in the grid.
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

		// batch render entities by z-index.
		for (int currentZ = 0; currentZ <= 2; currentZ++) {
			for (BasicEntity entity : allEntities) {
				if (entity.getZIndex() != currentZ)
					continue;

				// Compute the current grid position.
				Point2D currentGridPos = new Point2D(entity.getX(), entity.getY());

				// Retrieve or initialize interpolation data.
				InterpolationData interpData = interpolationMap.get(entity.getId());
				if (interpData == null) {
					interpData = new InterpolationData(currentGridPos, now);
					interpolationMap.put(entity.getId(), interpData);
				} else {
					interpData.update(currentGridPos, now);
				}

				// Compute the interpolation fraction using the passed timestamp.
				double fraction = Math.min(1.0, (double) (now - interpData.lastUpdateTime) / INTERP_DURATION_NANO);

				// Get the pixel rectangles for the previous and current grid positions.
				Rectangle2D prevRect = renderer.computeTileRect((int) interpData.previous.getX(), (int) interpData.previous.getY(), cameraOffset.getX(), cameraOffset.getY(), camera.getScale());
				Rectangle2D currRect = renderer.computeTileRect((int) interpData.current.getX(), (int) interpData.current.getY(), cameraOffset.getX(), cameraOffset.getY(), camera.getScale());

				// Linearly interpolate the top-left coordinates.
				double interpMinX = prevRect.getMinX() + fraction * (currRect.getMinX() - prevRect.getMinX());
				double interpMinY = prevRect.getMinY() + fraction * (currRect.getMinY() - prevRect.getMinY());
				Rectangle2D interpolatedRect = new Rectangle2D(interpMinX, interpMinY, currRect.getWidth(), currRect.getHeight());

				// Skip rendering if the entity is not visible.
				if (!CullingHandler.isRectVisible(interpolatedRect.getMinX(), interpolatedRect.getMinY(), interpolatedRect.getWidth(), interpolatedRect.getHeight(), canvasWidth, canvasHeight))
					continue;

				// Render the entity sprite.
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

	/**
	 * override the reloadSprites method to reload all entity sprites.
	 */
	@Override
	public void reloadSprites() {
		spriteLoader.reload();
	}
}
