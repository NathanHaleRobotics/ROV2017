package nathanhale.robotics.serial;

import com.fazecast.jSerialComm.SerialPort;

public class ROVSerialCommunicator {
	private final SerialPort port;
	private SerialSender sender;
	private SerialReceiver receiver;
	private volatile boolean exit;
	
	public ROVSerialCommunicator(SerialPort port) {
		this.port = port;
		port.openPort();
	}
	
	public Runnable getSender() {
		return sender == null ? new SerialSender() : sender;
	}
	
	public Runnable getReceiver() {
		return receiver == null ? new SerialReceiver() : receiver;
	}
	
	private class SerialSender implements Runnable {
		@Override
		public void run() {
			while(!exit) {
				
			}
		}
	}
	
	private class SerialReceiver implements Runnable {
		@Override
		public void run() {
			while(!exit) {
				
			}
		}
	}
}
