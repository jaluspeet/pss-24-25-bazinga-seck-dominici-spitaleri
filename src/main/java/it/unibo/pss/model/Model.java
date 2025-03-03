package it.unibo.pss.model;

import it.unibo.pss.model.entity.EntityManager;
import it.unibo.pss.model.world.World;
import it.unibo.pss.model.world.WorldManager;
import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.controller.observer.ModelObserver;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;

public class Model {
	private final World grid;
	private final EntityManager entityGenerator;
	private final List<ModelObserver> observers = new ArrayList<>();
	private long lastUpdate = 0;
	private int updateInterval;

	public Model(int width, int height) {
		this.grid = WorldManager.generateGrid(width, height);
		this.entityGenerator = new EntityManager(grid);
		entityGenerator.generateEntities();
		this.updateInterval = SharedConstants.ENTITY_UPDATE_INTERVAL; // initialize update interval
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
				if (now - lastUpdate >= updateInterval * 1_000_000) {
					updateSimulation();
					lastUpdate = now;
				}
			}
		}.start();
	}

	private void updateSimulation() {
		entityGenerator.updateCycle();
		notifyObservers();
	}

	public void setUpdateInterval(int interval) {
		this.updateInterval = interval;
	}

	public int getUpdateInterval() {
		return updateInterval;
	}

	public World getGrid() {
		return grid;
	}
}
