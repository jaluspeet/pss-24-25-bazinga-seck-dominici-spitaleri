package it.unibo.pss.view;

import javafx.stage.Stage;

public class View {

	public View(Stage stage, String title, int width, int height) {
		stage.setTitle(title);
		stage.setWidth(width);
		stage.setHeight(height);
		stage.show();
	}
}
