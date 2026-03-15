package br.com.devsuperior.dev_xp_ai.dto;

import java.util.List;

public record ErrorResponseDto(String message, List<String> details) {
}

