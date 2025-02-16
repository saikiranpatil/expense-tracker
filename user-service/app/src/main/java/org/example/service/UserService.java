package org.example.service;

import org.apache.catalina.User;
import org.example.entity.UserInfo;
import org.example.model.UserInfoDto;
import org.example.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public UserInfoDto createOrUpdate(UserInfoDto userInfoDto) {
        UnaryOperator<UserInfo> updateUserInfo = (userInfo) -> {
            BeanUtils.copyProperties(userInfoDto, userInfo);
            return userRepository.save(userInfo);
        };

        Supplier<UserInfo> saveUserInfo = () -> {
            return userRepository.save(userInfoDto.transformToUserInfo());
        };

        UserInfo newUserInfo = userRepository.findByUserId(userInfoDto.getUserId())
                .map(updateUserInfo)
                .orElseGet(saveUserInfo);

        UserInfoDto newUserInfoDto = new UserInfoDto();
        BeanUtils.copyProperties(newUserInfo, newUserInfoDto);
        return newUserInfoDto;
    }

    public UserInfoDto getUserInfo(String userId) {
        Function<UserInfo, UserInfoDto> mapToUserInfoDto = (userInfo) -> {
            UserInfoDto userInfoDto = new UserInfoDto();
            BeanUtils.copyProperties(userInfo, userInfoDto);
            return userInfoDto;
        };

        return userRepository.findByUserId(userId).map(mapToUserInfoDto).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
