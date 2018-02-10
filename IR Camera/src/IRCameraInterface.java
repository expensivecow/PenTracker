import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class IRCameraInterface {
	
	public static JFrame frame;
	public static BufferedImage mergedImg;
	public static BufferedImage cameraImg;
	public static BufferedImage cameraImg2;
	public static int WIDTH;
	public static int HEIGHT;
	
	public IRCameraInterface(int width, int height) {
		WIDTH = width;
		HEIGHT = height;
	}
	
	public static void main(String[] args) throws InterruptedException {
		int[] camera1Coordinates = {0,0,0,0,0,0,0,0};
		int[] camera2Coordinates = {0,0,0,0,0,0,0,0};
		
		IRCamera camera = new IRCamera("/dev/ttyACM2", 19200, camera1Coordinates);
		IRCamera camera2 = new IRCamera("/dev/ttyACM3", 19200, camera2Coordinates);

		// Camera interface 1
		IRCameraInterface cameraInterface = new IRCameraInterface(1023, 767);

	    frame = new JFrame("MergedFrame");
	    frame.setVisible(true);
	    
	    cameraInterface.start(Color.BLACK);
	    
	    frame.add(new JLabel(new ImageIcon(mergedImg)));
	    
	    frame.pack();
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);    
        
		while(true) {
			String camerastring = cleanUpString(camera.getCurrentFrame());
			String camera2string = cleanUpString(camera2.getCurrentFrame());

			String[] cameraValues = camerastring.split(",");
			String[] camera2Values = camera2string.split(",");
			
			if(Integer.parseInt(cameraValues[0]) != 1023 && Integer.parseInt(cameraValues[1]) != 1023) {
			    Graphics2D g2d = cameraImg.createGraphics();
		        g2d.setColor(Color.BLUE);
		        g2d.fillOval((Integer.parseInt(cameraValues[0])), (Integer.parseInt(cameraValues[1])), 10, 10);
		        g2d.dispose();
		        
		        mergedImg = mergeImage(cameraImg, cameraImg2);
		        frame.repaint();
		        
				System.out.println("Camera 1 " + camerastring);
			}
			
			if(Integer.parseInt(camera2Values[0]) != 1023 && Integer.parseInt(camera2Values[1]) != 1023) {
				Graphics2D g2d = cameraImg2.createGraphics();
		        g2d.setColor(Color.WHITE);
		        g2d.fillOval(Integer.parseInt(camera2Values[0]), Integer.parseInt(camera2Values[1]), 10, 10);
		        g2d.dispose();

		        mergedImg = mergeImage(cameraImg, cameraImg2);
		        frame.repaint();
		        
				System.out.println("Camera 2 " + camera2string);
			}
		}
	}
	
	public static String cleanUpString(String toClean) {
		String result = toClean.replace("[", "");
		result = result.replace("]", "");
		result = result.replace(" ", "");
		
		return result; 
	}
	
	public static BufferedImage mergeImage(Image left, Image right) {
	    Graphics2D g = mergedImg.createGraphics();
	    g.drawImage(left, 0, 0, null);
	    g.drawImage(right, left.getWidth(null), 0, null);
	    return mergedImg;
	}
	
	public void start(Color color){
		mergedImg = new BufferedImage(WIDTH*2, HEIGHT, BufferedImage.TYPE_INT_RGB);
	    cameraImg = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	    cameraImg2 = new BufferedImage(WIDTH, HEIGHT,BufferedImage.TYPE_INT_RGB);
	}
}