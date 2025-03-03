package it.unibo.pss.controller.observer;

public class ViewDTO {
	private final Command command;
	
	public ViewDTO(Command command) {
		this.command = command;
	}
	
	public Command getCommand() {
		return command;
	}
	
	public static abstract class Command {
		public enum Type {
			SPEED,
		}
		
		private final Type type;
		
		protected Command(Type type) {
			this.type = type;
		}
		
		public Type getType() {
			return type;
		}
	}
	
	public static class SpeedCommand extends Command {
		private final int delta;
		
		public SpeedCommand(int delta) {
			super(Type.SPEED);
			this.delta = delta;
		}
		
		public int getDelta() {
			return delta;
		}
	}
}
