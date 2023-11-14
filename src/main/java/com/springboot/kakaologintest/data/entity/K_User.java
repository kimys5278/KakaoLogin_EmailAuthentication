package com.springboot.kakaologintest.data.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class K_User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long K_id;

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

}
