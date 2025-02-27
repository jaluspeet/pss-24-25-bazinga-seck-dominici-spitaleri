package it.unibo.pss.view.components;

import it.unibo.pss.controller.observer.ModelDTO;

import java.util.ArrayList;
import java.util.List;

import it.unibo.pss.view.geometry.GeometryRenderer;
import it.unibo.pss.view.geometry.IsometricRenderer;
import it.unibo.pss.view.geometry.TopDownRenderer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

// canvas for rendering views
public class Viewport extends Canvas {

	private final List<Renderable> renderables = new ArrayList<>();
	private final Camera camera;
	private ModelDTO currentModelDTO;
	private final GeometryRenderer geometryRenderer;

	// constructor for Viewport
	public Viewport(double width, double height, boolean isometric) {
		super(width, height);
		camera = new Camera(this, () -> render(currentModelDTO));
		geometryRenderer = isometric ? new IsometricRenderer() : new TopDownRenderer();
	}

	// register a renderable view
	public void registerRenderable(Renderable r) {
		renderables.add(r);
	}

	// set the model data
	public void setModelDTO(ModelDTO modelDTO) {
		this.currentModelDTO = modelDTO;
	}

	// render the model data
	public void render(ModelDTO modelDTO) {
		setModelDTO(modelDTO);
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, getWidth(), getHeight());
		for (Renderable r : renderables) {
			r.render(gc, modelDTO, camera, geometryRenderer);
		}
	}

	// get the camera
	public Camera getCamera() {
		return camera;
	}

	// interface for renderable views
	public interface Renderable {
		void render(GraphicsContext gc, ModelDTO modelDTO, Camera camera, GeometryRenderer renderer);
	}
}
