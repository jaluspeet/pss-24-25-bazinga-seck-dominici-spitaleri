package it.unibo.pss.view;

import java.util.concurrent.atomic.AtomicInteger;

import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.controller.observer.ViewDTO;
import it.unibo.pss.controller.observer.ViewObserver;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.entity.PlantEntity;
import it.unibo.pss.model.entity.SheepEntity;
import it.unibo.pss.model.entity.WolfEntity;
import it.unibo.pss.view.views.EntityView;
import it.unibo.pss.view.views.StackView;
import it.unibo.pss.view.views.WorldView;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class View {
	private ModelDTO modelDTO;
	private final StackView viewport;

	public View(Stage stage, String title, int width, int height, ModelDTO modelDTO, ViewObserver viewObserver) {
		
		// viewport
		this.modelDTO = modelDTO;
		this.viewport = new StackView(width, height, true);
		viewport.registerRenderable(new WorldView());
		viewport.registerRenderable(new EntityView());

		// counters
		Label plantCounter = new Label("plant: 0");
		Label sheepCounter = new Label("sheep: 0");
		Label wolfCounter = new Label("wolf: 0");

		// controls
		Button speedUpButton = new Button("Speed Up");
		Button speedDownButton = new Button("Speed Down");
		speedUpButton.setOnAction(e -> viewObserver.onViewAction(new ViewDTO(new ViewDTO.SpeedCommand(-100))));
		speedDownButton.setOnAction(e -> viewObserver.onViewAction(new ViewDTO(new ViewDTO.SpeedCommand(100))));
		
		// layout
		FlowPane topBar = new FlowPane(10, 10);
		topBar.setPadding(new Insets(10));
		topBar.getChildren().addAll(plantCounter, sheepCounter, wolfCounter, speedUpButton, speedDownButton);

		BorderPane root = new BorderPane();
		root.setTop(topBar);
		root.setCenter(viewport);

		// window
		Scene scene = new Scene(root, width, height);
		stage.setScene(scene);
		stage.setTitle(title);
		stage.show();

		// updating
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
					else if (entity instanceof SheepEntity) preys.incrementAndGet();
					else if (entity instanceof WolfEntity) predators.incrementAndGet();
				})
				);

		plantCounter.setText("plant: " + plants.get());
		preyCounter.setText("sheep: " + preys.get());
		predatorCounter.setText("wolf: " + predators.get());
	}
}
