public class IRCameraInterface {
	public static void main(String[] args) throws InterruptedException {
		IRCamera camera = new IRCamera("/dev/ttyACM0", 19200);
		
		System.out.println(System.nanoTime());
		for(int i = 0; i < 1000; i++) {       
			camera.getCurrentFrame();
		}
		System.out.println(System.nanoTime());
	}
}