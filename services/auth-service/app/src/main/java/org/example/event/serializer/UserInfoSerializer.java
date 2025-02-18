package org.example.event.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.example.model.UserInfoDto;
import org.example.model.UserInfoEventDto;

import java.util.Map;

public class UserInfoSerializer implements Serializer<UserInfoEventDto> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, UserInfoEventDto userInfoEventDto) {
        byte[] serializedData = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            serializedData = objectMapper.writeValueAsBytes(userInfoEventDto);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing UserInfoDto", e);
        }

        return serializedData;
    }

    @Override
    public byte[] serialize(String topic, Headers headers, UserInfoEventDto data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}
