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

/**
 * Main view class responsible for creating the application window,
 * registering renderable views, and handling user input.
 */
public class View {
	private ModelDTO modelDTO;
	private final StackView viewport;
	private final Label actionLabel;
	private final Label plantCounter;
	private final Label sheepCounter;
	private final Label wolfCounter;
	private WorldView worldView;

	/**
	 * Constructor for View
	 *
	 * @param stage        The primary stage of the application.
	 * @param title        The title of the application window.
	 * @param width        The width of the application window.
	 * @param height       The height of the application window.
	 * @param viewObserver The observer handling view events.
	 */
	public View(Stage stage, String title, int width, int height, ViewObserver viewObserver) {
		this.viewport = new StackView(width, height, true);
		this.actionLabel = new Label("actions: ");
		this.plantCounter = new Label("plant: 0");
		this.sheepCounter = new Label("sheep: 0");
		this.wolfCounter = new Label("wolf: 0");

		initializeViewport();
		initializeUI(stage, title, width, height, viewObserver);
		setupEventHandlers(viewObserver);
		startRenderingLoop();
	}

	/**
	 * Initializes and registers renderable views.
	 */
	private void initializeViewport() {
		worldView = new WorldView();
		viewport.registerRenderable(worldView);
		viewport.registerRenderable(new EntityView());
	}

	/**
	 * Initializes the user interface components.
	 *
	 * @param stage        The primary stage.
	 * @param title        The title of the window.
	 * @param width        The width of the window.
	 * @param height       The height of the window.
	 * @param viewObserver The observer for handling user interactions.
	 */
	private void initializeUI(Stage stage, String title, int width, int height, ViewObserver viewObserver) {
		FlowPane topBar = createControlBar(viewObserver);
		BorderPane root = new BorderPane();
		root.setTop(topBar);
		root.setCenter(viewport);

		stage.setScene(new Scene(root, width, height));
		stage.setTitle(title);
		stage.show();
	}

	/**
	 * Creates the control bar with UI elements for interaction.
	 *
	 * @param viewObserver The observer handling user actions.
	 * @return A FlowPane containing the control bar elements.
	 */
	private FlowPane createControlBar(ViewObserver viewObserver) {
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

	/**
	 * Creates a button to control simulation speed.
	 *
	 * @param label        The label of the button.
	 * @param speedChange  The speed adjustment value.
	 * @param viewObserver The observer handling user actions.
	 * @return A Button instance.
	 */
	private Button createSpeedButton(String label, int speedChange, ViewObserver viewObserver) {
		Button button = new Button(label);
		button.setOnAction(_ -> viewObserver.onViewAction(new ViewDTO(new ViewDTO.SpeedCommand(speedChange))));
		return button;
	}

	/**
	 * Creates a button to toggle between different view modes.
	 *
	 * @return A Button instance.
	 */
	private Button createToggleViewButton() {
		Button toggleViewButton = new Button("Toggle View");
		toggleViewButton.setOnAction(_ -> toggleViewMode());
		return toggleViewButton;
	}

	/**
	 * Sets up event handlers for mouse clicks.
	 *
	 * @param viewObserver The observer handling user actions.
	 */
	private void setupEventHandlers(ViewObserver viewObserver) {
		setupMouseClickHandler(viewObserver);
	}

	/**
	 * Handle mouse clicks using the geometry renderer to convert screen to grid coordinates.
	 *
	 * @param viewObserver The observer handling user actions.
	 */
	private void setupMouseClickHandler(ViewObserver viewObserver) {
		viewport.setOnMouseClicked(e -> handleMouseClick(e.getSceneX(), e.getSceneY(), viewObserver));
	}

	/**
	 * Handles mouse clicks by converting screen coordinates to grid coordinates and
	 * sending a command to the observer.
	 *
	 * @param sceneX       The x-coordinate of the mouse click.
	 * @param sceneY       The y-coordinate of the mouse click.
	 * @param viewObserver The observer handling user actions.
	 * @param viewObserver The observer handling user actions.
	 */
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
			worldView.setHighlightedTile(tileX, tileY);
			viewObserver.onViewAction(new ViewDTO(new ViewDTO.EntityTileClickCommand(tileX, tileY)));
		}
	}

	/**
	 * Toggles between isometric and top-down view modes.
	 */
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

	/**
	 * Starts the rendering loop using an AnimationTimer.
	 */
	private void startRenderingLoop() {
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				viewport.render(modelDTO, now);
			}
		}.start();
		initializeCountersUpdater();
	}

	/**
	 * Initializes a periodic counter update every 500ms.
	 */
	private void initializeCountersUpdater() {
		Timeline counterTimeline = new Timeline(
				new KeyFrame(Duration.millis(500), _ -> updateCounters())
				);
		counterTimeline.setCycleCount(Timeline.INDEFINITE);
		counterTimeline.play();
	}

	/**
	 * Updates entity counters (plants, sheep, wolves) displayed in the UI.
	 */
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

	/**
	 * Updates the model and re-renders the viewport.
	 *
	 * @param newModelDTO The updated model data.
	 */
	public void updateModel(ModelDTO newModelDTO) {
		this.modelDTO = newModelDTO;
		viewport.render(modelDTO, System.nanoTime());
	}

	/**
	 * Sets the action text displayed in the UI.
	 *
	 * @param text The text to display.
	 */
	public void setActionText(String text) {
		actionLabel.setText(text);
	}
}

