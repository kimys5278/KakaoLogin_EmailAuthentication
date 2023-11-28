package com.springboot.kakaologintest.data.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TopFiveAppointmentResponseDto {
    private Long uid;
    private String userName;
    private String university;
    private int favoriteCount;
}
