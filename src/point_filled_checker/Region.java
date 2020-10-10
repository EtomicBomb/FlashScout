package point_filled_checker;

class Region {
    private int pixelCount;
    private int xSum;
    private int ySum;
    private int closestToCentroidX;
    private int closestToCentroidY;

    private int furthestFromCentroidX;
    private int furthestFromCentroidY;

    Region(int x, int y) {
        pixelCount = 0;

        xSum = 0;
        ySum = 0;
        closestToCentroidX = x;
        closestToCentroidY = y;
        furthestFromCentroidX = x;
        furthestFromCentroidY = y;
    }

    void addPixel(int x, int y) {
        pixelCount += 1;

        xSum += x;
        ySum += y;

        // we only have an approxmation of the centroid
        double bestInnerRadius = Math.hypot(centroidX()-closestToCentroidX, centroidY()-closestToCentroidY); // can't cache this cause centroidX estimate just changed
        double newInnerRadius = Math.hypot(centroidX()-x, centroidY()-y);
        if (newInnerRadius < bestInnerRadius) {
            closestToCentroidX = x;
            closestToCentroidY = y;
        }

        double bestOuterRadius = Math.hypot(centroidX()-furthestFromCentroidX, centroidY()-furthestFromCentroidY);
        double newOuterRadius = Math.hypot(centroidX()-x, centroidY()-y);
        if (newOuterRadius > bestOuterRadius) {
            furthestFromCentroidX = x;
            furthestFromCentroidY = y;
        }
    }

    boolean isAligner() { // this method retruns true on plenty of small noise, but that doesn't matter cause the result is always sorted
        double innerRadiusApprox = Math.hypot(centroidX()-closestToCentroidX, centroidY()-closestToCentroidY);
        double outerRadiusApprox = Math.hypot(centroidX()-furthestFromCentroidX, centroidY()-furthestFromCentroidY);

        double guessArea = Math.PI * (outerRadiusApprox*outerRadiusApprox - innerRadiusApprox*innerRadiusApprox);
        boolean ret = Math.abs((pixelCount /guessArea) - 1) < 0.5 && pixelCount > 500;

        if (ret) {
            System.out.println("area:"+ pixelCount+ " inner radius approx " +innerRadiusApprox + " outer radius approx: "+outerRadiusApprox + " guess area " + guessArea + " bruh " + (pixelCount / guessArea));
        }

        return ret;
    }

    int getPixelCount() {
        return pixelCount;
    }

    int centroidX() {
        return xSum / pixelCount;
    }

    int centroidY() {
        return ySum / pixelCount;
    }
}
