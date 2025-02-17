package it.unibo.pss.model;

import java.util.ArrayList;
import java.util.List;
import it.unibo.pss.controller.observer.ModelObserver;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.entity.EntityGenerator;
import it.unibo.pss.model.world.World;
import it.unibo.pss.model.world.WorldGenerator;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import it.unibo.pss.common.SharedConstants;

public class Model {

	private final World grid;
	private final List<ModelObserver> observers = new ArrayList<>();
	private AnimationTimer timer;
	private long lastUpdate = 0;
	private static final long UPDATE_INTERVAL = 500_000_000; // 500ms in nanoseconds

	public Model(int width, int height) {
		this.grid = WorldGenerator.generateGrid(width, height);
		EntityGenerator.generateEntities(grid, SharedConstants.ENTITY_COUNT);
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (now - lastUpdate >= UPDATE_INTERVAL) {
					updateSimulation();
					lastUpdate = now;
				}
			}
		};
		timer.start();
	}

	public World getGrid() {
		return grid;
	}

	/** Adds an observer. */
	public void addObserver(ModelObserver observer) {
		observers.add(observer);
	}

	/** Removes an observer. */
	public void removeObserver(ModelObserver observer) {
		observers.remove(observer);
	}

	/** Notifies observers when the model updates. */
	private void notifyObservers() {
		Platform.runLater(() -> observers.forEach(ModelObserver::onModelUpdated));
	}

	private void updateSimulation() {
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				for (BasicEntity entity : new java.util.ArrayList<>(grid.getTile(x, y).getEntities())) {
					entity.update();
				}
			}
		}
		notifyObservers();
	}
}
