package it.unibo.pss.view;

import it.unibo.pss.controller.model.ModelDTO;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

public class View {

	private ModelDTO modelDTO;

	public View(Stage stage, String title, int width, int height, ModelDTO modelDTO) {
		this.modelDTO = modelDTO;
		stage.setTitle(title);
		stage.setWidth(width);
		stage.setHeight(height);
		stage.show();

		startRendering();
	}

	/** Starts the AnimationTimer for rendering updates. */
	private void startRendering() {
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				refreshView();
			}
		}.start();
	}

	/** Updates the model data in the view. */
	public void updateModel(ModelDTO newModelDTO) {
		this.modelDTO = newModelDTO;
	}

	/** Refreshes the view based on the latest model data. */
	private void refreshView() {
		// TODO: Implement graphical rendering logic
		System.out.println(modelDTO.getGrid());
	}
}
