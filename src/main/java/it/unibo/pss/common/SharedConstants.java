package it.unibo.pss.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Loads configuration settings from a properties file. */
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

	public static final String WINDOW_TITLE = PROPERTIES.getProperty("window.title", "pss-javafx");
	public static final int WINDOW_WIDTH = Integer.parseInt(PROPERTIES.getProperty("window.width", "800"));
	public static final int WINDOW_HEIGHT = Integer.parseInt(PROPERTIES.getProperty("window.height", "600"));
	
	public static final int WORLD_WIDTH = Integer.parseInt(PROPERTIES.getProperty("world.width", "32"));
	public static final int WORLD_HEIGHT = Integer.parseInt(PROPERTIES.getProperty("world.height", "32"));
	public static final double WORLD_WATER_RATIO = Double.parseDouble(PROPERTIES.getProperty("world.water.ratio", "0.4"));
	public static final double WORLD_LAKE_RATIO = Double.parseDouble(PROPERTIES.getProperty("world.lake.ratio", "0.7"));
	public static final int WORLD_LAKE_COUNT = Integer.parseInt(PROPERTIES.getProperty("world.lake.count", "6"));
	
	public static final int ENTITY_COUNT = Integer.parseInt(PROPERTIES.getProperty("entity.count", "20"));

	public static final double CAMERA_SENSITIVITY = Double.parseDouble(PROPERTIES.getProperty("camera.sensitivity", "0.5"));
	public static final double CAMERA_FRICTION = Double.parseDouble(PROPERTIES.getProperty("camera.friction", "0.9"));
	public static final double CAMERA_INERTIA_THRESHOLD = Double.parseDouble(PROPERTIES.getProperty("camera.inertia.threshold", "0.01"));
	public static final double CAMERA_ZOOM_BASE = Double.parseDouble(PROPERTIES.getProperty("camera.zoom.base", "1.1"));
	public static final double CAMERA_ZOOM_SMOOTHING = Double.parseDouble(PROPERTIES.getProperty("camera.zoom.smoothing", "0.1"));
	public static final double CAMERA_MIN_SCALE = Double.parseDouble(PROPERTIES.getProperty("camera.min.scale", "0.1"));
	public static final double CAMERA_MAX_SCALE = Double.parseDouble(PROPERTIES.getProperty("camera.max.scale", "10.0"));
	public static final int CAMERA_FRAMERATE = Integer.parseInt(PROPERTIES.getProperty("camera.framerate", "60"));
	
	public static final double TILE_WIDTH = Double.parseDouble(PROPERTIES.getProperty("tile.width", "64"));
	public static final double TILE_HEIGHT = Double.parseDouble(PROPERTIES.getProperty("tile.height", "32"));
	public static final double SPRITE_WIDTH = Double.parseDouble(PROPERTIES.getProperty("image.width", "64"));
	public static final double SPRITE_HEIGHT = Double.parseDouble(PROPERTIES.getProperty("image.height", "64"));
	public static final double FOOTPRINT_HEIGHT = Double.parseDouble(PROPERTIES.getProperty("footprint.height", "32"));

	private SharedConstants() {}
}
