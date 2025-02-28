package it.unibo.pss.view.components;

import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.view.geometry.GeometryRenderer;
import it.unibo.pss.view.geometry.IsometricRenderer;
import it.unibo.pss.view.geometry.TopDownRenderer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import java.util.*;

public class Viewport extends Canvas {
	private final List<Renderable> renderables = new ArrayList<>();
	private final Camera camera;
	private final GeometryRenderer geometryRenderer;
	private ModelDTO currentModelDTO;

	public Viewport(double width, double height, boolean isometric) {
		super(width, height);
		camera = new Camera(this, () -> render(currentModelDTO));
		geometryRenderer = isometric ? new IsometricRenderer() : new TopDownRenderer();
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

	public Camera getCamera() {
		return camera;
	}

	public interface Renderable {
		void render(GraphicsContext gc, ModelDTO modelDTO, Camera camera, GeometryRenderer renderer);
	}
}
