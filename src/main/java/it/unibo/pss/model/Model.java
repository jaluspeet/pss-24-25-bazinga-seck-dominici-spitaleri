package it.unibo.pss.model;

import java.util.ArrayList;
import java.util.List;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.controller.observer.ModelObserver;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.entity.EntityGenerator;
import it.unibo.pss.model.world.World;
import it.unibo.pss.model.world.WorldGenerator;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;

public class Model {
	private final World grid;
	private final List<ModelObserver> observers = new ArrayList<>();
	private long lastUpdate = 0;

	public Model(int width, int height) {
		this.grid = WorldGenerator.generateGrid(width, height);
		EntityGenerator.generateEntities(grid);
		startSimulation();
	}

	public void addObserver(ModelObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(ModelObserver observer) {
		observers.remove(observer);
	}

	private void notifyObservers() {
		Platform.runLater(() -> observers.forEach(ModelObserver::onModelUpdated));
	}

	private void startSimulation() {
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (now - lastUpdate >= SharedConstants.ENTITY_UPDATE_INTERVAL * 1_000_000) {
					updateSimulation();
					lastUpdate = now;
				}
			}
		}.start();
	}

	private void updateSimulation() {
		grid.forEachTile(tile -> {
			List<BasicEntity> entitiesCopy = new ArrayList<>(tile.getEntities()); // list changes over time so a copy is needed
			entitiesCopy.stream().filter(BasicEntity::isAlive).forEach(BasicEntity::update);
		});
		notifyObservers();
	}

	public World getGrid() {
		return grid;
	}
}
