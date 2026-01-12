package com.abhinav.expense_tracker.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.abhinav.expense_tracker.entity.Category;
import com.abhinav.expense_tracker.entity.Expense;
import com.abhinav.expense_tracker.entity.ExpenseSplit;
import com.abhinav.expense_tracker.entity.User;
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
            sb.append(e.getDate()).append(",")
            .append(e.getDescription().replaceAll(","," ")).append(",")
            .append(e.getAmount()).append(",")
            .append(e.getCategory()!=null?e.getCategory().getName():"").append(",")
            .append(e.getGroup()!=null?e.getGroup().getName():"").append("\n");
        }
        return sb.toString();
    }

    public void importCsv(MultipartFile file,Long ownerId) throws IOException{
        var reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String header = reader.readLine();
        String line;
        User owner = userRepository.findById(ownerId).orElseThrow();
        while((line = reader.readLine())!=null){
            String[] parts = line.split(",");
            if(parts.length < 3){
                continue;
            }
            LocalDate date = LocalDate.parse(parts[0]);
            String desc = parts[1];
            BigDecimal amt = new BigDecimal(parts[2]);
            String catName = parts.length > 3 ? parts[3]:null;
            Expense e = new Expense();
            e.setDate(date);
            e.setDescription(desc);
            e.setAmount(amt);
            e.setOwner(owner);
            if(catName!=null && !catName.isBlank()){
                Category c = categoryRepository.findByName(catName).orElseGet(()->categoryRepository.save(new Category(catName)));
                e.setCategory(c);
            }
            ExpenseSplit s = new ExpenseSplit();
            s.setMemberIdentifier(owner.getUsername());
            s.setAmount(e.getAmount());
            s.setExpense(e);
            e.getSplits().add(s);
            expenseRepository.save(e);
        }
    }
}

