import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

public class IRCameraInterface {
	public static Mat cameraMat;
	public static Mat cameraMat2;
	public static Mat flipMat;
	public static Mat flipMat2;
	public static int WIDTH;
	public static int HEIGHT;
	static ImgWindow cameraWindow = ImgWindow.newWindow();
	static ImgWindow camera2Window = ImgWindow.newWindow();
	
	public IRCameraInterface(int width, int height) {
		WIDTH = width;
		HEIGHT = height;
	}

	
	public static void main(String[] args) throws InterruptedException, IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
        List<Point> callibList = new ArrayList<Point>();
        callibList.add(new Point(40.0, 30.0));
        callibList.add(new Point(508.0, 54.0));
        callibList.add(new Point(509.0, 688.0));
        callibList.add(new Point(34.0, 703.0));

        List<Point> callibList2 = new ArrayList<Point>();
        callibList2.add(new Point(40.0, 30.0));
        callibList2.add(new Point(508.0, 54.0));
        callibList2.add(new Point(509.0, 688.0));
        callibList2.add(new Point(34.0, 703.0));

        assert(callibList.size() == 4);
        assert(callibList2.size() == 4);
        
		IRCamera camera = new IRCamera("/dev/ttyACM1", 19200, callibList);
		IRCamera camera2 = new IRCamera("/dev/ttyACM0", 19200, callibList2);

		// Camera interface 1
		IRCameraInterface cameraInterface = new IRCameraInterface(767, 1023);  
        
		cameraInterface.start(Color.BLACK);
		
		while(true) {
			String camerastring = cleanUpString(camera.getCurrentFrame());
			String camera2string = cleanUpString(camera2.getCurrentFrame());

			String[] cameraValues = camerastring.split(",");
			String[] camera2Values = camera2string.split(",");
			
			if(Integer.parseInt(cameraValues[0]) != 1023 && Integer.parseInt(cameraValues[1]) != 1023) {
		        Point point = new Point(Double.parseDouble(cameraValues[1]), Double.parseDouble(cameraValues[0]));
		        Core.circle(cameraMat, point, 7, new Scalar(0, 0, 255));
		        Core.flip(cameraMat, flipMat, 0);
				System.out.println("Camera 1 " + camerastring);
			}
			
			if(Integer.parseInt(camera2Values[0]) != 1023 && Integer.parseInt(camera2Values[1]) != 1023) {
				Point point = new Point(Double.parseDouble(camera2Values[1]), Double.parseDouble(camera2Values[0]));
		        Core.circle(cameraMat2, point, 7, new Scalar(0, 255, 255));
		        Core.flip(cameraMat2, flipMat2, 0);
				System.out.println("Camera 2 " + camera2string);
			}

			cameraWindow.setImage(flipMat);
			camera2Window.setImage(flipMat2);
		}
	}

	private static String cleanUpString(String toClean) {
		String result = toClean.replace("[", "");
		result = result.replace("]", "");
		result = result.replace(" ", "");
		
		return result; 
	}

	private void start(Color color){
	    cameraMat = new Mat(HEIGHT, WIDTH, CvType.CV_8UC3);
	    cameraMat2 = new Mat(HEIGHT, WIDTH, CvType.CV_8UC3);
	    flipMat = new Mat(HEIGHT, WIDTH, CvType.CV_8UC3);
	    flipMat2 = new Mat(HEIGHT, WIDTH, CvType.CV_8UC3);
	}
}