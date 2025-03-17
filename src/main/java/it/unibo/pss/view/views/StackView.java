package it.unibo.pss.view.views;

import java.util.ArrayList;
import java.util.List;

import it.unibo.pss.view.handlers.PanZoomHandler;
import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.view.geometry.GeometryRenderer;
import it.unibo.pss.view.geometry.IsometricRenderer;
import it.unibo.pss.view.geometry.TopDownRenderer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Canvas that renders the model trough a list of registered renderables which act as layers
 */
public class StackView extends Canvas {
	private final List<Renderable> renderables = new ArrayList<>();
	private final PanZoomHandler camera;
	private GeometryRenderer geometryRenderer;
	private ModelDTO currentModelDTO;

	/**
	 * Constructor for StackView
	 *
	 * @param width     the width of the canvas
	 * @param height    the height of the canvas
	 * @param isometric true if the renderer should be isometric, false if top-down
	 */
	public StackView(double width, double height, boolean isometric) {
		super(width, height);
		camera = new PanZoomHandler(this, () -> render(currentModelDTO, System.nanoTime()));
		geometryRenderer = isometric ? new IsometricRenderer() : new TopDownRenderer();
	}

	/**
	 * set the geometry renderer (isometric or top-down)
	 *
	 * @param newRenderer the new renderer
	 */
	public void setGeometryRenderer(GeometryRenderer newRenderer) {
		this.geometryRenderer = newRenderer;
		render(currentModelDTO, System.nanoTime());
	}

	/** 
	 * get the geometry renderer (isometric or top-down)
	 *
	 * @return the geometry renderer
	 */
	public GeometryRenderer getGeometryRenderer() {
		return geometryRenderer;
	}

	/**
	 * get the camera handler
	 *
	 * @return the camera handler
	 */
	public PanZoomHandler getCamera() {
		return camera;
	}

	/**
	 * register a renderable object
	 *
	 * @param r the renderable object
	 */
	public void registerRenderable(Renderable r) {
		renderables.add(r);
	}

	/**
	 * trigger the rendering of each renderable
	 *
	 * @param modelDTO the model to render
	 * @param now      the current time
	 */
	public void render(ModelDTO modelDTO, long now) {
		currentModelDTO = modelDTO;
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, getWidth(), getHeight());
		renderables.forEach(r -> r.render(gc, modelDTO, camera, geometryRenderer, now));
	}

	/**
	 * clear the sprite caches (used for reloading sprites)
	 */
	public void clearSpriteCaches() {
		for (Renderable r : renderables) {
			if (r instanceof it.unibo.pss.view.sprites.SpriteCache) {
				((it.unibo.pss.view.sprites.SpriteCache) r).reloadSprites();
			}
		}
	}

	/**
	 * Interface for objects that can be rendered
	 */
	public interface Renderable {
		void render(GraphicsContext gc, ModelDTO modelDTO, PanZoomHandler camera, GeometryRenderer renderer, long now);
	}
}
