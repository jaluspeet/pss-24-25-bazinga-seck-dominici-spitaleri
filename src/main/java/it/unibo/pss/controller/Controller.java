package it.unibo.pss.controller;

import javafx.application.Application;
import javafx.stage.Stage;
import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.Model;
import it.unibo.pss.view.View;

public class Controller extends Application {

	private Model model;
	private View view;

	@Override
	public void start(Stage stage) {
		this.model = new Model(SharedConstants.WORLDGRID_WIDTH, SharedConstants.WORLDGRID_HEIGHT);
		this.view = new View(stage, SharedConstants.WINDOW_TITLE, SharedConstants.WINDOW_WIDTH, SharedConstants.WINDOW_HEIGHT);

		// XXX: Print the grid to the console
		// System.out.println(this.model.getGrid());
	}

	public static void main(String[] args) {
		launch(args);
	}
}
