package it.unibo.pss.view;

import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.view.components.Viewport;
import it.unibo.pss.view.views.EntityView;
import it.unibo.pss.view.views.WorldView;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/** Initializes the viewport and registers sub-views for rendering. */
public class View {

	private ModelDTO modelDTO;

	public View(Stage stage, String title, int width, int height, ModelDTO modelDTO) {
		this.modelDTO = modelDTO;
		Viewport viewport = new Viewport(width, height);
		viewport.registerRenderable(new WorldView());
		viewport.registerRenderable(new EntityView());

		StackPane root = new StackPane(viewport);
		Scene scene = new Scene(root, width, height);

		stage.setTitle(title);
		stage.setScene(scene);
		scene.setFill(Color.BLACK);
		stage.show();

		startRendering(viewport);
	}

	/** Starts the AnimationTimer for rendering updates. */
	private void startRendering(Viewport viewport) {
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				viewport.render(modelDTO);
			}
		}.start();
	}

	/** Updates the model data in the view. */
	public void updateModel(ModelDTO newModelDTO) {
		this.modelDTO = newModelDTO;
	}
}
