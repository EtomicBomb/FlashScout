package point_filled_checker;

import java.awt.image.BufferedImage;

public class BooleanMatrix {
    private int darkThreshold;
    BufferedImage image; // TODO: make private
    int base;
    int height;

    public BooleanMatrix(BufferedImage inputImage, int darkThreshold) {
        base = inputImage.getWidth();
        height = inputImage.getHeight();

        this.darkThreshold = darkThreshold;
        image = inputImage;
    }

    // This is the only operation we support
    boolean isSet(int x, int y) {
        int rgb = image.getRGB(x, y);

        return ((rgb >> 16 & 0xff) < darkThreshold)
                && ((rgb >> 8 & 0xff) < darkThreshold)
                && ((rgb & 0xff) < darkThreshold);
    }

    public BufferedImage toImage() {
        BufferedImage output = new BufferedImage(base, height, BufferedImage.TYPE_INT_RGB);

        for (int y=0; y<height; y++) {
            for (int x=0; x<base; x++) {
                if (isSet(x, y)) {
                    output.setRGB(x, y, 0xffffff); // white
                }
            }
        }

        return output;
    }
}
