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

    @Column(name = "kakao_nickname")
    private String nickname;

    @Column(name = "kakao_email")
    private String email;

    @Column(name = "user_nickname")
    public String user_nickname;

    @Column(name = "major_num")
    public Long major_num;

    @Column(name = "phone_number")
    public Long phone_number;

    @Column(name = "gender")
    public String gender;

    @Column(name = "user_role")
    private String UserRole;

    @Column(name = "major")
    private String major;
    @Builder
    public User(Long uid,Long id, String nickname,String email,String gender, String userRole,String major,Long major_num, Long phone_number
                ,String user_nickname) {
        this.uid = uid;
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.UserRole = userRole;
        this.major = major;
        this.major_num = major_num;
        this.phone_number = phone_number;
        this.user_nickname = user_nickname;
    }

    public User() {

    }
}
