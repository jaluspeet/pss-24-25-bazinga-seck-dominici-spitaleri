package it.unibo.bazinga.view;

import it.unibo.bazinga.controller.observer.ModelDTO;
import it.unibo.bazinga.controller.observer.ViewObserver;
import it.unibo.bazinga.view.handlers.ViewControlsHandler;
import it.unibo.bazinga.view.handlers.MouseHandler;
import it.unibo.bazinga.view.views.EntityView;
import it.unibo.bazinga.view.views.StackView;
import it.unibo.bazinga.view.views.WorldView;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class View {

	private ModelDTO modelDTO;
	private final StackView<StackView.Renderable> viewport;
	private final ViewControlsHandler controlsHandler;
	private final MouseHandler mouseHandler;
	private final ViewObserver viewObserver;
	private final WorldView worldView;

	public View(Stage stage, String title, int width, int height, ViewObserver viewObserver) {
		this.viewObserver = viewObserver;
		viewport = new StackView<>(width, height, true);

		// Register renderable views directly in View.java.
		worldView = new WorldView();
		viewport.registerRenderable(worldView);
		viewport.registerRenderable(new EntityView());

		// control bar
		controlsHandler = new ViewControlsHandler(viewport);

		// mouse handler
		mouseHandler = new MouseHandler(viewport, worldView, viewObserver);

		initializeUI(stage, title, width, height);
		startRenderingLoop();
	}

	private void initializeUI(Stage stage, String title, int width, int height) {
		BorderPane root = new BorderPane();
		root.setTop(controlsHandler.createControlBar(viewObserver));
		root.setCenter(viewport);
		stage.setScene(new Scene(root, width, height));
		stage.setTitle(title);
		stage.show();
	}

	/**
	 * Starts the rendering loop using an AnimationTimer.
	 */
	private void startRenderingLoop() {
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (modelDTO != null) {
					viewport.render(modelDTO, now);
				}
			}
		}.start();
	}

	/**
	 * Updates the model, triggers rendering, initializes counters,
	 * and updates the mouse handler with the current model.
	 */
	public void updateModel(ModelDTO newModelDTO) {
		this.modelDTO = newModelDTO;
		viewport.render(modelDTO, System.nanoTime());
		controlsHandler.initializeCountersUpdater(modelDTO);
		mouseHandler.setModelDTO(modelDTO);
	}

	public void setActionText(String text) {
		controlsHandler.setActionText(text);
	}

	/**
	 * Clears the sprite caches in the viewport.
	 */
	public void clearViewportCaches() {
		viewport.clearSpriteCaches();
	}
}

