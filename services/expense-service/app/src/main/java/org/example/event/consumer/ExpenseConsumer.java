package org.example.event.consumer;

import lombok.RequiredArgsConstructor;
import org.example.dto.ExpenseDto;
import org.example.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseConsumer {
    @Autowired
    private final ExpenseService expenseService;

    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId= "${spring.kafka.consumer.group-id}")
    public void listen(ExpenseDto expenseDto){
        try{
            expenseService.createExpense(expenseDto);
        }catch (Exception e){
            System.out.println("Error while consuming Expense:" + e);
        }
    }
}
