package com.springboot.kakaologintest.data.service;


import com.springboot.kakaologintest.data.dto.KUserDto;
import com.springboot.kakaologintest.data.dto.KUserResponseDto;

public interface KUserService {
    KUserResponseDto getKUser(Long K_id);
    KUserResponseDto saveK_User (KUserDto kUserDto);

}
