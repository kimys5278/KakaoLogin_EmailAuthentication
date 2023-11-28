package com.springboot.kakaologintest.data.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Review extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private String reviewPost;

    @Column
    private String imgUrl;

    @Column
    private boolean isLike;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) /*해당 약속*/
    @JoinColumn
    private Appointment appointmentId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) /*작성자*/
    @JoinColumn
    private User writer;
}
