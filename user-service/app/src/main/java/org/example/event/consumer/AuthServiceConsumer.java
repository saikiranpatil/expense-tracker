package org.example.event.consumer;

import org.example.entity.UserInfoDto;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceConsumer {
    private final UserRepository userRepository;

    @Autowired
    public AuthServiceConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserInfo(UserInfoDto userInfoDto) {
        if(userInfoDto == null){
            throw new RuntimeException("UserInfoDto found to be null");
        }
        System.out.println(eventData);
        userRepository.save((UserInfoDto) eventData);
    }
}