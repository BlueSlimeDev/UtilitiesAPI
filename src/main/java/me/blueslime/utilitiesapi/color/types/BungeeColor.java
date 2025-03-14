package me.blueslime.utilitiesapi.color.types;

import me.blueslime.utilitiesapi.color.ColorHandler;
import net.md_5.bungee.api.ChatColor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BungeeColor extends ColorHandler {
    private Method COLORIZE_METHOD;

    public BungeeColor() {
        try {
            COLORIZE_METHOD = ChatColor.class.getDeclaredMethod("of", String.class);
        } catch (Exception ignored) {
            COLORIZE_METHOD = null;
        }
    }

    public String execute(String message) {
        if (message == null) {
            return null;
        }

        message = processGradient(message);
        message = processHexColorCodes(message);

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String processGradient(String message) {
        Pattern gradientPattern = Pattern.compile("<#([a-fA-F0-9]{6})>(.*?)</#([a-fA-F0-9]{6})>");
        Matcher gradientMatcher = gradientPattern.matcher(message);
        StringBuffer sb = new StringBuffer();

        while (gradientMatcher.find()) {
            String startHex = "#" + gradientMatcher.group(1);
            String innerText = gradientMatcher.group(2);
            String endHex = "#" + gradientMatcher.group(3);
            String gradientText = applyGradient(innerText, startHex, endHex);
            gradientMatcher.appendReplacement(sb, Matcher.quoteReplacement(gradientText));
        }
        gradientMatcher.appendTail(sb);
        return sb.toString();
    }

    private String processHexColorCodes(String message) {
        message = replaceHexPattern(message, "&#[a-fA-F0-9]{6}", "&");
        message = replaceHexPattern(message, "#[a-fA-F0-9]{6}", "");
        return message;
    }

    private String replaceHexPattern(String message, String regex, String prefixToRemove) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String codeMatch = message.substring(matcher.start(), matcher.end());
            String code = codeMatch.replace(prefixToRemove, "");

            if (COLORIZE_METHOD != null) {
                try {
                    ChatColor color = (ChatColor) COLORIZE_METHOD.invoke(ChatColor.WHITE, code);
                    message = message.replace(codeMatch, color.toString());
                    // Reset matcher
                    matcher = pattern.matcher(message);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    // Check if an error exists.
                    message = message.replace(codeMatch, "");
                }
            }
        }
        return message;
    }

    private String applyGradient(String text, String startHex, String endHex) {
        int startR = Integer.parseInt(startHex.substring(1, 3), 16);
        int startG = Integer.parseInt(startHex.substring(3, 5), 16);
        int startB = Integer.parseInt(startHex.substring(5, 7), 16);

        int endR = Integer.parseInt(endHex.substring(1, 3), 16);
        int endG = Integer.parseInt(endHex.substring(3, 5), 16);
        int endB = Integer.parseInt(endHex.substring(5, 7), 16);

        StringBuilder result = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            double ratio = (length > 1) ? (double) i / (length - 1) : 0;

            int r = (int) Math.round(startR + (endR - startR) * ratio);
            int g = (int) Math.round(startG + (endG - startG) * ratio);
            int b = (int) Math.round(startB + (endB - startB) * ratio);

            // Formatear el color interpolado en formato hexadecimal
            String hexColor = String.format("#%02X%02X%02X", r, g, b);

            if (COLORIZE_METHOD != null) {
                try {
                    ChatColor color = (ChatColor) COLORIZE_METHOD.invoke(ChatColor.WHITE, hexColor);
                    result.append(color.toString());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    // En caso de error, se podría omitir el color o manejarlo de otra forma.
                }
            }

            // Add color character
            result.append(text.charAt(i));
        }
        return result.toString();
    }
}
