package nathanhale.robotics.main;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import nathanhale.robotics.serial.SerialCommunicator;
import nathanhale.robotics.ui.ROVControlPanel;
import nathanhale.robotics.util.ByteUtil;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public final class ControlPanelApplication extends Application {
	
	public static void main(String... args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		//in JavaFX, windows are called stages
		stage.setTitle("ROV Control Center");
		stage.setWidth(Screen.getPrimary().getBounds().getWidth()/2);
		stage.setHeight(Screen.getPrimary().getBounds().getHeight()/2);
		
		//set some stage properties including the title and window icon
		stage.getIcons().add(new Image(ControlPanelApplication.class.getResourceAsStream("/nathanhale/robotics/resources/icon.png"), 140, 140, true, false));
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if(event.getCode() == KeyCode.F11) {
				stage.setFullScreen(!stage.isFullScreen());
			}
		});

		//create a scrollbar area
		ROVControlPanel controlPanel = new ROVControlPanel();
		Scene scene = new Scene(controlPanel);
		scene.setFill(Color.BLUE); //so we can tell when there isn't anything filling the scene
		stage.setScene(scene);
		
		//create a string builder for all of the serial port and input device information
		StringBuilder sb = new StringBuilder();
		
		//write the list of serial ports to the string builder
		sb.append("Serial Ports:\n");
		for(SerialPort port : SerialPort.getCommPorts()) {
			sb.append('\t').append(port.getDescriptivePortName()).append('\n');
		}
		
		//load all the the JInput controller names and components into the string builder (unrelated to JavaFX code above)
		sb.append("Input Devices:\n");
		for(Controller controller : ControllerEnvironment.getDefaultEnvironment().getControllers()) {
			sb.append('\t').append(controller.getName()).append(":\n");
			for(Component component : controller.getComponents()) {
				sb.append("\t\t").append(component.getName()).append('\n');
			}
		}
		
		controlPanel.setDevicesContent(sb.toString());
		
		for(SerialPort port : SerialPort.getCommPorts()) {
			if(port.getDescriptivePortName().contains("rduino")) {
				communicator = new SerialCommunicator(port, (serial, data) -> {
					System.out.println("processing message of length " + data.length);
					System.out.print(new String(data));
//					if(data.length >= 2) {
//						short protocol = ByteUtil.getShort(data, 0);
//						switch(protocol) {
//						case 20:
//							serial.stopTransmission();
//							break;
//						default:
//							System.out.println("Received message of protocol " + protocol);
//						}
//					}
				});
				communicator.startTransmission();
			}
		}
		
		stage.setOnCloseRequest(event -> {
			if(communicator != null) {
				new Thread(() -> communicator.close()).start();
			}
		});
		
		//show the window
		stage.show();
	}
	
	volatile SerialCommunicator communicator = null;
}
