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
import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.world.World;
import it.unibo.pss.view.geometry.GeometryRenderer;
import it.unibo.pss.view.handlers.CameraOffsetHandler;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class View {
	private ModelDTO modelDTO;
	private final StackView viewport;
	private Label actionLabel = new Label("Actions: ");

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
		Button speedUpButton = new Button("+");
		Button speedDownButton = new Button("-");
		speedUpButton.setOnAction(e -> viewObserver.onViewAction(new ViewDTO(new ViewDTO.SpeedCommand(-100))));
		speedDownButton.setOnAction(e -> viewObserver.onViewAction(new ViewDTO(new ViewDTO.SpeedCommand(100))));

		// Layout: put counters, speed buttons, and the action label in a FlowPane.
		FlowPane topBar = new FlowPane(10, 10);
		topBar.setPadding(new Insets(10));
		topBar.getChildren().addAll(plantCounter, sheepCounter, wolfCounter, speedUpButton, speedDownButton, actionLabel);

		BorderPane root = new BorderPane();
		root.setTop(topBar);
		root.setCenter(viewport);

		Scene scene = new Scene(root, width, height);
		stage.setScene(scene);
		stage.setTitle(title);
		stage.show();

		viewport.setOnMouseClicked(e -> {
			Point2D localPoint = viewport.sceneToLocal(e.getSceneX(), e.getSceneY());
			GeometryRenderer isoRenderer = viewport.getGeometryRenderer();
			double scale = viewport.getCamera().getScale();
			World grid = modelDTO.getGrid();
			Point2D cameraOffset = CameraOffsetHandler.computeCameraOffset(
					isoRenderer,
					viewport.getCamera(),
					viewport.getWidth(),
					viewport.getHeight(),
					grid.getWidth(),
					grid.getHeight()
					);
			double A = SharedConstants.TILE_WIDTH / 2.0;
			double B = SharedConstants.TILE_HEIGHT / 2.0;
			double Xprime = localPoint.getX() / scale - cameraOffset.getX();
			double Yprime = localPoint.getY() / scale - cameraOffset.getY() + SharedConstants.TILE_HEIGHT;
			int tileX = (int) Math.floor(((Xprime / A) + (Yprime / B)) / 2);
			int tileY = (int) Math.floor(((Yprime / B) - (Xprime / A)) / 2);
			viewObserver.onViewAction(new ViewDTO(new ViewDTO.EntityTileClickCommand(tileX, tileY)));
		});

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

	private void updateCounters(Label plantCounter, Label sheepCounter, Label wolfCounter) {
		AtomicInteger plants = new AtomicInteger();
		AtomicInteger sheep = new AtomicInteger();
		AtomicInteger wolves = new AtomicInteger();

		modelDTO.getGrid().forEachTile(tile ->
				tile.getEntities().stream().filter(BasicEntity::isAlive).forEach(entity -> {
					if (entity instanceof PlantEntity) plants.incrementAndGet();
					else if (entity instanceof SheepEntity) sheep.incrementAndGet();
					else if (entity instanceof WolfEntity) wolves.incrementAndGet();
				})
				);

		plantCounter.setText("plant: " + plants.get());
		sheepCounter.setText("sheep: " + sheep.get());
		wolfCounter.setText("wolf: " + wolves.get());
	}

	public void setActionText(String text) {
		actionLabel.setText(text);
	}
}
