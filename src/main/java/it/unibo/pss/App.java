package it.unibo.pss;

import it.unibo.pss.controller.Controller;
import javafx.application.Application;

public final class App {
	private App() { }

	public static void main(final String[] args) {
		Application.launch(Controller.class, args);
	}
}
