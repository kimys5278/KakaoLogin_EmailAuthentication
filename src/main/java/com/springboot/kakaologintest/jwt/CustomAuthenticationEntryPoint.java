package com.springboot.kakaologintest.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.kakaologintest.data.dto.response.EntryPointErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request , HttpServletResponse response , AuthenticationException authenticationException)
            throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("[commence] 인증 실패로 response.sendError 발생");

        EntryPointErrorResponse entryPointErrorResponse = new EntryPointErrorResponse();
        entryPointErrorResponse.setMsg("인증이 실패 하였습니다.");

        response.setStatus(401);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(entryPointErrorResponse));
    }

//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
//            throws IOException, ServletException {
    // (1) 예외 속성 가져오기 전 null 체크 추가
//        String exception = (String) request.getAttribute(JwtProperties.HEADER_STRING);
//        if (exception == null) {
//            setResponse(response, "인증 오류 발생");
//            return;
//        }
//
//        String errorCode;
//        if (exception.equals("토큰이 만료되었습니다.")) {
//            errorCode = "토큰이 만료되었습니다.";
//            setResponse(response, errorCode);
//        } else if (exception.equals("유효하지 않은 토큰입니다.")) {
//            errorCode = "유효하지 않은 토큰입니다.";
//            setResponse(response, errorCode);
//        } else {
//            setResponse(response, "인증 오류");
//        }
//    }
//
//    // (2) setResponse 메서드
//    private void setResponse(HttpServletResponse response, String errorCode) throws IOException {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().println("{\"error\": \"" + errorCode + "\"}");



}
