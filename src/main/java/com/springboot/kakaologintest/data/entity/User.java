package com.springboot.kakaologintest.data.entity;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import javax.persistence.*;
import java.security.Timestamp;


@Entity
@Data
@Setter
@Getter

public class User extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="uid")
    private Long uid;

    @Column(name = "id")
    private Long id;

    @Column(name = "kakao_email")
    private String email;

    @Column(name = "user_nickname")
    public String nickname;

    @Column(name = "user_role")
    private String UserRole;

    @Column
    private String gender;

    @Column(name = "personal_email")
    private String personalEmail;

    @Builder
    public User(Long uid,Long id, String nickname,String email
            ,String gender, String userRole,String personalEmail) {
        this.uid = uid;
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.UserRole = userRole;
        this.personalEmail = personalEmail;
    }

    public User() {

    }
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private EmailConfirmation emailConfirmation;
}
