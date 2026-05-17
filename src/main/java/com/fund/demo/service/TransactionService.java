package com.example.demo.service;

import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getUserTransactionsByType(Long userId, String type) {
        return transactionRepository.findByUserUserIdAndType(userId, type);
    }

    public List<Transaction> getAllUserTransactions(Long userId) {
        return transactionRepository.findByUserUserId(userId);
    }

    public List<Transaction> getAllTransactionsByType(String type) {
        return transactionRepository.findByType(type);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getUserCurrentMonthTransactions(Long userId) {
        List<Transaction> all = transactionRepository.findByUserUserId(userId);
        int month = LocalDate.now().getMonthValue();
        return all.stream()
                .filter(t -> t.getDate() != null)
                .filter(t->t.getDate().getMonthValue() == month)
                .collect(Collectors.toList());
    }

    public List<Transaction> getAllCurrentMonthTransactions() {
        List<Transaction> all = transactionRepository.findAll();
        int month = LocalDate.now().getMonthValue();
        return all.stream()
                .filter(t -> t.getDate() != null)
                .filter(t->t.getDate().getMonthValue() == month)
                .collect(Collectors.toList());
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public void save(Transaction transaction) {
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDate.now());
        }
        transactionRepository.save(transaction);
    }

    public void delete(Long id) {
        transactionRepository.deleteById(id);
    }
}
