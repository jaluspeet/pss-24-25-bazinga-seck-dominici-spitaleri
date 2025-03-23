package it.unibo.pss.common;

import it.unibo.pss.controller.Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * StartupMenu shows a JavaFX menu that lets the user adjust all configuration values
 * via discrete sliders, each having 10 steps
 */
public class StartupMenu extends Application {

	private Properties config = new Properties();
	private final File externalConfigFile = new File("config.properties");
	private Map<String, DiscreteSlider> numericSliders = new HashMap<>();
	
	private static final int[] zeroTen = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	private static final int[] powersOfTwo = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};
	private static final int[] winWidths = {800, 1024, 1280, 1280, 1366, 1440, 1600, 1920, 2560, 3840};
	private static final int[] winHeights = {600, 768, 720, 800, 768, 900, 900, 1080, 1440, 2160};
	private static final int[] refreshRates = {5, 10, 15, 30, 60, 75, 90, 120, 144, 240 };


	// Helper class for sliders.
	private static class DiscreteSlider {
		Slider slider;
		int[] allowedValues;
		DiscreteSlider(Slider slider, int[] allowedValues) {
			this.slider = slider;
			this.allowedValues = allowedValues;
		}
		int getValue() {
			return allowedValues[(int) slider.getValue()];
		}
	}

	@Override
	public void start(Stage primaryStage) {
		loadConfig();

		TabPane tabPane = new TabPane();
		tabPane.getTabs().addAll(
				createWindowTab(),
				createWorldTab(),
				createSheepTab(),
				createWolfTab(),
				createPlantTab(),
				createCameraTab()
				);

		Button startButton = new Button("è ora di giocare!");
		startButton.setMaxWidth(Double.MAX_VALUE);
		startButton.setStyle("-fx-font-size: 32px;");
		VBox.setMargin(startButton, new Insets(10));

		startButton.setOnAction(e -> {
			updateConfigFromSliders();
			saveConfig();
			launchController(primaryStage);
		});

		VBox root = new VBox(10, tabPane, startButton);
		Scene scene = new Scene(root, 900, 650);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Startup Menu");
		primaryStage.show();
	}

	// Load configuration from external file if it exists.
	private void loadConfig() {
		if (externalConfigFile.exists()) {
			try (InputStream is = new FileInputStream(externalConfigFile)) {
				config.load(is);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	// Save configuration to the external file.
	private void saveConfig() {
		try (OutputStream os = new FileOutputStream(externalConfigFile)) {
			config.store(os, "generated from GUI");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// Update the configuration properties based on the slider positions.
	private void updateConfigFromSliders() {
		for (Map.Entry<String, DiscreteSlider> entry : numericSliders.entrySet()) {
			String key = entry.getKey();
			int value = entry.getValue().getValue();
			config.setProperty(key, String.valueOf(value));
		}
	}

	// Create the window tab
	private Tab createWindowTab() {
		Tab tab = new Tab("Window");
		tab.setClosable(false);
		VBox vbox = new VBox(15);
		vbox.setPadding(new Insets(10));

		VBox widthBox = createDiscreteSlider("window.width", winWidths);
		VBox heightBox = createDiscreteSlider("window.height", winHeights);

		vbox.getChildren().addAll(widthBox, heightBox);
		tab.setContent(vbox);
		return tab;
	}

	// Create the world tab.
	private Tab createWorldTab() {
		Tab tab = new Tab("World");
		tab.setClosable(false);
		VBox vbox = new VBox(15);
		vbox.setPadding(new Insets(10));

		vbox.getChildren().addAll(
				createDiscreteSlider("world.width", powersOfTwo),
				createDiscreteSlider("world.height", powersOfTwo)
				);
		
		vbox.getChildren().addAll(
				createDiscreteSlider("world.water.ratio", zeroTen),
				createDiscreteSlider("world.lake.ratio", zeroTen),
				createDiscreteSlider("world.lake.count", zeroTen)
				);
		
		vbox.getChildren().add(
				createDiscreteSlider("model.update.interval", powersOfTwo)
				);
		tab.setContent(vbox);
		return tab;
	}

	// Create the sheep tab.
	private Tab createSheepTab() {
		Tab tab = new Tab("Sheep");
		tab.setClosable(false);
		VBox vbox = new VBox(15);
		vbox.setPadding(new Insets(10));

		vbox.getChildren().addAll(
				createDiscreteSlider("sheep.count", powersOfTwo),
				createDiscreteSlider("sheep.speed", zeroTen),
				createDiscreteSlider("sheep.sight.range", powersOfTwo),
				createDiscreteSlider("sheep.energy.default", powersOfTwo),
				createDiscreteSlider("sheep.energy.hungry", powersOfTwo),
				createDiscreteSlider("sheep.energy.restore", powersOfTwo),
				createDiscreteSlider("sheep.energy.bazinga", powersOfTwo)
				);
		tab.setContent(vbox);
		return tab;
	}

	// Create the wolf tab.
	private Tab createWolfTab() {
		Tab tab = new Tab("Wolf");
		tab.setClosable(false);
		VBox vbox = new VBox(15);
		vbox.setPadding(new Insets(10));

		vbox.getChildren().addAll(
				createDiscreteSlider("wolf.count", powersOfTwo),
				createDiscreteSlider("wolf.speed", zeroTen),
				createDiscreteSlider("wolf.sight.range", powersOfTwo),
				createDiscreteSlider("wolf.energy.default", powersOfTwo),
				createDiscreteSlider("wolf.energy.hungry", powersOfTwo),
				createDiscreteSlider("wolf.energy.restore", powersOfTwo),
				createDiscreteSlider("wolf.energy.bazinga", powersOfTwo)
				);
		tab.setContent(vbox);
		return tab;
	}

	// Create the plant tab.
	private Tab createPlantTab() {
		Tab tab = new Tab("Plant");
		tab.setClosable(false);
		VBox vbox = new VBox(15);
		vbox.setPadding(new Insets(10));

		vbox.getChildren().addAll(
				createDiscreteSlider("plant.count", powersOfTwo),
				createDiscreteSlider("plant.resurrection.time", powersOfTwo)
				);
		tab.setContent(vbox);
		return tab;
	}

	// Create the camera tab.
	private Tab createCameraTab() {
		Tab tab = new Tab("Camera");
		tab.setClosable(false);
		VBox vbox = new VBox(15);
		vbox.setPadding(new Insets(10));

		vbox.getChildren().addAll(
				createDiscreteSlider("camera.sensitivity", zeroTen),
				createDiscreteSlider("camera.min.scale", zeroTen),
				createDiscreteSlider("camera.max.scale", zeroTen),
				createDiscreteSlider("camera.framerate", refreshRates)
				);
		tab.setContent(vbox);
		return tab;
	}

	private VBox createDiscreteSlider(String key, int[] allowedValues) {
		Label label = new Label(key);
		Slider slider = new Slider(0, allowedValues.length - 1, 0);
		slider.setMajorTickUnit(1);
		slider.setMinorTickCount(0);
		slider.setSnapToTicks(true);
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);
		slider.setLabelFormatter(new StringConverter<Double>() {
			@Override
			public String toString(Double object) {
				int idx = object.intValue();
				if (idx < 0 || idx >= allowedValues.length) return "";
				return String.valueOf(allowedValues[idx]);
			}
			@Override
			public Double fromString(String string) {
				return 0d;
			}
		});

		// Set initial value from config if present.
		String prop = config.getProperty(key);
		int initialIndex = 0;
		if (prop != null) {
			try {
				int val = Integer.parseInt(prop);
				for (int i = 0; i < allowedValues.length; i++) {
					if (allowedValues[i] == val) {
						initialIndex = i;
						break;
					}
				}
			} catch (NumberFormatException ex) {
				// Default remains index 0.
			}
		}
		slider.setValue(initialIndex);

		DiscreteSlider ds = new DiscreteSlider(slider, allowedValues);
		numericSliders.put(key, ds);

		VBox box = new VBox(5, label, slider);
		return box;
	}

	// Launch the main simulation by starting the Controller on a new Stage.
	private void launchController(Stage primaryStage) {
		try {
			Stage simulationStage = new Stage();
			Controller controller = new Controller();
			controller.start(simulationStage);
			primaryStage.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

