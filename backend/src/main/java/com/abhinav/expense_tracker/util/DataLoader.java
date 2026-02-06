package com.abhinav.expense_tracker.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.abhinav.expense_tracker.entity.AuthProvider; // Ensure this is imported
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

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private GroupRepository groupRepository;
    @Autowired private ExpenseRepository expenseRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only load data if the database is empty
        if (userRepository.count() == 0) {
            System.out.println("Loading Mock Data...");
            BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
            String defaultPassword = enc.encode("password");

            // --- 1. Create Users (Updated with Full Names) ---
            User abhinav = createUser("Abhinav Sinha", "abhinav", "abhinavsinha2002@gmail.com", defaultPassword);
            User anjalee = createUser("Anjalee Kumari", "anjalee", "anjalee@gmail.com", defaultPassword);
            User rohit   = createUser("Rohit Sharma", "rohit", "rohit@gmail.com", defaultPassword);
            User neha    = createUser("Neha Gupta", "neha", "neha@gmail.com", defaultPassword);
            User rahul   = createUser("Rahul Verma", "rahul", "rahul@gmail.com", defaultPassword);

            List<User> allUsers = Arrays.asList(abhinav, anjalee, rohit, neha, rahul);

            // --- 2. Create Categories ---
            Category food = createCategory("Food");
            Category transport = createCategory("Transport");
            Category utilities = createCategory("Utilities");
            Category entertainment = createCategory("Entertainment");
            Category travel = createCategory("Travel");
            Category shopping = createCategory("Shopping");

            List<Category> categories = Arrays.asList(food, transport, utilities, entertainment, travel, shopping);

            // --- 3. Create Groups ---
            
            // Group A: Shimla Trip (Abhinav & Anjalee)
            ExpenseGroup shimlaTrip = createGroup("Shimla Trip", abhinav, new HashSet<>(Arrays.asList(abhinav, anjalee)));
            
            // Group B: Flat 101 (Abhinav, Rohit, Rahul) - Shared living
            ExpenseGroup flat101 = createGroup("Flat 101", abhinav, new HashSet<>(Arrays.asList(abhinav, rohit, rahul)));
            
            // Group C: Office Lunch (All 5 members)
            ExpenseGroup officeGroup = createGroup("Office Gang", neha, new HashSet<>(allUsers));

            // --- 4. Generate Expenses ---
            Random random = new Random();

            // A. Create 40 Personal Expenses (No Group)
            for (int i = 0; i < 40; i++) {
                User owner = allUsers.get(random.nextInt(allUsers.size()));
                Category cat = categories.get(random.nextInt(categories.size()));
                BigDecimal amount = BigDecimal.valueOf(100 + random.nextInt(900)); 
                LocalDate date = LocalDate.now().minusDays(random.nextInt(100));

                createExpense(
                    owner.getUsername() + " Personal " + cat.getName(), 
                    amount, 
                    date, 
                    owner, 
                    null, // No Group
                    cat, 
                    null // No Splits
                );
            }

            // B. Create 30 "Flat 101" Expenses (Equal Splits)
            for (int i = 0; i < 30; i++) {
                User payer = Arrays.asList(abhinav, rohit, rahul).get(random.nextInt(3));
                Category cat = Arrays.asList(utilities, food).get(random.nextInt(2));
                BigDecimal totalAmount = BigDecimal.valueOf(300 + random.nextInt(1200)); 
                LocalDate date = LocalDate.now().minusDays(random.nextInt(60));

                createExpense(
                    "Flat " + cat.getName(), 
                    totalAmount, 
                    date, 
                    payer, 
                    flat101, 
                    cat, 
                    generateEqualSplits(totalAmount, flat101.getMembers())
                );
            }

            // C. Create 20 "Shimla Trip" Expenses (Uneven Splits)
            for (int i = 0; i < 20; i++) {
                User payer = (i % 2 == 0) ? abhinav : anjalee;
                BigDecimal totalAmount = BigDecimal.valueOf(2000 + random.nextInt(5000));
                LocalDate date = LocalDate.now().minusDays(100 + random.nextInt(10)); 

                List<ExpenseSplitData> splits = new ArrayList<>();
                BigDecimal share1 = totalAmount.multiply(new BigDecimal("0.60"));
                BigDecimal share2 = totalAmount.subtract(share1);
                
                splits.add(new ExpenseSplitData(abhinav.getUsername(), share1));
                splits.add(new ExpenseSplitData(anjalee.getUsername(), share2));

                createExpense(
                    "Shimla " + travel.getName(), 
                    totalAmount, 
                    date, 
                    payer, 
                    shimlaTrip, 
                    travel, 
                    splits
                );
            }

            // D. Create 10 "Office Lunch" Expenses
            for (int i = 0; i < 10; i++) {
                User payer = neha; 
                BigDecimal totalAmount = BigDecimal.valueOf(2500);
                LocalDate date = LocalDate.now().minusDays(random.nextInt(20));

                createExpense(
                    "Team Treat", 
                    totalAmount, 
                    date, 
                    payer, 
                    officeGroup, 
                    food, 
                    generateEqualSplits(totalAmount, officeGroup.getMembers())
                );
            }
            
            System.out.println("Data Loading Complete: 100 entries created.");
        }
    }

    // --- Helper Methods ---

    // UPDATED: Now accepts fullName and sets AuthProvider
    private User createUser(String fullName, String username, String email, String password) {
        User u = new User();
        u.setFullName(fullName); // <--- Set the new field
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(password);
        u.setEnabled(true);
        u.setAuthProvider(AuthProvider.LOCAL); // <--- Good practice to set this explicitly
        return userRepository.save(u);
    }

    private Category createCategory(String name) {
        return categoryRepository.save(new Category(name));
    }

    private ExpenseGroup createGroup(String name, User owner, Set<User> members) {
        ExpenseGroup g = new ExpenseGroup();
        g.setName(name);
        g.setOwner(owner);
        g.setMembers(members);
        return groupRepository.save(g);
    }

    private void createExpense(String desc, BigDecimal amount, LocalDate date, User owner, 
                               ExpenseGroup group, Category category, List<ExpenseSplitData> splitData) {
        Expense e = new Expense();
        e.setDescription(desc);
        e.setAmount(amount);
        e.setDate(date);
        e.setOwner(owner);
        e.setGroup(group);
        e.setCategory(category);

        if (splitData != null) {
            for (ExpenseSplitData sd : splitData) {
                ExpenseSplit s = new ExpenseSplit();
                s.setMemberIdentifier(sd.username);
                s.setAmount(sd.amount);
                s.setExpense(e);
                e.getSplits().add(s);
            }
        } else {
            ExpenseSplit s = new ExpenseSplit();
            s.setMemberIdentifier(owner.getUsername());
            s.setAmount(amount);
            s.setExpense(e);
            e.getSplits().add(s);
        }

        expenseRepository.save(e);
    }

    private List<ExpenseSplitData> generateEqualSplits(BigDecimal total, Set<User> members) {
        List<ExpenseSplitData> splits = new ArrayList<>();
        BigDecimal size = BigDecimal.valueOf(members.size());
        BigDecimal perHead = total.divide(size, 2, BigDecimal.ROUND_FLOOR); 
        
        BigDecimal currentSum = BigDecimal.ZERO;
        int count = 0;
        
        for (User u : members) {
            count++;
            if (count == members.size()) {
                BigDecimal remainder = total.subtract(currentSum);
                splits.add(new ExpenseSplitData(u.getUsername(), remainder));
            } else {
                splits.add(new ExpenseSplitData(u.getUsername(), perHead));
                currentSum = currentSum.add(perHead);
            }
        }
        return splits;
    }

    private static class ExpenseSplitData {
        String username;
        BigDecimal amount;

        public ExpenseSplitData(String username, BigDecimal amount) {
            this.username = username;
            this.amount = amount;
        }
    }
}