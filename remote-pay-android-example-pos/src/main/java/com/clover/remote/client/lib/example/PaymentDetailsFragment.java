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

package com.clover.remote.client.lib.example;

import com.clover.remote.PendingPaymentEntry;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.lib.example.model.POSAdditionalChargeAmount;
import com.clover.remote.client.lib.example.model.POSCard;
import com.clover.remote.client.lib.example.model.POSOrder;
import com.clover.remote.client.lib.example.model.POSPayment;
import com.clover.remote.client.lib.example.model.POSRefund;
import com.clover.remote.client.lib.example.model.POSStore;
import com.clover.remote.client.lib.example.model.POSTransaction;
import com.clover.remote.client.lib.example.model.StoreObserver;
import com.clover.remote.client.lib.example.utils.CurrencyUtils;
import com.clover.remote.client.messages.DisplayReceiptOptionsRequest;
import com.clover.remote.client.messages.IncrementPreauthRequest;
import com.clover.remote.client.messages.RefundPaymentRequest;
import com.clover.remote.client.messages.TipAdjustAuthRequest;
import com.clover.remote.client.messages.VoidPaymentRefundRequest;
import com.clover.remote.client.messages.VoidPaymentRequest;
import com.clover.sdk.v3.payments.CardTransactionState;
import com.clover.sdk.v3.payments.CardTransactionType;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class PaymentDetailsFragment extends Fragment implements AdjustTipFragment.AdjustTipFragmentListener, RefundPaymentFragment.PaymentRefundListener, IncrementAuthorizationFragment.IncrementAuthorizationListener {
  private static final String TAG = PaymentDetailsFragment.class.getSimpleName();
  private View view;
  private TextView title, transactionTitle, date, total, paymentStatus, refundStatus, tender, cardDetails, employee, deviceId, paymentId, entryMethod, transactionType,
      transactionState, absoluteTotal, tip, fees, refundDate, refundTotal, refundTender, refundEmployee, refundDevice, refundId;
  private LinearLayout tipRow, feesRow, refundRow, paymentSuccessfulRow;
  private ImageView paymentStatusImage, refundStatusImage;
  private Button refund, voidPayment, addTip, receiptSale, receiptRefund, increaseAuth, viewIncrements;
  private POSTransaction transaction;
  private POSStore store;
  private WeakReference<ICloverConnector> cloverConnectorWeakReference;
  private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
  private DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
  private StoreObserver mStoreObserver;

  public static PaymentDetailsFragment newInstance(POSTransaction transaction, ICloverConnector cloverConnector, POSStore store) {
    PaymentDetailsFragment fragment = new PaymentDetailsFragment();
    fragment.setStore(store);
    fragment.setCloverConnector(cloverConnector);
    fragment.setTransaction(transaction);
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  public PaymentDetailsFragment(){

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    view = inflater.inflate(R.layout.fragment_payment_details, container, false);

    title = (TextView) view.findViewById(R.id.PaymentDetailsTitle);
    transactionTitle = (TextView) view.findViewById(R.id.PaymentDetailsTransactionTitle);
    date = (TextView) view.findViewById(R.id.PaymentDetailsDate);
    total = (TextView) view.findViewById(R.id.PaymentDetailsTotal);
    paymentStatusImage = (ImageView) view.findViewById(R.id.PaymentDetailsPaymentStatusImage);
    paymentStatus = (TextView) view.findViewById(R.id.PaymentDetailsStatus);
    refundStatus = (TextView) view.findViewById(R.id.PaymentDetailsRefundStatus);
    refundStatusImage = (ImageView) view.findViewById(R.id.PaymentDetailsRefundStatusImage);
    tender = (TextView) view.findViewById(R.id.PaymentDetailsTender);
    cardDetails = (TextView) view.findViewById(R.id.PaymentDetailsCardDetails);
    employee = (TextView) view.findViewById(R.id.PaymentDetailsEmployee);
    deviceId = (TextView) view.findViewById(R.id.PaymentDetailsDeviceId);
    paymentId = (TextView) view.findViewById(R.id.PaymentDetailsPaymentId);
    entryMethod = (TextView) view.findViewById(R.id.PaymentDetailsEntryMethod);
    transactionType = (TextView) view.findViewById(R.id.PaymentDetailsTransactionType);
    transactionState = (TextView) view.findViewById(R.id.PaymentDetailsTransactionState);
    absoluteTotal = (TextView) view.findViewById(R.id.PaymentDetailsAbsoluteTotal);
    tipRow = (LinearLayout) view.findViewById(R.id.PaymentDetailsTipRow);
    tip = (TextView) view.findViewById(R.id.PaymentDetailsTip);
    feesRow = (LinearLayout) view.findViewById(R.id.PaymentDetailsFeesRow);
    fees = (TextView) view.findViewById(R.id.PaymentDetailsFees);
    refundRow = (LinearLayout) view.findViewById(R.id.PaymentDetailsRefundRow);
    paymentSuccessfulRow = (LinearLayout) view.findViewById(R.id.PaymentDetailsPaymentSuccessfulRow);
    refundTotal = (TextView) view.findViewById(R.id.PaymentDetailsRefundTotal);
    refundTender = (TextView) view.findViewById(R.id.PaymentDetailsRefundTender);
    refundEmployee = (TextView) view.findViewById(R.id.PaymentDetailsRefundEmployee);
    refundDevice = (TextView) view.findViewById(R.id.PaymentDetailsRefundDeviceId);
    refundId = (TextView) view.findViewById(R.id.PaymentDetailsRefundId);
    refundDate = (TextView) view.findViewById(R.id.PaymentDetailsRefundDate);
    populateFields();

    //listen for changes to the transaction that this fragment is displaying
    mStoreObserver = new PaymentDetailsStoreObserver();
    store.addStoreObserver(mStoreObserver);

    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    if (mStoreObserver != null) {
      store.removeStoreObserver(mStoreObserver);
    }
  }

  private void populateFields(){
    refund = (Button) view.findViewById(R.id.PaymentDetailsRefund);
    refund.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (((POSPayment) transaction).getRefundId() != null) {
          voidPaymentRefund();
        } else {
          showRefundPaymentDialog();
        }
      }
    });
    voidPayment = (Button) view.findViewById(R.id.PaymentDetailsVoidPayment);
    voidPayment.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        voidPayment();
      }
    });
    addTip = (Button) view.findViewById(R.id.PaymentDetailsAddTip);
    addTip.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showAdjustTipDialog();
      }
    });
    increaseAuth = view.findViewById(R.id.PaymentDetailsIncrementAuth);
    increaseAuth.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showIncrementAuthDialog();
      }
    });

    viewIncrements = view.findViewById(R.id.viewIncrementsButton);
    viewIncrements.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        IncrementListFragment fragment = IncrementListFragment.newInstance(transaction);
        fragment.show(fm, fragment.DEFAULT_FRAGMENT_TAG);
      }
    });

    transactionTitle.setText(transaction.getTransactionTitle());
    date.setText(dateFormat.format(transaction.getDate())+" • "+timeFormat.format(transaction.getDate()));
    total.setText(CurrencyUtils.convertToString(transaction.getAmount()));
    tender.setText(transaction.getTender());
    cardDetails.setText(transaction.getCardDetails());
    employee.setText(transaction.getEmployee());
    deviceId.setText(transaction.getDeviceId());
    paymentId.setText(transaction.getId());
    entryMethod.setText(transaction.getEntryMethod().toString());
    enableView(refund);
    enableView(addTip);
    enableView(voidPayment);

    //conditionally show/hide the Increase Amount button for pre-auths
    if (transaction.getTransactionType() == CardTransactionType.PREAUTH &&
        transaction.getTransactionState() != CardTransactionState.CLOSED) {
      enableView(increaseAuth);
      increaseAuth.setVisibility(View.VISIBLE);
    } else {
      disableView(increaseAuth);
      increaseAuth.setVisibility(View.INVISIBLE);
    }

    //conditionally show/hide the View Increments button for pre-auths
    if (transaction.getTransactionType() == CardTransactionType.PREAUTH &&
        transaction.getIncrements() != null &&
        !transaction.getIncrements().isEmpty()) {
      viewIncrements.setVisibility(View.VISIBLE);
      enableView(viewIncrements);
    } else {
      viewIncrements.setVisibility(View.INVISIBLE);
      disableView(viewIncrements);
    }

    if(transaction.getTransactionType() != null) {
      transactionType.setText(transaction.getTransactionType().toString());
    }
    if(transaction.getTransactionState() != null) {
      transactionState.setText(transaction.getTransactionState().toString());
    }

    if(!transaction.getRefund()){
      receiptSale = (Button) view.findViewById(R.id.receiptsButton);
      receiptSale.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          DisplayReceiptOptionsRequest request = new DisplayReceiptOptionsRequest();
          request.setPaymentId(transaction.getId());
          request.setOrderId(((POSPayment) transaction).getCloverOrderId());
          getCloverConnector().displayReceiptOptions(request);
        }
      });
      title.setText("Payment Details");
      paymentStatusImage.setImageResource(R.drawable.status_green);
      paymentStatus.setText("Payment Successful");
      if(transaction.getTransactionTitle() == "Auth"){
        addTip.setVisibility(View.VISIBLE);
      }
      if(((POSPayment)transaction).getTipAmount() != 0){
        addTip.setText("Adjust Tip");
        tipRow.setVisibility(View.VISIBLE);
        tip.setText(CurrencyUtils.convertToString(((POSPayment)transaction).getTipAmount()));
      } else {
        tipRow.setVisibility(View.GONE);
      }
      if ((((POSPayment) transaction).getAdditionalCharges() != null) && (((POSPayment) transaction).getAdditionalCharges().size() != 0)) {
        feesRow.setVisibility(View.VISIBLE);
        long additionalCharges = 0;
        for (POSAdditionalChargeAmount additionalChargeAmount : ((POSPayment) transaction).getAdditionalCharges()) {
          additionalCharges += additionalChargeAmount.getAmount();
        }
        fees.setText(CurrencyUtils.convertToString(additionalCharges));
      } else {
        feesRow.setVisibility(View.GONE);
      }
      if(((POSPayment)transaction).getPaymentStatus() == POSPayment.Status.VOIDED){
        disableView(refund);
        disableView(addTip);
        disableView(voidPayment);
        transactionType.setText("VOIDED");
      }
      if(((POSPayment) transaction).getRefundId() != null){
        POSTransaction refund = store.getTransactionByTransactionId(((POSPayment) transaction).getRefundId());
        addRefund((POSRefund)refund, transaction.getAmount());
      }
      else{
        refundRow.setVisibility(View.GONE);
        refund.setText("Refund");
        receiptRefund = (Button) view.findViewById(R.id.receiptsButtonRefund);
        receiptRefund.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            DisplayReceiptOptionsRequest request = new DisplayReceiptOptionsRequest();
            request.setPaymentId(transaction.getId());
            request.setOrderId(((POSPayment) transaction).getCloverOrderId());
            request.setRefundId(((POSPayment) transaction).getRefundId());
            getCloverConnector().displayReceiptOptions(request);
          }
        });
      }
      absoluteTotal.setText(CurrencyUtils.convertToString(((POSPayment)transaction).getAmountWithAdditionalChargesAndTip()));
    }
    else{
      title.setText("Manual Refund Details");
      absoluteTotal.setText(CurrencyUtils.convertToString(((POSPayment)transaction).getAmountWithAdditionalCharges()));
      disableView(refund);
      disableView(voidPayment);
      disableView(addTip);
      tipRow.setVisibility(View.GONE);
      feesRow.setVisibility(View.GONE);
      refundRow.setVisibility(View.GONE);
      paymentSuccessfulRow.setVisibility(View.GONE);
    }
  }

  public void disableView (View view){
    view.setEnabled(false);
    view.setAlpha((float)0.4);
  }

  public void enableView (View view){
    view.setEnabled(true);
    view.setAlpha(1);
  }

  private void showAdjustTipDialog() {
    FragmentManager fm = getFragmentManager();
    AdjustTipFragment adjustTipFragment = AdjustTipFragment.newInstance(((POSPayment)transaction).getTipAmount());
    adjustTipFragment.addListener(this);
    adjustTipFragment.show(fm, "fragment_enter_payment_id");
  }

  private void showIncrementAuthDialog() {
    FragmentManager fm = getFragmentManager();
    IncrementAuthorizationFragment incAuthFragment = IncrementAuthorizationFragment.newInstance(this);
    incAuthFragment.show(fm, "fragment_increment_authorization");
  }

  //Increment Authorization Listener
  @Override
  public void onIncrementAuthorization(long incrementAmount) {
    IncrementPreauthRequest ipa = new IncrementPreauthRequest();
    ipa.setAmount(incrementAmount);
    ipa.setPaymentId(transaction.getId());
    getCloverConnector().incrementPreAuth(ipa);
  }

  public void setTransaction (POSTransaction posTransaction){
    this.transaction = posTransaction;
    if(view != null) {
      populateFields();
    }
  }

  public ICloverConnector getCloverConnector(){
    return cloverConnectorWeakReference.get();
  }

  public void setCloverConnector(ICloverConnector cloverConnector) {
    cloverConnectorWeakReference = new WeakReference<ICloverConnector>(cloverConnector);
  }

  public void setStore(POSStore posStore){
    this.store = posStore;
  }

  public void voidPayment(){
    VoidPaymentRequest vpr = new VoidPaymentRequest();
    vpr.setPaymentId(transaction.getId());
    vpr.setOrderId(((POSPayment)transaction).getCloverOrderId());
    vpr.setVoidReason("USER_CANCEL");
    vpr.setDisableReceiptSelection(store.getDisableReceiptOptions() != null ? store.getDisableReceiptOptions() : false);
    vpr.setDisablePrinting(store.getDisablePrinting() != null ? store.getDisablePrinting() : false);
    Log.d(TAG, "VoidPaymentRequest: " + vpr.toString());
    getCloverConnector().voidPayment(vpr);
  }

  public void paymentVoided (final POSTransaction posTransaction){
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        transaction = posTransaction;
        transactionType.setText("VOIDED");
        paymentId.setText("");
        disableView(refund);
        disableView(voidPayment);
        disableView(addTip);
      }
    });
  }

  public void refundVoided (final POSTransaction posTransaction, String refundId){
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        transaction = posTransaction;
        refundStatus.setText("Refund Voided");
        refundStatusImage.setImageResource(R.drawable.status_red);
        paymentId.setText("");
        refund.setText("Refund");
        enableView(refund);
        enableView(voidPayment);
        disableView(addTip);
      }
    });
  }

  public void voidPaymentRefund(){
    VoidPaymentRefundRequest vprr = new VoidPaymentRefundRequest();
    vprr.setRefundId(((POSPayment)transaction).getRefundId());
    vprr.setOrderId(((POSPayment)transaction).getCloverOrderId());
    vprr.setDisableReceiptSelection(store.getDisableReceiptOptions() != null ? store.getDisableReceiptOptions() : false);
    vprr.setDisablePrinting(store.getDisablePrinting() != null ? store.getDisablePrinting() : false);
    Log.d(TAG, "VoidPaymentRefundRequest: " + vprr.toString());
    getCloverConnector().voidPaymentRefund(vprr);
  }

  public void setTip(final long tipAmount){
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if(tipAmount > 0){
          tipRow.setVisibility(View.VISIBLE);
          tip.setText(CurrencyUtils.convertToString(tipAmount));
          addTip.setText("Adjust Tip");
          absoluteTotal.setText(CurrencyUtils.convertToString(((POSPayment)transaction).getAmountWithAdditionalChargesAndTip()));
        }
      }
    });
  }


  public void addRefund(final POSRefund posRefund, final long amount){
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        refundDate.setText(dateFormat.format(posRefund.getDate())+" • "+timeFormat.format(posRefund.getDate()));
        refundRow.setVisibility(View.VISIBLE);
        refundTotal.setText(CurrencyUtils.convertToString(amount));
        refundTender.setText(posRefund.getTender());
        refundEmployee.setText(posRefund.getEmployee());
        refundDevice.setText(posRefund.getDeviceId());
        refundId.setText(posRefund.getId());

        refund.setText(getString(R.string.void_refund));
        enableView(refund);
        disableView(voidPayment);
        disableView(addTip);
      }
    });
  }

  private void showRefundPaymentDialog(){
    FragmentManager fm = getFragmentManager();
    RefundPaymentFragment refundPaymentFragment = RefundPaymentFragment.newInstance(((POSPayment)transaction).getAmountWithAdditionalCharges());
    refundPaymentFragment.addListener(this);
    refundPaymentFragment.show(fm, "fragment_refund_payment");
  }

  @Override
  public void makePartialRefund(long amount) {
    RefundPaymentRequest refund = new RefundPaymentRequest();
    refund.setAmount(amount);
    refund.setPaymentId(transaction.getId());
    refund.setOrderId(((POSPayment)transaction).getCloverOrderId());
    refund.setFullRefund(false);
    refund.setDisablePrinting(store.getDisablePrinting() != null ? store.getDisablePrinting() : false);
    refund.setDisableReceiptSelection(store.getDisableReceiptOptions() != null ? store.getDisableReceiptOptions() : false);
    final ICloverConnector cloverConnector = cloverConnectorWeakReference.get();
    Log.d(TAG, "RefundPaymentRequest - Partial: " + refund.toString());
    cloverConnector.refundPayment(refund);
  }

  @Override
  public void makeFullRefund() {
    RefundPaymentRequest refund = new RefundPaymentRequest();
    refund.setAmount(transaction.getAmount());
    refund.setPaymentId(transaction.getId());
    refund.setOrderId(((POSPayment)transaction).getCloverOrderId());
    refund.setFullRefund(true);
    final ICloverConnector cloverConnector = cloverConnectorWeakReference.get();
    Log.d(TAG, "RefundPaymentRequest - Full: " + refund.toString());
    cloverConnector.refundPayment(refund);
  }

  //Adjust Tip Listener
  @Override
  public void onSave(long tipAmount) {
    TipAdjustAuthRequest taar = new TipAdjustAuthRequest();
    taar.setPaymentId(transaction.getId());
    taar.setOrderId(((POSPayment)transaction).getCloverOrderId());
    taar.setTipAmount(tipAmount);
    Log.d(TAG, "TipAdjustAuthRequest: " + taar.toString());
    getCloverConnector().tipAdjustAuth(taar);
  }

  private class PaymentDetailsStoreObserver implements StoreObserver {
    @Override
    public void onCurrentOrderChanged(POSOrder currentOrder) {
    }

    @Override
    public void newOrderCreated(POSOrder order, boolean userInitiated) {
    }

    @Override
    public void cardAdded(POSCard card) {
    }

    @Override
    public void refundAdded(POSTransaction refund) {
    }

    @Override
    public void preAuthAdded(POSPayment payment) {
    }

    @Override
    public void preAuthRemoved(POSPayment payment) {
    }

    @Override
    public void pendingPaymentsRetrieved(List<PendingPaymentEntry> pendingPayments) {
    }

    @Override
    public void transactionsChanged(List<POSTransaction> transactions) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          POSTransaction updatedTransaction = store.getTransactionByTransactionId(transaction.getId());
          if (updatedTransaction != null) {
            setTransaction(updatedTransaction);
            populateFields();
          }
        }
      });
    }
  }
}
