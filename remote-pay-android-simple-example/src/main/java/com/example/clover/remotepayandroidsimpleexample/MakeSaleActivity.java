package com.example.clover.remotepayandroidsimpleexample;

import com.clover.remote.Challenge;
import com.clover.remote.client.CloverConnector;
import com.clover.remote.client.DefaultCloverConnectorListener;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.MerchantInfo;
import com.clover.remote.client.messages.ConfirmPaymentRequest;
import com.clover.remote.client.messages.SaleRequest;
import com.clover.remote.client.messages.SaleResponse;
import com.clover.remote.client.messages.VerifySignatureRequest;
import com.clover.sdk.v3.payments.Payment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by rachel.antion on 7/13/17.
 */

public class MakeSaleActivity extends Activity {

  private final String TAG = MakeSaleActivity.class.getSimpleName();
  private ICloverConnector cloverConnector;
  private static SaleRequest pendingSale;
  private TextView makeSaleText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_make_sale);
    makeSaleText = (TextView)findViewById(R.id.make_sale_text);

    cloverConnector = MainActivity.getCloverConnector();
    cloverConnector.addCloverConnectorListener(new TestListener(cloverConnector));
    try {
      pendingSale = new SaleRequest(1000, MainActivity.getNextId());
      pendingSale.setCardEntryMethods(CloverConnector.CARD_ENTRY_METHOD_MANUAL | CloverConnector.CARD_ENTRY_METHOD_MAG_STRIPE | CloverConnector.CARD_ENTRY_METHOD_NFC_CONTACTLESS | CloverConnector.CARD_ENTRY_METHOD_ICC_CONTACT);
      String saleInfo = "Making sale request: \n";
      saleInfo += ("  External ID: " + pendingSale.getExternalId()+"\n");
      saleInfo +=("  Amount: " + pendingSale.getAmount()+"\n");
      saleInfo += ("  Tip Amount: " + pendingSale.getTipAmount()+"\n");
      saleInfo += ("  Tax Amount: " + pendingSale.getTaxAmount());
      addText(saleInfo);
      Log.d(TAG,"sale info:" + saleInfo);
      cloverConnector.sale(pendingSale);
    } catch (Exception ex) {
      Log.d(TAG,"Error submitting sale request");
      ex.printStackTrace();
      exit();
    }
  }

  private final class TestListener extends DefaultCloverConnectorListener {
    public TestListener(ICloverConnector cloverConnector) {
      super(cloverConnector);
    }

    @Override
    public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
      Log.d(TAG,"Confirm Payment Request");
      addText("Confirm Payment Request");

      Challenge[] challenges = request.getChallenges();
      if (challenges != null && challenges.length > 0)
      {
        for (Challenge challenge : challenges) {
          Log.d(TAG,"Received a challenge: " + challenge.type);
          addText("Received a challenge: " + challenge.type);
        }
      }

      addText("Automatically processing challenges");
      Log.d(TAG, "Automatically processing challenges");
      cloverConnector.acceptPayment(request.getPayment());
    }

    @Override
    public void onSaleResponse(SaleResponse response) {
      try {
        if (response.isSuccess()) {
          Payment payment = response.getPayment();
          if (payment.getExternalPaymentId().equals(pendingSale.getExternalId())) {
            String saleRequest = ("Sale Request Successful\n");
            saleRequest += ("  ID: " + payment.getId()+"\n");
            saleRequest += ("  External ID: " + payment.getExternalPaymentId()+"\n");
            saleRequest += ("  Order ID: " + payment.getOrder().getId()+"\n");
            saleRequest += ("  Amount: " + payment.getAmount()+"\n");
            saleRequest += ("  Tip Amount: " + payment.getTipAmount()+"\n");
            saleRequest += ("  Tax Amount: " + payment.getTaxAmount()+"\n");
            saleRequest += ("  Offline: " + payment.getOffline()+"\n");
            saleRequest += ("  Authorization Code: " + payment.getCardTransaction().getAuthCode()+"\n");
            saleRequest += ("  Card Type: " + payment.getCardTransaction().getCardType()+"\n");
            saleRequest += ("  Last 4: " + payment.getCardTransaction().getLast4());
            addText(saleRequest);
            Log.d(TAG,"sales request: "+saleRequest);
          } else {
            addText("Sale Request/Response mismatch - " + pendingSale.getExternalId() + " vs " + payment.getExternalPaymentId());
            Log.d(TAG,"Sale Request/Response mismatch - " + pendingSale.getExternalId() + " vs " + payment.getExternalPaymentId());
          }
        } else {
          addText("Sale Request Failed - " + response.getReason());
          Log.d(TAG,"Sale Request Failed - " + response.getReason());
        }
      } catch (Exception ex) {
        Log.d(TAG,"Error handling sale response");
        ex.printStackTrace();
      }
    }

    @Override
    public void onVerifySignatureRequest(VerifySignatureRequest request) {
      super.onVerifySignatureRequest(request);
      addText("Verify Signature Request - Signature automatically accepted by default");
      Log.d(TAG,"Verify Signature Request - Signature automatically accepted by default");
    }


  }

  private void setText(String text){
    final String message = text;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        makeSaleText.setText(message);
      }
    });
  }

  private void addText(String text){
    final String message = "\n\n" + text;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        makeSaleText.append(message);
      }
    });
  }

  private void exit(){
    Log.d(TAG,"exiting");
    cloverConnector.showWelcomeScreen();
    cloverConnector.dispose();
    setText("Disconnected");
    synchronized (this) {
      notifyAll();
    }
  }
}
