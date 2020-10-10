package point_filled_checker;

import linear_algebra.Matrix;
import linear_algebra.Point;
import linear_algebra.Transform;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class BarFilledChecker {
    private BooleanMatrix booleanMatrix;
    private Matrix m; // our transformation matrix mapping a point in our 0-1 document
    // space to image space

    public BufferedImage debug; // TODO: remove


    public BarFilledChecker(BooleanMatrix booleanMatrix, Point[] layoutCenters) throws Exception {
        debug = new BufferedImage(booleanMatrix.image.getColorModel(), booleanMatrix.image.copyData(null), false, null);

        Point[] alignerCenters;
        try {
            alignerCenters = AlignerCentersFinder.findAlignerCenters(booleanMatrix);
        } catch (Exception e) {
            ImageIO.write(debug, "png", new File("centers.png"));

            throw e;
        }

        for (Point p : alignerCenters) {
            for (int dx=-10; dx<10; dx++) {
                for (int dy=-10; dy<10; dy++) {
                    debug.setRGB((int)p.x + dx, (int)p.y + dy, 0xff << 8);
                }
            }
        }


        try {
            ImageIO.write(debug, "png", new File("centers.png"));
        } catch (Exception e) {

        }


        this.booleanMatrix = booleanMatrix;
        this.m = Transform.getPerspectiveTransformMatrix(alignerCenters, layoutCenters);
    }

    public boolean isFilled(Point position) {
        // takes a bar coordinate in our document space (0..1) and checks if it is filled in our image
//
//        Mat documentPoint = new MatOfPoint2f(position);
//
//        MatOfPoint2f newPointMat = new MatOfPoint2f(new Point(0,0)); // includes dummy point?
//        Core.perspectiveTransform(documentPoint, newPointMat, m);
//
//        Point newPoint = newPointMat.toArray()[0];

        Point newPoint = Transform.transformPoint(m, position);


        boolean ret = false;
        // TODO:
        for (int dy=-10; dy<=10; dy++) {
            for (int dx=-10; dx<=10; dx++) {
                boolean thisIsFilled = booleanMatrix.isSet((int)newPoint.x+dx, (int)newPoint.y+dy);
                ret |= thisIsFilled;
                debug.setRGB((int)newPoint.x+dx, (int)newPoint.y+dy, 0xff << (ret?16:0));
            }
        }

        return ret;
    }
}
