package com.springboot.kakaologintest.data.dto.request;

import lombok.Data;

@Data
public class UserDto {
    private String name;

    private String nickname;

    private String major;

    private Long major_num;

    private String gender;
}
