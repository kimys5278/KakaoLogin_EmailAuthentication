package com.springboot.kakaologintest.data.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import javax.persistence.*;
import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Entity
//@Data
@Setter
@Getter
public class User extends  BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long uid;

    @Column
    private String name;

    @Column
    private String nickname;

    @Column
    private String major;

    @Column
    private Long major_num;

    @Column
    private String gender;

    @Column(unique = true) // 중복을 방지하기 위해 unique 속성 추가
    private String email;

    @Column(unique = true) // 중복을 방지하기 위해 unique 속성 추가
    private Long kakaoId;

    @Column(unique = true) // 중복을 방지하기 위해 unique 속성 추가
    private String userRole;

    @Builder
    public User(Long uid, String name, String nickname, String major,
                Long major_num, String gender, String email, Long kakaoId,String userRole) {
        this.uid = uid;
        this.name = name;
        this.nickname = nickname;
        this.major = major;
        this.major_num = major_num;
        this.gender = gender;
        this.email = email;
        this.kakaoId = kakaoId;
        this.userRole = userRole;

    }

    public User() {

    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Member> appointmentList;

}
