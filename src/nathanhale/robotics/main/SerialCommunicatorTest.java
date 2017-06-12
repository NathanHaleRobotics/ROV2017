package nathanhale.robotics.main;

import com.fazecast.jSerialComm.SerialPort;
import nathanhale.robotics.serial.SerialCommunicator;
import nathanhale.robotics.util.ByteUtil;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.Controller.Type;
import net.java.games.input.ControllerEnvironment;

public class SerialCommunicatorTest {
	public static void main(String[] args) {
		for(SerialPort port : SerialPort.getCommPorts()) {
			if(port.getDescriptivePortName().contains("rduino")) {
				port.openPort();
				port.setBaudRate(4800);
				System.out.println("Opened serial port " + port.getDescriptivePortName());
				SerialCommunicator com = new SerialCommunicator(port, data -> {
					System.out.println("recieved data " + ByteUtil.getInteger(data, 0));
				});
				
				com.startTransmission();
				
				Controller joystick = findJoystick();
				byte[] buffer = new byte[4];
				while(true) {
					//System.out.println("sending");
					//joystick.poll();
					//ByteUtil.putInteger(buffer, 0, (int)(joystick.getComponent(Identifier.Axis.X).getPollData() * 100));
					//com.send(buffer);
					ByteUtil.putInteger(buffer, 0, 4506545);
					com.send(buffer);
					try {
						Thread.sleep(250);
					} catch(InterruptedException e) {
						break;
					}
				}
				com.close();
			}
		}
	}
	
	public static Controller findJoystick() {
		for(Controller c : ControllerEnvironment.getDefaultEnvironment().getControllers()) {
			if(c.getType() == Type.STICK && c.getComponent(Identifier.Axis.X) != null && c.getComponent(Identifier.Axis.Y) != null) {
				return c;
			}
		}
		return null;
	}
}
