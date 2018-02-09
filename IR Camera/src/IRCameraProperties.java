public class IRCameraProperties {
	private final int MAX_CAMERA_VALUE = 1023;
	private String portDescriptor;
	private int _numCameras;
	private int[] _x;
	private int[] _y;
	
	public IRCameraProperties(int numCameras) {
		_numCameras = numCameras;
		_x = new int[numCameras];
		_y = new int[numCameras];
		
		for(int i = 0; i < numCameras; i++) {
			_x[i] = MAX_CAMERA_VALUE;
			_y[i] = MAX_CAMERA_VALUE;
		}
	}
	
	public int getNumCameras() {
		return _numCameras;
	}
	
	public int[] Get_X_Properties() {
		return _x;
	}

	public int[] Get_Y_Properties() {
		return _y;
	}
	
	public int Get_X_Slot(int slot) {
		return _x[slot];
	}
	
	public int Get_Y_Slot(int slot) {
		return _y[slot];
	}
	
	public String toString() {
		String result = "";
		String delimiter = ",";
		String semicolon = ";";
		
		for(int i = 0; i < (this.getNumCameras() - 1); i++) {
			result += Get_X_Slot(i);
			result += delimiter;
			result += Get_Y_Slot(i);
			
			if (i == this.getNumCameras() - 1) {
				delimiter = semicolon;
			}
			result += delimiter;
		}
		
		return result;
	}
}
