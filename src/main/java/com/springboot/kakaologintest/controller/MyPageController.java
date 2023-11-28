package com.springboot.kakaologintest.controller;

import com.springboot.kakaologintest.data.service.MyPageService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/myPage")
public class MyPageController {
    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    @GetMapping("/getOwnsTopThree")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "사용자 인증 Token", required = true, dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getOwnsTopThree(HttpServletRequest request){
        return ResponseEntity.ok(myPageService.getOwnTopThree(request.getHeader("Authorization")));
    }
    @GetMapping("/getOwnsDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "사용자 인증 Token", required = true, dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getOwnDetail(HttpServletRequest request,
                                          @RequestParam(defaultValue = "0")int page,
                                          @RequestParam(defaultValue = "8")int size){
        return ResponseEntity.ok(myPageService.getOwnDetail(page,size,request.getHeader("Authorization")));
    }
    @GetMapping("/getParticipatingTopThree")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "사용자 인증 Token", required = true, dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getParticipatingTopThree(HttpServletRequest request){
        return ResponseEntity.ok(myPageService.getParticipatingTopThree(request.getHeader("Authorization")));
    }
    @GetMapping("/getParticipatingDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "사용자 인증 Token", required = true, dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getParticipatingDetail(HttpServletRequest request,
                                                    @RequestParam(defaultValue = "0")int page,
                                                    @RequestParam(defaultValue = "8")int size){
        return ResponseEntity.ok(myPageService.getParticipatingDetail(page,size,request.getHeader("Authorization")));
    }
    @GetMapping("/getAllFavorites")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "사용자 인증 Token", required = true, dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getAllFavorites(HttpServletRequest request,
                                             @RequestParam(defaultValue = "0")int page,
                                             @RequestParam(defaultValue = "8")int size){
        return ResponseEntity.ok(myPageService.getAllFavorites(page,size,request.getHeader("Authorization")));
    }
}
