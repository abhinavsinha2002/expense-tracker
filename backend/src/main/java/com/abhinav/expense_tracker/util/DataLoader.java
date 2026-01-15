package com.abhinav.expense_tracker.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.abhinav.expense_tracker.entity.Category;
import com.abhinav.expense_tracker.entity.Expense;
import com.abhinav.expense_tracker.entity.ExpenseGroup;
import com.abhinav.expense_tracker.entity.ExpenseSplit;
import com.abhinav.expense_tracker.entity.User;
import com.abhinav.expense_tracker.repository.CategoryRepository;
import com.abhinav.expense_tracker.repository.ExpenseRepository;
import com.abhinav.expense_tracker.repository.GroupRepository;
import com.abhinav.expense_tracker.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private GroupRepository groupRepository;
    @Autowired private ExpenseRepository expenseRepository;

    @Override
    public void run(String... args) throws Exception{
        if(userRepository.count()  ==0){
            BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
            User u1 = new User();
            u1.setUsername("abhinav");
            u1.setEmail("abhinavsinha2002@gmail.com");
            u1.setPassword(enc.encode("password"));
            u1.setEnabled(true);
            User u2 = new User();
            u2.setUsername("anjalee");
            u2.setEmail("anjalee@gmail.com");
            u2.setPassword(enc.encode("password"));
            u2.setEnabled(true);
            userRepository.save(u1);
            userRepository.save(u2);

            categoryRepository.save(new Category("Food"));
            categoryRepository.save(new Category("Transport"));

            ExpenseGroup g = new ExpenseGroup();
            g.setName("Shimla Trip");
            g.setOwner(u1);
            Set<User> mem = new HashSet<>();
            mem.add(u1);
            mem.add(u2);
            g.setMembers(mem);
            groupRepository.save(g);

            Expense e1 = new Expense();
            e1.setDescription("Dinner");
            e1.setAmount(new BigDecimal("1500"));
            e1.setDate(LocalDate.now().minusDays(3));
            e1.setOwner(u1);
            e1.setGroup(g);

            ExpenseSplit s1 = new ExpenseSplit();
            s1.setMemberIdentifier("abhinav");
            s1.setAmount(new BigDecimal("450"));
            s1.setExpense(e1);

            ExpenseSplit s2 = new ExpenseSplit();
            s2.setMemberIdentifier("anjalee");
            s2.setAmount(new BigDecimal("1050"));
            s2.setExpense(e1);

            e1.getSplits().add(s1);
            e1.getSplits().add(s2);
            expenseRepository.save(e1);
        }    
    }
}
