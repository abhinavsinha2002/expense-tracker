package com.abhinav.expense_tracker.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abhinav.expense_tracker.dto.SettleUpTransactionDto;
import com.abhinav.expense_tracker.entity.Expense;
import com.abhinav.expense_tracker.entity.ExpenseGroup;
import com.abhinav.expense_tracker.entity.ExpenseSplit;
import com.abhinav.expense_tracker.entity.User;
import com.abhinav.expense_tracker.repository.ExpenseRepository;
import com.abhinav.expense_tracker.repository.GroupRepository;
import com.abhinav.expense_tracker.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExpenseRepository expenseRepository;

    @Transactional
    public ExpenseGroup createGroup(String name, String currency, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
        ExpenseGroup g = new ExpenseGroup();
        g.setName(name);
        g.setCurrency(currency);
        g.setOwner(owner);
        g.setMembers(new HashSet<>(Collections.singletonList(owner)));
        return groupRepository.save(g);
    }

    public void joinGroup(String token, String email) {
        ExpenseGroup group = groupRepository.findByInviteToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Invite link"));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        group.getMembers().add(user);
        groupRepository.save(group);
    }

    public List<SettleUpTransactionDto> settleUp(Long groupId) {
        ExpenseGroup g = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        Map<String, Double> net = new HashMap<>();
        for (User m : g.getMembers()) {
            net.put(m.getEmail(), 0.0);
        }
        List<Expense> expenses = expenseRepository.findByGroup(g);
        for (Expense e : expenses) {
            String payer = e.getOwner().getEmail();
            double paid = e.getAmount().doubleValue();
            if (e.getSplits() != null && !e.getSplits().isEmpty()) {
                for (ExpenseSplit s : e.getSplits()) {
                    String member = s.getMemberIdentifier();
                    double owed = s.getAmount().doubleValue();
                    net.put(member, net.getOrDefault(member, 0.0) - owed);
                }
                net.put(payer, net.getOrDefault(payer, 0.0) + paid);
            } else {
                int size = g.getMembers().size();
                double per = paid / size;
                for (User m : g.getMembers()) {
                    net.put(m.getEmail(), net.getOrDefault(m.getEmail(), 0.0) - per);
                }
                net.put(payer, net.getOrDefault(payer, 0.0) + paid);
            }
        }
        PriorityQueue<Map.Entry<String, Double>> creditors = new PriorityQueue<>(
                (a, b) -> Double.compare(b.getValue(), a.getValue()));
        PriorityQueue<Map.Entry<String, Double>> debtors = new PriorityQueue<>(
                (a, b) -> Double.compare(a.getValue(), b.getValue()));
        for (Map.Entry<String, Double> e : net.entrySet()) {
            double val = Math.round(e.getValue() * 100.0) / 100.0;
            if (Math.abs(val) < 0.01) {
                continue;
            }
            Map.Entry<String, Double> entry = new AbstractMap.SimpleEntry<>(e.getKey(), val);
            if (val > 0) {
                creditors.add(entry);
            } else {
                debtors.add(entry);
            }
        }
        List<SettleUpTransactionDto> res = new ArrayList<>();
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Map.Entry<String, Double> cred = creditors.poll();
            Map.Entry<String, Double> debt = debtors.poll();
            double amount = Math.min(cred.getValue(), -debt.getValue());
            amount = Math.round(amount * 100.0) / 100.0;
            res.add(new SettleUpTransactionDto(debt.getKey(), cred.getKey(), amount));
            double newCred = Math.round((cred.getValue() - amount) * 100.0) / 100.0;
            double newDebt = Math.round((debt.getValue() + amount) * 100.0) / 100.0;
            if (newCred > 0.009) {
                creditors.add(new AbstractMap.SimpleEntry<>(cred.getKey(), newCred));
            }
            if (newDebt < -0.009) {
                debtors.add(new AbstractMap.SimpleEntry<>(debt.getKey(), newDebt));
            }
        }
        return res;
    }

    public List<ExpenseGroup> findAll() {
        return groupRepository.findAll();
    }

    public ExpenseGroup getGroup(Long id, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ExpenseGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grop not found"));

        if (!group.getMembers().contains(user)) {
            throw new IllegalArgumentException("Access Denied: You are not a member of this group.");
        }
        return group;
    }

    public List<ExpenseGroup> findGroupsForUser(String email) {

        Optional<User> u = userRepository.findByEmail(email);
        if (u.isEmpty()) {
            return List.of();
        }
        return groupRepository.findAll().stream().filter(g -> g.getMembers().contains(u.get())).toList();
    }

    public List<Expense> getGroupExpenses(Long groupId, String email) {
        ExpenseGroup group = getGroup(groupId, email);
        return expenseRepository.findByGroup(group);
    }
}
