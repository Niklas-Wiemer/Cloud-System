package baunetzwerk.cloud.logger;

import baunetzwerk.cloud.utils.ChatColor;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LoggerFormatter extends Formatter {

    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        String message = "";
        if (record.getLevel().intValue() == Level.SEVERE.intValue()) {
            message += "&4[ERROR] &c";
        } else if (record.getLevel().intValue() == Level.WARNING.intValue()) {
            message += "&6[WARN] &e";
        } else if (record.getLevel().intValue() == Level.INFO.intValue()) {
            message += "&9[INFO] &b";
        } else if (record.getLevel().intValue() == Level.FINE.intValue()) {
            message += "&3[DEBUG] &b";
        }
        message += format.format(record.getMillis()) + " &8| ";

        message += "&f" + formatMessage(record) + "\n";
        return ChatColor.translateAlternateColorCodes(message + "&r");
    }

}
