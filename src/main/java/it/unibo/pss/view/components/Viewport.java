package it.unibo.pss.view.components;

import it.unibo.pss.controller.model.ModelDTO;
import it.unibo.pss.view.renderer.GeometryRenderer;
import it.unibo.pss.view.renderer.IsometricRenderer;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/** A canvas that applies camera transforms and geometry rendering. */
public class Viewport extends Canvas {

	private final List<Renderable> renderables = new ArrayList<>();
	private final Camera camera;
	private ModelDTO currentModelDTO;
	private final GeometryRenderer geometryRenderer;

	public Viewport(double width, double height) {
		super(width, height);
		camera = new Camera(this, () -> render(currentModelDTO));
		geometryRenderer = new IsometricRenderer();
	}

	/** Registers a view to be rendered. */
	public void registerRenderable(Renderable r) {
		renderables.add(r);
	}

	/** Sets the current model data. */
	public void setModelDTO(ModelDTO modelDTO) {
		this.currentModelDTO = modelDTO;
	}

	/** Clears the canvas and renders all registered views using camera and geometry renderer. */
	public void render(ModelDTO modelDTO) {
		setModelDTO(modelDTO);
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, getWidth(), getHeight());
		gc.save();
		gc.scale(camera.getScale(), camera.getScale());
		gc.translate(-camera.getPanX(), -camera.getPanY());
		for (Renderable r : renderables) {
			r.render(gc, modelDTO, camera, geometryRenderer);
		}
		gc.restore();
	}

	public Camera getCamera() {
		return camera;
	}

	/** Renderable views now receive a geometry renderer instance. */
	public interface Renderable {
		void render(GraphicsContext gc, ModelDTO modelDTO, Camera camera, GeometryRenderer renderer);
	}
}
