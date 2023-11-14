package com.springboot.kakaologintest.data.dto;

import lombok.Data;

@Data
public class KUserDto {
    private Long K_id;

    private String name;

    private String nickname;

    private String major;

    private Long major_num;

    private String gender;
}
