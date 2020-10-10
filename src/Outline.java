import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The most generic description of a page. Contains no information about where any objects on a page are, only the document
 * title and the individual fields themselves.
 */
public class Outline {
    String documentTitle;
    ArrayList<Field> fields;

    public Outline(ArrayList<Field> fields, String documentTitle) {
        this.documentTitle = documentTitle;
        this.fields = fields;
    }

    Outline(String string) throws Exception {
        // the idea behind this is that you could send people an outline
        // of a test in an email or something,

        // function just parses files that look like this:
        /* SAMPLE FILE:

            2020 Game

            Number(4) Team Number
            Boolean() Red
            Boolean() Blue
            Number(1) Starting Position

        */

        Pattern regex = Pattern.compile("(Number|Boolean)\\((\\d*)\\)\\s*(.*)");
        // we want to first get the title


        ArrayList<Field> fields = new ArrayList<>();
        String documentTitle = ""; // will overwrite
        boolean haveFoundTitle = false;

        for (int i=0; i<string.length(); i++) {
            char c = string.charAt(i);

            if (!Character.isWhitespace(c)) {
                if (haveFoundTitle) {
                    // we want to find fields

                    StringBuilder lineBuilder = new StringBuilder();
                    while (i<string.length() && string.charAt(i) != '\n') {
                        lineBuilder.append(string.charAt(i));
                        i++;
                    }
                    String line = lineBuilder.toString();

                    Matcher result = regex.matcher(line);
                    if (!result.matches()) {
                        throw new Exception("improper field syntax");
                    }
                    String type = result.group(1);
                    String descriptor = result.group(3);

                    Field field;
                    if (type.equals("Boolean")) {
                        field = new Field(descriptor);
                    } else {
                        try {
                            int digitCount = Integer.parseInt(result.group(2));
                            field = new Field(descriptor, digitCount);
                        } catch (NumberFormatException e) {
                            throw new Exception("digit length for number not given");
                        }
                    }

                    fields.add(field);
                } else {
                    StringBuilder title = new StringBuilder();
                    while (i<string.length() && string.charAt(i) != '\n') {
                        title.append(string.charAt(i));
                        i++;
                    }

                    documentTitle = title.toString();
                    haveFoundTitle = true;
                }
            }
        }

        if (!haveFoundTitle) {
            throw new Exception("no title found");
        }

        this.fields = fields;
        this.documentTitle = documentTitle;
    }

    static class Field {
        String descriptor;
        boolean isNumber;
        int digitCount; // field only used if isNumber is true

        Field(String descriptor) { // constuctor for booleans
            this.descriptor = descriptor;
            this.isNumber = false;
        }

        Field(String descriptor, int digitCount) {
            this.descriptor = descriptor;
            this.digitCount = digitCount;
            this.isNumber = true;
        }
    }
}
