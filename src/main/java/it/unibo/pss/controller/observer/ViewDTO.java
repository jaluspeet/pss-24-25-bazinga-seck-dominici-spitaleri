package it.unibo.pss.controller.observer;

/**
 * Data Transfer Object for the view.
 * Contains the command that the view wants to send to the controller.
 */
public class ViewDTO {
	private final Command command;

	public ViewDTO(Command command) { this.command = command; }
	public Command getCommand() { return command; }

	/**
	 * Represents a view's command for the model (speed, tile click, ... ).
	 */
	public static abstract class Command {
		public enum Type { SPEED, TILE_CLICK }
		private final Type type;
		protected Command(Type type) { this.type = type; }
		public Type getType() { return type; }
	}

	/**
	 * Command to change the speed of the simulation.
	 */
	public static class SpeedCommand extends Command {
		private final int delta;
		public SpeedCommand(int delta) { super(Type.SPEED); this.delta = delta; }
		public int getDelta() { return delta; }
	}

	/**
	 * Command to notify the controller that the user clicked on a tile.
	 */
	public static class EntityTileClickCommand extends Command {
		private final int tileX;
		private final int tileY;
		public EntityTileClickCommand(int tileX, int tileY) {
			super(Type.TILE_CLICK);
			this.tileX = tileX;
			this.tileY = tileY;
		}

		public int getTileX() { return tileX; }
		public int getTileY() { return tileY; }
	}
}
