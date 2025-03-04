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

	public void generateInitialEntities() {
		EntityFactory factory = new EntityFactory(this.world);
		factory.generateInitialEntities(this);
	}

	public void addEntity(BasicEntity entity) {
		entities.add(entity);
		entityMap.put(entity.getId(), entity);
		world.getTile(entity.getX(), entity.getY()).addEntity(entity);
	}

	public void updateCycle() {
		// 1. subtract energy from non-plant entities
		reduceEnergy();

		// 2. remove dead
		removeDeadEntities();

		// 3. gather requests from living entities
		List<ActionHandler.RequestWrapper> requests = actionHandler.collectRequests(new ArrayList<>(entities));

		// 4. validate requests
		List<ActionHandler.Action> approvedActions = actionHandler.validateRequests(requests);

		// 5. process the approved actions
		actionHandler.processActions(approvedActions);

		// 6. reset bazinged flag on all entities
		resetBazingedFlags();

		// 7. handle plant resurrection
		resurrectPlants();
	}

	// helpers

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

	private void removeEntity(BasicEntity entity) {
		var tile = world.getTile(entity.getX(), entity.getY());
		if (tile != null) {
			tile.removeEntity(entity);
		}
		entityMap.remove(entity.getId());
	}

	public void killEntity(BasicEntity toKill) {
		removeEntity(toKill);
		toKill.subtractEnergy(toKill.getEnergy());
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
				p.addEnergy(1); // resurrect
				var tile = world.getTile(p.getX(), p.getY());
				tile.addEntity(p);
				entities.add(p);
				entityMap.put(p.getId(), p);
				deadPlants.remove(p);
			}
		}
	}

	// getters

	public BasicEntity getEntityById(int id) {
		return entityMap.get(id);
	}

	public List<BasicEntity> getEntities() {
		return this.entities;
	}
}
