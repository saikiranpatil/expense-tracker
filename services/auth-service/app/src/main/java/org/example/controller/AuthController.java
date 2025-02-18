package org.example.controller;

import org.example.entity.RefreshToken;
import org.example.event.eventProducer.UserInfoProducer;
import org.example.model.UserInfoDto;
import org.example.model.UserInfoEventDto;
import org.example.model.response.JwtResponseDto;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.example.service.UserDetailsServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private UserInfoProducer userInfoProducer;

    @PostMapping("auth/v1/signup")
    public ResponseEntity<?> signUpUser(@RequestBody UserInfoDto userInfoDto) {
        Boolean userAlreadySignedUp = userDetailsServiceImpl.signUpUser(userInfoDto);

        if (userAlreadySignedUp) return new ResponseEntity<>("User Already Exists with given username", HttpStatus.BAD_REQUEST);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername());
        String jwtToken = jwtService.createToken(userInfoDto.getUsername());
        JwtResponseDto jwtResponseDto = new JwtResponseDto(jwtToken, refreshToken.getToken());
        return new ResponseEntity<JwtResponseDto>(jwtResponseDto, HttpStatus.OK);
    }
}
