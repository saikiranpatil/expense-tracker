package org.example.event.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.example.model.UserInfoDto;
import org.example.model.UserInfoEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public class UserInfoDeserializer implements Deserializer<org.example.model.UserInfoEventDto> {
    @Value("${spring.kafka.topic.name}")
    private String TOPIC_NAME;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public UserInfoEventDto deserialize(String topic, byte[] data) {
        UserInfoEventDto userInfoEventDto = null;

        try {
            userInfoEventDto = objectMapper.readValue(data, UserInfoEventDto.class);
        }catch (Exception e) {
            throw new RuntimeException("Error deserializing UserInfoDto", e);
        }

        return userInfoEventDto;
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
