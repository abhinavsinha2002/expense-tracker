package com.abhinav.expense_tracker.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abhinav.expense_tracker.dto.ExpenseDto;
import com.abhinav.expense_tracker.dto.ExpenseSplitDto;
import com.abhinav.expense_tracker.entity.Category;
import com.abhinav.expense_tracker.entity.Expense;
import com.abhinav.expense_tracker.entity.ExpenseGroup;
import com.abhinav.expense_tracker.entity.ExpenseSplit;
import com.abhinav.expense_tracker.entity.User;
import com.abhinav.expense_tracker.repository.CategoryRepository;
import com.abhinav.expense_tracker.repository.ExpenseRepository;
import com.abhinav.expense_tracker.repository.GroupRepository;
import com.abhinav.expense_tracker.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ExpenseService {
    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private GroupRepository groupRepository;

    @Transactional
    public Expense createExpenseFromDto(ExpenseDto dto,String ownerUsername){
        User owner = userRepository.findByUsername(ownerUsername).orElseThrow(()->new IllegalArgumentException("Owner not found"));
        Expense e=new Expense();
        e.setDescription(dto.getDescription());
        e.setAmount(dto.getAmount());
        e.setDate(dto.getDate()==null?LocalDate.now():dto.getDate());
        e.setOwner(owner);
        if(dto.getCategory()!=null){
            Category c=categoryRepository.findByName(dto.getCategory()).orElseGet(()->categoryRepository.save(new Category(dto.getCategory())));
            e.setCategory(c);
        }
        if(dto.getGroupId()!=null){
            ExpenseGroup g=groupRepository.findById(dto.getGroupId()).orElseThrow(()->new IllegalArgumentException("Group not found"));
            e.setGroup(g);
        }

        if(dto.getSplits()!=null && !dto.getSplits().isEmpty()){
            BigDecimal sum=dto.getSplits().stream().map(ExpenseSplitDto::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
            if(sum.compareTo(dto.getAmount())!=0){
                throw new IllegalArgumentException("Splits don't sum to total");
            }
            List<ExpenseSplit> splits=dto.getSplits().stream().map(sdto->{
                ExpenseSplit s=new ExpenseSplit();
                s.setMemberIdentifier(sdto.getMember());
                s.setAmount(sdto.getAmount());
                s.setExpense(e);
                return s;
            }).collect(Collectors.toList());
            e.getSplits().addAll(splits);
        }
        else{
            if(e.getGroup()!=null){
                Set<User> members = e.getGroup().getMembers();
                BigDecimal per=e.getAmount().divide(BigDecimal.valueOf(members.size()),2,BigDecimal.ROUND_HALF_UP);
                for(User m:members){
                    ExpenseSplit s=new ExpenseSplit();
                    s.setMemberIdentifier(m.getUsername());
                    s.setAmount(per);
                    s.setExpense(e);
                    e.getSplits().add(s);
                }
            }
        
            else{
                ExpenseSplit s=new ExpenseSplit();
                s.setMemberIdentifier(owner.getUsername());
                s.setAmount(e.getAmount());
                s.setExpense(e);
                e.getSplits().add(s);
            }
        }
        return expenseRepository.save(e);
    }

    public List<Expense> getExpensesForUser(String username){
        User u=userRepository.findByUsername(username).orElseThrow(()->new IllegalArgumentException("User not found"));
        return expenseRepository.findByOwnerId(u.getId());
    }

    public void deleteExpense(Long id,String username){
        Expense e=expenseRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Not found"));
        if(!e.getOwner().getUsername().equals(username)){
            throw new IllegalArgumentException("Only owner can delete");
        }
        expenseRepository.delete(e);
    }

    public Map<String,Object> yearlySummary(int year,String username){
        LocalDate start=LocalDate.of(year,1,1);
        LocalDate end=LocalDate.of(year,12,31);
        List<Expense> all = expenseRepository.findByDateBetween(start,end).stream()
                            .filter(e->e.getOwner()!=null && e.getOwner().getUsername().equals(username)).collect(Collectors.toList());
        BigDecimal total = all.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        Map<String,BigDecimal> byCategory=new HashMap<>();
        for(Expense e:all){
            String cat=e.getCategory()!=null ? e.getCategory().getName():"Uncategorized";
            byCategory.put(cat,byCategory.getOrDefault(cat, BigDecimal.ZERO).add(e.getAmount()));
        }
        Map<String,Object> res=new HashMap<>();
        res.put("total",total);
        res.put("byCategory",byCategory);
        return res;
    }

    public List<Expense> getAllExpenses(String username){
        User u = userRepository.findByUsername(username).orElseThrow(()->new IllegalArgumentException("User not found"));
        Long id = u.getId();
        return expenseRepository.findByOwnerId(id);
    }
}
