package main;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class WarpedFrameTransformer {
	private List<Point> corners = new ArrayList<Point>();
	private List<Point> target = new ArrayList<Point>();
	private List<Point> newPoints = new ArrayList<Point>();
	private Mat img;
	private Mat proj;
	private Mat trans;
	
	public WarpedFrameTransformer(Mat image, List<Point> cornerPoints) {
		// Initialize target mat coordinates
		target.add(new Point(0, 0));
		target.add(new Point(image.cols(), 0));
		target.add(new Point(image.cols(), image.rows()));
		target.add(new Point(0, image.rows()));
		
		corners = cornerPoints;
		
		img = image;
	}
	
	public Mat getProjection() {
		Mat result = null;
		if(corners.size() == 4) {
			Mat cornersMat = Converters.vector_Point2f_to_Mat(corners);
			Mat targetMat = Converters.vector_Point2f_to_Mat(target);
			trans = Imgproc.getPerspectiveTransform(cornersMat, targetMat);
			proj = new Mat();

			Imgproc.warpPerspective(img, proj, trans, new Size(img.cols(), img.rows()));

			Mat transformed = new Mat();
			if(newPoints.size() > 0) {
				Core.perspectiveTransform(Converters.vector_Point2f_to_Mat(newPoints), transformed, trans);
				List<Point> transPoints = new ArrayList<Point>();
				Converters.Mat_to_vector_Point2f(transformed, transPoints);
				for(Point p: transPoints) {
					Core.circle(proj, p, 2, new Scalar(255, 255, 0), 2);
				}
			}
			
			result = proj;
		}
		
		return result;
	}
}