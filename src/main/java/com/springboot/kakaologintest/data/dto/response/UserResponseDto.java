package com.springboot.kakaologintest.data.dto.response;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;

    private String name;

    private String nickname;

    private String major;

    private Long major_num;

    private String gender;

}
