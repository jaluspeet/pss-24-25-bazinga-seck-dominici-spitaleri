package it.unibo.pss.view;

import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.controller.observer.ViewDTO;
import it.unibo.pss.controller.observer.ViewObserver;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.entity.PlantEntity;
import it.unibo.pss.model.entity.SheepEntity;
import it.unibo.pss.model.entity.WolfEntity;
import it.unibo.pss.model.world.World;
import it.unibo.pss.view.geometry.GeometryRenderer;
import it.unibo.pss.view.handlers.CameraOffsetHandler;
import it.unibo.pss.view.sprites.SpritePathResolver;
import it.unibo.pss.view.views.EntityView;
import it.unibo.pss.view.views.StackView;
import it.unibo.pss.view.views.WorldView;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.List;

public class View {
	private ModelDTO modelDTO;
	private final StackView viewport;
	private final Label actionLabel;
	private final Label plantCounter;
	private final Label sheepCounter;
	private final Label wolfCounter;
	// Keep a reference to WorldView to update the highlighted tile.
	private WorldView worldView;

	public View(Stage stage, String title, int width, int height, ViewObserver viewObserver) {
		this.viewport = new StackView(width, height, true);
		this.actionLabel = new Label("Actions: ");
		this.plantCounter = new Label("plant: 0");
		this.sheepCounter = new Label("sheep: 0");
		this.wolfCounter = new Label("wolf: 0");

		initializeViewport();
		initializeUI(stage, title, width, height, viewObserver);
		setupEventHandlers(viewObserver);
		startRenderingLoop();
	}

	// Register renderable components.
	private void initializeViewport() {
		// Create a WorldView instance and keep a reference for highlighting.
		worldView = new WorldView();
		viewport.registerRenderable(worldView);
		viewport.registerRenderable(new EntityView());
	}

	private void initializeUI(Stage stage, String title, int width, int height, ViewObserver viewObserver) {
		FlowPane topBar = createControlBar(viewObserver);
		BorderPane root = new BorderPane();
		root.setTop(topBar);
		root.setCenter(viewport);

		stage.setScene(new Scene(root, width, height));
		stage.setTitle(title);
		stage.show();
	}

	// Create the control bar with fixed controls and a scrollable action list.
	private FlowPane createControlBar(ViewObserver viewObserver) {
		Button speedUpButton = createSpeedButton("+", -100, viewObserver);
		Button speedDownButton = createSpeedButton("-", 100, viewObserver);
		Button toggleViewButton = createToggleViewButton();

		// Wrap the action label in a ScrollPane for horizontal scrolling.
		ScrollPane actionScrollPane = new ScrollPane(actionLabel);
		actionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		actionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		double actionPaneWidth = 200;
		double actionPaneHeight = 30;
		actionScrollPane.setPrefSize(actionPaneWidth, actionPaneHeight);
		actionScrollPane.setMinSize(actionPaneWidth, actionPaneHeight);
		actionScrollPane.setMaxSize(actionPaneWidth, actionPaneHeight);

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
		double fixedPanelHeight = 50;
		topBar.setPrefHeight(fixedPanelHeight);
		topBar.setMinHeight(fixedPanelHeight);
		topBar.setMaxHeight(fixedPanelHeight);

		return topBar;
	}

	// Create a speed button.
	private Button createSpeedButton(String label, int speedChange, ViewObserver viewObserver) {
		Button button = new Button(label);
		button.setOnAction(e -> viewObserver.onViewAction(new ViewDTO(new ViewDTO.SpeedCommand(speedChange))));
		return button;
	}

	// Create the toggle view button.
	private Button createToggleViewButton() {
		Button toggleViewButton = new Button("Toggle View");
		toggleViewButton.setOnAction(e -> toggleViewMode());
		return toggleViewButton;
	}

	private void setupEventHandlers(ViewObserver viewObserver) {
		setupMouseClickHandler(viewObserver);
	}

