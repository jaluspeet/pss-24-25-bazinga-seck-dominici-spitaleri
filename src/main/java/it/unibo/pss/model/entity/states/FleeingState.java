package it.unibo.pss.model.entity.states;

import it.unibo.pss.model.entity.AnimalEntity;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.world.World;

public class FleeingState implements BasicEntity.EntityState {
	private final AnimalEntity predator;

	public FleeingState(AnimalEntity predator) {
		this.predator = predator;
	}

	@Override
	public void execute(BasicEntity entity) {
		AnimalEntity prey = (AnimalEntity) entity;
		int dx = prey.getX() - predator.getX();
		int dy = prey.getY() - predator.getY();
		int fleeStepX = (dx == 0 ? 0 : Integer.signum(dx) * Math.min(prey.getSpeed(), Math.abs(dx)));
		int fleeStepY = (dy == 0 ? 0 : Integer.signum(dy) * Math.min(prey.getSpeed(), Math.abs(dy)));
		int newX = prey.getX() + fleeStepX;
		int newY = prey.getY() + fleeStepY;
		World.Tile targetTile = prey.getGrid().getTile(newX, newY);
		if (targetTile != null && targetTile.getType() == World.Tile.TileType.LAND) {
			prey.moveTo(newX, newY);
		} else {
			prey.moveRandomly();
		}
	}

	@Override
	public String getName() {
		return "FLEEING";
	}
}
