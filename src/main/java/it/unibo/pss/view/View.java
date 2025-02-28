package it.unibo.pss.view;

import java.util.concurrent.atomic.AtomicInteger;

import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.model.entity.*;
import it.unibo.pss.view.views.EntityView;
import it.unibo.pss.view.views.WorldView;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import it.unibo.pss.view.views.StackView;

public class View {
	private ModelDTO modelDTO;
	private final StackView viewport;

	public View(Stage stage, String title, int width, int height, ModelDTO modelDTO) {
		this.modelDTO = modelDTO;
		this.viewport = new StackView(width, height, true);
		viewport.registerRenderable(new WorldView());
		viewport.registerRenderable(new EntityView());

		Label plantCounter = new Label("plant: 0");
		Label sheepCounter = new Label("sheep: 0");
		Label wolfCounter = new Label("wolf: 0");

		BorderPane root = new BorderPane();
		root.setTop(new HBox(20, plantCounter, sheepCounter, wolfCounter));
		root.setCenter(viewport);

		stage.setScene(new Scene(root, width, height));
		stage.setTitle(title);
		stage.show();

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				viewport.render(modelDTO);
				updateCounters(plantCounter, sheepCounter, wolfCounter);
			}
		}.start();
	}

	public void updateModel(ModelDTO newModelDTO) {
		this.modelDTO = newModelDTO;
		viewport.render(modelDTO);
	}

	private void updateCounters(Label plantCounter, Label preyCounter, Label predatorCounter) {
		AtomicInteger plants = new AtomicInteger();
		AtomicInteger preys = new AtomicInteger();
		AtomicInteger predators = new AtomicInteger();

		modelDTO.getGrid().forEachTile(tile -> 
			tile.getEntities().stream().filter(BasicEntity::isAlive).forEach(entity -> {
				if (entity instanceof PlantEntity) plants.incrementAndGet();
				else if (entity instanceof PreyEntity) preys.incrementAndGet();
				else if (entity instanceof PredatorEntity) predators.incrementAndGet();
			})
		);

		plantCounter.setText("plant: " + plants.get());
		preyCounter.setText("sheep: " + preys.get());
		predatorCounter.setText("wolf: " + predators.get());
	}
}
