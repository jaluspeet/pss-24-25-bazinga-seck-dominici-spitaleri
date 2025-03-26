package it.unibo.bazinga.view.views;

import it.unibo.bazinga.controller.observer.ModelDTO;
import it.unibo.bazinga.model.world.World;
import it.unibo.bazinga.view.geometry.GeometryRenderer;
import it.unibo.bazinga.view.handlers.CameraOffsetHandler;
import it.unibo.bazinga.view.handlers.CullingHandler;
import it.unibo.bazinga.view.sprites.WorldSpriteLoader;
import it.unibo.bazinga.view.sprites.SpriteCache;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * The WorldView class is responsible for rendering the world grid (terrain).
 */
public class WorldView implements StackView.Renderable, SpriteCache {
	private final WorldSpriteLoader spriteLoader = new WorldSpriteLoader("/world");
	private int highlightedTileX = -1;
	private int highlightedTileY = -1;

	/**
	 * The render method is called by the view manager to render the world grid.
	 *
	 * @param gc The graphics context to render to.
	 * @param modelDTO The model data to render.
	 * @param camera The camera handler to use for rendering.
	 * @param renderer The geometry renderer to use for rendering.
	 * @param now The current time in milliseconds.
	 */
	@Override
	public void render(GraphicsContext gc, ModelDTO modelDTO, it.unibo.bazinga.view.handlers.PanZoomHandler camera, GeometryRenderer renderer, long now) {
		World grid = modelDTO.getGrid();
		Point2D cameraOffset = CameraOffsetHandler.computeCameraOffset(renderer, camera, gc.getCanvas().getWidth(), gc.getCanvas().getHeight(), grid.getWidth(), grid.getHeight());

		// Render all tiles.
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				Rectangle2D rect = renderer.computeTileRect(x, y, cameraOffset.getX(), cameraOffset.getY(), camera.getScale());
				if (!CullingHandler.isRectVisible(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), gc.getCanvas().getWidth(), gc.getCanvas().getHeight()))
					continue;

				Image sprite = spriteLoader.getTileSprite(grid.getTile(x, y), now);
				if (sprite != null) {
					gc.drawImage(sprite, rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
				}
			}
		}

		if (highlightedTileX >= 0 && highlightedTileY >= 0 && highlightedTileX < grid.getWidth() && highlightedTileY < grid.getHeight()) {
			gc.setFill(Color.rgb(255, 255, 0, 0.3)); // semi-transparent yellow
			double[][] tileShape = renderer.computeTileOutline(highlightedTileX, highlightedTileY, cameraOffset.getX(), cameraOffset.getY(), camera.getScale());
			gc.fillPolygon(tileShape[0], tileShape[1], tileShape[0].length);
		}
	}

	/**
	 * The reloadSprites method is called by the view manager to reload the sprites
	 * (useful when the sprites are changed at runtime).
	 */
	@Override
	public void reloadSprites() {
		spriteLoader.reload();
	}

	/**
	 * Set tile to be highlighted (with a colored outline).
	 *
	 * @param x The x-coordinate of the tile to highlight.
	 * @param y The y-coordinate of the tile to highlight.
	 */
	public void setHighlightedTile(int x, int y) {
		this.highlightedTileX = x;
		this.highlightedTileY = y;
	}
}
