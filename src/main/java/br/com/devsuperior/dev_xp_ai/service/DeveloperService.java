package br.com.devsuperior.dev_xp_ai.service;

import br.com.devsuperior.dev_xp_ai.dto.DeveloperCreateRequestDto;
import br.com.devsuperior.dev_xp_ai.dto.DeveloperResponseDto;
import br.com.devsuperior.dev_xp_ai.dto.UpdateExperienceRequestDto;
import br.com.devsuperior.dev_xp_ai.entity.DeveloperEntity;
import br.com.devsuperior.dev_xp_ai.exception.BadRequestException;
import br.com.devsuperior.dev_xp_ai.exception.ConflictException;
import br.com.devsuperior.dev_xp_ai.exception.NotFoundException;
import br.com.devsuperior.dev_xp_ai.repository.DeveloperRepository;
import br.com.devsuperior.dev_xp_ai.util.StringNormalizerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class DeveloperService {

    private static final Logger log = LoggerFactory.getLogger(DeveloperService.class);

    private static final Pattern SIMPLE_EMAIL_PATTERN =
            Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._-]{3,30}$");
    private static final Set<String> VALID_UFS = Set.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT",
            "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO",
            "RR", "SC", "SP", "SE", "TO"
    );

    private final DeveloperRepository developerRepository;

    public DeveloperService(DeveloperRepository developerRepository) {
        this.developerRepository = developerRepository;
    }

    @Transactional
    public DeveloperResponseDto createDeveloper(DeveloperCreateRequestDto request) {
        log.info("createDeveloper - start | correlationId={}", MDC.get("correlationId"));

        List<String> validationErrors = validateCreateRequest(request);
        if (!validationErrors.isEmpty()) {
            throw new BadRequestException("Falha de validacao", validationErrors);
        }

        String normalizedEmail = StringNormalizerUtil.toLowerCaseTrimmed(request.email());
        String normalizedNickname = StringNormalizerUtil.toLowerCaseTrimmed(request.nickname());
        String normalizedUf = StringNormalizerUtil.toUpperCaseTrimmed(request.uf());
        String normalizedLanguage = StringNormalizerUtil.toTitleCase(request.primaryLanguage());
        String normalizedName = StringNormalizerUtil.toTitleCase(request.fullName());
        List<String> normalizedSkills = StringNormalizerUtil.normalizeSkillsToTitleCase(request.skills());

        if (developerRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("Email ja cadastrado",
                    List.of("Ja existe um desenvolvedor com este email."));
        }

        if (developerRepository.existsByNicknameIgnoreCase(normalizedNickname)) {
            throw new ConflictException("Nickname ja cadastrado",
                    List.of("Ja existe um desenvolvedor com este nickname."));
        }

        DeveloperEntity entity = new DeveloperEntity(
                normalizedName,
                normalizedEmail,
                normalizedNickname,
                normalizedUf,
                request.yearsOfExperience(),
                normalizedLanguage,
                request.interestedInAi(),
                normalizedSkills
        );

        DeveloperEntity saved = developerRepository.save(entity);
        DeveloperResponseDto response = toResponseDto(saved);

        log.info("createDeveloper - end | correlationId={} | id={}", MDC.get("correlationId"), saved.getId());
        return response;
    }

    @Transactional(readOnly = true)
    public List<DeveloperResponseDto> listDevelopers(String uf, String language) {
        log.info("listDevelopers - start | correlationId={} | uf={} | language={}",
                MDC.get("correlationId"), uf, language);

        boolean hasUf = StringNormalizerUtil.hasText(uf);
        boolean hasLanguage = StringNormalizerUtil.hasText(language);

        if (hasUf) {
            String normalizedUf = StringNormalizerUtil.toUpperCaseTrimmed(uf);
            if (!VALID_UFS.contains(normalizedUf)) {
                throw new BadRequestException("Filtro invalido",
                        List.of("O filtro UF deve ser uma sigla de estado brasileira valida."));
            }
            uf = normalizedUf;
        }

        List<DeveloperEntity> entities;
        if (hasUf && hasLanguage) {
            entities = developerRepository.findAllByUfAndPrimaryLanguageIgnoreCaseOrderById(
                    uf, language.trim());
        } else if (hasUf) {
            entities = developerRepository.findAllByUfOrderById(uf);
        } else if (hasLanguage) {
            entities = developerRepository.findAllByPrimaryLanguageIgnoreCaseOrderById(language.trim());
        } else {
            entities = developerRepository.findAllByOrderById();
        }

        List<DeveloperResponseDto> response = entities.stream().map(this::toResponseDto).toList();

        log.info("listDevelopers - end | correlationId={} | count={}",
                MDC.get("correlationId"), response.size());
        return response;
    }

    @Transactional(readOnly = true)
    public DeveloperResponseDto getDeveloperById(Long id) {
        log.info("getDeveloperById - start | correlationId={} | id={}", MDC.get("correlationId"), id);

        DeveloperEntity entity = developerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Desenvolvedor nao encontrado",
                        List.of("Nao existe desenvolvedor com id " + id + ".")));

        DeveloperResponseDto response = toResponseDto(entity);
        log.info("getDeveloperById - end | correlationId={} | id={}", MDC.get("correlationId"), id);
        return response;
    }

    @Transactional
    public DeveloperResponseDto updateExperience(Long id, UpdateExperienceRequestDto request) {
        log.info("updateExperience - start | correlationId={} | id={}", MDC.get("correlationId"), id);

        List<String> validationErrors = validateExperienceUpdate(request);
        if (!validationErrors.isEmpty()) {
            throw new BadRequestException("Falha de validacao", validationErrors);
        }

        DeveloperEntity entity = developerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Desenvolvedor nao encontrado",
                        List.of("Nao existe desenvolvedor com id " + id + ".")));

        entity.setYearsOfExperience(request.yearsOfExperience());
        DeveloperEntity saved = developerRepository.save(entity);
        DeveloperResponseDto response = toResponseDto(saved);

        log.info("updateExperience - end | correlationId={} | id={}", MDC.get("correlationId"), id);
        return response;
    }

    private List<String> validateCreateRequest(DeveloperCreateRequestDto request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("O corpo da requisicao e obrigatorio.");
            return errors;
        }
        if (!StringNormalizerUtil.hasText(request.fullName())
                || request.fullName().trim().length() < 5
                || request.fullName().trim().length() > 120) {
            errors.add("fullName deve ter entre 5 e 120 caracteres.");
        }
        if (!StringNormalizerUtil.hasText(request.email())
                || !SIMPLE_EMAIL_PATTERN.matcher(request.email().trim()).matches()) {
            errors.add("email deve ter um formato valido.");
        }
        if (!StringNormalizerUtil.hasText(request.nickname())
                || !NICKNAME_PATTERN.matcher(request.nickname().trim()).matches()) {
            errors.add("nickname deve ter de 3 a 30 caracteres e usar apenas letras, numeros, ponto, underscore ou hifen.");
        }
        if (!StringNormalizerUtil.hasText(request.uf())
                || !VALID_UFS.contains(request.uf().trim().toUpperCase(Locale.ROOT))) {
            errors.add("uf deve ser uma sigla de estado brasileira valida.");
        }
        if (request.yearsOfExperience() == null
                || request.yearsOfExperience() < 0
                || request.yearsOfExperience() > 60) {
            errors.add("yearsOfExperience deve estar entre 0 e 60.");
        }
        if (!StringNormalizerUtil.hasText(request.primaryLanguage())
                || request.primaryLanguage().trim().length() > 50) {
            errors.add("primaryLanguage e obrigatorio e deve ter no maximo 50 caracteres.");
        }
        if (request.interestedInAi() == null) {
            errors.add("interestedInAi e obrigatorio.");
        }
        if (request.skills() == null || request.skills().isEmpty()) {
            errors.add("skills deve conter ao menos um item.");
        } else {
            if (request.skills().size() > 10) {
                errors.add("skills pode conter no maximo 10 itens.");
            }
            for (String skill : request.skills()) {
                if (!StringNormalizerUtil.hasText(skill)
                        || skill.trim().length() < 2
                        || skill.trim().length() > 30) {
                    errors.add("cada skill deve ter entre 2 e 30 caracteres.");
                    break;
                }
            }
        }
        return errors;
    }

    private List<String> validateExperienceUpdate(UpdateExperienceRequestDto request) {
        List<String> errors = new ArrayList<>();
        if (request == null || request.yearsOfExperience() == null) {
            errors.add("yearsOfExperience e obrigatorio.");
            return errors;
        }
        if (request.yearsOfExperience() < 0 || request.yearsOfExperience() > 60) {
            errors.add("yearsOfExperience deve estar entre 0 e 60.");
        }
        return errors;
    }

    private DeveloperResponseDto toResponseDto(DeveloperEntity entity) {
        return new DeveloperResponseDto(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getUf(),
                entity.getYearsOfExperience(),
                entity.getPrimaryLanguage(),
                entity.getInterestedInAi(),
                entity.getSkills()
        );
    }
}

