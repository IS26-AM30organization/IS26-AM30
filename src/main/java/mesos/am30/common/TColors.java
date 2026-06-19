package mesos.am30.common;

// Utility for handling TUI colors.
public class TColors {
    //Reset
    public static final String RESET = "\033[0m";

    //Standard Colors
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String CYAN = "\033[36m";
    public static final String BROWN = "\033[38;5;130m";
    public static final String ORANGE = "\033[38;5;208m";
    public static final String GOLD = "\033[38;5;220m";
    public static final String PINK = "\033[38;5;206m";
    public static final String DARK_GRAY = "\033[38;5;238m";

    // Bold Colors
    public static final String RED_B = "\033[1;31m";
    public static final String GREEN_B = "\033[1;32m";
    public static final String YELLOW_B = "\033[1;33m";
    public static final String BLUE_B = "\033[1;34m";
    public static final String MAGENTA_B = "\033[1;35m";
    public static final String CYAN_B = "\033[1;36m";
    public static final String SILVER_B = "\033[1;38;5;246m";

    // get the visible length of a Character sequence
    public static int getVisibleLength(CharSequence text) {
        if (text == null) {
            return 0;
        }
        String ansiRegex = "\u001B\\[[0-9;]*m";

        return text.toString().replaceAll(ansiRegex, "").length();
    }
}

