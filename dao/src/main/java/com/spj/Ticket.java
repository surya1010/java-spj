package com.spj;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigInteger;

@Entity
@Table(name = "tickets")
public class Ticket {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private BigInteger totalQuantity;
  private BigInteger availableQuantity;

  private int isAvailable;

  public Ticket() {
  }

  public Ticket(String name, BigInteger totalQuantity, BigInteger availableQuantity) {
    this.name = name;
    this.totalQuantity = totalQuantity;
    this.availableQuantity = availableQuantity;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigInteger getTotalQuantity() {
    return totalQuantity;
  }

  public void setTotalQuantity(BigInteger totalQuantity) {
    this.totalQuantity = totalQuantity;
  }

  public BigInteger getAvailableQuantity() {
    return availableQuantity;
  }

  public void setAvailableQuantity(BigInteger availableQuantity) {
    this.availableQuantity = availableQuantity;
  }


}