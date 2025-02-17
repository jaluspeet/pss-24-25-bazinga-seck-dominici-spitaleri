package it.unibo.pss.model;

import it.unibo.pss.controller.model.ModelObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Model {

	private final WorldGrid grid;
	private final List<ModelObserver> observers = new ArrayList<>();
	private final Timer timer = new Timer(true); // Daemon timer

	public Model(int width, int height) {
		this.grid = WorldGridGenerator.generateGrid(width, height);

		// Schedule model updates every 500ms
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updateSimulation();
			}
		}, 0, 500);
	}

	public WorldGrid getGrid() {
		return grid;
	}

	/** Adds an observer to listen for model updates. */
	public void addObserver(ModelObserver observer) {
		observers.add(observer);
	}

	/** Removes an observer. */
	public void removeObserver(ModelObserver observer) {
		observers.remove(observer);
	}

	/** Notifies observers when the model updates. */
	private void notifyObservers() {
		for (ModelObserver observer : observers) {
			observer.onModelUpdated();
		}
	}

	/** Updates the simulation logic and notifies observers. */
	private void updateSimulation() {
		// TODO: Implement simulation update logic
		notifyObservers();
	}
}
