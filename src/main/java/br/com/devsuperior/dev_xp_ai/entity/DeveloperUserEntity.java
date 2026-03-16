package br.com.devsuperior.dev_xp_ai.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_developer_user")
public class DeveloperUserEntity {

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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = false)
    private DeveloperExperienceEntity experience;

    public DeveloperUserEntity() {
    }

    public DeveloperUserEntity(String fullName, String email, String nickname, String uf) {
        this.fullName = fullName;
        this.email = email;
        this.nickname = nickname;
        this.uf = uf;
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

    public DeveloperExperienceEntity getExperience() { return experience; }
    public void setExperience(DeveloperExperienceEntity experience) { this.experience = experience; }
}

