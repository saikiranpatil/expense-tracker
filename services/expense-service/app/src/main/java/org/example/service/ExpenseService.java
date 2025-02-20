package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.example.dto.ExpenseDto;
import org.example.entity.ExpenseEntity;
import org.example.repository.ExpenseRepository;
import org.example.utils.ExpenseMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void createExpense(ExpenseDto expenseDto){
        try{
            expenseRepository.save(objectMapper.convertValue(expenseDto, ExpenseEntity.class));
        }catch (Exception e){
            System.out.println("Error While creating expense:" + e);
        }
    }

    // TODOS: Implement Distributed Locking to handle acid principles of sql in update query
    public boolean updateExpense(ExpenseDto expenseDto){
        ExpenseEntity expenseEntity = expenseRepository.findByUserIdAndExternalId(expenseDto.getUserId(), expenseDto.getExternalId())
                .orElseThrow(() -> new RuntimeException("Expenses not found with given userId and externalId"));

        // Use MapStruct to update only non-null fields
        expenseMapper.updateExpenseEntityFromDto(expenseDto, expenseEntity);
        expenseRepository.save(expenseEntity);

        return true;
    }

    public List<ExpenseDto> getExpenses(String userId){
        List<ExpenseEntity> expensesEntities = expenseRepository.findByUserId(userId);
        return objectMapper.convertValue(expensesEntities, new TypeReference<List<ExpenseDto>>() {});
    }
}