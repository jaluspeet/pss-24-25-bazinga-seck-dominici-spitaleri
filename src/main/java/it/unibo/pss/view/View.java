package it.unibo.pss.view;

import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.model.entity.PlantEntity;
import it.unibo.pss.model.entity.PreyEntity;
import it.unibo.pss.model.entity.PredatorEntity;
import it.unibo.pss.view.components.Viewport;
import it.unibo.pss.view.views.EntityView;
import it.unibo.pss.view.views.WorldView;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class View {

	private ModelDTO modelDTO;

	// constructor for View
	public View(Stage stage, String title, int width, int height, ModelDTO modelDTO) {
	
		// initialize the modelDTO, register renderables
		this.modelDTO = modelDTO;
		Viewport viewport = new Viewport(width, height, false);
		viewport.registerRenderable(new WorldView());
		viewport.registerRenderable(new EntityView());

		// XXX: counter
		Label plantCounter = new Label("plant: 0");
		Label sheepCounter = new Label("sheep: 0");
		Label wolfCounter = new Label("wolf: 0");
		HBox counterBox = new HBox(20, plantCounter, sheepCounter, wolfCounter);
		BorderPane root = new BorderPane();
		root.setTop(counterBox);
		root.setCenter(viewport);

		// create the scene
		Scene scene = new Scene(root, width, height);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		startRendering(viewport, plantCounter, sheepCounter, wolfCounter);
	}

	// start rendering updates
	private void startRendering(Viewport viewport, Label plantCounter, Label preyCounter, Label predatorCounter) {
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				viewport.render(modelDTO);

				// XXX: update counters
				updateCounters(plantCounter, preyCounter, predatorCounter);
			}
		}.start();
	}

	// update the model data in the view
	public void updateModel(ModelDTO newModelDTO) {
		this.modelDTO = newModelDTO;
	}

	// XXX: entity counters
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
		plantCounter.setText("plant: " + plants);
		preyCounter.setText("sheep: " + preys);
		predatorCounter.setText("wolf: " + predators);
	}
}
