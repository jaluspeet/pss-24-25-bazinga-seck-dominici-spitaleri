package it.unibo.pss.view.views;

import it.unibo.pss.view.geometry.GeometryRenderer;
import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.model.world.World;
import it.unibo.pss.view.components.CullingUtil;
import it.unibo.pss.view.components.Camera;
import it.unibo.pss.view.components.Viewport.Renderable;
import it.unibo.pss.view.sprites.WorldSpriteLoader;
import it.unibo.pss.view.components.CameraUtil;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class WorldView implements Renderable {
	private final WorldSpriteLoader spriteLoader = new WorldSpriteLoader("/sprites/world");

	@Override
	public void render(GraphicsContext gc, ModelDTO modelDTO, Camera camera, GeometryRenderer renderer) {
		World grid = modelDTO.getGrid();
		Point2D cameraOffset = CameraUtil.computeCameraOffset(renderer, camera, gc.getCanvas().getWidth(), gc.getCanvas().getHeight(), grid.getWidth(), grid.getHeight());

		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				Rectangle2D rect = renderer.computeTileRect(x, y, cameraOffset.getX(), cameraOffset.getY(), camera.getScale());
				if (!CullingUtil.isRectVisible(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), gc.getCanvas().getWidth(), gc.getCanvas().getHeight())) 
					continue;

				Image sprite = spriteLoader.getTileSprite(grid.getTile(x, y));
				if (sprite != null) {
					gc.drawImage(sprite, rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
				}
			}
		}
	}
}
