package it.unibo.pss.view;

import it.unibo.pss.common.SharedConstants;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class View {
	private ModelDTO modelDTO;
	private final StackView viewport;
	private final Label actionLabel;
	private final Label plantCounter;
	private final Label sheepCounter;
	private final Label wolfCounter;

	public View(Stage stage, String title, int width, int height, ViewObserver viewObserver) {
		this.viewport = new StackView(width, height, true);
		this.actionLabel = new Label("actions: ");
		this.plantCounter = new Label("plant: 0");
		this.sheepCounter = new Label("sheep: 0");
		this.wolfCounter = new Label("wolf: 0");

		initializeViewport();
		initializeUI(stage, title, width, height, viewObserver);
		setupMouseClickHandler(viewObserver);
		startRenderingLoop();
	}

	private void initializeViewport() {
		viewport.registerRenderable(new WorldView());
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

	private FlowPane createControlBar(ViewObserver viewObserver) {
		Button speedUpButton = createSpeedButton("+", -100, viewObserver);
		Button speedDownButton = createSpeedButton("-", 100, viewObserver);
		Button toggleViewButton = new Button("Toggle View");
		toggleViewButton.setOnAction(e -> {
			GeometryRenderer current = viewport.getGeometryRenderer();
			if (current instanceof it.unibo.pss.view.geometry.IsometricRenderer) {
				viewport.setGeometryRenderer(new it.unibo.pss.view.geometry.TopDownRenderer());
				SpritePathResolver.setMode("topdown");
			} else {
				viewport.setGeometryRenderer(new it.unibo.pss.view.geometry.IsometricRenderer());
				SpritePathResolver.setMode("isometric");
			}
			viewport.clearSpriteCaches();
		});
		FlowPane topBar = new FlowPane(10, 10);
		topBar.setPadding(new Insets(10));
		topBar.getChildren().addAll(plantCounter, sheepCounter, wolfCounter, speedUpButton, speedDownButton, toggleViewButton, actionLabel);
		return topBar;
	}

	private Button createSpeedButton(String label, int speedChange, ViewObserver viewObserver) {
		Button button = new Button(label);
		button.setOnAction(e -> viewObserver.onViewAction(new ViewDTO(new ViewDTO.SpeedCommand(speedChange))));
		return button;
	}

	private void setupMouseClickHandler(ViewObserver viewObserver) {
		viewport.setOnMouseClicked(e -> {
			Point2D localPoint = viewport.sceneToLocal(e.getSceneX(), e.getSceneY());
			GeometryRenderer renderer = viewport.getGeometryRenderer();
			double scale = viewport.getCamera().getScale();
			World grid = modelDTO.getGrid();
			Point2D cameraOffset = CameraOffsetHandler.computeCameraOffset(renderer, viewport.getCamera(),
					viewport.getWidth(), viewport.getHeight(), grid.getWidth(), grid.getHeight());

			int tileX, tileY;

			if (renderer instanceof it.unibo.pss.view.geometry.IsometricRenderer) {
				Point2D gridCoords = screenToIsometricGrid(localPoint, scale, cameraOffset);
				tileX = (int) gridCoords.getX();
				tileY = (int) gridCoords.getY();
			} else {
				tileX = (int) ((localPoint.getX() / scale - cameraOffset.getX()) / SharedConstants.TILE_WIDTH);
				tileY = (int) ((localPoint.getY() / scale - cameraOffset.getY()) / SharedConstants.TILE_HEIGHT);
			}

			if (tileX >= 0 && tileY >= 0 && tileX < grid.getWidth() && tileY < grid.getHeight()) {
				viewObserver.onViewAction(new ViewDTO(new ViewDTO.EntityTileClickCommand(tileX, tileY)));
			}
		});
	}

	private Point2D screenToIsometricGrid(Point2D screenPoint, double scale, Point2D cameraOffset) {
		double A = SharedConstants.TILE_WIDTH / 2.0;
		double B = SharedConstants.TILE_HEIGHT / 2.0;

		double Xprime = (screenPoint.getX() / scale) - cameraOffset.getX();
		double Yprime = (screenPoint.getY() / scale) - cameraOffset.getY() + SharedConstants.TILE_HEIGHT;

		int tileX = (int) Math.floor(((Xprime / A) + (Yprime / B)) / 2);
		int tileY = (int) Math.floor(((Yprime / B) - (Xprime / A)) / 2);

		return new Point2D(tileX, tileY);
	}

	private void startRenderingLoop() {
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				viewport.render(modelDTO);
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
						if (entity instanceof PlantEntity) plants++;
						else if (entity instanceof SheepEntity) sheep++;
						else if (entity instanceof WolfEntity) wolves++;
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

	public void updateModel(ModelDTO newModelDTO) {
		this.modelDTO = newModelDTO;
		viewport.render(modelDTO);
	}

	public void setActionText(String text) {
		actionLabel.setText(text);
	}
}
