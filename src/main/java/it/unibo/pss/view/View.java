package it.unibo.pss.view;

import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.model.entity.PlantEntity;
import it.unibo.pss.model.entity.PreyEntity;
import it.unibo.pss.model.entity.PredatorEntity;
import it.unibo.pss.view.components.Viewport;
import it.unibo.pss.view.views.EntityView;
import it.unibo.pss.view.views.WorldView;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class View {

	private ModelDTO modelDTO;

	public View(Stage stage, String title, int width, int height, ModelDTO modelDTO) {
		this.modelDTO = modelDTO;
		Viewport viewport = new Viewport(width, height);
		viewport.registerRenderable(new WorldView());
		viewport.registerRenderable(new EntityView());

		// Create a HBox for counters
		Label plantCounter = new Label("Plants: 0");
		Label preyCounter = new Label("Preys: 0");
		Label predatorCounter = new Label("Predators: 0");
		HBox counterBox = new HBox(20, plantCounter, preyCounter, predatorCounter);
		counterBox.setPadding(new Insets(10));
		counterBox.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: white;");

		// Use BorderPane to place counterBox at the top and viewport at the center.
		BorderPane root = new BorderPane();
		root.setTop(counterBox);
		root.setCenter(viewport);

		Scene scene = new Scene(root, width, height);
		stage.setTitle(title);
		stage.setScene(scene);
		scene.setFill(Color.GREY);
		stage.show();

		startRendering(viewport, plantCounter, preyCounter, predatorCounter);
	}

	/** Starts the AnimationTimer for rendering updates and counter updates. */
	private void startRendering(Viewport viewport, Label plantCounter, Label preyCounter, Label predatorCounter) {
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				viewport.render(modelDTO);
				updateCounters(plantCounter, preyCounter, predatorCounter);
			}
		}.start();
	}

	/** Updates the model data in the view. */
	public void updateModel(ModelDTO newModelDTO) {
		this.modelDTO = newModelDTO;
	}

	/** Iterates over the grid to count each entity type. */
	private void updateCounters(Label plantCounter, Label preyCounter, Label predatorCounter) {
		int plants = 0, preys = 0, predators = 0;
		var grid = modelDTO.getGrid();
		int cols = grid.getWidth();
		int rows = grid.getHeight();
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				var tile = grid.getTile(x, y);
				for (var entity : tile.getEntities()) {
					if (!entity.isAlive()) continue;
					if (entity instanceof PlantEntity) {
						plants++;
					} else if (entity instanceof PreyEntity) {
						preys++;
					} else if (entity instanceof PredatorEntity) {
						predators++;
					}
				}
			}
		}
		plantCounter.setText("Plants: " + plants);
		preyCounter.setText("Preys: " + preys);
		predatorCounter.setText("Predators: " + predators);
	}
}
