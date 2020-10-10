import linear_algebra.Point;
import point_filled_checker.BarFilledChecker;
import point_filled_checker.BooleanMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

class Parser {
    private static final int DARK_THRESHOLD = 120;

     static ParseResult[] parse(Layout layout, BufferedImage image) throws Exception {
        Layout.Meta.Aligner[] as = layout.meta.aligners;
        Point[] alignerCenters = {
            new Point(as[0].centerX, as[0].centerY),
            new Point(as[1].centerX, as[1].centerY),
            new Point(as[2].centerX, as[2].centerY),
            new Point(as[3].centerX, as[3].centerY)
        };

        BooleanMatrix booleanMatrix = new BooleanMatrix(image, DARK_THRESHOLD);
        ImageIO.write(booleanMatrix.toImage(), "png", new File("blackandwhite.png"));

        BarFilledChecker filledChecker = new BarFilledChecker(booleanMatrix, alignerCenters);

        int fieldsCount = layout.fields.length;

        ParseResult[] parseResults = new ParseResult[fieldsCount];

        for (var f=0; f<fieldsCount; f++) {
            Layout.Field field = layout.fields[f];

            ParseResult result;

            if (field.kind == Layout.FieldKind.BOOLEAN) {
                Point center = new Point(field.getBar().x+field.getBar().base/2, field.getBar().y+field.getBar().height/2);
                boolean isFilled = filledChecker.isFilled(center);

                result = new ParseResult(isFilled);


            } else { // we are numeric
                int sum = 0;
                int powerOfTen = 1;

                boolean hasSeenEmpty = false;
                boolean allAreEmpty = true;


                for (int i=field.getDigits().length-1; i>=0 ;i--) { // traverse backwards
                    Layout.Field.Digit digit = field.getDigits()[i];
                    int pattern = 0;
                    for (int j=0; j<7; j++) {
                        Layout.Bar bar = digit.bars[j];
                        Point center = new Point(bar.x+bar.base/2, bar.y+bar.height/2);

                        if (filledChecker.isFilled(center)) {
                            pattern |= (1 << (6-j));
                        }
                    }

                    int d=-1;
                    boolean wasEmpty = false;
                    boolean wasInvalid = false;
                    switch (pattern) {
                        case 0b1111110: d=0; break;
                        case 0b0110000: d=1; break;
                        case 0b1101101: d=2; break;
                        case 0b1111001: d=3; break;
                        case 0b0110011: d=4; break;
                        case 0b1011011: d=5; break;
                        case 0b1011111: case 0b0011111: d=6; break;
                        case 0b1110000: d=7; break;
                        case 0b1111111: d=8; break;
                        case 0b1111011: case 0b1110011: d=9; break;
                        case 0b0000000: wasEmpty = true; break;
                        default: wasInvalid = true;
                    }


                    if (!wasInvalid && !wasEmpty) {
                        if (hasSeenEmpty) {
                            ImageIO.write(filledChecker.debug, "png", new File("bruh.png")); // TODO: remove

                            throw new Exception("blank in the middle of number");
                        } else {
                            allAreEmpty = false;
                            sum += d*powerOfTen;
                        }
                    } else if (wasInvalid) {
                        ImageIO.write(filledChecker.debug, "png", new File("bruh.png")); // TODO: remove

                        throw new Exception("ha this did'nt work " + pattern);
                    } else if (wasEmpty) {
                        hasSeenEmpty = true;
                    } // no way both are true

                    powerOfTen *= 10;
                }

                if (allAreEmpty) {
                    ImageIO.write(filledChecker.debug, "png", new File("bruh.png")); // TODO: remove
                    throw new Exception("no bars in the number were filled in");
                }

                result = new ParseResult(sum);
            }

            parseResults[f] = result;
        }

        ImageIO.write(filledChecker.debug, "png", new File("bruh.png"));

        return parseResults;
    }

    // how bad is this pattern really?
    static class ParseResult {
        boolean booleanResult;
        int numericResult;
        boolean isNumber;
        boolean wasError;
        String error;

        ParseResult(boolean booleanResult) {
            this.booleanResult = booleanResult;
            this.isNumber = false;
            this.wasError = false;
        }
        ParseResult(int numericResult) {
            this.numericResult = numericResult;
            this.isNumber = true;
            this.wasError = false;
        }

        ParseResult(String error) {
            this.error = error;
            this.isNumber = false;
            this.wasError = true;
        }

        @Override
        public String toString() {
            if (wasError) {
                return error;
            } else if (isNumber) {
                return Integer.toString(numericResult);
            } else {
                return Boolean.toString(booleanResult);
            }
        }
    }
}
