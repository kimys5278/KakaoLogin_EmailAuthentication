package com.springboot.kakaologintest.data.service.Impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.kakaologintest.data.dto.KakaoProfile;
import com.springboot.kakaologintest.data.dto.OauthToken;
import com.springboot.kakaologintest.data.entity.EmailConfirmation;
import com.springboot.kakaologintest.data.entity.User;
import com.springboot.kakaologintest.data.repository.ConfirmationRepository;
import com.springboot.kakaologintest.data.repository.UserRepository;
import com.springboot.kakaologintest.data.service.UserService;
import com.springboot.kakaologintest.jwt.JwtProperties;
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
import java.util.Date;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    @Value("${kakao.client.secret}")
    private String clientSecret;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationRepository confirmationRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    public OauthToken getAccessToken(String code){
        RestTemplate rt = new RestTemplate();
        //타임아웃  설정시 HttpComponentsClientHttpRequestFactory 객체 생성
        //우리는 타임아웃 설정안함.
        rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret); // 생략 가능!

        HttpEntity<MultiValueMap<String,String>>kakaoTokenRequest =
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

    public String saveUserAndGetToken(String token){

        KakaoProfile profile  = findProfile(token);
        User user = userRepository.findByEmail(profile.getKakao_account().getEmail());
        if(user == null){
            user = User.builder()
                    .id(profile.getId())
                    .nickname(profile.getKakao_account().getProfile().getNickname())
                    .email(profile.getKakao_account().getEmail())

                    .gender(profile.getKakao_account().gender)
                    .userRole("ROLE_USER").build();

            userRepository.save(user);
        }
        String userToken = createToken(user);
        System.out.println("User Token: " + userToken); // User Token 출력
        return createToken(user);
    }

    public String createToken(User user){
        logger.info("[createToken] 토큰 생성 시작");
        String jwtToken = JWT.create()
                //토큰이름 -> 유저이메일
                .withSubject(user.getEmail())
                //토큰 만료시간
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.EXPIRATION_TIME))

                //(2-5)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
        logger.info("[createToken] 토큰 생성 완료");
        System.out.println("JWT Token: " + jwtToken); // JWT Token 출력
        return jwtToken;
    }

    // UserService 클래스 안에 메소드 추가
    public String getKakaoToken(String jwtToken) {
        // JWT 토큰 검증
        DecodedJWT decodedJWT = verifyToken(jwtToken);

        // JWT 클레임에서 원하는 정보 추출
        // 예를 들어, "usercode" 클레임을 추출하는 경우
        Long usercode = decodedJWT.getClaim("uid").asLong();

        // 추출한 정보를 문자열로 반환 (사용자 식별 정보 또는 다른 필요한 정보에 따라 변경)
        return usercode.toString();
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

    public User getUser(HttpServletRequest request) { //(1)
        //(2)
        String email = (String)request.getAttribute("email");

        //(3)
        User user = userRepository.findByEmail(email);

        //(4)
        System.out.println("getUser : "+user);
        return user;

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
    public void savePersonalEmail(Long uid, String personalEmail) {
        User user = userRepository.findById(uid).orElse(null);
        if (user != null) {
            user.setPersonalEmail(personalEmail);
            userRepository.save(user);
        }

    }
    public boolean isEmailVerified(String personalEmail) {
        EmailConfirmation confirmation = confirmationRepository.findByPersonalEmail(personalEmail);
        return confirmation != null && confirmation.isVerified();
    }
}
