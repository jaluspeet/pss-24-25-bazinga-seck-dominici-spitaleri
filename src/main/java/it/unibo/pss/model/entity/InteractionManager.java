package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import it.unibo.pss.common.SharedConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InteractionManager {
	private final World grid;
	private final Map<Integer, BasicEntity> entityMap;

	public InteractionManager(World grid, Map<Integer, BasicEntity> entityMap) {
		this.grid = grid;
		this.entityMap = entityMap;
	}

	public boolean validateMove(BasicEntity entity, BasicEntity.Direction dir) {
		int newX = entity.getX(), newY = entity.getY();
		switch(dir) {
			case UP: newY--; break;
			case DOWN: newY++; break;
			case LEFT: newX--; break;
			case RIGHT: newX++; break;
		}
		World.Tile tile = grid.getTile(newX, newY);
		if (tile == null || !tile.getType().equals(World.Tile.TileType.LAND))
			return false;

		boolean isBlocked = tile.getEntities().stream()
			.anyMatch(e -> !(e instanceof PlantEntity) && e.isAlive());

		return !isBlocked && entity.getEnergy() > 0;
	}

	public void processMove(BasicEntity entity, BasicEntity.Direction dir) {
		int newX = entity.getX(), newY = entity.getY();
		switch(dir) {
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
		List<World.Tile> adjacent = getAdjacentTiles(entity.getX(), entity.getY());
		for (World.Tile t : adjacent)
			for (BasicEntity other : t.getEntities())
				if (other.getId() == targetId)
					return true;
		return false;
	}

	public void processInteract(BasicEntity entity, int targetId) {
		BasicEntity target = entityMap.get(targetId);
		if (target == null)
			return;

		// prey
		if (entity.getPreyType() != null && target.getClass().equals(entity.getPreyType())) {
			if (!target.isAlive())
				return;
			killEntity(target);
		}

		// bazinga
		if (entity.getClass().equals(target.getClass()) &&
				target.getEnergy() >= SharedConstants.SHEEP_ENERGY_BAZINGA &&
				!target.hasBazinged) {
			World.Tile freeTile = findFreeAdjacentTile(entity.getX(), entity.getY());
			if (freeTile != null) {
				BasicEntity offspring = entity.spawnOffspring();
				addEntity(offspring);
				entity.subtractEnergy(SharedConstants.SHEEP_ENERGY_BAZINGA);
				target.subtractEnergy(SharedConstants.SHEEP_ENERGY_BAZINGA);
				entity.setBazinged();
				target.setBazinged();
			}
				}
	}

	private List<World.Tile> getAdjacentTiles(int x, int y) {
		List<World.Tile> tiles = new ArrayList<>();
		tiles.add(grid.getTile(x, y - 1));
		tiles.add(grid.getTile(x, y + 1));
		tiles.add(grid.getTile(x - 1, y));
		tiles.add(grid.getTile(x + 1, y));
		tiles.removeIf(t -> t == null);
		return tiles;
	}

	private World.Tile findFreeAdjacentTile(int x, int y) {
		for (World.Tile t : getAdjacentTiles(x, y))
			if (t.getEntities().isEmpty())
				return t;
		return null;
	}

	private void killEntity(BasicEntity entity) {
		World.Tile tile = grid.getTile(entity.getX(), entity.getY());
		if (entity instanceof PlantEntity) {
			((PlantEntity) entity).kill();
		} else {
			tile.removeEntity(entity);
			entity.energy = 0;
		}
	}

	private void addEntity(BasicEntity entity) {
		World.Tile tile = grid.getTile(entity.getX(), entity.getY());
		tile.addEntity(entity);
	}
}
