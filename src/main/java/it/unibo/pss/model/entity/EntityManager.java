package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import it.unibo.pss.common.SharedConstants;
import java.util.*;

/**
 * EntityManager is responsible for managing all entities in the world.
 * keeps track of all entities, their positions, and their states.
 */
public class EntityManager {
	private final List<BasicEntity> entities = new ArrayList<>();
	private final Map<Integer, BasicEntity> entityMap = new HashMap<>();
	private final List<PlantEntity> deadPlants = new ArrayList<>();
	private final World world;
	private final ActionHandler actionHandler;

	/**
	 * Constructor for EntityManager.
	 *
	 * @param world the world in which the entities are placed.
	 */
	public EntityManager(World world) {
		this.world = world;
		this.actionHandler = new ActionHandler(this.world, this);
	}

	/**
	 * Generates the initial entities in the world.
	 */
	public void generateInitialEntities() {
		EntityFactory factory = new EntityFactory(this.world);
		factory.generateInitialEntities(this);
	}

	/**
	 * Adds an entity to the world.
	 *
	 * @param entity the entity to be added.
	 */
	public void addEntity(BasicEntity entity) {
		entities.add(entity);
		entityMap.put(entity.getId(), entity);
		world.getTile(entity.getX(), entity.getY()).addEntity(entity);
	}

	/**
	 * Removes an entity from the world.
	 *
	 * @param entity the entity to be removed.
	 */
	public void killEntity(BasicEntity toKill) {
		removeEntity(toKill);
		toKill.subtractEnergy(toKill.getEnergy());
	}

	/**
	 * Removes an entity from the world.
	 *
	 * @param entity the entity to be removed.
	 */
	private void removeEntity(BasicEntity entity) {
		var tile = world.getTile(entity.getX(), entity.getY());
		if (tile != null) { tile.removeEntity(entity); }
		entityMap.remove(entity.getId());
	}


	/**
	 * Updates the state of the entities in the world.
	 */
	public void updateCycle() {
		reduceEnergy();
		removeDeadEntities();
		List<ActionHandler.RequestWrapper> requests = actionHandler.collectRequests(new ArrayList<>(entities));
		List<ActionHandler.Action> approvedActions = actionHandler.validateRequests(requests);
		actionHandler.processActions(approvedActions);
		resetBazingedFlags();
		resurrectPlants();
	}


	/**
	 * Reduces the energy of all entities in the world.
	 */
	private void reduceEnergy() {
		for (BasicEntity entity : new ArrayList<>(entities)) {
			if (!(entity instanceof PlantEntity)) { entity.subtractEnergy(1); }
		}
	}

	/**
	 * Removes all dead entities from the world.
	 */
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

	/**
	 * Resets the bazinged flags of all entities in the world.
	 */
	private void resetBazingedFlags() {
		for (BasicEntity e : entities) { e.resetBazinged(); }
	}

	/**
	 * Resurrects all dead plants in the world.
	 */
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


	/**
	 * Returns the entity at the specified position.
	 *
	 * @param x the x-coordinate of the position.
	 * @param y the y-coordinate of the position.
	 * @return the entity at the specified position.
	 */
	public BasicEntity getEntityById(int id) {
		return entityMap.get(id);
	}

	/**
	 * Returns the entity at the specified position.
	 *
	 * @param x the x-coordinate of the position.
	 * @param y the y-coordinate of the position.
	 * @return the entity at the specified position.
	 */
	public List<BasicEntity> getEntities() {
		return this.entities;
	}
}
