package it.unibo.pss;

import it.unibo.pss.controller.Controller;
import javafx.application.Application;

/** Main application entry-point's class. */
public final class App {
	private App() { }

	/** Main application entry-point. */
	public static void main(final String[] args) {
		Application.launch(Controller.class, args);
	}
}
