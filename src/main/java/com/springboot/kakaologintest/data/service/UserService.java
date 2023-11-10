package com.springboot.kakaologintest.data.service;


import com.springboot.kakaologintest.data.dto.KakaoProfile;
import com.springboot.kakaologintest.data.dto.OauthToken;
import com.springboot.kakaologintest.data.entity.User;
import javax.servlet.http.HttpServletRequest;
import com.auth0.jwt.interfaces.DecodedJWT;

public interface UserService {
    OauthToken getAccessToken(String code);
    KakaoProfile findProfile(String token);
    String saveUserAndGetToken(String token);
    String createToken(User user);
    String getKakaoToken(String jwtToken);
    User getUser(HttpServletRequest request);
    DecodedJWT verifyToken(String token);
}
