package com.springboot.kakaologintest.data.service.Impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.springboot.kakaologintest.data.dto.request.UserDto;


import com.springboot.kakaologintest.data.dto.response.BaseResponseDto;
import com.springboot.kakaologintest.data.dto.response.UserResponseDto;
import com.springboot.kakaologintest.data.entity.EmailConfirmation;
import com.springboot.kakaologintest.data.entity.User;

import com.springboot.kakaologintest.data.repository.ConfirmationRepository;
import com.springboot.kakaologintest.data.repository.UserRepository;
import com.springboot.kakaologintest.data.service.UserService;
import com.springboot.kakaologintest.jwt.JwtProperties;
import com.springboot.kakaologintest.kakaoinfo.KakaoProfile;
import com.springboot.kakaologintest.kakaoinfo.OauthToken;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@Transactional
@Service
public class UserServiceImpl implements UserService  {
    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    @Value("${kakao.client.secret}")
    private String clientSecret;



    private UserRepository userRepository;
    private ConfirmationRepository confirmationRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,ConfirmationRepository confirmationRepository){
        this.userRepository = userRepository;
        this.confirmationRepository = confirmationRepository;
    }


    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    public OauthToken getAccessToken(String code){
        RestTemplate rt = new RestTemplate();

        rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret); // 생략 가능!

        HttpEntity<MultiValueMap<String,String>> kakaoTokenRequest =
                new HttpEntity<>(params,headers);
        //HttpEntity는 HTTP 요청에 필요한 데이터와 헤더 정보를 함께 묶어 표현하는 역할.

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        String responseBody = accessTokenResponse.getBody();
        //ObjectMapper는 리플렉션을 활용해서 객체로부터 Json 형태의 문자열을 만들어내는데,
        //이것을 직렬화(Serialize)라고 한다.
        //Gson,Json,Simple,ObjectMapper 등 여러가지 라이브러리가 있지만, 내가 원래 사용하던 ObjectMapper사용.
        ObjectMapper objectMapper = new ObjectMapper();

        OauthToken oauthToken = null;
        try{
            oauthToken = objectMapper.readValue(accessTokenResponse.getBody(),OauthToken.class);
        }catch(JsonMappingException e){
            e.printStackTrace();
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        System.out.println("카카오 AcessToken = "+oauthToken.getAccess_token());

        return oauthToken;

    }

    public KakaoProfile findProfile(String token) {
        RestTemplate rt2 = new RestTemplate();

        OauthToken oauthToken = null;

        HttpHeaders headers2 = new HttpHeaders();

        //Bearer: OAuth 2.0 토큰을 사용하는 인증 스킴의 일종
        //"Bearer" 다음에 오는 토큰 값은 서버가 클라이언트를 식별하고 인증.
        headers2.add("Authorization","Bearer "+ token);
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String,String>>kakaoProfileRequest =
                new HttpEntity<>(headers2);

        ResponseEntity<String> kakaoProfileReponse = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        String responseBody = kakaoProfileReponse.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileReponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(kakaoProfile);
        return kakaoProfile;
    }

    @Override
    public String saveUserAndGetToken(String token, HttpServletRequest request) {
        try {
            KakaoProfile profile = findProfile(token);

            request.getSession().setAttribute("kakaoId", profile.getId());

            User user = userRepository.findByKakaoId(profile.getId());
            if (user != null && user.getKakaoId() != null) {
                return createToken(user);
            } else {
                return "로그인 실패: 인가코드가 틀렸거나,신규 회원이 아니라면 회원가입을 하세요.";
            }
        } catch (Exception e) {
            // findProfile에서 발생한 예외 처리
            logger.error("로그인 실패: ", e);
            return "로그인 실패";
        }
    }

    public String createToken(User user){
        String jwtToken = JWT.create()
                //토큰이름 -> 유저이메일
                .withSubject(user.getEmail())
                //토큰 만료시간
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.EXPIRATION_TIME))

                //(2-5)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
        return jwtToken;
    }

    // UserService 클래스 안에 메소드 추가
    public String getKakaoToken(String jwtToken) {
        // JWT 토큰 검증
        DecodedJWT decodedJWT = verifyToken(jwtToken);

        // JWT 클레임에서 원하는 정보 추출
        // 예를 들어, "u_id" 클레임을 추출하는 경우
        Long uid = decodedJWT.getClaim("uid").asLong();

        // 추출한 정보를 문자열로 반환 (사용자 식별 정보 또는 다른 필요한 정보에 따라 변경)
        return uid.toString();
    }



    public DecodedJWT verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(JwtProperties.SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            return verifier.verify(token);
        } catch (Exception exception){
            // 유효하지 않은 토큰인 경우
            throw new RuntimeException("Invalid token");
        }
    }

    @Override
    public String getUsername(String token){
        logger.info("[getUsername] 토큰 기반 회원 구별 정보 추출");
        String info = Jwts.parser().setSigningKey(JwtProperties.SECRET.getBytes()).parseClaimsJws(token).getBody()
                .getSubject();
        logger.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료, info: {}", info);
        return info;
    }


    @Override
    public UserResponseDto getUsers(Long id) {
        User user = userRepository.findById(id).get();
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getUid());
        userResponseDto.setName(user.getName());
        userResponseDto.setNickname(user.getNickname());
        userResponseDto.setMajor(user.getMajor());
        userResponseDto.setMajor_num(user.getMajor_num());
        userResponseDto.setGender(user.getGender());

        return userResponseDto;
    }

    // 카카오 ID를 세션에 저장하는 메서드
//    @Override
//    public void saveKakaoIdInSession(String token, HttpServletRequest request) {
//        KakaoProfile profile = findProfile(token);
//        request.getSession().setAttribute("kakaoId", profile.getId());
//
//    }

    // 회원 정보 입력 및 카카오 ID를 세션에서 가져오는 메서드
    @Override
    public String saveUserWithSessionInfo(UserDto userDto, HttpServletRequest request) {

        Long kakaoId = (Long) request.getSession().getAttribute("kakaoId");
        User user = userRepository.findByKakaoId(kakaoId);

        EmailConfirmation confirmation = confirmationRepository.findByKakaoId(kakaoId);

        if (user == null) {
            user = new User();
            user.setKakaoId(kakaoId);
            if (confirmation != null) {
                user.setEmail(confirmation.getPersonalEmail());
            }
            user.setName(userDto.getName());
            user.setMajor(userDto.getMajor());
            user.setMajor_num(userDto.getMajor_num());
            user.setGender(userDto.getGender());
            user.setNickname(userDto.getNickname());
            user.setUserRole("USER");

            userRepository.save(user);
            return "회원가입 성공";
        } else {
            return "화원 정보를 정확히 기입해 주세요.";
        }

    }



}