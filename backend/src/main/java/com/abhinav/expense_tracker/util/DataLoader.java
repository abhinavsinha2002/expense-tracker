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

import com.abhinav.expense_tracker.entity.AuthProvider;
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
        // Requires empty DB to run
        if (userRepository.count() == 0) {
            System.out.println("Loading Dated Test Data with VND Currency...");
            BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
            String pwd = enc.encode("password");

            // 1. Create Users
            User abhinav = createUser("Abhinav Sinha", "abhinav", "abhinav@gmail.com", pwd);
            User anjalee = createUser("Anjalee Kumari", "anjalee", "anjalee@gmail.com", pwd);
            User ravi    = createUser("Ravi Kumar", "ravi", "ravi@gmail.com", pwd);

            // 2. Create Categories
            Category food = createCategory("Food");
            Category travel = createCategory("Travel");
            Category shopping = createCategory("Shopping");
            Category bills = createCategory("Utilities");

            // 3. Create Groups with VIETNAMESE CURRENCY (VND)
            // Passing "VND" here to test frontend currency binding
            ExpenseGroup patnaGroup = createGroup("Patna Group", "VND", abhinav, new HashSet<>(Arrays.asList(abhinav, anjalee)));
            ExpenseGroup blrGroup = createGroup("Bangalore Trip", "VND", abhinav, new HashSet<>(Arrays.asList(abhinav, ravi)));

            // 4. Create Expenses with Specific Dates

            // === 1. LAST 30 DAYS (1M) ===
            // Abhinav Personal: 200 (Today), 500 (15 days ago)
            createPersonal(abhinav, 200, food, 0); 
            createPersonal(abhinav, 500, shopping, 15);

            // Patna Group: Dinner (5 days ago), Snacks (10 days ago)
            createSplitExpense("Dinner", BigDecimal.valueOf(800), 5, abhinav, patnaGroup, food, 
                Arrays.asList(new Split(abhinav, 400), new Split(anjalee, 400)));
            
            createSplitExpense("Snacks", BigDecimal.valueOf(600), 10, abhinav, patnaGroup, food, 
                Arrays.asList(new Split(abhinav, 300), new Split(anjalee, 300)));

            // Bangalore Group: Breakfast (2 days ago)
            createSplitExpense("Breakfast", BigDecimal.valueOf(500), 2, abhinav, blrGroup, food, 
                Arrays.asList(new Split(abhinav, 250), new Split(ravi, 250)));


            // === 2. LAST 3 MONTHS (3M - Includes 1M data) ===
            // Expenses between 31 and 90 days ago
            
            // Abhinav Personal: 800 (45 days ago)
            createPersonal(abhinav, 800, bills, 45);

            // Patna Group: Lunch (60 days ago)
            createSplitExpense("Lunch", BigDecimal.valueOf(1200), 60, anjalee, patnaGroup, food, 
                Arrays.asList(new Split(abhinav, 600), new Split(anjalee, 600)));

            // Bangalore Group: Cab (40 days ago)
            createSplitExpense("Cab", BigDecimal.valueOf(1600), 40, abhinav, blrGroup, travel, 
                Arrays.asList(new Split(abhinav, 100), new Split(ravi, 1500)));


            // === 3. LAST 6 MONTHS (6M - Includes 1M & 3M data) ===
            // Expenses between 91 and 180 days ago

            // Abhinav Personal: 900 (120 days ago)
            createPersonal(abhinav, 900, food, 120);

            // Patna Group: Shopping (150 days ago)
            createSplitExpense("Shopping", BigDecimal.valueOf(5000), 150, abhinav, patnaGroup, shopping, 
                Arrays.asList(new Split(abhinav, 2000), new Split(anjalee, 3000)));

            // Bangalore Group: Tickets (100 days ago)
            createSplitExpense("Tickets", BigDecimal.valueOf(3500), 100, ravi, blrGroup, travel, 
                Arrays.asList(new Split(abhinav, 2000), new Split(ravi, 1500)));


            // === 4. LAST 1 YEAR (1Y - Includes all above) ===
            // Expenses between 181 and 365 days ago

            // Abhinav Personal: 1200 (200 days ago)
            createPersonal(abhinav, 1200, shopping, 200);

            // Patna Group Edge Cases (250 days ago)
            createSplitExpense("Anjalee Gifted Shirt", BigDecimal.valueOf(600), 250, anjalee, patnaGroup, shopping, 
                Arrays.asList(new Split(abhinav, 600))); // Abhinav owes full
            
            createSplitExpense("Personal Charger in Group", BigDecimal.valueOf(500), 250, abhinav, patnaGroup, shopping, 
                Arrays.asList(new Split(abhinav, 500))); // Abhinav paid for self

            // Bangalore Group Edge Cases (300 days ago)
            createSplitExpense("Hotel", BigDecimal.valueOf(1300), 300, ravi, blrGroup, travel, 
                Arrays.asList(new Split(abhinav, 600), new Split(ravi, 700)));

            createSplitExpense("Ravi paid for Abhinav's Meal", BigDecimal.valueOf(450), 300, ravi, blrGroup, food, 
                Arrays.asList(new Split(abhinav, 450))); // Abhinav owes full

            createSplitExpense("Abhinav Personal Souvenir", BigDecimal.valueOf(300), 300, abhinav, blrGroup, shopping, 
                Arrays.asList(new Split(abhinav, 300))); // Abhinav paid for self


            System.out.println("Dated Test Data with VND Loaded Successfully!");
        }
    }

    // --- Helpers ---

    private User createUser(String name, String username, String email, String pwd) {
        User u = new User();
        u.setFullName(name);
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(pwd);
        u.setEnabled(true);
        u.setAuthProvider(AuthProvider.LOCAL);
        return userRepository.save(u);
    }

    private Category createCategory(String name) {
        return categoryRepository.save(new Category(name));
    }

    // UPDATED: Now accepts 'currency' string
    private ExpenseGroup createGroup(String name, String currency, User owner, Set<User> members) {
        ExpenseGroup g = new ExpenseGroup();
        g.setName(name);
        g.setCurrency(currency); // Set the currency here
        g.setOwner(owner);
        g.setMembers(members);
        return groupRepository.save(g);
    }

    private void createPersonal(User user, double amount, Category cat, int daysAgo) {
        Expense e = new Expense();
        e.setDescription("Personal " + cat.getName());
        e.setAmount(BigDecimal.valueOf(amount));
        e.setDate(LocalDate.now().minusDays(daysAgo)); 
        e.setOwner(user);
        e.setCategory(cat);
        
        ExpenseSplit s = new ExpenseSplit();
        s.setMemberIdentifier(user.getEmail());
        s.setAmount(BigDecimal.valueOf(amount));
        s.setExpense(e);
        e.getSplits().add(s);
        expenseRepository.save(e);
    }

    private void createSplitExpense(String desc, BigDecimal total, int daysAgo, User payer, ExpenseGroup group, Category cat, List<Split> splits) {
        Expense e = new Expense();
        e.setDescription(desc);
        e.setAmount(total);
        e.setDate(LocalDate.now().minusDays(daysAgo));
        e.setOwner(payer);
        e.setGroup(group);
        e.setCategory(cat);

        for(Split sp : splits) {
            ExpenseSplit s = new ExpenseSplit();
            s.setMemberIdentifier(sp.user.getEmail());
            s.setAmount(BigDecimal.valueOf(sp.amount));
            s.setExpense(e);
            e.getSplits().add(s);
        }
        expenseRepository.save(e);
    }

    class Split {
        User user;
        double amount;
        Split(User u, double a) { user=u; amount=a; }
    }
}