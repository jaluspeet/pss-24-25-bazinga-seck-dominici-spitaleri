package it.unibo.bazinga;

import it.unibo.bazinga.common.StartupMenu;
import javafx.application.Application;

public final class App {
	public static void main(final String[] args) {
		Application.launch(StartupMenu.class, args);
	}

	private App() { }
}

