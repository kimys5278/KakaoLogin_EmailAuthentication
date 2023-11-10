package com.springboot.kakaologintest.controller;

import com.springboot.kakaologintest.data.dto.KakaoProfile;
import com.springboot.kakaologintest.data.dto.OauthToken;
import com.springboot.kakaologintest.data.service.Impl.UserServiceImpl;
import com.springboot.kakaologintest.jwt.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserServiceImpl userService;

    // 프론트에서 인가코드 받아오는 url
    @GetMapping("/oauth/token")
    public ResponseEntity getLogin(@RequestParam("code") String code) {

        OauthToken oauthToken = userService.getAccessToken(code);

        String jwtToken = userService.saveUserAndGetToken(oauthToken.getAccess_token());
//      User user = userService.saveUser(oauthToken.getAccess_token());

        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

        return ResponseEntity.ok().headers(headers).body(oauthToken);

    }

    @GetMapping("/user/profile")
    public ResponseEntity<KakaoProfile> getUserProfiles(@RequestParam("code") String code,HttpServletRequest request) {
        KakaoProfile kakaoProfile = userService.findProfile(code);
        return ResponseEntity.ok().body(kakaoProfile);
    }

}