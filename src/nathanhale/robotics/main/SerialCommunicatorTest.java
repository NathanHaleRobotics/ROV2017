package nathanhale.robotics.main;

import com.fazecast.jSerialComm.SerialPort;
import java.util.Scanner;
import nathanhale.robotics.serial.SerialCommunicator;

public class SerialCommunicatorTest {
	public static void main(String[] args) {
		for(SerialPort port : SerialPort.getCommPorts()) {
			if(port.getDescriptivePortName().contains("rduino")) {
				port.openPort();
				SerialCommunicator com = new SerialCommunicator(port, (a, b) -> {
					System.out.println("received data");
					System.out.println(new String(b));
				});
				
				com.startTransmission();
				Scanner scanner = new Scanner(System.in);
				String line;
				while(!(line = scanner.nextLine()).equals("exit")) {
					com.send(line);
				}
				scanner.close();
				com.close();
			}
		}
	}
}
