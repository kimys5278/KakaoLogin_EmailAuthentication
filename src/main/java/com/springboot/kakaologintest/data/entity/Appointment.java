package com.springboot.kakaologintest.data.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="appointment")
public class Appointment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    @Column(nullable = false)
    private Long pid;
    @Column(nullable = false)
    private String maxPeople;
    @Column(nullable = false)
    private String minPeople;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String latitude; // 경도
    @Column(nullable = false)
    private String longitude; // 위도
    @Column(nullable = false)
    private String time;
    @Column(nullable = false)
    private String expiredDate;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Boolean approved;
    @Column(nullable = true)
    private String mission;
    @Column
    @Builder.Default
    private int viewCount=0;
    @Column
    @Builder.Default
    private int favoriteCount=0;
    @Column
    private String collegeCategory;

    @OneToMany(mappedBy = "appointment",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Member> members;
}
