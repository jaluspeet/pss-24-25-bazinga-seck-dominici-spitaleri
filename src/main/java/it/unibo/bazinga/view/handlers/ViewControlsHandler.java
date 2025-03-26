package it.unibo.bazinga.view.handlers;

import it.unibo.bazinga.controller.observer.ModelDTO;
import it.unibo.bazinga.controller.observer.ViewDTO;
import it.unibo.bazinga.controller.observer.ViewObserver;
import it.unibo.bazinga.model.entity.BasicEntity;
import it.unibo.bazinga.model.entity.PlantEntity;
import it.unibo.bazinga.model.entity.SheepEntity;
import it.unibo.bazinga.model.entity.WolfEntity;
import it.unibo.bazinga.view.geometry.GeometryRenderer;
import it.unibo.bazinga.view.sprites.SpritePathResolver;
import it.unibo.bazinga.view.views.StackView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

public class ViewControlsHandler {

	private final StackView<StackView.Renderable> viewport;
	private final Label actionLabel;
	private final Label plantCounter;
	private final Label sheepCounter;
	private final Label wolfCounter;

	public ViewControlsHandler(StackView<StackView.Renderable> viewport) {
		this.viewport = viewport;
		this.actionLabel = new Label("actions: ");
		this.plantCounter = new Label("plant: 0");
		this.sheepCounter = new Label("sheep: 0");
		this.wolfCounter = new Label("wolf: 0");
	}

	/**
	 * Creates the control bar containing the counters, speed buttons,
	 * toggle view button and an action label scroll pane.
	 */
	public FlowPane createControlBar(ViewObserver viewObserver) {
		Button speedUpButton = createSpeedButton("+", -100, viewObserver);
		Button speedDownButton = createSpeedButton("-", 100, viewObserver);
		Button toggleViewButton = createToggleViewButton();

		ScrollPane actionScrollPane = new ScrollPane(actionLabel);
		actionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		actionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		actionScrollPane.setPrefSize(200, 30);
		actionScrollPane.setMinSize(200, 30);
		actionScrollPane.setMaxSize(200, 30);

		FlowPane topBar = new FlowPane(10, 10);
		topBar.setPadding(new Insets(10));
		topBar.getChildren().addAll(
				plantCounter,
				sheepCounter,
				wolfCounter,
				speedUpButton,
				speedDownButton,
				toggleViewButton,
				actionScrollPane
				);
		topBar.setPrefHeight(50);
		topBar.setMinHeight(50);
		topBar.setMaxHeight(50);
		return topBar;
	}

	private Button createSpeedButton(String label, int speedChange, ViewObserver viewObserver) {
		Button button = new Button(label);
		button.setOnAction(e -> viewObserver.onViewAction(new ViewDTO(new ViewDTO.SpeedCommand(speedChange))));
		return button;
	}

	private Button createToggleViewButton() {
		Button toggleViewButton = new Button("Toggle View");
		toggleViewButton.setOnAction(e -> toggleViewMode());
		return toggleViewButton;
	}

	/**
	 * Initializes a periodic counter update using a Timeline.
	 */
	public void initializeCountersUpdater(ModelDTO modelDTO) {
		Timeline counterTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> updateCounters(modelDTO)) );
		counterTimeline.setCycleCount(Timeline.INDEFINITE);
		counterTimeline.play();
	}

	private void updateCounters(ModelDTO modelDTO) {
		new Thread(() -> {
			int plants = 0;
			int sheep = 0;
			int wolves = 0;

			for (int x = 0; x < modelDTO.getGrid().getWidth(); x++) {
				for (int y = 0; y < modelDTO.getGrid().getHeight(); y++) {
					for (BasicEntity entity : modelDTO.getGrid().getTile(x, y).getEntities()) {
						if (!entity.isAlive()) continue;
						if (entity instanceof PlantEntity) { plants++; } 
						else if (entity instanceof SheepEntity) { sheep++; }
						else if (entity instanceof WolfEntity) { wolves++; }
					}
				}
			}
			final int finalPlants = plants;
			final int finalSheep = sheep;
			final int finalWolves = wolves;
			Platform.runLater(() -> {
				plantCounter.setText("plant: " + finalPlants);
				sheepCounter.setText("sheep: " + finalSheep);
				wolfCounter.setText("wolf: " + finalWolves);
			});
		}).start();
	}

	public void setActionText(String text) {
		actionLabel.setText(text);
	}

	/**
	 * Toggles the view mode between isometric and top-down.
	 */
	public void toggleViewMode() {
		GeometryRenderer current = viewport.getGeometryRenderer();
		if (current instanceof it.unibo.bazinga.view.geometry.IsometricRenderer) {
			viewport.setGeometryRenderer(new it.unibo.bazinga.view.geometry.TopDownRenderer());
			SpritePathResolver.setMode("topdown");
		} else {
			viewport.setGeometryRenderer(new it.unibo.bazinga.view.geometry.IsometricRenderer());
			SpritePathResolver.setMode("isometric");
		}
		viewport.clearSpriteCaches();
	}

	/**
	 * Reloads sprites by clearing caches.
	 */
	public void reloadSprites() {
		viewport.clearSpriteCaches();
	}
}

