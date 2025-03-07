package it.unibo.pss.view.views;

import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.model.world.World;
import it.unibo.pss.view.geometry.GeometryRenderer;
import it.unibo.pss.view.handlers.CameraOffsetHandler;
import it.unibo.pss.view.handlers.CullingHandler;
import it.unibo.pss.view.sprites.WorldSpriteLoader;
import it.unibo.pss.view.sprites.SpriteCache;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class WorldView implements StackView.Renderable, SpriteCache {
	private final WorldSpriteLoader spriteLoader = new WorldSpriteLoader("/world");
	// Holds the highlighted tile coordinates.
	private int highlightedTileX = -1;
	private int highlightedTileY = -1;

	@Override
	public void render(GraphicsContext gc, ModelDTO modelDTO,
			it.unibo.pss.view.handlers.PanZoomHandler camera, GeometryRenderer renderer, long now) {
		World grid = modelDTO.getGrid();
		Point2D cameraOffset = CameraOffsetHandler.computeCameraOffset(
				renderer, camera, gc.getCanvas().getWidth(), gc.getCanvas().getHeight(),
				grid.getWidth(), grid.getHeight()
				);

		// Render all tiles.
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				Rectangle2D rect = renderer.computeTileRect(x, y, cameraOffset.getX(), cameraOffset.getY(), camera.getScale());
				if (!CullingHandler.isRectVisible(rect.getMinX(), rect.getMinY(),
							rect.getWidth(), rect.getHeight(),
							gc.getCanvas().getWidth(), gc.getCanvas().getHeight()))
					continue;

				Image sprite = spriteLoader.getTileSprite(grid.getTile(x, y), now);
				if (sprite != null) {
					gc.drawImage(sprite, rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
				}
			}
		}

		// Draw the highlight on top.
		if (highlightedTileX >= 0 && highlightedTileY >= 0 && highlightedTileX < grid.getWidth() && highlightedTileY < grid.getHeight()) {
			gc.setStroke(Color.YELLOW);
			gc.setLineWidth(3);
			double[][] outline = renderer.computeTileOutline(highlightedTileX, highlightedTileY, cameraOffset.getX(), cameraOffset.getY(), camera.getScale());
			gc.strokePolygon(outline[0], outline[1], outline[0].length);
		}
	}

	@Override
	public void reloadSprites() {
		spriteLoader.reload();
	}

	// Setter to update the highlighted tile.
	public void setHighlightedTile(int x, int y) {
		this.highlightedTileX = x;
		this.highlightedTileY = y;
	}
}
