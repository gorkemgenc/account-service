package com.github.service.accountservice.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Getter
@Setter
@Entity
@Table(name = "process_type")
@EntityListeners(AuditingEntityListener.class)
public class TransactionType {

    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "description", unique = true)
    @Size(min = 1, max = 20)
    private String type;

    public TransactionType(){}

    public TransactionType(String type){
        this.type = type;
    }
}
