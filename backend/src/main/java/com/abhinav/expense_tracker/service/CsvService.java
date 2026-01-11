package com.abhinav.expense_tracker.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.abhinav.expense_tracker.entity.Expense;
import com.abhinav.expense_tracker.repository.CategoryRepository;
import com.abhinav.expense_tracker.repository.ExpenseRepository;
import com.abhinav.expense_tracker.repository.UserRepository;

public class CsvService {
    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;

    public String exportCsvForUser(Long userId){
        List<Expense> expenses = expenseRepository.findByOwnerId(userId);
        StringBuilder sb=new StringBuilder("date,description,amount,category,group\n");
        for(Expense e:expenses){
            sb.append(e.getAmount()).append(",")
            .append(e.getDescription().replaceAll(","," ")).append(",")
            .append(e.getAmount()).append(",")
            .append(e.getCategory()!=null?e.getCategory().getName():"").append(",")
            .append(e.getGroup()!=null?e.getGroup().getName():"").append("\n");
        }
        return sb.toString();
    }

    public String importCsv(MultipartFile file,Long ownerId) throws IOException{
        
    }
}
