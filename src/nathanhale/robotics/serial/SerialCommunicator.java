package nathanhale.robotics.serial;

import com.fazecast.jSerialComm.SerialPort;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class SerialCommunicator implements Closeable {
	/** The serial port to communicate over **/
	private final SerialPort port;
	
	/** object passed to constructor that handles data processing logic **/
	private final MessageProcessor processor;
	
	/** seperate threads for sending and receiving data **/
	private Thread senderThread, receiverThread;
	
	/** The queue of data to be sent over the serial port **/
	private final BlockingQueue<byte[]> sendQueue;
	
	/** flags for communicator state **/
	private volatile boolean running, exit;
	
	/** DataOutputStream to be able to easily send data **/
	private final DataOutputStream sender;
	
	private volatile int maxMessageLength;
	
	/**
	 * Can be passed to the SerialCommunicator constructor to process incoming serial port data
	 */
	@FunctionalInterface
	public static interface MessageProcessor {
		void process(byte[] data);
	}
	
	/**
	 * Constructs a new SerialCommunicator with no MessageProcessor
	 * @param port the serial port to use 
	 */
	public SerialCommunicator(SerialPort port) {
		this(port, (c) -> {});
	}
	
	/**
	 * Create a new SerialCommunicator
	 * @param port the serial port to use
	 * @param processor the code to run each time data is received
	 */
	public SerialCommunicator(SerialPort port, MessageProcessor processor) {
		this.port = port;
		this.processor = processor;
		this.sendQueue = new LinkedBlockingQueue<byte[]>();
		this.sender = new DataOutputStream(port.getOutputStream());
		this.maxMessageLength = 128;
		this.port.openPort();
	}
	
	//character encoding to use when sending strings over the serial port
	private static final Charset utf8 = Charset.forName("UTF8");
	
	public void send(byte[] data) {
		if(data.length > maxMessageLength)
			throw new RuntimeException("message too large");
		try {
			sendQueue.put(data);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Start communication over the serial port
	 */
	public void startTransmission() {
		if(running)
			throw new UnsupportedOperationException("SerialCommunicator already running");
		if(!port.isOpen())
			throw new UnsupportedOperationException("Serial port not open");
		
		//start threads with senders and receivers.
		receiverThread = new Thread(new SerialReceiver(), port.getDescriptivePortName() + " Receiver Thread");
		senderThread = new Thread(new SerialSender(), port.getDescriptivePortName() + " Sender Thread");
		receiverThread.start();
		senderThread.start();
		running = true;
	}
	
	/**
	 * Stop transmission and close the SerialPort
	 */
	@Override
	public void close() {
		try {
			stopTransmission();
			port.closePort();
			sender.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stop the SerialCommunicator from sending and receiving data
	 */
	public void stopTransmission() {
		exit = true;
		if(running) {
			try {
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
	 * Handles data sending
	 * @author Solomon Ritzow
	 */
	private final class SerialSender implements Runnable {
		@Override
		public void run() {
			try {
				while(!exit) {
					byte[] data = sendQueue.take();
					sender.writeInt(data.length);
					sender.write(data);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				
			}
		}
	}
	
	/**
	 * Parses received packets of data and passes them to the SerialCommunicator's MessageProcessor
	 * @author Solomon Ritzow
	 */
	private final class SerialReceiver implements Runnable {
		@Override
		public void run() {
			while(!exit) {
				try {
					int length = //TODO receiving wrong values, or reading them wrong
						(((byte)port.getInputStream().read() & 255) << 24) |
						(((byte)port.getInputStream().read() & 255) << 16) |
						(((byte)port.getInputStream().read() & 255) << 8) | 
						(((byte)port.getInputStream().read() & 255) << 0);
					System.out.println("length " + length);
//					byte[] data = new byte[port.bytesAvailable()];
//					port.readBytes(data, data.length);
//					System.out.println(new String(data, "ASCII"));
					if(length > 0 && length <= 2048) {
						byte[] data = new byte[length];
						port.getInputStream().read(data, 0, length);
						processor.process(data);
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
