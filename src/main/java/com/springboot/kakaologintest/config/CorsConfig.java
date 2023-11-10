package com.springboot.kakaologintest.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        //쿠키와 자격 증명을 원본 간에 공유할 수 있게 허용
        config.setAllowCredentials(true);

        //모든 원본이 API에 접근할 수 있도록 허용
        config.addAllowedOriginPattern("+");

        //라이언트에서 모든 HTTP 헤더를 보낼 수 있게 허용
        config.addAllowedHeader("*");

        //응답에서 모든 헤더를 노출
        config.addExposedHeader("*");

        //모든 HTTP 메서드 (GET, POST, PUT, DELETE 등)를 허용
        config.addAllowedMethod("*");
        //특정 URL 패턴에 대한 CORS 구성 등록
        source.registerCorsConfiguration("/api/**", config);

        //새로운 CorsFilter 인스턴스 반환:
        return new CorsFilter(source);
    }

}
