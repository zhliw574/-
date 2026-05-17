package com.example.demo.repository;

import com.example.demo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserUserId(Long userId);
    List<Transaction> findByType(String type);
    List<Transaction> findByUserUserIdAndType(Long userId, String type);
}
