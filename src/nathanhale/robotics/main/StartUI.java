package nathanhale.robotics.main;

import java.io.FileInputStream;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
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
	public void start(Stage stage) throws Exception {

		//in JavaFX, windows are called stages
		stage.setTitle("ROV Control Center");
		stage.setWidth(Screen.getPrimary().getBounds().getWidth()/2);
		stage.setHeight(Screen.getPrimary().getBounds().getHeight()/2);
		
		//set some stage properties including the title and window icon
		stage.getIcons().add(new Image(new FileInputStream("res/icon.jpg"), 140, 140, true, false));
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if(event.getCode() == KeyCode.F11) {
				stage.setFullScreen(!stage.isFullScreen());
			}
		});
		
		//create a scrollbar area
		VBox root = new VBox();
		Tab infoTab = new Tab("Information");
		infoTab.setClosable(false);
		ScrollPane sp = new ScrollPane();
		infoTab.setContent(sp);
		Tab controlsTab = new Tab("Controls");
		controlsTab.setClosable(false);
		TabPane tabs = new TabPane(infoTab, controlsTab);
		root.getChildren().add(tabs);
		
		//scenes are the topmost container inside a stage, they can be swapped out
		Scene scene = new Scene(root);
		scene.setFill(Color.BLUE); //so we can tell when there isn't anything filling the scene
		stage.setScene(scene);
		
		//load all the the JInput controller names and components into a String (unrelated to JavaFX code above)
		StringBuilder sb = new StringBuilder();
		for(Controller controller : ControllerEnvironment.getDefaultEnvironment().getControllers()) {
			sb.append(controller.getName());
			sb.append(":\n");
			for(Component component : controller.getComponents()) {
				sb.append("\t");
				sb.append(component.getName());
				sb.append('\n');
			}
		}
		
		//display the list of controllers inside the scrollpane
		Text devices = new Text(sb.toString());
		devices.setFont(Font.font(20));
		sp.setContent(devices);
		
		//show the window
		stage.show();
	}
}
