import linear_algebra.Matrix;
import linear_algebra.Point;
import linear_algebra.Transform;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {

        BufferedImage image = ImageIO.read(new File("resources/image18.png"));

        Layout layout = LayoutEngine.layout(new Outline(readFileToString("resources/outline.txt")));
        String svgText = SVGinator.makeSVG(layout);

        Parser.ParseResult[] result = Parser.parse(layout, image);
        System.out.println(Arrays.toString(result));

        Files.writeString(Paths.get("test.svg"), svgText, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String readFileToString(String fileName) throws IOException {
        // https://stackoverflow.com/a/3403112
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
}