	// Handle mouse clicks using the geometry renderer to convert screen to grid coordinates.
	private void setupMouseClickHandler(ViewObserver viewObserver) {
		viewport.setOnMouseClicked(e -> handleMouseClick(e.getSceneX(), e.getSceneY(), viewObserver));
	}

	private void handleMouseClick(double sceneX, double sceneY, ViewObserver viewObserver) {
		Point2D localPoint = viewport.sceneToLocal(sceneX, sceneY);
		double scale = viewport.getCamera().getScale();
		World grid = modelDTO.getGrid();
		Point2D cameraOffset = CameraOffsetHandler.computeCameraOffset(
				viewport.getGeometryRenderer(),
				viewport.getCamera(),
				viewport.getWidth(),
				viewport.getHeight(),
				grid.getWidth(),
				grid.getHeight()
				);
		Point2D gridCoords = viewport.getGeometryRenderer().screenToGrid(localPoint, scale, cameraOffset);
		int tileX = (int) gridCoords.getX();
		int tileY = (int) gridCoords.getY();

		if (tileX >= 0 && tileY >= 0 && tileX < grid.getWidth() && tileY < grid.getHeight()) {
			// Update the highlighted tile in WorldView.
			worldView.setHighlightedTile(tileX, tileY);
			viewObserver.onViewAction(new ViewDTO(new ViewDTO.EntityTileClickCommand(tileX, tileY)));
		}
	}

	private void toggleViewMode() {
		GeometryRenderer current = viewport.getGeometryRenderer();
		if (current instanceof it.unibo.pss.view.geometry.IsometricRenderer) {
			viewport.setGeometryRenderer(new it.unibo.pss.view.geometry.TopDownRenderer());
			SpritePathResolver.setMode("topdown");
		} else {
			viewport.setGeometryRenderer(new it.unibo.pss.view.geometry.IsometricRenderer());
			SpritePathResolver.setMode("isometric");
		}
		viewport.clearSpriteCaches();
	}

	// Start a single AnimationTimer and pass its timestamp to all renderables.
	private void startRenderingLoop() {
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				viewport.render(modelDTO, now);
			}
		}.start();
		initializeCountersUpdater();
	}

	private void initializeCountersUpdater() {
		Timeline counterTimeline = new Timeline(
				new KeyFrame(Duration.millis(500), e -> updateCounters())
				);
		counterTimeline.setCycleCount(Timeline.INDEFINITE);
		counterTimeline.play();
	}

	private void updateCounters() {
		new Thread(() -> {
			int plants = 0;
			int sheep = 0;
			int wolves = 0;

			for (int x = 0; x < modelDTO.getGrid().getWidth(); x++) {
				for (int y = 0; y < modelDTO.getGrid().getHeight(); y++) {
					for (BasicEntity entity : modelDTO.getGrid().getTile(x, y).getEntities()) {
						if (!entity.isAlive()) continue;
						if (entity instanceof PlantEntity) {
							plants++;
						} else if (entity instanceof SheepEntity) {
							sheep++;
						} else if (entity instanceof WolfEntity) {
							wolves++;
						}
					}
				}
			}
			final int p = plants, s = sheep, w = wolves;
			Platform.runLater(() -> {
				plantCounter.setText("plant: " + p);
				sheepCounter.setText("sheep: " + s);
				wolfCounter.setText("wolf: " + w);
			});
		}).start();
	}

	// Update the model and re-render with the current timestamp.
	public void updateModel(ModelDTO newModelDTO) {
		this.modelDTO = newModelDTO;
		viewport.render(modelDTO, System.nanoTime());
	}

	// Set the actions text in the scrollable area.
	public void setActionText(String text) {
		actionLabel.setText(text);
	}

	// Alternatively, to display a list of actions separated by commas.
	public void setActions(List<String> actions) {
		String joinedActions = String.join(", ", actions);
		actionLabel.setText("Actions: " + joinedActions);
	}
}
