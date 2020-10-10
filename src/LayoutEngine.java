/**
 * Just one of possibly many functions that can create a custom layout
 */
class LayoutEngine {
    private static final double BAR_WIDTH = 0.01;
    private static final double BAR_LENGTH = 0.03;

    private static final double FIELD_START_Y = 0.3;
    private static final double VERTICAL_FIELD_SPACE = 0.1;

    private static final double BAR_VERTICAL_OFFSET = 0.03;
    private static final double TEXT_WIDTH_MULTIPLIER = 0.6;
    private static final double TEXT_GAP = 0.02;
    private static final double FIELD_START_X = 0.2;
    private static final double BAR_SPACE = 0.003;
    private static final double DIGIT_GAP = 3*BAR_LENGTH;

    private static final double DESCRIPTOR_FONT_SIZE = 0.05; // not a fraction


    private static final String ELEMENT_COLOR = "#CFE2F3";
    private static final double ALIGNER_DISTANCE_FROM_CORNER = 0.121428; // TODO: parameterize
    private static final double ALIGNER_INNER_RADIUS = 0.05;
    private static final double ALIGNER_OUTER_RADIUS = 0.0714;
    private static final double TITLE_FONT_SIZE = 0.13;
    private static final double DOCUMENT_TITLE_X = 0.2;
    private static final double DOCUMENT_TITLE_Y = 0.05;

    private static Layout.Meta.Aligner[] ALIGNERS = {
        new Layout.Meta.Aligner(ALIGNER_DISTANCE_FROM_CORNER, ALIGNER_DISTANCE_FROM_CORNER), // top left
        new Layout.Meta.Aligner(1-ALIGNER_DISTANCE_FROM_CORNER, ALIGNER_DISTANCE_FROM_CORNER), // top right
        new Layout.Meta.Aligner(1-ALIGNER_DISTANCE_FROM_CORNER, 1-ALIGNER_DISTANCE_FROM_CORNER), // bottom right
        new Layout.Meta.Aligner(ALIGNER_DISTANCE_FROM_CORNER, 1-ALIGNER_DISTANCE_FROM_CORNER), // bottom left
    };
    private static Layout.Meta META = new Layout.Meta(ELEMENT_COLOR, ALIGNER_INNER_RADIUS, ALIGNER_OUTER_RADIUS, TITLE_FONT_SIZE, DOCUMENT_TITLE_X, DOCUMENT_TITLE_Y, ALIGNERS);


    static Layout layout(Outline outline) {
        double y = FIELD_START_Y;

        int fieldCount = outline.fields.size();

        Layout.Field[] layoutFields = new Layout.Field[fieldCount];

        for (int i=0; i<fieldCount; i++) {
            Outline.Field outlineField = outline.fields.get(i);

            Layout.Field layoutField;

            double x = FIELD_START_X + TEXT_WIDTH_MULTIPLIER*DESCRIPTOR_FONT_SIZE*(double)outlineField.descriptor.length() + TEXT_GAP;

            Layout.Field.Descriptor descriptor = new Layout.Field.Descriptor(FIELD_START_X, y, DESCRIPTOR_FONT_SIZE, outlineField.descriptor);


            if (outlineField.isNumber) {
                Layout.Field.Digit[] digits = new Layout.Field.Digit[outlineField.digitCount];
                for (int j=0; j<outlineField.digitCount; j++) {
                    Layout.Bar[] bars = new Layout.Bar[7];

                    for (SevenSegmentDigitOffset o : SevenSegmentDigitOffset.values()) {
                        bars[o.ordinal()] = new Layout.Bar(x+o.xOffset, y+o.yOffset,  o.base, o.height);
                    }

                    x += DIGIT_GAP;
                    digits[j] = new Layout.Field.Digit(bars);
                }
                layoutField = new Layout.Field(digits, descriptor);

            } else {
                Layout.Bar bar = new Layout.Bar(x, y+BAR_VERTICAL_OFFSET, BAR_LENGTH, BAR_WIDTH);
                layoutField = new Layout.Field(bar, descriptor);
            }

            layoutFields[i] = layoutField;

            y += VERTICAL_FIELD_SPACE;
        }

        return new Layout(outline.documentTitle, layoutFields, META);
    }


    enum SevenSegmentDigitOffset {
        ONE(BAR_WIDTH+BAR_SPACE, 0, BAR_LENGTH, BAR_WIDTH),
        TWO(BAR_WIDTH+BAR_LENGTH+2*BAR_SPACE, BAR_WIDTH+ BAR_SPACE, BAR_WIDTH, BAR_LENGTH),
        THREE(BAR_WIDTH+BAR_LENGTH+2*BAR_SPACE, 2*BAR_WIDTH+BAR_LENGTH+3* BAR_SPACE, BAR_WIDTH, BAR_LENGTH),
        FOUR(BAR_WIDTH+ BAR_SPACE, 2*BAR_WIDTH+2*BAR_LENGTH+4* BAR_SPACE, BAR_LENGTH, BAR_WIDTH),
        FIVE(0, 2*BAR_WIDTH+BAR_LENGTH+3* BAR_SPACE, BAR_WIDTH, BAR_LENGTH),
        SIX(0, BAR_WIDTH+ BAR_SPACE, BAR_WIDTH, BAR_LENGTH),
        SEVEN(BAR_WIDTH+ BAR_SPACE, BAR_WIDTH+BAR_LENGTH+2* BAR_SPACE, BAR_LENGTH, BAR_WIDTH);

        double xOffset;
        double yOffset;
        double base;
        double height;

        SevenSegmentDigitOffset(double xOffset, double yOffset, double base, double height) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.base = base;
            this.height = height;
        }
    }
}
