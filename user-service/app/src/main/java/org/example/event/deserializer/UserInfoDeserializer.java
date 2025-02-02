package org.example.event.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.Deserializers;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.example.entity.UserInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public class UserInfoDeserializer implements Deserializer<UserInfoDto> {
    @Value("${spring.kafka.topic.name}")
    private String TOPIC_NAME;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public UserInfoDto deserialize(String topic, byte[] data) {
        UserInfoDto userInfoDto = null;

        if(data==null) return null;

        try {
            userInfoDto = objectMapper.readValue(data, UserInfoDto.class);
        }catch (Exception e) {
            throw new RuntimeException("Error deserializing UserInfoDto", e);
        }

        return userInfoDto;
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
