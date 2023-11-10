package com.springboot.kakaologintest.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.springboot.kakaologintest.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired //(2)
    UserRepository userRepository;

    //로그인 요청이 오면 사용자 인증 후 JWT 토큰을 생성하여 클라이언트에게 응답
    //이제 사용자는 매번 로그인을 하는 것이 아니라, 이전에 발급 받은 JWT 토큰을 들고 서버로 요청을 하면
    //서버는 해당 JWT 토큰을 검증하여 유효한 토큰인지 확인 후 클라이언트 요청을 처리

    @Override //(3)
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // HTTP 요청 헤더에서 JWT(Jason Web Token)를 추출하려고 시도, JWT는 인증 및 권한 부여를 위해 사용되는 토큰
        String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);

        //(5)
        if(jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        //(6)
        String token = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "");

        Long usercode = null;

        //JWT 토큰을 검증
        //HMAC512 알고리즘을 사용하여 JWT를 해독하고 토큰에 포함된 클레임 중 "id" 클레임을 추출하여 유저 코드를 얻음.
        // 만약 토큰이 만료되었거나 유효하지 않은 경우에 대비하여 예외 처리를 수행
        try {
            usercode = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
                    .getClaim("id").asLong();

        } catch (TokenExpiredException e) {
            e.printStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "토큰이 만료되었습니다.");
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "유효하지 않은 토큰입니다.");
        }

        //(8)
        request.setAttribute("uid", usercode);

        //(9)
        filterChain.doFilter(request, response);
    }
}