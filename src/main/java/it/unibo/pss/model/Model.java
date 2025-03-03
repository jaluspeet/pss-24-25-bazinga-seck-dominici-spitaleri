package it.unibo.pss.model;

import it.unibo.pss.model.entity.EntityManager;
import it.unibo.pss.model.entity.BasicEntity;
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

	public String getTileActions(int tileX, int tileY) {
		if(tileX < 0 || tileY < 0 || tileX >= grid.getWidth() || tileY >= grid.getHeight()){
			return "Invalid tile";
		}
		World.Tile tile = grid.getTile(tileX, tileY);
		StringBuilder sb = new StringBuilder();
		for (BasicEntity entity : tile.getEntities()) {
			if (entity instanceof it.unibo.pss.model.entity.PlantEntity) continue;
			String entityName = getEntityName(entity);
			int energy = entity.getEnergy();
			BasicEntity.Request req = entity.getNextRequest();
			String action;
			if (req == null) {
				action = "idle";
			} else if (req.type == BasicEntity.ActionType.MOVE) {
				action = "moving " + req.direction;
			} else if (req.type == BasicEntity.ActionType.INTERACT) {
				BasicEntity target = findEntityById(req.targetId);
				String targetName = (target != null) ? getEntityName(target) : "unknown";
				if (entity instanceof it.unibo.pss.model.entity.SheepEntity) {
					action = entity.getEnergy() >= SharedConstants.SHEEP_ENERGY_BAZINGA ? "bazinga with " + targetName : "eating " + targetName;
				} else if (entity instanceof it.unibo.pss.model.entity.WolfEntity) {
					action = "chasing " + targetName;
				} else {
					action = "interacting with " + targetName;
				}
			} else {
				action = "performing action";
			}
			sb.append(entityName)
				.append("(").append(energy).append("): ")
				.append(action).append("\n");
		}
		String result = sb.toString().trim();
		return result.isEmpty() ? "No actions" : result;
	}

	private BasicEntity findEntityById(int id) {
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				for (BasicEntity e : grid.getTile(x, y).getEntities()) {
					if (e.getId() == id) return e;
				}
			}
		}
		return null;
	}

	private String getEntityName(BasicEntity e) {
		if (e instanceof it.unibo.pss.model.entity.SheepEntity)
			return "sheep" + e.getId();
		else if (e instanceof it.unibo.pss.model.entity.WolfEntity)
			return "wolf" + e.getId();
		else if (e instanceof it.unibo.pss.model.entity.PlantEntity)
			return "plant" + e.getId();
		else
			return "entity" + e.getId();
	}

	public World getGrid() {
		return grid;
	}
}
