package com.springboot.kakaologintest.controller;

import com.springboot.kakaologintest.data.dto.request.UserDto;

import com.springboot.kakaologintest.data.dto.response.UserResponseDto;
import com.springboot.kakaologintest.data.service.Impl.UserServiceImpl;
import com.springboot.kakaologintest.kakaoinfo.KakaoProfile;
import com.springboot.kakaologintest.kakaoinfo.OauthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserServiceImpl userService;

    // 프론트에서 인가코드 받아오는 url
    @GetMapping("/oauth/token/login")
    public ResponseEntity<?> getLogin(@RequestParam("code") String code,HttpServletRequest request  ) {

        OauthToken oauthToken = userService.getAccessToken(code);

        String jwtToken = userService.saveUserAndGetToken(oauthToken.getAccess_token(),request);

//      User user = userService.saveUser(oauthToken.getAccess_token());
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

        return ResponseEntity.ok(jwtToken);

    }

//     카카오 인가 코드 처리 및 카카오 ID 세션에 저장
//    @GetMapping("/oauth/callback")
//    public ResponseEntity<?> kakaoLoginCallback(@RequestParam String code, HttpServletRequest request) {
//        OauthToken oauthToken = userService.getAccessToken(code);
//        userService.saveKakaoIdInSession(oauthToken.getAccess_token(), request);
//
//        // 다른 처리 필요에 따라 추가 (예: 프론트엔드 페이지로 리다이렉트)
//        return ResponseEntity.ok().build();
//    }

    // 회원 정보 입력 및 JWT 토큰 발급
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto, HttpServletRequest request) {
        String jwtToken = userService.saveUserWithSessionInfo(userDto, request);
        return ResponseEntity.ok(jwtToken);
    }

    @GetMapping("/test")
    public ResponseEntity<?> getInfo(String token){
        String email = userService.getUsername(token);
        return ResponseEntity.ok(email);
    }

    @GetMapping("/user/profile")
    public ResponseEntity<KakaoProfile> getUserProfiles(@RequestParam("code") String code, HttpServletRequest request) {
        KakaoProfile kakaoProfile = userService.findProfile(code);
        return ResponseEntity.ok().body(kakaoProfile);
    }


    @GetMapping()
    public ResponseEntity<UserResponseDto> getUser(Long uid){
        UserResponseDto userResponseDto = userService.getUsers(uid);

        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);

    }

}