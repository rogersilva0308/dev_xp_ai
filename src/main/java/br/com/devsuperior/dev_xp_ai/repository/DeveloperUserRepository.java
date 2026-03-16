package br.com.devsuperior.dev_xp_ai.repository;

import br.com.devsuperior.dev_xp_ai.entity.DeveloperUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeveloperUserRepository extends JpaRepository<DeveloperUserEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNicknameIgnoreCase(String nickname);

    List<DeveloperUserEntity> findAllByOrderById();

    List<DeveloperUserEntity> findAllByUfOrderById(String uf);

    List<DeveloperUserEntity> findAllByExperiencePrimaryLanguageIgnoreCaseOrderById(String primaryLanguage);

    List<DeveloperUserEntity> findAllByUfAndExperiencePrimaryLanguageIgnoreCaseOrderById(String uf, String primaryLanguage);
}

