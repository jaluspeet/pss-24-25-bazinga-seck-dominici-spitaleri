package it.unibo.pss.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class SharedConstants {

	private static final Properties PROPERTIES = new Properties();

	static {
		try (InputStream input = SharedConstants.class.getResourceAsStream("/config.properties")) {
			if (input == null) {
				throw new IllegalStateException("Configuration file not found: /config.properties");
			}
			PROPERTIES.load(input);
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	// static constants
	public static final String WINDOW_TITLE = "pss-javafx";
	public static final double TILE_WIDTH = 64;
	public static final double TILE_HEIGHT = 32;
	public static final double SPRITE_WIDTH = 64;
	public static final double SPRITE_HEIGHT = 64;

	// use config.properties file to override

	// window
	public static final int WINDOW_WIDTH = Integer.parseInt(PROPERTIES.getProperty("window.width", "2560"));
	public static final int WINDOW_HEIGHT = Integer.parseInt(PROPERTIES.getProperty("window.height", "1600"));
	
	// simulation
	public static final int WORLD_WIDTH = Integer.parseInt(PROPERTIES.getProperty("world.width", "128"));
	public static final int WORLD_HEIGHT = Integer.parseInt(PROPERTIES.getProperty("world.height", "128"));
	public static final double WORLD_WATER_RATIO = Double.parseDouble(PROPERTIES.getProperty("world.water.ratio", "0.4"));
	public static final double WORLD_LAKE_RATIO = Double.parseDouble(PROPERTIES.getProperty("world.lake.ratio", "0.7"));
	public static final int WORLD_LAKE_COUNT = Integer.parseInt(PROPERTIES.getProperty("world.lake.count", "6"));
	public static final int ENTITY_UPDATE_INTERVAL = Integer.parseInt(PROPERTIES.getProperty("model.update.interval", "250"));
	
	// sheep
	public static final int SHEEP_COUNT = Integer.parseInt(PROPERTIES.getProperty("sheep.count", "500"));
	public static final int SHEEP_MOVEMENT_SPEED = Integer.parseInt(PROPERTIES.getProperty("sheep.speed", "2"));
	public static final int SHEEP_SIGHT_RANGE = Integer.parseInt(PROPERTIES.getProperty("sheep.sight.range", "20"));
	public static final int SHEEP_ENERGY_DEFAULT = Integer.parseInt(PROPERTIES.getProperty("sheep.energy.default", "500"));
	public static final int SHEEP_ENERGY_HUNGRY = Integer.parseInt(PROPERTIES.getProperty("sheep.energy.hungry", "600"));
	public static final int SHEEP_ENERGY_RESTORE = Integer.parseInt(PROPERTIES.getProperty("sheep.energy.restore", "200"));
	public static final int SHEEP_ENERGY_BAZINGA = Integer.parseInt(PROPERTIES.getProperty("sheep.energy.bazinga", "500"));

	// wolf
	public static final int WOLF_COUNT = Integer.parseInt(PROPERTIES.getProperty("wolf.count", "4"));
	public static final int WOLF_MOVEMENT_SPEED = Integer.parseInt(PROPERTIES.getProperty("wolf.speed", "1"));
	public static final int WOLF_SIGHT_RANGE = Integer.parseInt(PROPERTIES.getProperty("wolf.sight.range", "1000"));
	public static final int WOLF_ENERGY_DEFAULT = Integer.parseInt(PROPERTIES.getProperty("wolf.energy.default", "1000"));
	public static final int WOLF_ENERGY_HUNGRY = Integer.parseInt(PROPERTIES.getProperty("wolf.energy.hungry", "200"));
	public static final int WOLF_ENERGY_RESTORE = Integer.parseInt(PROPERTIES.getProperty("wolf.energy.restore", "250"));
	public static final int WOLF_ENERGY_BAZINGA = Integer.parseInt(PROPERTIES.getProperty("wolf.energy.bazinga", "800"));

	// plant
	public static final int PLANT_COUNT = Integer.parseInt(PROPERTIES.getProperty("plant.count", "4000"));
	public static final int PLANT_RESURRECTION_TIME = Integer.parseInt(PROPERTIES.getProperty("plant.resurrection.time", "1000"));

	// camera
	public static final double CAMERA_SENSITIVITY = Double.parseDouble(PROPERTIES.getProperty("camera.sensitivity", "0.5"));
	public static final double CAMERA_FRICTION = Double.parseDouble(PROPERTIES.getProperty("camera.friction", "0.9"));
	public static final double CAMERA_INERTIA_THRESHOLD = Double.parseDouble(PROPERTIES.getProperty("camera.inertia.threshold", "0.01"));
	public static final double CAMERA_ZOOM_BASE = Double.parseDouble(PROPERTIES.getProperty("camera.zoom.base", "1.01"));
	public static final double CAMERA_ZOOM_SMOOTHING = Double.parseDouble(PROPERTIES.getProperty("camera.zoom.smoothing", "0.1"));
	public static final double CAMERA_MIN_SCALE = Double.parseDouble(PROPERTIES.getProperty("camera.min.scale", "0.1"));
	public static final double CAMERA_MAX_SCALE = Double.parseDouble(PROPERTIES.getProperty("camera.max.scale", "10.0"));
	public static final int CAMERA_FRAMERATE = Integer.parseInt(PROPERTIES.getProperty("camera.framerate", "60"));

	private SharedConstants() {}
}
