package nathanhale.robotics.ui;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public final class ROVControlPanel extends VBox {
	private final Tab controls;
	private final Tab devices;
	
	public ROVControlPanel() {
		VBox root = new VBox();
		controls = new Tab("Controls");
		devices = new Tab("Device List");
		controls.setClosable(false);
		devices.setClosable(false);
		TabPane tabs = new TabPane(controls, devices);
		root.getChildren().add(tabs);
		this.getChildren().add(root);
	}
	
	/** temporary **/
	public void setDevicesContent(String deviceList) {
		Text text = new Text(deviceList);
		text.setFont(Font.font("consolas", 16));
		ScrollPane sp = new ScrollPane();
		sp.setContent(text);
		devices.setContent(sp);
	}
}
