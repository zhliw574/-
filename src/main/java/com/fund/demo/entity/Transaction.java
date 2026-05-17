package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private Double amount;

    @Column(name = "create_time")
    private LocalDate date;

    private String type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
