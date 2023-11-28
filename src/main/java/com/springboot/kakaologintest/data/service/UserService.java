package com.springboot.kakaologintest.data.service;


import com.springboot.kakaologintest.data.dto.request.UserDto;

import com.springboot.kakaologintest.data.dto.response.UserResponseDto;
import com.springboot.kakaologintest.data.entity.User;
import javax.servlet.http.HttpServletRequest;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.springboot.kakaologintest.kakaoinfo.KakaoProfile;
import com.springboot.kakaologintest.kakaoinfo.OauthToken;

public interface UserService {
    OauthToken getAccessToken(String code);
    KakaoProfile findProfile(String token);
//    String saveUserAndGetToken(String token);
    String createToken(User user);
    String getUsername(String token);
    DecodedJWT verifyToken(String token);
    String getKakaoToken(String jwtToken);
    UserResponseDto getUsers(Long id);
    String saveUserAndGetToken(String token,HttpServletRequest request);

    String saveUserWithSessionInfo(UserDto userDto, HttpServletRequest request);

}
