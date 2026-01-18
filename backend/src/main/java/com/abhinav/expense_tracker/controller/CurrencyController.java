package com.abhinav.expense_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abhinav.expense_tracker.service.CurrencyService;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {
    @Autowired private CurrencyService currencyService;
    
    @GetMapping("/convert")
    public ResponseEntity<Double> convert(@RequestParam String from,@RequestParam String to, @RequestParam double amount ){
        return ResponseEntity.ok(currencyService.convert(from, to, amount));
    }
}
