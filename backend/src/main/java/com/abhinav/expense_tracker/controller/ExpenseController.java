package com.abhinav.expense_tracker.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abhinav.expense_tracker.dto.ExpenseDto;
import com.abhinav.expense_tracker.dto.ExpenseResponseDto;
import com.abhinav.expense_tracker.entity.Expense;
import com.abhinav.expense_tracker.service.ExpenseService;
import com.abhinav.expense_tracker.util.DtoMapper;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired private ExpenseService expenseService;
    
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ExpenseDto dto,Authentication auth){
        Expense e = expenseService.createExpenseFromDto(dto, auth.getName());
        ExpenseResponseDto responseDto = DtoMapper.toExpenseDto(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ExpenseDto dto, Authentication auth){
        Expense e = expenseService.updateExpense(id,dto,auth.getName());
        ExpenseResponseDto responseDto = DtoMapper.toExpenseDto(e);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDto>> myExpenses(Authentication auth){
        List<Expense> expenses = expenseService.getExpensesForUser(auth.getName());
        List<ExpenseResponseDto> dtos = expenses.stream().map(DtoMapper::toExpenseDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth){
        expenseService.deleteExpense(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics")
    public ResponseEntity<List<ExpenseResponseDto>> getAnalytics(@RequestParam("start") String startDate, @RequestParam("end") String endDate, Authentication auth){
        LocalDate start  = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        return ResponseEntity.ok(expenseService.getAnalytics(start, end, auth.getName()));
    }

    //@GetMapping("/all")
    //public ResponseEntity<List<Expense>> all(Authentication auth){
        //List<Expense> expenses = expenseService.getExpensesForUser(auth.getName());
        //List<ExpenseResponseDto> dtos = expenses.stream().map(DtoMapper::toExpenseDto).collect(Collectors.toList());
        //return ResponseEntity.ok(expenseService.getAllExpenses());
    //}
}
