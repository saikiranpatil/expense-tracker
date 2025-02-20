package org.example.event.deserializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.example.dto.ExpenseDto;

import java.util.Map;

public class ExpenseDeserializer implements Deserializer<ExpenseDto> {
    @Override
    public void configure(Map configs, boolean isKey) {}

    @Override
    public ExpenseDto deserialize(String s, byte[] bytes) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        ExpenseDto expenseDto = null;

        try {
            expenseDto = objectMapper.readValue(bytes, ExpenseDto.class);
        }catch (Exception e) {
            System.out.println("Error in deserializing expense:"+ e);
        }

        return expenseDto;
    }

    @Override
    public void close() {}
}
