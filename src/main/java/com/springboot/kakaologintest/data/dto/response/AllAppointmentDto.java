package com.springboot.kakaologintest.data.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AllAppointmentDto {
    private Long uid;
    private String title;
    private String maxPeople;
    private String minPeople;
    private int favoriteCount;
}
