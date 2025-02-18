package org.example.event.consumer;

import org.example.model.UserInfoDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceConsumer {
    @Autowired
    private UserService userService;

    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserInfo(UserInfoDto userInfoDto) {
        try{
            // Todo: Make it transactional, to handle idempotency and validate email, phoneNumber etc
            userService.createOrUpdate(userInfoDto);
        }catch(Exception ex){
            System.out.println("Error while Creating UserInfo Event");
        }
    }
}