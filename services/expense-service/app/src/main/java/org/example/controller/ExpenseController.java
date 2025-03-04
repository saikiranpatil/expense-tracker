package org.example.controller;

import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.example.dto.ExpenseDto;
import org.example.service.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @GetMapping("/expense/v1/getExpenses")
    public ResponseEntity<List<ExpenseDto>> getExpenses(@RequestParam("X-User-Id") @NonNull String userId){
        List<ExpenseDto> expenses = null;

        try{
            expenses=expenseService.getExpenses(userId);
        }catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<List<ExpenseDto>>(expenses, HttpStatus.OK);
    }

    @GetMapping("/expense/v1/health")
    public ResponseEntity<String> health(){
        return new ResponseEntity<String>("Active", HttpStatus.OK);
    }

    @PostMapping("/expense/v1/addExpense")
    public ResponseEntity<ExpenseDto> addExpense(@RequestHeader(value = "X-User-Id") @NonNull String userId , @RequestBody ExpenseDto expenseDto){
        ExpenseDto expense = null;

        try{
            expenseDto.setUserId(userId);
            expense=expenseService.createExpense(expenseDto);
        }catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<ExpenseDto>(expense, HttpStatus.OK);
    }
}
