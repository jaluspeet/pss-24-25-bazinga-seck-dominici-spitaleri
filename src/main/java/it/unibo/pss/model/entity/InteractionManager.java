package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;

import java.util.*;

public class InteractionManager {
	private final World grid;
	private final Map<Integer, BasicEntity> entityMap;

	public InteractionManager(World grid, Map<Integer, BasicEntity> entityMap) {
		this.grid = grid;
		this.entityMap = entityMap;
	}

	public boolean validateMove(BasicEntity entity, BasicEntity.Direction dir) {
		int newX = entity.getX(), newY = entity.getY();
		switch (dir) {
			case UP: newY--; break;
			case DOWN: newY++; break;
			case LEFT: newX--; break;
			case RIGHT: newX++; break;
		}

		World.Tile tile = grid.getTile(newX, newY);
		if (tile == null || !tile.getType().equals(World.Tile.TileType.LAND)) return false;

		boolean isBlocked = tile.getEntities().stream().anyMatch(e -> !(e instanceof PlantEntity) && e.isAlive());
		return !isBlocked && entity.getEnergy() > 0;
	}

	public void processMove(BasicEntity entity, BasicEntity.Direction dir) {
		int newX = entity.getX(), newY = entity.getY();
		switch (dir) {
			case UP: newY--; break;
			case DOWN: newY++; break;
			case LEFT: newX--; break;
			case RIGHT: newX++; break;
		}

		World.Tile currentTile = grid.getTile(entity.getX(), entity.getY());
		World.Tile targetTile = grid.getTile(newX, newY);

		if (targetTile != null) {
			currentTile.removeEntity(entity);
			entity.setPosition(newX, newY);
			targetTile.addEntity(entity);
			entity.subtractEnergy(1);
		}
	}

	public boolean validateInteract(BasicEntity entity, int targetId) {
		BasicEntity target = entityMap.get(targetId);
		if (target == null || !target.isAlive()) return false;

		List<World.Tile> adjacent = getAdjacentTiles(entity.getX(), entity.getY());
		return adjacent.stream().anyMatch(t -> t.getEntities().contains(target));
	}

	public void processInteract(BasicEntity entity, int targetId) {
		BasicEntity target = entityMap.get(targetId);
		if (target == null || !target.isAlive()) return;

		// Predation
		if (entity.getPreyType() != null && entity.getPreyType().isInstance(target)) {
			killEntity(target);
			entity.addEnergy(entity.getEnergyRestore());
			return;
		}

		// Reproduction
		if (entity.getClass().equals(target.getClass()) &&
				entity.getEnergy() >= entity.getEnergyBazinga() &&
				target.getEnergy() >= target.getEnergyBazinga() &&
				!entity.hasBazinged && !target.hasBazinged) {
			spawnOffspring(entity, target);
				}
	}

	private void killEntity(BasicEntity entity) {
		World.Tile tile = grid.getTile(entity.getX(), entity.getY());
		tile.removeEntity(entity);
		entityMap.remove(entity.getId());
		entity.subtractEnergy(entity.getEnergy());
	}

	private void spawnOffspring(BasicEntity parent1, BasicEntity parent2) {
		World.Tile freeTile = findFreeAdjacentTile(parent1.getX(), parent1.getY());
		if (freeTile != null) {
			BasicEntity offspring = parent1.spawnOffspring();
			addEntity(offspring);
			parent1.subtractEnergy(parent1.getEnergyBazinga());
			parent2.subtractEnergy(parent2.getEnergyBazinga());
			parent1.setBazinged();
			parent2.setBazinged();
		}
	}

	private World.Tile findFreeAdjacentTile(int x, int y) {
		for (World.Tile t : getAdjacentTiles(x, y)) {
			if (t.getEntities().isEmpty()) return t;
		}
		return null;
	}

	private List<World.Tile> getAdjacentTiles(int x, int y) {
		List<World.Tile> tiles = new ArrayList<>(Arrays.asList(
					grid.getTile(x, y - 1), grid.getTile(x, y + 1),
					grid.getTile(x - 1, y), grid.getTile(x + 1, y)
					));
		tiles.removeIf(Objects::isNull);
		return tiles;
	}

	private void addEntity(BasicEntity entity) {
		World.Tile tile = grid.getTile(entity.getX(), entity.getY());
		tile.addEntity(entity);
		entityMap.put(entity.getId(), entity);
	}
}
