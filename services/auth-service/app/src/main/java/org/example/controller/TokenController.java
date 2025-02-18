package org.example.controller;

import org.example.entity.RefreshToken;
import org.example.model.request.AuthRequestDto;
import org.example.model.request.RefreshTokenRequestDto;
import org.example.model.response.JwtResponseDto;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.example.Utils.FunctionalWithException.functionalWithException;

@Controller
public class TokenController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/auth/v1/login")
    public ResponseEntity<?> AuthenticateAndGetToken(@RequestBody AuthRequestDto authRequestDto) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword())
        );

        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("Invalid Credentials", HttpStatus.UNAUTHORIZED);
        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDto.getUsername());
        String accessToken = jwtService.createToken(authRequestDto.getUsername());

        JwtResponseDto jwtResponseDto = new JwtResponseDto(accessToken, refreshToken.getToken());
        return new ResponseEntity<>(jwtResponseDto, HttpStatus.OK);
    }

    @PostMapping("/auth/v1/refreshToken")
    public JwtResponseDto refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) throws Exception {
        return refreshTokenService.findByToken(refreshTokenRequestDto.getToken())
                .map(functionalWithException(refreshTokenService::verifyExpiration))
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.createToken(userInfo.getUsername());
                    return JwtResponseDto.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDto.getToken())
                            .build();
                })
                .orElseThrow(() -> new Exception("Invalid refresh token or token has expired"));
    }
}
