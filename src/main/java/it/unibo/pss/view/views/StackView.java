package it.unibo.pss.view.views;

import java.util.ArrayList;
import java.util.List;

import it.unibo.pss.view.handlers.PanZoomHandler;
import it.unibo.pss.view.sprites.SpriteCache;
import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.view.geometry.GeometryRenderer;
import it.unibo.pss.view.geometry.IsometricRenderer;
import it.unibo.pss.view.geometry.TopDownRenderer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class StackView extends Canvas {
	private final List<Renderable> renderables = new ArrayList<>();
	private final PanZoomHandler camera;
	private GeometryRenderer geometryRenderer;
	private ModelDTO currentModelDTO;

	public StackView(double width, double height, boolean isometric) {
		super(width, height);
		camera = new PanZoomHandler(this, () -> render(currentModelDTO));
		geometryRenderer = isometric ? new IsometricRenderer() : new TopDownRenderer();
	}

	public void setGeometryRenderer(GeometryRenderer newRenderer) {
		this.geometryRenderer = newRenderer;
		render(currentModelDTO);
	}

	public GeometryRenderer getGeometryRenderer() {
		return geometryRenderer;
	}

	public PanZoomHandler getCamera() {
		return camera;
	}

	public void registerRenderable(Renderable r) {
		renderables.add(r);
	}

	public void render(ModelDTO modelDTO) {
		currentModelDTO = modelDTO;
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, getWidth(), getHeight());
		renderables.forEach(r -> r.render(gc, modelDTO, camera, geometryRenderer));
	}

	public void clearSpriteCaches() {
		for (Renderable r : renderables) {
			if (r instanceof SpriteCache) {
				((SpriteCache) r).reloadSprites();
			}
		}
	}

	public interface Renderable {
		void render(GraphicsContext gc, ModelDTO modelDTO, PanZoomHandler camera, GeometryRenderer renderer);
	}
}
