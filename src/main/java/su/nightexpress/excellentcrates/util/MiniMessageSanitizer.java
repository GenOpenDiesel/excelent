package su.nightexpress.excellentcrates.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class MiniMessageSanitizer {

    private static final Map<Character, String> LEGACY_TAGS = Map.ofEntries(
        Map.entry('0', "black"),
        Map.entry('1', "dark_blue"),
        Map.entry('2', "dark_green"),
        Map.entry('3', "dark_aqua"),
        Map.entry('4', "dark_red"),
        Map.entry('5', "dark_purple"),
        Map.entry('6', "gold"),
        Map.entry('7', "gray"),
        Map.entry('8', "dark_gray"),
        Map.entry('9', "blue"),
        Map.entry('a', "green"),
        Map.entry('b', "aqua"),
        Map.entry('c', "red"),
        Map.entry('d', "light_purple"),
        Map.entry('e', "yellow"),
        Map.entry('f', "white"),
        Map.entry('k', "obfuscated"),
        Map.entry('l', "bold"),
        Map.entry('m', "strikethrough"),
        Map.entry('n', "underlined"),
        Map.entry('o', "italic"),
        Map.entry('r', "reset")
    );

    private MiniMessageSanitizer() {
    }

    @NotNull
    public static String sanitize(@NotNull String text) {
        return escapeMalformedTags(convertLegacyCodes(text));
    }

    @NotNull
    public static List<String> sanitize(@NotNull List<String> text) {
        return text.stream().map(MiniMessageSanitizer::sanitize).toList();
    }

    @NotNull
    private static String convertLegacyCodes(@NotNull String text) {
        StringBuilder converted = new StringBuilder(text.length());

        for (int index = 0; index < text.length(); index++) {
            char current = text.charAt(index);
            if ((current != '&' && current != '§') || index + 1 >= text.length()) {
                converted.append(current);
                continue;
            }

            char next = Character.toLowerCase(text.charAt(index + 1));
            String rgb = readLegacyRgb(text, index);
            if (rgb != null) {
                converted.append("<#").append(rgb).append(">");
                index += current == '&' && next == '#' ? 7 : 13;
                continue;
            }

            String tag = LEGACY_TAGS.get(next);
            if (tag != null) {
                converted.append('<').append(tag).append('>');
                index++;
                continue;
            }

            converted.append(current);
        }

        return converted.toString();
    }

    private static String readLegacyRgb(@NotNull String text, int index) {
        char marker = text.charAt(index);
        char next = Character.toLowerCase(text.charAt(index + 1));

        if (marker == '&' && next == '#' && index + 7 < text.length()) {
            String hex = text.substring(index + 2, index + 8);
            return isHex(hex) ? hex : null;
        }

        if (next != 'x' || index + 13 >= text.length()) {
            return null;
        }

        StringBuilder hex = new StringBuilder(6);
        for (int offset = index + 2; offset <= index + 12; offset += 2) {
            if (text.charAt(offset) != marker) {
                return null;
            }
            hex.append(text.charAt(offset + 1));
        }

        String hexText = hex.toString();
        return isHex(hexText) ? hexText : null;
    }

    @NotNull
    private static String escapeMalformedTags(@NotNull String text) {
        StringBuilder escaped = new StringBuilder(text.length());

        for (int index = 0; index < text.length(); index++) {
            char current = text.charAt(index);
            if (current == '<') {
                int end = text.indexOf('>', index + 1);
                if (end < 0) {
                    escaped.append("\\<");
                    continue;
                }

                String content = text.substring(index + 1, end);
                if (!isPlausibleTag(content)) {
                    escaped.append("\\<").append(content).append("\\>");
                }
                else {
                    escaped.append('<').append(content).append('>');
                }
                index = end;
                continue;
            }

            if (current == '>') {
                escaped.append("\\>");
                continue;
            }

            escaped.append(current);
        }

        return escaped.toString();
    }

    private static boolean isPlausibleTag(@NotNull String content) {
        String tag = content.trim();
        if (tag.isEmpty()) {
            return false;
        }

        if (tag.charAt(0) == '/') {
            tag = tag.substring(1);
        }

        if (tag.startsWith("#")) {
            return tag.length() == 7 && isHex(tag.substring(1));
        }

        return tag.matches("[A-Za-z][A-Za-z0-9_-]*(?::[^<>]*)?");
    }

    private static boolean isHex(@NotNull String text) {
        return text.matches("[0-9a-fA-F]{6}");
    }
}
