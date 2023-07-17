package baunetzwerk.cloud.utils;

public class ChatColor {


    public static String translateAlternateColorCodes(String textToTranslate) {
        for (ChatColors chatColors : ChatColors.values())
            textToTranslate = textToTranslate.replace("&" + chatColors.symbol, chatColors.getColor());
        return textToTranslate;
    }

    private enum ChatColors {
        BLACK('0', "30"),
        DARK_BLUE('1', "34"),
        DARK_GREEN('2', "32"),
        DARK_CYAN('3', "36"),
        DARK_RED('4', "31"),
        DARK_MAGENTA('5', "35"),
        ORANGE('6', "33"),
        GRAY('7', "37"),
        DARK_GRAY('8', "90"),
        BLUE('9', "94"),
        GREEN('a', "92"),
        CYAN('b', "96"),
        RED('c', "91"),
        MAGENTA('d', "95"),
        YELLOW('e', "93"),
        WHITE('f', "97"),
        RESET('r', "0");

        private final char symbol;
        private final String result;

        ChatColors(char symbol, String result) {
            this.symbol = symbol;
            this.result = result;
        }

        private String getColor() {
            return "\u001B[" + result + "m";
        }

    }

}
