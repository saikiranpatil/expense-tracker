package org.example.event.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.example.model.UserInfoDto;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserInfoDeserializer implements Deserializer<UserInfoDto> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public UserInfoDto deserialize(String topic, byte[] bytes) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(bytes, UserInfoDto.class);
        } catch (Exception e) {
            System.out.println("Error while deserializing: " + e.getMessage());
        }

        return null;
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}