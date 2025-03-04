package org.example.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.entity.UserInfo;
import org.example.event.eventProducer.UserInfoProducer;
import org.example.model.UserInfoDto;
import org.example.model.UserInfoEventDto;
import org.example.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserInfoProducer userInfoProducer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo extractedUserInfo = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return new CustomUserDetails(extractedUserInfo);
    }

    public String getUserIdByUsername(String userName){
        return userRepository.findByUsername(userName)
                .map(UserInfo::getUserId)
                .orElseThrow(() -> new UsernameNotFoundException("UserId not found"));
    }

    public UserInfo checkIfUserExists(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    public Boolean signUpUser(UserInfoDto userInfoDto){
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        if(userRepository.findByUsername(userInfoDto.getUsername()).isPresent()){
            return true;
        }

        String uuid = UUID.randomUUID().toString();
        userRepository.save(new UserInfo(uuid, userInfoDto.getUsername(), userInfoDto.getPassword(), new HashSet<>()));
        userInfoDto.setUserId(uuid);
        UserInfoEventDto userInfoEventDto = new UserInfoEventDto();
        BeanUtils.copyProperties(userInfoDto, userInfoEventDto);
//        userInfoProducer.sendUserInfo(userInfoEventDto);

        return false;
    }
}
