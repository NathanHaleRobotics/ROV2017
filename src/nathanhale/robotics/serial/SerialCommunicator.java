package nathanhale.robotics.serial;

import com.fazecast.jSerialComm.SerialPort;
import java.io.Closeable;
import java.io.IOException;

public final class SerialCommunicator implements Closeable {
	/** The serial port to communicate over **/
	private final SerialPort port;
	
	/** object passed to constructor that handles data processing logic **/
	private final MessageProcessor processor;
	
	/** seperate threads for sending and receiving data **/
	private Thread senderThread, receiverThread;
	
	/** flags for communicator state **/
	private volatile boolean running, exit;
	
	//message processor interface declaration, has one method that passes the communicator and data received
	public static interface MessageProcessor {
		void process(SerialCommunicator serial, byte[] data);
	}
	
	/**
	 * Create a new SerialCommunicator
	 * @param port the serial port to use
	 * @param processor the code to run each time data is received
	 */
	public SerialCommunicator(SerialPort port, MessageProcessor processor) {
		this.port = port;
		this.processor = processor;
		this.port.openPort();
	}
	
	/**
	 * Start communication over the serial port
	 */
	public void startTransmission() {
		if(running)
			throw new UnsupportedOperationException("SerialCommunicator already running");
		
		//start threads with senders and receivers.
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
	
	/**
	 * Handles data sending, TODO implement this, should use a BlockingQueue to send byte arrays that are available via a send() method
	 * @author Solomon Ritzow
	 *
	 */
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
	
	/**
	 * Parses received packets of data and passes them to the SerialCommunicator's MessageProcessor
	 * @author Solomon Ritzow
	 *
	 */
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
