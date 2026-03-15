package br.com.devsuperior.dev_xp_ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "tb_developer")
public class DeveloperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "nickname", nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(name = "uf", nullable = false, length = 2)
    private String uf;

    @Column(name = "years_of_experience", nullable = false)
    private Integer yearsOfExperience;

    @Column(name = "primary_language", nullable = false, length = 50)
    private String primaryLanguage;

    @Column(name = "interested_in_ai", nullable = false)
    private Boolean interestedInAi;

    @Convert(converter = SkillsConverter.class)
    @Column(name = "skills", nullable = false, length = 300)
    private List<String> skills;

    public DeveloperEntity() {
    }

    public DeveloperEntity(
            String fullName,
            String email,
            String nickname,
            String uf,
            Integer yearsOfExperience,
            String primaryLanguage,
            Boolean interestedInAi,
            List<String> skills
    ) {
        this.fullName = fullName;
        this.email = email;
        this.nickname = nickname;
        this.uf = uf;
        this.yearsOfExperience = yearsOfExperience;
        this.primaryLanguage = primaryLanguage;
        this.interestedInAi = interestedInAi;
        this.skills = skills;
    }

    public Long getId() { return id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getPrimaryLanguage() { return primaryLanguage; }
    public void setPrimaryLanguage(String primaryLanguage) { this.primaryLanguage = primaryLanguage; }

    public Boolean getInterestedInAi() { return interestedInAi; }
    public void setInterestedInAi(Boolean interestedInAi) { this.interestedInAi = interestedInAi; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}

