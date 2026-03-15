package br.com.devsuperior.dev_xp_ai.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class SkillsConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return "";
        }
        return skills.stream()
                .map(String::trim)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<String> convertToEntityAttribute(String csv) {
        if (csv == null || csv.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}

