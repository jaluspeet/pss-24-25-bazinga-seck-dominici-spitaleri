package it.unibo.pss.common;

import java.io.*;
import java.util.Properties;

public final class SharedConstants {

	private static final Properties PROPERTIES = new Properties();

	static {
		// Look for an external config file; if not found, load from the bundled resource.
		File externalConfig = new File("config.properties");
		try (InputStream input = externalConfig.exists()
				? new FileInputStream(externalConfig)
				: SharedConstants.class.getResourceAsStream("/config.properties")) {
			if (input != null) {
				PROPERTIES.load(input);
			} else {
				System.err.println("No configuration file found. Using defaults.");
			}
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	// General
	public static final String WINDOW_TITLE = "pss-javafx";
	public static final double TILE_WIDTH = 64;
	public static final double TILE_HEIGHT = 32;
	public static final double SPRITE_WIDTH = 64;
	public static final double SPRITE_HEIGHT = 64;

	// Window
	public static final int WINDOW_WIDTH = Integer.parseInt(PROPERTIES.getProperty("window.width", "1920"));
	public static final int WINDOW_HEIGHT = Integer.parseInt(PROPERTIES.getProperty("window.height", "1080"));

	// Simulation
	public static final int WORLD_WIDTH = Integer.parseInt(PROPERTIES.getProperty("world.width", "128"));
	public static final int WORLD_HEIGHT = Integer.parseInt(PROPERTIES.getProperty("world.height", "128"));
	public static final double WORLD_WATER_RATIO = Double.parseDouble(PROPERTIES.getProperty("world.water.ratio", "4"));
	public static final double WORLD_LAKE_RATIO = Double.parseDouble(PROPERTIES.getProperty("world.lake.ratio", "7"));
	public static final int WORLD_LAKE_COUNT = Integer.parseInt(PROPERTIES.getProperty("world.lake.count", "6"));
	public static final int ENTITY_UPDATE_INTERVAL = Integer.parseInt(PROPERTIES.getProperty("model.update.interval", "500"));

	// Sheep
	public static final int SHEEP_COUNT = Integer.parseInt(PROPERTIES.getProperty("sheep.count", "100"));
	public static final int SHEEP_MOVEMENT_SPEED = Integer.parseInt(PROPERTIES.getProperty("sheep.speed", "2"));
	public static final int SHEEP_SIGHT_RANGE = Integer.parseInt(PROPERTIES.getProperty("sheep.sight.range", "20"));
	public static final int SHEEP_ENERGY_DEFAULT = Integer.parseInt(PROPERTIES.getProperty("sheep.energy.default", "500"));
	public static final int SHEEP_ENERGY_HUNGRY = Integer.parseInt(PROPERTIES.getProperty("sheep.energy.hungry", "600"));
	public static final int SHEEP_ENERGY_RESTORE = Integer.parseInt(PROPERTIES.getProperty("sheep.energy.restore", "200"));
	public static final int SHEEP_ENERGY_BAZINGA = Integer.parseInt(PROPERTIES.getProperty("sheep.energy.bazinga", "500"));

	// Wolf
	public static final int WOLF_COUNT = Integer.parseInt(PROPERTIES.getProperty("wolf.count", "4"));
	public static final int WOLF_MOVEMENT_SPEED = Integer.parseInt(PROPERTIES.getProperty("wolf.speed", "1"));
	public static final int WOLF_SIGHT_RANGE = Integer.parseInt(PROPERTIES.getProperty("wolf.sight.range", "1000"));
	public static final int WOLF_ENERGY_DEFAULT = Integer.parseInt(PROPERTIES.getProperty("wolf.energy.default", "1000"));
	public static final int WOLF_ENERGY_HUNGRY = Integer.parseInt(PROPERTIES.getProperty("wolf.energy.hungry", "200"));
	public static final int WOLF_ENERGY_RESTORE = Integer.parseInt(PROPERTIES.getProperty("wolf.energy.restore", "250"));
	public static final int WOLF_ENERGY_BAZINGA = Integer.parseInt(PROPERTIES.getProperty("wolf.energy.bazinga", "800"));

	// Plant
	public static final int PLANT_COUNT = Integer.parseInt(PROPERTIES.getProperty("plant.count", "4000"));
	public static final int PLANT_RESURRECTION_TIME = Integer.parseInt(PROPERTIES.getProperty("plant.resurrection.time", "1000"));

	// Camera
	public static final double CAMERA_SENSITIVITY = Double.parseDouble(PROPERTIES.getProperty("camera.sensitivity", "5"));
	public static final double CAMERA_MIN_SCALE = Double.parseDouble(PROPERTIES.getProperty("camera.min.scale", "1"));
	public static final double CAMERA_MAX_SCALE = Double.parseDouble(PROPERTIES.getProperty("camera.max.scale", "10"));
	public static final int CAMERA_FRAMERATE = Integer.parseInt(PROPERTIES.getProperty("camera.framerate", "60"));

	private SharedConstants() { }
}

