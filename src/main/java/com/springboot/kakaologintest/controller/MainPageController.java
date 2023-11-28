package com.springboot.kakaologintest.controller;

import com.springboot.kakaologintest.data.service.MainPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mainPage")
public class MainPageController {
    private final MainPageService mainPageService;

    @Autowired
    public MainPageController(MainPageService mainPageService) {
        this.mainPageService = mainPageService;
    }
    @GetMapping("/getAll") // 전체 약속 반환
    public ResponseEntity<?> getAllAppointment(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "8") int size,
                                               @RequestParam(required = false) List<String> collegeList,
                                               @RequestParam(defaultValue = "NEW")String sort){
        if(collegeList == null){
            collegeList = new ArrayList<>();
        }

        return ResponseEntity.ok(mainPageService.getAllAppointment(page, size, collegeList, sort));
    }
    @GetMapping("/getRightAway") // 당일 만료일 약속들 4개
    public ResponseEntity<?> getRightAwayAppointment(){
        return ResponseEntity.ok(mainPageService.getRightAwayAppointment());
    }
    @GetMapping("/getTopFive")
    public ResponseEntity<?> getTopFiveAppointment(){
        return ResponseEntity.ok(mainPageService.getTopFiveAppointment());
    }
    @GetMapping("/getFavorites")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "사용자 인증 Token", required = true, dataType = "String", paramType = "header")
//    })
    public ResponseEntity<?> getUserFavoriteAppointment(String token){
        return ResponseEntity.ok(mainPageService.getFavoriteAppointment(token));
    }
}
