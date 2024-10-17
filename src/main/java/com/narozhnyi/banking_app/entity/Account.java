package com.narozhnyi.banking_app.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account extends AuditingEntity {

  @Id
  @UuidGenerator
  @Column(name = "account_id")
  private UUID accountId;

  private String accountNumber;

  @Column(precision = 19, scale = 2)
  private BigDecimal balance;

  public void depositBalance(BigDecimal amount) {
    setBalance(getBalance().add(amount));
  }

  public void withdrawBalance(BigDecimal amount) {
    setBalance(getBalance().subtract(amount));
  }
}
