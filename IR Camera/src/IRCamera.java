import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Queue;
import com.fazecast.jSerialComm.*;


public class IRCamera {
	private final static byte newLineIndicator = 10;
	private final static byte commaIndicator = 44;

	//private IRFrame currentFrame;
	private SerialPort comPort;
	
	// Coordinates in the format (x1, y1), (x2, y2), (x3, y3), (x4, y4)
	private int[] coordinates;
	
	public IRCamera(String portName, int baudRate, int[] fourCoordinates) {
		comPort = InitializeComPort(comPort, portName, baudRate);

		if (ClearBuffer(comPort)) {
			System.out.println("Buffer is emptied");
		}
		
		coordinates = new int[8];
		
		for (int i = 0; i < 8; i++) {
			coordinates[i] = fourCoordinates[i];
		}
		
	}
	
	public String getCurrentFrame() {
		String defaultIntString = "0";
		String tempIntString = defaultIntString;
		Queue<Integer> lineValue = new LinkedList<Integer>();
		boolean first = true;
		while(true) {
			if(comPort.bytesAvailable() != 0) {
				byte[] readBuffer = new byte[comPort.bytesAvailable()];
				comPort.readBytes(readBuffer, comPort.bytesAvailable());
				
				for(int i = 0; i < readBuffer.length; i++) {
					if(readBuffer[i] == newLineIndicator) {
						lineValue.add(Integer.parseInt(tempIntString));
						
						if (first) {
							first = false;
						}
						else {
							return lineValue.toString();
							//System.out.println(lineValue.toString());
						}

						tempIntString = "0";
						lineValue.clear();
					}
					else if (readBuffer[i] == commaIndicator) {
						lineValue.add(Integer.parseInt(tempIntString));
						tempIntString = "0";
					}
					else {
						int number = -1;
						
						switch(readBuffer[i]) {
							case 48:
								number = 0;
								break;
							case 49:
								number = 1;
								break;
							case 50:
								number = 2;
								break;
							case 51:
								number = 3;
								break;
							case 52:
								number = 4;
								break;
							case 53:
								number = 5;
								break;
							case 54:
								number = 6;
								break;
							case 55:
								number = 7;
								break;
							case 56:
								number = 8;
								break;
							case 57:
								number = 9;
								break;
							default:
								number = -1;
								break;
						}
						
						if (number != -1) {
							tempIntString += Character.toString((char) readBuffer[i]);
						}
					}
				}	
			}
		}
	}
	
	public void ReInitializePort(String portName, int baudRate) {
		if (comPort.isOpen())
			comPort.closePort();
		
		this.comPort = InitializeComPort(this.comPort, portName, baudRate);
	}
	
	private SerialPort InitializeComPort(SerialPort port, String portName, int baudRate) {
		port = SerialPort.getCommPort(portName);
		
		port.openPort();
		port.setBaudRate(baudRate);
		
		return port;
	}
	
	private boolean ClearBuffer(SerialPort port) {
		boolean emptied = false;
		
		byte[] tempBuffer = new byte[port.bytesAvailable()];
		port.readBytes(tempBuffer, port.bytesAvailable());
		
		if(port.bytesAvailable() == 0) {
			emptied = true;
		}
		
		return emptied;
	}
}