package com.github.service.accountservice.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Getter
@Setter
@Entity
@Table(name = "account_transaction")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Transaction typeId must be provided")
    @ManyToOne
    @JoinColumn(name = "type_id")
    private TransactionType type;

    @Min(value = 0, message = "Amount should be positive")
    @NotNull(message = "Amount should be positive")
    @Column(name = "amount")
    private BigDecimal amount;

    @NotNull(message = "Account must be provided")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    public Transaction(){}

    public Transaction(TransactionType type, BigDecimal amount, Account account){
        this.type = type;
        this.amount = amount;
        this.account = account;
        createdTime = new Date();
    }

    public Transaction(TransactionType type, BigDecimal amount, Account account, Product product){
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.product = product;
        createdTime = new Date();
    }
}
