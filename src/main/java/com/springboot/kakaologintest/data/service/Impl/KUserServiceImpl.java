package com.springboot.kakaologintest.data.service.Impl;

import com.springboot.kakaologintest.data.dto.KUserDto;
import com.springboot.kakaologintest.data.dto.KUserResponseDto;
import com.springboot.kakaologintest.data.entity.K_User;

import com.springboot.kakaologintest.data.repository.KUserRepository;
import com.springboot.kakaologintest.data.service.KUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KUserServiceImpl implements KUserService {
    @Autowired
    KUserRepository kUserRepository;
    @Override
    public KUserResponseDto getKUser(Long K_id) {
        K_User kUser = kUserRepository.findById(K_id).get();
        KUserResponseDto kUserResponseDto = new KUserResponseDto();
        kUserResponseDto.setK_id(kUser.getK_id());
        kUserResponseDto.setName(kUser.getName());
        kUserResponseDto.setNickname(kUser.getNickname());
        kUserResponseDto.setMajor(kUser.getMajor());
        kUserResponseDto.setMajor_num(kUser.getMajor_num());
        kUserResponseDto.setGender(kUser.getGender());

        return kUserResponseDto;
    }
    @Override
    public KUserResponseDto saveK_User(KUserDto kUserDto){
        K_User kuser = new K_User();
        kuser.setName(kUserDto.getName());
        kuser.setNickname(kUserDto.getNickname());
        kuser.setMajor(kUserDto.getMajor());
        kuser.setMajor_num(kUserDto.getMajor_num());
        kuser.setGender(kUserDto.getGender());

        K_User savedK_User = kUserRepository.save(kuser);
        KUserResponseDto kUserResponseDto = new KUserResponseDto();
        kUserResponseDto.setName(savedK_User.getName());
        kUserResponseDto.setNickname(savedK_User.getNickname());
        kUserResponseDto.setMajor(savedK_User.getMajor());
        kUserResponseDto.setMajor_num(savedK_User.getMajor_num());
        kUserResponseDto.setGender(savedK_User.getGender());

        return kUserResponseDto;
    }
}
