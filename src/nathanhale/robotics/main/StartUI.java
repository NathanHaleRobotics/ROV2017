package nathanhale.robotics.main;

import javafx.application.Application;

/**
 * The main class of the ROV Controller, which begins the user interface and other communication components
 */
public final class StartUI {
	public static void main(String... args) {
		Application.launch(ControlPanelApplication.class, args);
	}
}
