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
    @Autowired private GroupActivityService activityService;

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

        handleSplits(e,dto,owner);

        Expense saved = expenseRepository.save(e);

        if(saved.getGroup()!=null){
            activityService.logExpenseAdded(owner,saved.getGroup(),saved);
        }

        return saved;

        ////

        ////
    }

    @Transactional
    public Expense updateExpense(Long id, ExpenseDto dto, String email){
        Expense existing = expenseRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Expense not found"));
        User actor = userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("User not found"));

        if(!existing.getOwner().getEmail().equals(email)){
            throw new IllegalArgumentException("Only owner can edit");
        }

        Expense oldSnapshot = new Expense();
        oldSnapshot.setDescription(existing.getDescription());
        oldSnapshot.setAmount(existing.getAmount());

        existing.setDescription(dto.getDescription());
        existing.setAmount(dto.getAmount());
        if(dto.getDate()!=null){
            existing.setDate(dto.getDate());
        }
        if(dto.getCategory()!=null){
            Category c = categoryRepository.findByName(dto.getCategory()).orElseGet(()->categoryRepository.save(new Category(dto.getCategory())));
            existing.setCategory(c);
        }

        if(dto.getSplits() != null) {
            existing.getSplits().clear();
            handleSplits(existing, dto, existing.getOwner());
        }

        Expense saved = expenseRepository.save(existing);
        if(saved.getGroup()!=null){
            activityService.logExpenseUpdated(actor, saved.getGroup(), oldSnapshot, saved);
        }

        return saved;
    }

    public List<Expense> getExpensesForUser(String username){
        User u=userRepository.findByUsername(username).orElseThrow(()->new IllegalArgumentException("User not found"));
        return expenseRepository.findByUserInvolvement(u.getUsername());
    }

    public void deleteExpense(Long id,String email){
        Expense e=expenseRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Not found"));
        User actor = userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("User not found"));
        
        if(!e.getOwner().getEmail().equals(email)){
            throw new IllegalArgumentException("Only owner can delete");
        }

        if(e.getGroup()!=null){
            activityService.logExpenseDeleted(actor, e.getGroup(), e);
        }
        expenseRepository.delete(e);
    }

    private void handleSplits(Expense e, ExpenseDto dto, User owner){
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
    }

    public Map<String,Object> getAnalytics(LocalDate start, LocalDate end, String email){
        
        List<Expense> all = expenseRepository.findByUserInvolvementAndDateBetween(start, end,email);
        BigDecimal total = BigDecimal.ZERO; 
        Map<String,BigDecimal> byCategory=new HashMap<>();
        for(Expense e:all){
            BigDecimal myShare = BigDecimal.ZERO;
            if(e.getSplits()!=null && !e.getSplits().isEmpty()){
                for(ExpenseSplit s:e.getSplits()){
                    if(s.getMemberIdentifier().equals(email)){
                        myShare=myShare.add(s.getAmount());
                    }
                }
            }
            else{
                if(e.getOwner().getEmail().equals(email)){
                    myShare = e.getAmount();
                }
            }
            if(myShare.compareTo(BigDecimal.ZERO)>0){
                total = total.add(myShare);
                String cat=e.getCategory()!=null ? e.getCategory().getName():"Uncategorized";
                byCategory.put(cat,byCategory.getOrDefault(cat, BigDecimal.ZERO).add(myShare));
            }     
        }
        Map<String,Object> res=new HashMap<>();
        res.put("total",total);
        res.put("byCategory",byCategory);
        return res;
    }

    public List<Expense> getAllExpenses(){
        return expenseRepository.findAll();
    }
}
