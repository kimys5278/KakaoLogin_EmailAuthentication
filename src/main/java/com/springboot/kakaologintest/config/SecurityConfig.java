package com.springboot.kakaologintest.config;


import com.springboot.kakaologintest.data.service.UserService;
import com.springboot.kakaologintest.jwt.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final UserService userService;
    private final CorsConfig corsFilter;
    @Autowired
    public SecurityConfig(UserService userService, CorsConfig corsFilter){
        this.userService = userService;
        this.corsFilter = corsFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() // REST API는 UI를 사용하지 않으므로 기본설정을 비활성화

                .sessionManagement()
                .sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS); // JWT Token 인증방식으로 세션은 필요 없으므로 비활성화


        http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and();

        http.authorizeRequests() // 리퀘스트에 대한 사용권한 체크
                // 가입 및 로그인 주소는 허용
                .antMatchers("/api/v1/user/oauth/token","/api/v1/user/KakaoProfilen").permitAll()


                // MainPage
                .antMatchers("/api/v1/mainPage/getAll").permitAll()
                .antMatchers("/api/v1/mainPage/getRightAway").permitAll()
                .antMatchers("/api/v1/mainPage/getFavorites").permitAll()
                .antMatchers("/api/v1/mainPage/getTopFive").permitAll()

                //MyPage
                .antMatchers("/api/v1/myPage/**").hasRole("USER")
                .antMatchers("/api/**").permitAll()

                .antMatchers("**exception**").permitAll()
                .anyRequest().hasRole("ADMIN") // 나머지 요청은 인증된 ADMIN만 접근 가능

                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint());

    }

    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
                "/swagger-ui.html", "/webjars/**", "/swagger/**", "/sign-api/exception");
    }

}