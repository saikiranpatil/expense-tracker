package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="expense")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="external_id")
    private String externalId;
    @Column(name="user_id")
    private String userId;
    private Integer amount;
    private String currency;
    private String account;
    private String payee;
    private String txnType;
    private Date date;
    @Column(name="created_at")
    private Timestamp createdAt;
    private String description;
    private String category;

    @PreUpdate
    @PrePersist
    private void handleExternalId(){
        if(this.externalId==null){
            this.externalId=UUID.randomUUID().toString();
        }

        System.out.println("asdfdasfasd");

        if(this.createdAt==null) {
            this.createdAt= new Timestamp(System.currentTimeMillis());
        }
    }
}
