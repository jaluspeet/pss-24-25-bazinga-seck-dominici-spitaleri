package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import it.unibo.pss.common.SharedConstants;
import java.util.*;

public class EntityManager {
	private final List<BasicEntity> entities = new ArrayList<>();
	private final Map<Integer, BasicEntity> entityMap = new HashMap<>();
	private final List<PlantEntity> deadPlants = new ArrayList<>();
	private final World world;
	private final ActionHandler actionHandler;

	public EntityManager(World world) {
		this.world = world;
		this.actionHandler = new ActionHandler(this.world, this);
	}

	// ====== ENTITY MANAGEMENT ======

	public void generateInitialEntities() {
		EntityFactory factory = new EntityFactory(this.world);
		factory.generateInitialEntities(this);
	}

	public void addEntity(BasicEntity entity) {
		entities.add(entity);
		entityMap.put(entity.getId(), entity);
		world.getTile(entity.getX(), entity.getY()).addEntity(entity);
	}

	public void killEntity(BasicEntity toKill) {
		removeEntity(toKill);
		toKill.subtractEnergy(toKill.getEnergy());
	}

	private void removeEntity(BasicEntity entity) {
		var tile = world.getTile(entity.getX(), entity.getY());
		if (tile != null) {
			tile.removeEntity(entity);
		}
		entityMap.remove(entity.getId());
	}

	// ====== SIMULATION CYCLE ======

	public void updateCycle() {
		reduceEnergy();
		removeDeadEntities();
		List<ActionHandler.RequestWrapper> requests = actionHandler.collectRequests(new ArrayList<>(entities));
		List<ActionHandler.Action> approvedActions = actionHandler.validateRequests(requests);
		actionHandler.processActions(approvedActions);
		resetBazingedFlags();
		resurrectPlants();
	}

	// ====== HELPERS ======

	private void reduceEnergy() {
		for (BasicEntity entity : new ArrayList<>(entities)) {
			if (!(entity instanceof PlantEntity)) {
				entity.subtractEnergy(1);
			}
		}
	}

	private void removeDeadEntities() {
		for (BasicEntity entity : new ArrayList<>(entities)) {
			if (!entity.isAlive()) {
				removeEntity(entity);
				if (entity instanceof PlantEntity plant) {
					plant.setResurrectionDelay(SharedConstants.PLANT_RESURRECTION_TIME);
					deadPlants.add(plant);
				}
				entities.remove(entity);
			}
		}
	}

	private void resetBazingedFlags() {
		for (BasicEntity e : entities) {
			e.resetBazinged();
		}
	}

	private void resurrectPlants() {
		for (PlantEntity p : new ArrayList<>(deadPlants)) {
			p.decrementResurrectionDelay();
			if (p.getResurrectionDelay() <= 0) {
				p.addEnergy(1);
				var tile = world.getTile(p.getX(), p.getY());
				tile.addEntity(p);
				entities.add(p);
				entityMap.put(p.getId(), p);
				deadPlants.remove(p);
			}
		}
	}

	// ====== GETTERS ======

	public BasicEntity getEntityById(int id) {
		return entityMap.get(id);
	}

	public List<BasicEntity> getEntities() {
		return this.entities;
	}
}
