package br.com.devsuperior.dev_xp_ai.repository;

import br.com.devsuperior.dev_xp_ai.entity.DeveloperEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeveloperRepository extends JpaRepository<DeveloperEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNicknameIgnoreCase(String nickname);

    List<DeveloperEntity> findAllByOrderById();

    List<DeveloperEntity> findAllByUfOrderById(String uf);

    List<DeveloperEntity> findAllByPrimaryLanguageIgnoreCaseOrderById(String primaryLanguage);

    List<DeveloperEntity> findAllByUfAndPrimaryLanguageIgnoreCaseOrderById(String uf, String primaryLanguage);
}

