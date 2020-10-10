class Layout {
    String documentTitle;
    Field[] fields;
    Meta meta;


    Layout(String documentTitle, Field[] fields, Meta meta) {
        this.documentTitle = documentTitle;
        this.fields = fields;
        this.meta = meta;
    }

    // you know, java's lack of algebraic types doesn't tempt me to embrace the Visitor/Interface/whatever idiom
    enum FieldKind {
        NUMERIC,
        BOOLEAN;
    }

    static class Field {
        FieldKind kind;
        private Bar bar; // only non-null when we are boolean
        private Digit[] digits; // only valid when we are numeric
        private Descriptor descriptor;

        Field(Bar bar, Descriptor descriptor) {
            this.bar = bar;
            this.descriptor = descriptor;
            this.kind = FieldKind.BOOLEAN;
        }
        Field(Digit[] digits, Descriptor descriptor) {
            this.digits = digits;
            this.descriptor = descriptor;
            this.kind = FieldKind.NUMERIC;
        }

        Descriptor getDescriptor() {
            return descriptor;
        }

        Digit[] getDigits() {
            assert kind == FieldKind.NUMERIC;
            return digits;
        }

        Bar getBar() {
            assert kind == FieldKind.BOOLEAN;
            return bar;
        }

        static class Digit {
            Bar[] bars;
            Digit(Bar[] bars) {
                this.bars = bars;
            }
        }

        static class Descriptor {
            double x;
            double y;
            double textSize;
            String text;

            Descriptor(double x, double y, double textSize, String text) {
                this.x = x;
                this.y = y;
                this.textSize = textSize;
                this.text = text;
            }
        }
    }

    static class Meta {
        String elementColor;
        double alignerInnerRadius;
        double alignerOuterRadius;
        double titleFontSize;
        double titleX;
        double titleY;
        Aligner[] aligners;

        Meta(String elementColor, double alignerInnerRadius, double alignerOuterRadius, double titleFontSize, double titleX, double titleY, Aligner[] aligners) {
            this.elementColor = elementColor;
            this.alignerInnerRadius = alignerInnerRadius;
            this.alignerOuterRadius = alignerOuterRadius;
            this.titleFontSize = titleFontSize;
            this.titleX = titleX;
            this.titleY = titleY;
            this.aligners = aligners;
        }

        static class Aligner {
            double centerX;
            double centerY;

            public Aligner(double centerX, double centerY) {
                this.centerX = centerX;
                this.centerY = centerY;
            }
        }
    }

    static class Bar {
        double x;
        double y;
        double base;
        double height;

        Bar(double x, double y, double base, double height) {
            this.x = x;
            this.y = y;
            this.base = base;
            this.height = height;
        }
    }

}
