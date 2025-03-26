package it.unibo.bazinga.view.views;

import java.util.ArrayList;
import java.util.List;

import it.unibo.bazinga.view.handlers.PanZoomHandler;
import it.unibo.bazinga.controller.observer.ModelDTO;
import it.unibo.bazinga.view.geometry.GeometryRenderer;
import it.unibo.bazinga.view.geometry.IsometricRenderer;
import it.unibo.bazinga.view.geometry.TopDownRenderer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Canvas that renders the model through a list of registered renderables.
 *
 * @param <T> the type of renderable objects.
 */
public class StackView<T extends StackView.Renderable> extends Canvas {
	private final List<T> renderables = new ArrayList<>();
	private final PanZoomHandler camera;
	private GeometryRenderer geometryRenderer;
	private ModelDTO currentModelDTO;

	/**
	 * Constructor for StackView.
	 *
	 * @param width     the width of the canvas.
	 * @param height    the height of the canvas.
	 * @param isometric true if the renderer should be isometric, false if top-down.
	 */
	public StackView(double width, double height, boolean isometric) {
		super(width, height);
		camera = new PanZoomHandler(this, () -> render(currentModelDTO, System.nanoTime()));
		geometryRenderer = isometric ? new IsometricRenderer() : new TopDownRenderer();
	}

	/**
	 * Sets the geometry renderer (isometric or top-down).
	 *
	 * @param newRenderer the new renderer.
	 */
	public void setGeometryRenderer(GeometryRenderer newRenderer) {
		this.geometryRenderer = newRenderer;
		render(currentModelDTO, System.nanoTime());
	}

	/**
	 * Gets the geometry renderer.
	 *
	 * @return the geometry renderer.
	 */
	public GeometryRenderer getGeometryRenderer() {
		return geometryRenderer;
	}

	/**
	 * Gets the camera handler.
	 *
	 * @return the camera handler.
	 */
	public PanZoomHandler getCamera() {
		return camera;
	}

	/**
	 * Registers a renderable object.
	 *
	 * @param renderable the renderable object.
	 */
	public void registerRenderable(T renderable) {
		renderables.add(renderable);
	}

	/**
	 * Triggers the rendering of each renderable.
	 *
	 * @param modelDTO the model to render.
	 * @param now      the current time.
	 */
	public void render(ModelDTO modelDTO, long now) {
		currentModelDTO = modelDTO;
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, getWidth(), getHeight());
		for (T renderable : renderables) {
			renderable.render(gc, modelDTO, camera, geometryRenderer, now);
		}
	}

	/**
	 * Clears the sprite caches (used for reloading sprites).
	 */
	public void clearSpriteCaches() {
		for (T renderable : renderables) {
			if (renderable instanceof it.unibo.bazinga.view.sprites.SpriteCache) {
				((it.unibo.bazinga.view.sprites.SpriteCache) renderable).reloadSprites();
			}
		}
	}

	/**
	 * Interface for objects that can be rendered.
	 */
	public interface Renderable {
		void render(javafx.scene.canvas.GraphicsContext gc, 
				it.unibo.bazinga.controller.observer.ModelDTO modelDTO,
				it.unibo.bazinga.view.handlers.PanZoomHandler camera, 
				GeometryRenderer renderer, 
				long now);
	}
}

