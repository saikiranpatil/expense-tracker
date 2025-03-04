package org.example.controller;

import org.example.entity.RefreshToken;
import org.example.event.eventProducer.UserInfoProducer;
import org.example.model.UserInfoDto;
import org.example.model.UserInfoEventDto;
import org.example.model.response.JwtResponseDto;
import org.example.model.response.PingResponse;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.example.service.UserDetailsServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/auth/v1/signup")
    public ResponseEntity<?> signUpUser(@RequestBody UserInfoDto userInfoDto) {
        Boolean userAlreadySignedUp = userDetailsServiceImpl.signUpUser(userInfoDto);

        if (userAlreadySignedUp) return new ResponseEntity<>("User Already Exists with given username", HttpStatus.BAD_REQUEST);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername());
        String jwtToken = jwtService.createToken(userInfoDto.getUsername());
        JwtResponseDto jwtResponseDto = new JwtResponseDto(jwtToken, refreshToken.getToken());
        return new ResponseEntity<JwtResponseDto>(jwtResponseDto, HttpStatus.OK);
    }

    @GetMapping("/auth/v1/ping")
    public ResponseEntity<?> ping() {
        System.out.println("ping");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()) {
            String userId = userDetailsServiceImpl.getUserIdByUsername(authentication.getName());
            if(!ObjectUtils.isEmpty(userId)) {
                return new ResponseEntity<>(new PingResponse(userId), HttpStatus.OK);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    @GetMapping("/auth/v1/health")
    public ResponseEntity<?> health() {
        return new ResponseEntity<>("Active", HttpStatus.OK);
    }
}
