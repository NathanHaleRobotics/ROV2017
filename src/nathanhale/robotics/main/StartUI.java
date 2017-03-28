package nathanhale.robotics.main;

import net.java.games.input.*;

public class StartUI {
	public static void main(String... args) {
		for(Controller controller : ControllerEnvironment.getDefaultEnvironment().getControllers()) {
			System.out.println(controller.getName() + ":");
			for(Component component : controller.getComponents()) {
				System.out.println(" - " + component.getName());
			}
			System.out.println();
		}
	}
}
