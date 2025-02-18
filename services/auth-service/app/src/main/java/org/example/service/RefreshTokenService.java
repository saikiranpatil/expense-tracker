package org.example.service;

import org.example.entity.RefreshToken;
import org.example.entity.UserInfo;
import org.example.repository.RefreshTokenRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(String username) throws UsernameNotFoundException {
        UserInfo extractedUserInfo = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(extractedUserInfo)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(60000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) throws Exception {
        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new Exception(refreshToken + " token has expired. please login again");
        }
        return refreshToken;
    }

    public Optional<RefreshToken> findByToken(String token) throws Exception{
        return refreshTokenRepository.findByToken(token);
    }
}
