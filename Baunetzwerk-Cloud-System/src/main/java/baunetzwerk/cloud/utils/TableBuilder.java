package baunetzwerk.cloud.utils;

public class TableBuilder {

    public static String createTable(String[] header, String[][] content) {
        StringBuilder table = new StringBuilder("\n");

        // Calculate table width
        int[] widthPerRow = new int[header.length];

        for (int i = 0; i < header.length; i++) {
            if (header[i].length() > widthPerRow[i])
                widthPerRow[i] = header[i].length();
        }

        for (int i = 0; i < content.length; i++) {
            for (int j = 0; j < content[i].length; j++) {
                if (content[i][j] == null) continue;
                if (content[i][j].length() > widthPerRow[j]) {
                    widthPerRow[j] = content[i][j].length();
                }
            }
        }
        // Create Header
        String line = createLine(widthPerRow);

        table.append(line);
        table.append(createContent(header, widthPerRow, "&b"));
        table.append(line);

        for (String[] c : content) {
            table.append(createContent(c, widthPerRow, "&f"));
            table.append(line);
        }

        return table.toString();
    }

    private static String createLine(int[] width) {
        StringBuilder builder = new StringBuilder("+");
        for (int w : width) {
            for (int i = 0; i < w + 2; i++)
                builder.append("-");
            builder.append("+");
        }
        builder.append("\n");
        return builder.toString();
    }

    private static String createContent(String[] content, int[] width, String color) {
        StringBuilder builder = new StringBuilder("| ");
        String c;
        for (int i = 0; i < content.length; i++) {
            c = content[i];
            if (content[i] == null)
                c = "NULL";
            builder.append(color).append(c).append("&r");
            for (int j = width[i] - c.length(); j > 0; j--)
                builder.append(" ");
            builder.append(" | ");
        }
        builder.append("\n");
        return builder.toString();
    }

}
