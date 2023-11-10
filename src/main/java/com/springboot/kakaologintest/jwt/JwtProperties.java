package com.springboot.kakaologintest.jwt;


public interface JwtProperties {
    //인터페이스 내에 정의되는 필드는 자동으로 public static final
    String SECRET="asdasjfhjkwq123124@@!#";
    //JWT 의 Signatuer 를 해싱할 때 사용되는 비밀 키
    int EXPIRATION_TIME =8640000;
    //토큰의 만료 기간 -> 10일
    String TOKEN_PREFIX = "Bearer ";
    //토큰 앞에 붙는 정해진 형식
    // 꼭 Bearer 뒤에 한 칸 공백을 넣어줘야 함.
    String HEADER_STRING = "Authorization";
    //더의 Authorization 이라는 항목에 토큰을 넣어줌.

}
