package nathanhale.robotics.main;

import java.io.FileInputStream;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * The main class of the ROV Controller, which begins the user interface and other communication components
 * @author Solomon Ritzow
 *
 */
public class StartUI extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		//a stage is the equivalent of a window in JavaFX
		
		//a generic node that contains other nodes and resizes to fit them
		Group group = new Group();
		
		//base scene class that is contained in a Stage
		Scene scene = new Scene(group);
		
		//set some stage properties including the title and window icon
		stage.setTitle("ROV Controller");
		stage.getIcons().add(new Image(new FileInputStream("res/icon.jpg"), 140, 140, true, false));
		stage.setScene(scene);
		
		//add some text the the scene
		Text text = new Text("Hello world");
		text.setFont(Font.font(100));
		text.setCursor(Cursor.CROSSHAIR);
		text.setX(10);
		text.setY(100);
		group.getChildren().add(text); //add the text
		
		//show the window
		stage.show();
		
		for(Controller controller : ControllerEnvironment.getDefaultEnvironment().getControllers()) {
			System.out.println(controller.getName() + ":");
			for(Component component : controller.getComponents()) {
				System.out.println(" - " + component.getIdentifier() + ": " + component.getName());
			}
			System.out.println();
		}
	}
}
