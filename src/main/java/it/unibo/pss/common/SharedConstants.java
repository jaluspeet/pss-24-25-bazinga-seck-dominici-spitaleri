package it.unibo.pss.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads configuration settings from a properties file and defines shared constants.
 */
public final class SharedConstants {

	private static final String CONFIG_FILE = "/config.properties";
	private static final Properties PROPERTIES = new Properties();

	static {
		try (InputStream input = SharedConstants.class.getResourceAsStream(CONFIG_FILE)) {
			if (input == null) {
				throw new IOException("Configuration file not found: " + CONFIG_FILE);
			}
			PROPERTIES.load(input);
		} catch (IOException e) {
			throw new ExceptionInInitializerError("Failed to load configuration: " + e.getMessage());
		}
	}

	// shared constants
	public static final int WINDOW_WIDTH = getInt("window.width", 800);
	public static final int WINDOW_HEIGHT = getInt("window.height", 600);
	public static final String WINDOW_TITLE = getString("enable.logging", "pss-javafx");
	public static final int WORLDGRID_WIDTH = getInt("worldgrid.width", 32);
	public static final int WORLDGRID_HEIGHT = getInt("worldgrid.height", 32);

	// parsing methods
	private SharedConstants() {}

	protected static int getInt(String key, int defaultValue) {
		return PROPERTIES.containsKey(key) ? Integer.parseInt(PROPERTIES.getProperty(key)) : defaultValue;
	}

	protected static double getDouble(String key, double defaultValue) {
		return PROPERTIES.containsKey(key) ? Double.parseDouble(PROPERTIES.getProperty(key)) : defaultValue;
	}

	private static boolean getBoolean(String key, boolean defaultValue) {
		return PROPERTIES.containsKey(key) ? Boolean.parseBoolean(PROPERTIES.getProperty(key)) : defaultValue;
	}

	private static String getString(String key, String defaultValue) {
		return PROPERTIES.containsKey(key) ? PROPERTIES.getProperty(key) : defaultValue;
	}
}
