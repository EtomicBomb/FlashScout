class SVGinator {
    // possibly pass in a data structure represnting these constants
//    private static final String ELEMENT_COLOR = "#CFE2F3";
//    private static final double ALIGNER_DISTANCE_FROM_CORNER = 0.121428; // TODO: parameterize
//    private static final double ALIGNER_INNER_RADIUS = 0.05;
//    private static final double ALIGNER_OUTER_RADIUS = 0.0714;
//    private static final double TITLE_FONT_SIZE = 0.13;
//    private static final double DOCUMENT_TITLE_X = 0.2;
//    private static final double DOCUMENT_TITLE_Y = 0.05;


    static String makeSVG(Layout layout) {
        SVGBuilder svgBuilder = new SVGBuilder();
        
        for (Layout.Field field : layout.fields) {

            Layout.Field.Descriptor descriptor = field.getDescriptor();
            svgBuilder.addText(descriptor.x, descriptor.y, descriptor.textSize, descriptor.text, layout.meta.elementColor);
            
            if (field.kind == Layout.FieldKind.NUMERIC) {
                for (Layout.Field.Digit digit : field.getDigits()) {
                    for (Layout.Bar bar : digit.bars) {
                        svgBuilder.addRectangle(bar.x, bar.y, bar.base, bar.height, layout.meta.elementColor);
                    }
                }
                
            } else if (field.kind == Layout.FieldKind.BOOLEAN) {
                Layout.Bar bar = field.getBar();
                svgBuilder.addRectangle(bar.x, bar.y, bar.base, bar.height, layout.meta.elementColor);

            }
        }


        svgBuilder.addText(layout.meta.titleX, layout.meta.titleY, layout.meta.titleFontSize, layout.documentTitle, layout.meta.elementColor);

        for (Layout.Meta.Aligner pos : layout.meta.aligners) {
            svgBuilder.addCircle(pos.centerX, pos.centerY, "black", layout.meta.alignerOuterRadius);
            svgBuilder.addCircle(pos.centerX, pos.centerY, "white", layout.meta.alignerInnerRadius);
        }

        return svgBuilder.finish();
    }


    /**
     * This is a really janky low level SVG file generator. Hopefully I will eventually care enough to do something proper here
     */
    private static class SVGBuilder {
        StringBuilder inner;

        SVGBuilder() {
            inner = new StringBuilder();
            // our svg header
            inner.append("<svg height=\"8.5in\" width=\"8.5in\" xmlns=\"http://www.w3.org/2000/svg\">\n");
            inner.append("\t<rect fill=\"white\" height=\"100%\" width=\"100%\" x=\"0\" y=\"0\"/>\n"); // white rectangle for the background
        }

        void addCircle(double x, double y, String color, double r) {
            inner.append("\t<circle cx=\"");
            inner.append(100*x);
            inner.append("%\" cy=\"");
            inner.append(100*y);
            inner.append("%\" fill=\"");
            inner.append(color);
            inner.append("\" r=\"");
            inner.append(100*r);
            inner.append("%\"/>\n");
        }

        void addRectangle(double x, double y, double base, double height, String color) {
            inner.append("\t<rect fill=\"");
            inner.append(color);
            inner.append("\" height=\"");
            inner.append(100*height);
            inner.append("%\" width=\"");
            inner.append(100*base);
            inner.append("%\" x=\"");
            inner.append(100*x);
            inner.append("%\" y=\"");
            inner.append(100*y);
            inner.append("%\"/>\n");
        }

        void addText(double x, double y, double fontSize, String text, String color) {
            // for text, the x, y points to the bottom left hand corner for whatever reason. This is unlike rectangles and circles and stuff.
            inner.append("\t<text fill=\"");
            inner.append(color);
            inner.append("\" font-family=\"monospace\" font-size=\"");
            inner.append(toPoints(fontSize));
            inner.append("\" x=\"");
            inner.append(100*x);
            inner.append("%\" y=\"");
            inner.append(100*(y+fontSize)); // must use a sensible format
            inner.append("%\">");
            inner.append(text);
            inner.append("</text>\n");
        }


        String finish() {
            inner.append("</svg>");
            return inner.toString();
        }


        static double toPoints(double widthFraction) {
            // we know our document is 8.5 x 8.5
            // text in svgs must exculisvely use points an a unit, but we use width fractions for everythnig else
            return 72 * 8.5 * widthFraction;
        }
    }
}
