package br.com.devsuperior.dev_xp_ai.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class StringNormalizerUtil {

    private StringNormalizerUtil() {
    }

    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static String toTitleCase(String text) {
        if (!hasText(text)) {
            return "";
        }
        String normalized = text.trim().replaceAll("\\s+", " ");
        return Arrays.stream(normalized.split(" "))
                .map(StringNormalizerUtil::capitalizeWord)
                .collect(Collectors.joining(" "));
    }

    public static String toLowerCaseTrimmed(String text) {
        return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
    }

    public static String toUpperCaseTrimmed(String text) {
        return text == null ? "" : text.trim().toUpperCase(Locale.ROOT);
    }

    public static List<String> normalizeSkillsToTitleCase(List<String> skills) {
        if (skills == null) {
            return List.of();
        }
        return skills.stream()
                .map(StringNormalizerUtil::toTitleCase)
                .toList();
    }

    private static String capitalizeWord(String word) {
        String lower = word.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}

