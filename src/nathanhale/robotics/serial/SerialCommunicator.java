package nathanhale.robotics.serial;

import com.fazecast.jSerialComm.SerialPort;
import java.io.Closeable;
import java.io.IOException;

public final class SerialCommunicator implements Closeable {
	private final SerialPort port;
	private final MessageProcessor processor;
	private Thread senderThread, receiverThread;
	private volatile boolean running, exit;
	
	public static interface MessageProcessor {
		void process(SerialCommunicator serial, byte[] data);
	}
	
	public SerialCommunicator(SerialPort port, MessageProcessor processor) {
		this.port = port;
		this.processor = processor;
		this.port.openPort();
	}
	
	public void startTransmission() {
		senderThread = new Thread(new SerialSender(), port.getDescriptivePortName() + " Sender Thread");
		receiverThread = new Thread(new SerialReceiver(), port.getDescriptivePortName() + " Receiver Thread");
		senderThread.start();
		receiverThread.start();
		running = true;
	}
	
	@Override
	public void close() {
		stopTransmission();
		port.closePort();
	}
	
	public void stopTransmission() {
		exit = true;
		if(running) {
			try {
				//senderThread.interrupt();
				//receiverThread.interrupt();
				senderThread.join();
				receiverThread.join();
				running = false;
			} catch (InterruptedException e) {
				
			} finally {
				senderThread = null;
				receiverThread = null;
			}
		}
	}
	
	private final class SerialSender implements Runnable {
		@Override
		public void run() {
			try {
				while(!exit) {
					Thread.sleep(500);
				}
			} catch (InterruptedException e) {
				
			}
		}
	}
	
	private final class SerialReceiver implements Runnable {
		@Override
		public void run() {
			while(!exit) {
				try {
					int length =((port.getInputStream().read() & 255) << 24) |
								((port.getInputStream().read() & 255) << 16) |
								((port.getInputStream().read() & 255) << 8) | 
								((port.getInputStream().read() & 255) << 0);
					
					if(length > 2048) {
						System.err.println("Received oversized message of length " + length);
					} else if(length > 0) {
						byte[] data = new byte[length];
						port.getInputStream().read(data);
						processor.process(SerialCommunicator.this, data);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
