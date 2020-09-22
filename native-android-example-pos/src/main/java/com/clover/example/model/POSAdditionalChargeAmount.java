package com.clover.example.model;


import java.io.Serializable;

public class POSAdditionalChargeAmount implements Serializable {
  java.lang.String id;
  java.lang.Long amount;
  java.lang.Long rate;

  public POSAdditionalChargeAmount(String id, Long amount, Long rate) {
    this.id = id;
    this.amount = amount;
    this.rate = rate;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }

  public Long getRate() {
    return rate;
  }

  public void setRate(Long rate) {
    this.rate = rate;
  }
}
