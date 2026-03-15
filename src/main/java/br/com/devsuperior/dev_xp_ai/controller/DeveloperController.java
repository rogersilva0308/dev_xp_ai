package br.com.devsuperior.dev_xp_ai.controller;

import br.com.devsuperior.dev_xp_ai.dto.DeveloperCreateRequestDto;
import br.com.devsuperior.dev_xp_ai.dto.DeveloperResponseDto;
import br.com.devsuperior.dev_xp_ai.dto.ErrorResponseDto;
import br.com.devsuperior.dev_xp_ai.dto.UpdateExperienceRequestDto;
import br.com.devsuperior.dev_xp_ai.exception.BadRequestException;
import br.com.devsuperior.dev_xp_ai.exception.ConflictException;
import br.com.devsuperior.dev_xp_ai.exception.NotFoundException;
import br.com.devsuperior.dev_xp_ai.service.DeveloperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    private static final Logger log = LoggerFactory.getLogger(DeveloperController.class);

    private final DeveloperService developerService;

    public DeveloperController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @PostMapping
    public ResponseEntity<?> createDeveloper(
            @RequestHeader("correlationId") UUID correlationId,
            @RequestBody DeveloperCreateRequestDto request
    ) {
        MDC.put("correlationId", correlationId.toString());
        log.info("createDeveloper - start | correlationId={}", correlationId);
        try {
            DeveloperResponseDto response = developerService.createDeveloper(request);
            log.info("createDeveloper - end | correlationId={} | id={}", correlationId, response.id());
            return ResponseEntity
                    .created(URI.create("/developers/" + response.id()))
                    .body(response);
        } catch (BadRequestException ex) {
            return badRequest(ex.getMessage(), ex.getDetails());
        } catch (ConflictException ex) {
            return conflict(ex.getMessage(), ex.getDetails());
        } finally {
            MDC.clear();
        }
    }

    @GetMapping
    public ResponseEntity<?> listDevelopers(
            @RequestHeader("correlationId") UUID correlationId,
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String language
    ) {
        MDC.put("correlationId", correlationId.toString());
        log.info("listDevelopers - start | correlationId={}", correlationId);
        try {
            List<DeveloperResponseDto> response = developerService.listDevelopers(uf, language);
            log.info("listDevelopers - end | correlationId={}", correlationId);
            return ResponseEntity.ok(response);
        } catch (BadRequestException ex) {
            return badRequest(ex.getMessage(), ex.getDetails());
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDeveloperById(
            @RequestHeader("correlationId") UUID correlationId,
            @PathVariable Long id
    ) {
        MDC.put("correlationId", correlationId.toString());
        log.info("getDeveloperById - start | correlationId={} | id={}", correlationId, id);
        try {
            DeveloperResponseDto response = developerService.getDeveloperById(id);
            log.info("getDeveloperById - end | correlationId={} | id={}", correlationId, id);
            return ResponseEntity.ok(response);
        } catch (NotFoundException ex) {
            return notFound(ex.getMessage(), ex.getDetails());
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{id}/experience")
    public ResponseEntity<?> updateExperience(
            @RequestHeader("correlationId") UUID correlationId,
            @PathVariable Long id,
            @RequestBody UpdateExperienceRequestDto request
    ) {
        MDC.put("correlationId", correlationId.toString());
        log.info("updateExperience - start | correlationId={} | id={}", correlationId, id);
        try {
            DeveloperResponseDto response = developerService.updateExperience(id, request);
            log.info("updateExperience - end | correlationId={} | id={}", correlationId, id);
            return ResponseEntity.ok(response);
        } catch (BadRequestException ex) {
            return badRequest(ex.getMessage(), ex.getDetails());
        } catch (NotFoundException ex) {
            return notFound(ex.getMessage(), ex.getDetails());
        } finally {
            MDC.clear();
        }
    }

    private ResponseEntity<ErrorResponseDto> badRequest(String message, List<String> details) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(message, details));
    }

    private ResponseEntity<ErrorResponseDto> conflict(String message, List<String> details) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(message, details));
    }

    private ResponseEntity<ErrorResponseDto> notFound(String message, List<String> details) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(message, details));
    }
}

