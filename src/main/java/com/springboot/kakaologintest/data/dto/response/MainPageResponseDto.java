package com.springboot.kakaologintest.data.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MainPageResponseDto {
    private Long uid;
    private String title;
}
