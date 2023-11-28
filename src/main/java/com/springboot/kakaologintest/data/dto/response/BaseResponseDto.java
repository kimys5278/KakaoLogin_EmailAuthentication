package com.springboot.kakaologintest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BaseResponseDto {
    private boolean success;
    private String msg;
}
