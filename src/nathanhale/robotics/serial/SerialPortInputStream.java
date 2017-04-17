package nathanhale.robotics.serial;

import java.io.IOException;
import java.io.InputStream;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;

public class SerialPortInputStream extends InputStream {
	
	private final SerialPort port;
	private final Object lock;
	private volatile boolean closed;
	
	public SerialPortInputStream(SerialPort port) throws SerialPortException {
		this.lock = new Object();
		this.port = port;
		port.addEventListener(event -> {
			if(event.getEventType() == SerialPortEvent.RXCHAR) {
				synchronized(lock) {
					lock.notifyAll();
				}
			}
		});
	}

	@Override
	public int read() throws IOException {
		try {
			waitBytes(1);
			return port.readBytes(1)[0];
		} catch(SerialPortException e) {
			throw new IOException(e);
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        try {
			waitBytes(len - off);
			System.arraycopy(port.readBytes(len - off), 0, b, off, len);
			return len - off;
		} catch (SerialPortException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			closed = true;
			synchronized(lock) {
				lock.notifyAll();	
			}
			port.closePort();
		} catch (SerialPortException e) {
			throw new IOException(e);
		}
	}
	
	private void waitBytes(int bytes) throws SerialPortException {
		synchronized(lock) {
			while(port.getInputBufferBytesCount() < bytes && !closed) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
