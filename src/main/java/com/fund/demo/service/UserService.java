package com.example.demo.service;

import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return null;
        }
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public List<Transaction> getUserCurrentMonthTransactions(Long userId) {
        List<Transaction> all = transactionRepository.findByUserUserId(userId);
        int currentMonth = LocalDate.now().getMonthValue();
        return all.stream().filter(t -> t.getDate().getMonthValue() == currentMonth).collect(Collectors.toList());
    }

    public double getUserCurrentMonthExpense(Long userId) {
        List<Transaction> list = getUserCurrentMonthTransactions(userId);
        return list.stream().filter(t->t.getType().equals("餐饮")||t.getType().equals("水电")||t.getType().equals("交通")||t.getType().equals("生活用品")||t.getType().equals("其他"))
                .mapToDouble(Transaction::getAmount).sum();
    }

    public double getUserCurrentMonthIncome(Long userId) {
        List<Transaction> list = getUserCurrentMonthTransactions(userId);
        return list.stream().filter(t->t.getType().equals("工资"))
                .mapToDouble(Transaction::getAmount).sum();
    }
}
