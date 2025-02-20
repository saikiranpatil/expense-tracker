package org.example.controller;

import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.example.dto.ExpenseDto;
import org.example.service.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @GetMapping("/expense/v1")
    public ResponseEntity<List<ExpenseDto>> getExpenses(@PathParam("user_id") @NonNull String userId){
        List<ExpenseDto> expenses = null;

        try{
            expenses=expenseService.getExpenses(userId);
        }catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<List<ExpenseDto>>(expenses, HttpStatus.OK);
    }
}
