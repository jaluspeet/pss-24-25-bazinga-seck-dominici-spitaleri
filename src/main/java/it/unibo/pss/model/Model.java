package it.unibo.pss.model;

import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.entity.EntityManager;
import it.unibo.pss.model.world.World;
import it.unibo.pss.model.world.WorldManager;
import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.controller.observer.ModelObserver;
import it.unibo.pss.controller.observer.ModelDTO;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
	private final World grid;
	private final EntityManager entityManager;
	private final List<ModelObserver> observers = new ArrayList<>();
	private long lastUpdate = 0;
	private int updateInterval;

	public Model(int width, int height) {
		this.grid = WorldManager.generateGrid(width, height);
		this.entityManager = new EntityManager(grid);
		this.entityManager.generateInitialEntities();
		this.updateInterval = SharedConstants.ENTITY_UPDATE_INTERVAL;
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
		entityManager.updateCycle();
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

	public ModelDTO getLatestModelDTO() {
		Map<Integer,String> entityActions = new HashMap<>();
		for (BasicEntity e : entityManager.getEntities()) {
			if (e.isAlive()) {
				entityActions.put(e.getId(), getActionString(e));
			}
		}
		return new ModelDTO(grid, entityActions);
	}

	private BasicEntity findEntityById(int id) {
		for (BasicEntity e : entityManager.getEntities()) {
			if (e.getId() == id) {
				return e;
			}
		}
		return null;
	}

	private String getActionString(BasicEntity e) {
		BasicEntity.Request req = e.getNextRequest();
		if (req == null) {
			return "IDLE";
		}
		return req.toActionString(e, this::findEntityById);
	}

	public String getTileActions(int tileX, int tileY) {
		if (tileX < 0 || tileY < 0 || tileX >= grid.getWidth() || tileY >= grid.getHeight()) {
			return "Invalid tile";
		}
		World.Tile tile = grid.getTile(tileX, tileY);
		if (tile.getEntities().isEmpty()) {
			return "No actions";
		}

		StringBuilder sb = new StringBuilder();
		for (BasicEntity entity : tile.getEntities()) {
			if (!entity.isAlive()) {
				continue;
			}
			String entityName = getEntityName(entity);
			int energy = entity.getEnergy();
			String actionKey = getActionString(entity);
			sb.append(entityName)
				.append("(").append(energy).append("): ")
				.append(actionKey)
				.append("\n");
		}
		String result = sb.toString().trim();
		return result.isEmpty() ? "no actions" : result;
	}

	private String getEntityName(BasicEntity e) {
		if (e instanceof it.unibo.pss.model.entity.SheepEntity) {
			return "sheep" + e.getId();
		} else if (e instanceof it.unibo.pss.model.entity.WolfEntity) {
			return "wolf" + e.getId();
		} else if (e instanceof it.unibo.pss.model.entity.PlantEntity) {
			return "plant" + e.getId();
		} else {
			return "entity" + e.getId();
		}
	}
}
