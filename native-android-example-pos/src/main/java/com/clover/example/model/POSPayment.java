/*
 * Copyright (C) 2018 Clover Network, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clover.example.model;


import com.clover.sdk.v3.payments.AdditionalChargeAmount;
import com.clover.sdk.v3.payments.CardEntryType;
import com.clover.sdk.v3.payments.CardTransactionState;
import com.clover.sdk.v3.payments.CardTransactionType;
import com.clover.sdk.v3.payments.CardType;
import com.clover.sdk.v3.payments.IncrementalAuthorization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class POSPayment extends POSTransaction implements Serializable{

  private long tipAmount, taxAmount;
  private long cashBackAmount;
  private String cloverOrderId, status;
  private transient POSOrder order;
  private String externalPaymentId, refundId;
  private List<POSRefund> refunds;
  private List<POSAdditionalChargeAmount> additionalCharges;

  public enum Status {
    PAID, VOIDED, REFUNDED, AUTHORIZED, PREAUTHORIZED
  }

  public POSPayment(String paymentID, String orderID, String employeeID, long amount, Date date) {
    this(paymentID, orderID, employeeID, amount, 0, 0, date);
  }

  public POSPayment(String paymentID, String orderID, String employeeID, long amount, long tip, long cashBack, Date date) {
    this(paymentID, null, orderID, employeeID, amount, tip, cashBack, date);
  }

  public POSPayment(String paymentID, String externalPaymentId, String orderID, String employeeID, long amount, long tip, long cashBack, Date date) {
    this.tipAmount = tip;
    this.cashBackAmount = cashBack;
    this.externalPaymentId = externalPaymentId;
  }

  public POSPayment(long amount, String cardDetails, CardType cardType, Date date, String id, String tender, String transactionTitle, CardTransactionType transactionType, boolean refund,
                    CardEntryType entryMethod, CardTransactionState transactionState, long cashBackAmount, String cloverOrderId, String externalPaymentId, long taxAmount, long tipAmount, List<AdditionalChargeAmount> additionalCharges){
    super (amount, cardDetails, cardType, date, id, tender, transactionTitle, transactionType, refund, entryMethod,  transactionState);

    this.cashBackAmount = cashBackAmount;
    this.cloverOrderId = cloverOrderId;
    this.externalPaymentId = externalPaymentId;
    this.taxAmount = taxAmount;
    this.tipAmount = tipAmount;
    if (additionalCharges != null) {
      this.additionalCharges = new ArrayList<>();
      for (AdditionalChargeAmount thisAdditionalCharge : additionalCharges) {
        this.additionalCharges.add(new POSAdditionalChargeAmount(thisAdditionalCharge.getId(), thisAdditionalCharge.getAmount(), thisAdditionalCharge.getRate()));
      }
    }
  }

  private Status _status;

  public Status getPaymentStatus() {
    return _status;
  }

  public void setPaymentStatus(Status status) {
    _status = status;
    if(order != null) { // will be null at ctor time, but don't want to notify of a change at that point anyway
      order.notifyObserverPaymentChanged(this);
    }
  }

  public boolean isVoided() {
    return _status == Status.VOIDED;
  }

  public boolean isRefunded() {
    return _status == Status.REFUNDED;
  }

  public long getTipAmount() {
    return tipAmount;
  }

  public long getCashBackAmount() {
    return cashBackAmount;
  }

  public POSOrder getOrder() {
    return order;
  }

  public void setOrder(POSOrder order) {
    this.order = order;
  }

  public void setTipAmount(long tipAmount) {
    this.tipAmount = tipAmount;
  }

  // Returns the sum of all additional charges
  public long getAdditionalChargesTotal() {
    long additionalCharges = 0;
    if (getAdditionalCharges() != null) {
      for (POSAdditionalChargeAmount additionalChargeAmount : getAdditionalCharges()) {
        additionalCharges += additionalChargeAmount.getAmount();
      }
    }
    return additionalCharges;
  }
  // Returns the sum of all incremental auths
  public long getIncrementalAuthTotal() {
    long incrementalAuth = 0;
    if (getIncrements() != null) {
      for (IncrementalAuthorization incrementalAuthorization : getIncrements()) {
        incrementalAuth += incrementalAuthorization.getAmount();
      }
    }
    return incrementalAuth;
  }

  // Returns the charge amount, not including additional charges, incremental auths, and tips
  public long getAmount() {
    return super.getAmount();
  }

  // Returns the total charge amount, including all additional charges and incremental auths
  public long getAmountWithAdditionalCharges() {
    return getAmount() + getAdditionalChargesTotal() + getIncrementalAuthTotal();
  }

  // Returns the total charge amount, including all additional charges, incremental auths, and tips
  public long getAmountWithAdditionalChargesAndTip() {
    return getAmountWithAdditionalCharges() + getTipAmount();
  }
  public void setExternalPaymentId(String externalPaymentId) {
    this.externalPaymentId = externalPaymentId;
  }

  public String getExternalPaymentId() {
    return externalPaymentId;
  }

  public long getTaxAmount() {
    return taxAmount;
  }

  public void setTaxAmount(long taxAmount) {
    this.taxAmount = taxAmount;
  }

  public void setCashBackAmount(long cashBackAmount) {
    this.cashBackAmount = cashBackAmount;
  }

  public String getCloverOrderId() {
    return cloverOrderId;
  }

  public void setCloverOrderId(String cloverOrderId) {
    this.cloverOrderId = cloverOrderId;
  }


  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }


  public List<POSRefund> getRefunds() {
    return refunds;
  }

  public void setRefunds(List<POSRefund> refunds) {
    this.refunds = refunds;
  }

  public Status get_status() {
    return _status;
  }

  public void set_status(Status _status) {
    this._status = _status;
  }

  public String getRefundId() {
    return refundId;
  }

  public void setRefundId(String refundId) {
    this.refundId = refundId;
  }

  public List<POSAdditionalChargeAmount> getAdditionalCharges() {
    return additionalCharges;
  }

  public void setAdditionalCharges(List<POSAdditionalChargeAmount> additionalCharges) {
    this.additionalCharges = additionalCharges;
  }

}
