package com.springboot.kakaologintest.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDto {
    private Long pid;
    private String maxPeople;
    private String minPeople;
    private String title;
    private String latitude;
    private String longitude;
    private String time;
    private String expiredDate;
    private String description;
    private String mission;
    private Boolean approved;
}
