package org.example.controller;

import org.example.model.UserInfoDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/user/v1/modify")
    public ResponseEntity<UserInfoDto> signUpUser(@RequestBody UserInfoDto userInfoDto) {
        try {
            UserInfoDto newUserInfoDto = userService.createOrUpdate(userInfoDto);
            return new ResponseEntity<>(newUserInfoDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/v1/getUser")
    public ResponseEntity<UserInfoDto> getUserInfo(@RequestHeader("X-User-Id") String userId) {
        try {
            UserInfoDto userInfoDto = userService.getUserInfo(userId);
            return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/v1/health")
    public ResponseEntity<?> health() {
        return new ResponseEntity<>("Active", HttpStatus.OK);
    }
}
