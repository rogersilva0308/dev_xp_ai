package br.com.devsuperior.dev_xp_ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "tb_developer_experience")
public class DeveloperExperienceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id", nullable = false, unique = true)
    private DeveloperUserEntity user;

    @Column(name = "years_of_experience", nullable = false)
    private Integer yearsOfExperience;

    @Column(name = "primary_language", nullable = false, length = 50)
    private String primaryLanguage;

    @Column(name = "interested_in_ai", nullable = false)
    private Boolean interestedInAi;

    @Convert(converter = SkillsConverter.class)
    @Column(name = "skills", nullable = false, length = 300)
    private List<String> skills;

    public DeveloperExperienceEntity() {
    }

    public DeveloperExperienceEntity(
            DeveloperUserEntity user,
            Integer yearsOfExperience,
            String primaryLanguage,
            Boolean interestedInAi,
            List<String> skills
    ) {
        this.user = user;
        this.yearsOfExperience = yearsOfExperience;
        this.primaryLanguage = primaryLanguage;
        this.interestedInAi = interestedInAi;
        this.skills = skills;
    }

    public Long getId() { return id; }

    public DeveloperUserEntity getUser() { return user; }
    public void setUser(DeveloperUserEntity user) { this.user = user; }

    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getPrimaryLanguage() { return primaryLanguage; }
    public void setPrimaryLanguage(String primaryLanguage) { this.primaryLanguage = primaryLanguage; }

    public Boolean getInterestedInAi() { return interestedInAi; }
    public void setInterestedInAi(Boolean interestedInAi) { this.interestedInAi = interestedInAi; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}

