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

import com.clover.remote.CardData;
import com.clover.remote.Challenge;
import com.clover.remote.InputOption;
import com.clover.remote.client.CloverConnectorFactory;
import com.clover.remote.client.CloverDeviceConfiguration;
import com.clover.remote.client.DualDisplayCloverDeviceConfiguration;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.ICloverConnectorListener;
import com.clover.remote.client.MerchantInfo;
import com.clover.remote.client.USBCloverDeviceConfiguration;
import com.clover.remote.client.WebSocketCloverDeviceConfiguration;
import com.clover.remote.client.lib.example.model.POSCard;
import com.clover.remote.client.lib.example.model.POSDiscount;
import com.clover.remote.client.lib.example.model.POSItem;
import com.clover.remote.client.lib.example.model.POSMessage;
import com.clover.remote.client.lib.example.model.POSOrder;
import com.clover.remote.client.lib.example.model.POSPayment;
import com.clover.remote.client.lib.example.model.POSRefund;
import com.clover.remote.client.lib.example.model.POSStore;
import com.clover.remote.client.lib.example.model.POSTransaction;
import com.clover.remote.client.lib.example.utils.CurrencyUtils;
import com.clover.remote.client.lib.example.utils.IdUtils;
import com.clover.remote.client.lib.example.utils.ImageUtil;
import com.clover.remote.client.lib.example.utils.SecurityUtils;
import com.clover.remote.client.messages.AuthResponse;
import com.clover.remote.client.messages.CapturePreAuthResponse;
import com.clover.remote.client.messages.CheckBalanceResponse;
import com.clover.remote.client.messages.CloseoutRequest;
import com.clover.remote.client.messages.CloseoutResponse;
import com.clover.remote.client.messages.CloverDeviceErrorEvent;
import com.clover.remote.client.messages.CloverDeviceEvent;
import com.clover.remote.client.messages.ConfirmPaymentRequest;
import com.clover.remote.client.messages.CustomActivityResponse;
import com.clover.remote.client.messages.CustomerProvidedDataEvent;
import com.clover.remote.client.messages.DisplayReceiptOptionsResponse;
import com.clover.remote.client.messages.IncrementPreauthResponse;
import com.clover.remote.client.messages.InvalidStateTransitionResponse;
import com.clover.remote.client.messages.ManualRefundRequest;
import com.clover.remote.client.messages.ManualRefundResponse;
import com.clover.remote.client.messages.MessageFromActivity;
import com.clover.remote.client.messages.OpenCashDrawerRequest;
import com.clover.remote.client.messages.PaymentResponse;
import com.clover.remote.client.messages.PreAuthRequest;
import com.clover.remote.client.messages.PreAuthResponse;
import com.clover.remote.client.messages.PrintJobStatusRequest;
import com.clover.remote.client.messages.PrintJobStatusResponse;
import com.clover.remote.client.messages.PrintManualRefundDeclineReceiptMessage;
import com.clover.remote.client.messages.PrintManualRefundReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentDeclineReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentMerchantCopyReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentReceiptMessage;
import com.clover.remote.client.messages.PrintRefundPaymentReceiptMessage;
import com.clover.remote.client.messages.PrintRequest;
import com.clover.remote.client.messages.ReadCardDataRequest;
import com.clover.remote.client.messages.ReadCardDataResponse;
import com.clover.remote.client.messages.RefundPaymentResponse;
import com.clover.remote.client.messages.ResetDeviceResponse;
import com.clover.remote.client.messages.ResultCode;
import com.clover.remote.client.messages.RetrieveDeviceStatusRequest;
import com.clover.remote.client.messages.RetrieveDeviceStatusResponse;
import com.clover.remote.client.messages.RetrievePaymentResponse;
import com.clover.remote.client.messages.RetrievePendingPaymentsResponse;
import com.clover.remote.client.messages.RetrievePrintersRequest;
import com.clover.remote.client.messages.RetrievePrintersResponse;
import com.clover.remote.client.messages.SaleResponse;
import com.clover.remote.client.messages.SignatureRequest;
import com.clover.remote.client.messages.SignatureResponse;
import com.clover.remote.client.messages.TipAdjustAuthResponse;
import com.clover.remote.client.messages.TipRequest;
import com.clover.remote.client.messages.TipResponse;
import com.clover.remote.client.messages.VaultCardResponse;
import com.clover.remote.client.messages.VerifySignatureRequest;
import com.clover.remote.client.messages.VoidPaymentRefundResponse;
import com.clover.remote.client.messages.VoidPaymentResponse;
import com.clover.remote.message.TipAddedMessage;
import com.clover.sdk.v3.merchant.TipSuggestion;
import com.clover.sdk.v3.payments.CardTransaction;
import com.clover.sdk.v3.payments.CardTransactionState;
import com.clover.sdk.v3.payments.Credit;
import com.clover.sdk.v3.payments.Payment;
import com.clover.sdk.v3.payments.Result;
import com.clover.sdk.v3.printer.Printer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.app.AlertDialog;
import android.app.Dialog;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;

import java.net.URI;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

public class ExamplePOSActivity extends FragmentActivity implements CurrentOrderFragment.OnFragmentInteractionListener,
    AvailableItem.OnFragmentInteractionListener, OrdersFragment.OnFragmentInteractionListener,
    RegisterFragment.OnFragmentInteractionListener, SignatureFragment.OnFragmentInteractionListener,
    CardsFragment.OnFragmentInteractionListener, ManualRefundsFragment.OnFragmentInteractionListener,
    ProcessingFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener, ChooseSaleTypeFragment.ChooseSaleTypeListener,
    PreAuthDialogFragment.PreAuthDialogFragmentListener, EnterTipFragment.EnterTipDialogFragmentListener, AdjustTipFragment.AdjustTipFragmentListener, EnterCustomerNameFragment.EnterCustomerNameListener,
    EnterPaymentIdFragment.EnterPaymentIdFragmentListener, RefundPaymentFragment.PaymentRefundListener, TipSuggestionFragment.TipSuggestionListener {

  private static final String TAG = ExamplePOSActivity.class.getSimpleName();
  public static final String EXAMPLE_POS_SERVER_KEY = "clover_device_endpoint";
  public static final int WS_ENDPOINT_ACTIVITY = 123;
  public static final int SVR_ACTIVITY = 456;
  public static final String EXTRA_CLOVER_CONNECTOR_CONFIG = "EXTRA_CLOVER_CONNECTOR_CONFIG";
  public static final String EXTRA_WS_ENDPOINT = "WS_ENDPOINT";
  public static final String EXTRA_CLEAR_TOKEN = "CLEAR_TOKEN";
  public static final String EXTRA_RAID_ID = "RAID_ID";
  private static final String DEFAULT_EID = "DFLTEMPLYEE";
  private static int RESULT_LOAD_IMG = 1;
  public static List<Printer> printers;
  private Printer printer;
  public static String lastPrintRequestId;
  private int printRequestId = 0;

  // Package name for example custom activities
  public static final String CUSTOM_ACTIVITY_PACKAGE = "com.clover.cfp.examples.";

  private Dialog ratingsDialog;
  private ListView ratingsList;
  private ArrayAdapter<String> ratingsAdapter;

  Payment currentPayment = null;
  Challenge[] currentChallenges = null;
  PaymentConfirmationListener paymentConfirmationListener = new PaymentConfirmationListener() {
    @Override
    public void onRejectClicked(Challenge challenge) { // Reject payment and send the challenge along for logging/reason
      JsonObject rejectMsg = new JsonObject();
      rejectMsg.add("currentPayment", POSMessage.gson.toJsonTree(currentPayment));
      rejectMsg.add("challenge", POSMessage.gson.toJsonTree(challenge));
      recordMessage(new POSMessage("RejectPayment", POSMessage.MessageSrc.POS, rejectMsg));
      cloverConnector.rejectPayment(currentPayment, challenge);
      currentChallenges = null;
      currentPayment = null;
    }

    @Override
    public void onAcceptClicked(final int challengeIndex) {
      if (challengeIndex == currentChallenges.length - 1) { // no more challenges, so accept the payment
        JsonObject acceptMsg = new JsonObject();
        acceptMsg.add("currentPayment", POSMessage.gson.toJsonTree(currentPayment));
        recordMessage(new POSMessage("AcceptPayment", POSMessage.MessageSrc.POS, acceptMsg));
        cloverConnector.acceptPayment(currentPayment);
        currentChallenges = null;
        currentPayment = null;
      } else { // show the next challenge
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            showPaymentConfirmation(paymentConfirmationListener, currentChallenges[challengeIndex + 1], challengeIndex + 1);
          }
        });
      }
    }
  };

  boolean usb = true;

  ICloverConnector cloverConnector;

  POSStore store = new POSStore();
  private AlertDialog pairingCodeDialog;

  private transient CloverDeviceEvent.DeviceEventState lastDeviceEvent;
  private SharedPreferences sharedPreferences;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_example_pos);
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    initStore();

    String posName = "Clover Example POS";
    String applicationId = getIntent().getStringExtra(EXTRA_RAID_ID);
    CloverDeviceConfiguration config;

    String configType = getIntent().getStringExtra(EXTRA_CLOVER_CONNECTOR_CONFIG);
    if ("USB".equals(configType)) {
      config = new USBCloverDeviceConfiguration(this, applicationId);
    } else if ("GMC".equals(configType)){
      config = new DualDisplayCloverDeviceConfiguration(this, applicationId);
    } else if ("WS".equals(configType)) {

      String serialNumber = "Aisle 3";
      String authToken = null;

      URI uri = (URI) getIntent().getSerializableExtra(EXTRA_WS_ENDPOINT);

      String query = uri.getRawQuery();
      if (query != null) {
        try {
          String[] nameValuePairs = query.split("&");
          for (String nameValuePair : nameValuePairs) {
            String[] nameAndValue = nameValuePair.split("=", 2);
            String name = URLDecoder.decode(nameAndValue[0], "UTF-8");
            String value = URLDecoder.decode(nameAndValue[1], "UTF-8");

            if ("authenticationToken".equals(name)) {
              authToken = value;
            } else {
              Log.w(TAG, String.format("Found query parameter \"%s\" with value \"%s\"",
                  name, value));
            }
          }
          uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), null, uri.getFragment());
        } catch (Exception e) {
          Log.e(TAG, "Error extracting query information from uri.", e);
          setResult(RESULT_CANCELED);
          finish();
          return;
        }
      }

      // NOTE:  At the moment, we are always loading our certs from resources.  Opened JIRA SEMI-2147 to
      // add capability to load from the network endpoints dynamically.  Will need to refactor this code
      // to pull network access off the main thread though...
      KeyStore trustStore = SecurityUtils.createTrustStore(true);

      if (authToken == null) {
        boolean clearToken = getIntent().getBooleanExtra(EXTRA_CLEAR_TOKEN, false);
        if (!clearToken) {
          authToken = sharedPreferences.getString("AUTH_TOKEN", null);
        }
      }
      config = new WebSocketCloverDeviceConfiguration(uri, applicationId, trustStore, posName, serialNumber, authToken) {
        @Override
        public int getMaxMessageCharacters() {
          return 0;
        }

        @Override
        public void onPairingCode(final String pairingCode) {
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              // If we previously created a dialog and the pairing failed, reuse
              // the dialog previously created so that we don't get a stack of dialogs
              if (pairingCodeDialog != null) {
                pairingCodeDialog.setMessage("Enter pairing code: " + pairingCode);
              } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ExamplePOSActivity.this);
                builder.setTitle("Pairing Code");
                builder.setMessage("Enter pairing code: " + pairingCode);
                pairingCodeDialog = builder.create();
              }
              pairingCodeDialog.show();
            }
          });
        }

        @Override
        public void onPairingSuccess(String authToken) {
          Preferences.userNodeForPackage(ExamplePOSActivity.class).put("AUTH_TOKEN", authToken);
          sharedPreferences.edit().putString("AUTH_TOKEN", authToken).apply();
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if (pairingCodeDialog != null && pairingCodeDialog.isShowing()) {
                pairingCodeDialog.dismiss();
                pairingCodeDialog = null;
              }
            }
          });
        }
      };
    } else {
      finish();
      return;
    }

    cloverConnector = CloverConnectorFactory.createICloverConnector(config);
    initialize();

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    HomeFragment home = HomeFragment.newInstance(cloverConnector);
    fragmentTransaction.add(R.id.contentContainer, home, "HOME");
    fragmentTransaction.commit();

    ratingsDialog = new Dialog(ExamplePOSActivity.this);
    ratingsDialog.setContentView(R.layout.finalratings_layout);
    ratingsDialog.setCancelable(true);
    ratingsDialog.setCanceledOnTouchOutside(true);
    ratingsList = (ListView) ratingsDialog.findViewById(R.id.ratingsList);
    ratingsAdapter = new ArrayAdapter<>(ExamplePOSActivity.this, android.R.layout.simple_list_item_1, new String[0]);

  }

  public List<Printer> getPrinters() {
    return this.printers;
  }

  private void initStore() {
    // initialize store...
    store.addAvailableItem(new POSItem("0", "Chicken Nuggets", 539, true, true));
    store.addAvailableItem(new POSItem("1", "Hamburger", 699, true, true));
    store.addAvailableItem(new POSItem("2", "Cheeseburger", 759, true, true));
    store.addAvailableItem(new POSItem("3", "Double Hamburger", 819, true, true));
    store.addAvailableItem(new POSItem("4", "Double Cheeseburger", 899, true, true));
    store.addAvailableItem(new POSItem("5", "Bacon Cheeseburger", 999, true, true));
    store.addAvailableItem(new POSItem("6", "Small French Fries", 239, true, true));
    store.addAvailableItem(new POSItem("7", "Medium French Fries", 259, true, true));
    store.addAvailableItem(new POSItem("8", "Large French Fries", 279, true, true));
    store.addAvailableItem(new POSItem("9", "Small Fountain Drink", 169, true, true));
    store.addAvailableItem(new POSItem("10", "Medium Fountain Drink", 189, true, true));
    store.addAvailableItem(new POSItem("11", "Large Fountain Drink", 229, true, true));
    store.addAvailableItem(new POSItem("12", "Chocolate Milkshake", 449, true, true));
    store.addAvailableItem(new POSItem("13", "Vanilla Milkshake", 419, true, true));
    store.addAvailableItem(new POSItem("14", "Strawberry Milkshake", 439, true, true));
    store.addAvailableItem(new POSItem("15", "Ice Cream Cone", 189, true, true));
    store.addAvailableItem(new POSItem("16", "$25 Gift Card", 2500, false, false));
    store.addAvailableItem(new POSItem("17", "$50 Gift Card", 5000, false, false));

    store.addAvailableDiscount(new POSDiscount("10% Off", 0.1f));
    store.addAvailableDiscount(new POSDiscount("$5 Off", 500));
    store.addAvailableDiscount(new POSDiscount("None", 0));

    store.createOrder(false);
    // Per Transaction Settings defaults
    //store.setTipMode(SaleRequest.TipMode.ON_SCREEN_BEFORE_PAYMENT);
    //store.setSignatureEntryLocation(DataEntryLocation.ON_PAPER);
    //store.setDisablePrinting(false);
    //store.setDisableReceiptOptions(false);
    //store.setDisableDuplicateChecking(false);
    //store.setAllowOfflinePayment(false);
    //store.setForceOfflinePayment(false);
    //store.setApproveOfflinePaymentWithoutPrompt(true);
    //store.setAutomaticSignatureConfirmation(true);
    //store.setAutomaticPaymentConfirmation(true);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(TAG, "onActivityResult: requestCode: " + requestCode + " resultCode: " + requestCode + " Intent: " + data);
    if (requestCode == WS_ENDPOINT_ACTIVITY) {
      if (!usb) {
        initialize();
      }
    }
    if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
        && null != data) {
      // Get the Image from data

      Uri selectedImage = data.getData();
      String[] filePathColumn = {MediaStore.Images.Media.DATA};

      // Get the cursor
      Cursor cursor = getContentResolver().query(selectedImage,
          filePathColumn, null, null, null);
      // Move to first row
      cursor.moveToFirst();

      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
      String imgDecodableString = cursor.getString(columnIndex);
      printImage(imgDecodableString);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_parent, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      return true;
    } else if (id == R.id.action_req_res_logs) {
      showMessageLog();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public void initialize() {

    if (cloverConnector != null) {
      cloverConnector.dispose();
    }

    ICloverConnectorListener ccListener = new ICloverConnectorListener() {
      public void onDeviceDisconnected() {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(ExamplePOSActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onDeviceDisconnected");
            ((TextView) findViewById(R.id.ConnectionStatusLabel)).setText("Disconnected");
          }
        });

      }

      public void onDeviceConnected() {

        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            showMessage("Connecting...", Toast.LENGTH_SHORT);
            Log.d(TAG, "onDeviceConnected");
            ((TextView) findViewById(R.id.ConnectionStatusLabel)).setText("Connecting");
          }
        });
      }

      public void onDeviceReady(final MerchantInfo merchantInfo) {
        Log.d(TAG, "onDeviceReady: MerchantInfo: " + merchantInfo.toString());
        runOnUiThread(new Runnable() {
          public void run() {
            if (pairingCodeDialog != null && pairingCodeDialog.isShowing()) {
              pairingCodeDialog.dismiss();
              pairingCodeDialog = null;
            }
            showMessage("Ready!", Toast.LENGTH_SHORT);
            ((TextView) findViewById(R.id.ConnectionStatusLabel)).setText(String.format("Connected: %s (%s)", merchantInfo.getDeviceInfo().getSerial(), merchantInfo.getMerchantName()));
          }
        });
        RetrievePrintersRequest rpr = new RetrievePrintersRequest();
        recordMessage(rpr);
        cloverConnector.retrievePrinters(rpr);
      }

      public void onError(final Exception e) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            showMessage("Error: " + e.getMessage(), Toast.LENGTH_LONG);
          }
        });
      }

      public void onDebug(final String s) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            showMessage("Debug: " + s, Toast.LENGTH_LONG);
          }
        });
      }

      @Override
      public void onDeviceActivityStart(final CloverDeviceEvent deviceEvent) {
        Log.d(TAG, "onDeviceActivityStart: CloverDeviceEvent: " + deviceEvent);
        recordMessage(deviceEvent);
        lastDeviceEvent = deviceEvent.getEventState();
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            ((TextView) findViewById(R.id.DeviceStatus)).setText(deviceEvent.getMessage());
            Toast.makeText(ExamplePOSActivity.this, deviceEvent.getMessage(), Toast.LENGTH_SHORT).show();
            LinearLayout ll = (LinearLayout) findViewById(R.id.DeviceOptionsPanel);
            ll.removeAllViews();

            for (final InputOption io : deviceEvent.getInputOptions()) {
              Button btn = new Button(ExamplePOSActivity.this);
              btn.setText(io.description);
              btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  JsonObject ioMsg = new JsonObject();
                  ioMsg.add("inputOption", POSMessage.gson.toJsonTree(io));
                  recordMessage(new POSMessage("InvokeInputOption", POSMessage.MessageSrc.POS, ioMsg));
                  cloverConnector.invokeInputOption(io);
                }
              });
              ll.addView(btn);
            }
          }
        });
      }

      @Override
      public void onReadCardDataResponse(final ReadCardDataResponse response) {
        Log.d(TAG, "onReadCardDataResponse: " + response.toString());
        recordMessage(response);
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ExamplePOSActivity.this);
            builder.setTitle("Read Card Data Response");
            if (response.isSuccess()) {

              LayoutInflater inflater = ExamplePOSActivity.this.getLayoutInflater();

              View view = inflater.inflate(R.layout.card_data_table, null);
              ListView listView = (ListView) view.findViewById(R.id.cardDataListView);


              if (listView != null) {
                class RowData {
                  RowData(String label, String value) {
                    this.text1 = label;
                    this.text2 = value;
                  }

                  String text1;
                  String text2;
                }

                ArrayAdapter<RowData> data = new ArrayAdapter<RowData>(getBaseContext(), android.R.layout.simple_list_item_2) {
                  @Override
                  public View getView(int position, View convertView, ViewGroup parent) {
                    View v = convertView;

                    if (v == null) {
                      LayoutInflater vi;
                      vi = LayoutInflater.from(getContext());
                      v = vi.inflate(android.R.layout.simple_list_item_2, null);
                    }

                    RowData rowData = getItem(position);

                    if (rowData != null) {
                      TextView primaryColumn = (TextView) v.findViewById(android.R.id.text1);
                      TextView secondaryColumn = (TextView) v.findViewById(android.R.id.text2);

                      primaryColumn.setText(rowData.text2);
                      secondaryColumn.setText(rowData.text1);
                    }

                    return v;
                  }
                };
                listView.setAdapter(data);
                CardData cardData = response.getCardData();
                data.addAll(new RowData("Encrypted", cardData.encrypted + ""));
                data.addAll(new RowData("Cardholder Name", cardData.cardholderName));
                data.addAll(new RowData("First Name", cardData.firstName));
                data.addAll(new RowData("Last Name", cardData.lastName));
                data.addAll(new RowData("Expiration", cardData.exp));
                data.addAll(new RowData("First 6", cardData.first6));
                data.addAll(new RowData("Last 4", cardData.last4));
                data.addAll(new RowData("Track 1", cardData.track1));
                data.addAll(new RowData("Track 2", cardData.track2));
                data.addAll(new RowData("Track 3", cardData.track3));
                data.addAll(new RowData("Masked Track 1", cardData.maskedTrack1));
                data.addAll(new RowData("Masked Track 2", cardData.maskedTrack2));
                data.addAll(new RowData("Masked Track 3", cardData.maskedTrack3));
                data.addAll(new RowData("Pan", cardData.pan));

              }
              builder.setView(view);

            } else if (response.getResult() == ResultCode.CANCEL) {
              builder.setMessage("Get card data canceled.");
            } else {
              builder.setMessage("Error getting card data. " + response.getReason() + ": " + response.getMessage());
            }

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

          }
        });
      }

      @Override
      public void onDeviceActivityEnd(final CloverDeviceEvent deviceEvent) {
        Log.d(TAG, "onDeviceActivityEnd: CloverDeviceEvent: " + deviceEvent);
        recordMessage(deviceEvent);
        if (deviceEvent.getEventState() == lastDeviceEvent) {
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              ((TextView) findViewById(R.id.DeviceStatus)).setText("");
              LinearLayout ll = (LinearLayout) findViewById(R.id.DeviceOptionsPanel);
              ll.removeAllViews();
            }
          });
        }
      }

      @Override
      public void onDeviceError(CloverDeviceErrorEvent deviceErrorEvent) {
        Log.d(TAG, "onDeviceError: CloverDeviceErrorEvent: " + deviceErrorEvent);
        recordMessage(deviceErrorEvent);
        showMessage("DeviceError: " + deviceErrorEvent.getMessage(), Toast.LENGTH_LONG);
      }

      @Override
      public void onAuthResponse(final AuthResponse response) {
        Log.d(TAG, "onAuthResponse: " + response.toString());
        recordMessage(response);
        if (response != null) {
          if (response.isSuccess()) {
            if (response.getPayment() != null) {
              Payment _payment = response.getPayment();
              CardTransaction cardTransaction = _payment.getCardTransaction();
              String cardDetails;
              if (cardTransaction.getCardType() != null) {
                cardDetails = cardTransaction.getCardType().toString() + " " + cardTransaction.getLast4();
              } else {
                //This is for custom tenders
                cardDetails = _payment.getTender().getLabel();
              }
              long cashback = _payment.getCashbackAmount() == null ? 0 : _payment.getCashbackAmount();
              long tip = _payment.getTipAmount() == null ? 0 : _payment.getTipAmount();
              Log.d("AuthResponse External: ", _payment.getExternalPaymentId());
              if (_payment.getExternalPaymentId().equals(store.getCurrentOrder().getPendingPaymentId())) {
                POSPayment payment = new POSPayment(_payment.getAmount(), cardDetails, cardTransaction.getCardType(), new Date(_payment.getCreatedTime()), _payment.getId(), _payment.getTender().getLabel(),
                    "Auth", cardTransaction.getType(), false, cardTransaction.getEntryType(), cardTransaction.getState(), cashback, _payment.getOrder().getId(), _payment.getExternalPaymentId(), _payment.getTaxAmount(), tip, _payment.getAdditionalCharges());
                setPaymentStatus(payment, response);
                payment.setResult(response.getResult() == ResultCode.SUCCESS ? Result.SUCCESS : Result.FAIL);
                if (_payment.isNotEmptyIncrements()) {
                  payment.setIncrements(_payment.getIncrements());
                }

                store.addPaymentToOrder(payment, store.getCurrentOrder());
                store.addTransaction(payment);
                showMessage("Auth successfully processed.", Toast.LENGTH_SHORT);
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    RegisterFragment registerFragment = (RegisterFragment) getSupportFragmentManager().findFragmentByTag("REGISTER");
                    if(store.getCurrentOrder().isCompleted()) {
                      store.createOrder(false);
                      registerFragment.setOrder(store.getCurrentOrder());
                      showRegister(null);
                    } else {
                      registerFragment.onPartiallyPaid();
                    }
                  }
                });
              }
            } else {
              externalMismatch();
            }
          } else {
            if (response.getResult() != ResultCode.CANCEL) {
              showMessage("Auth error:" + response.getResult(), Toast.LENGTH_LONG);

              String msg = "There was a problem processing the transaction";
              JsonObject jsonMsg = new JsonObject();
              jsonMsg.addProperty("message", msg);
              recordMessage(new POSMessage("ShowMessage", POSMessage.MessageSrc.POS, jsonMsg));
              cloverConnector.showMessage(msg);
            } else {
              showMessage("Request was cancelled", Toast.LENGTH_LONG);
            }
          }
        } else {
          showMessage("Error: Null AuthResponse", Toast.LENGTH_LONG);
        }
        SystemClock.sleep(3000);
        recordMessage("ShowWelcomeScreen");
        cloverConnector.showWelcomeScreen();
      }

      @Override
      public void onPreAuthResponse(final PreAuthResponse response) {
        Log.d(TAG, "onPreAuthResponse: " + response.toString());
        recordMessage(response);

        if (response.isSuccess()) {
          Payment _payment = response.getPayment();
          if (_payment.getExternalPaymentId().equals(store.getCurrentOrder().getPendingPaymentId())) {
            CardTransaction cardTransaction = _payment.getCardTransaction();
            String cardDetails = cardTransaction.getCardType().toString() + " " + cardTransaction.getLast4();
            long cashback = _payment.getCashbackAmount() == null ? 0 : _payment.getCashbackAmount();
            long tip = _payment.getTipAmount() == null ? 0 : _payment.getTipAmount();
            POSPayment payment = new POSPayment(_payment.getAmount(), cardDetails, cardTransaction.getCardType(), new Date(_payment.getCreatedTime()), _payment.getId(), _payment.getTender().getLabel(),
                "Auth", cardTransaction.getType(), false, cardTransaction.getEntryType(), cardTransaction.getState(), cashback, _payment.getOrder().getId(), _payment.getExternalPaymentId(), _payment.getTaxAmount(), tip, _payment.getAdditionalCharges());
            setPaymentStatus(payment, response);
            payment.setResult(response.getResult() == ResultCode.SUCCESS ? Result.SUCCESS : Result.FAIL);
            if (_payment.isNotEmptyIncrements()) {
              payment.setIncrements(_payment.getIncrements());
            }

            showMessage("PreAuth successfully processed.", Toast.LENGTH_SHORT);

            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                store.getCurrentOrder().setPreAuth(payment);
                store.addTransaction(payment);
                showPreauthInfo(payment);
              }
            });
          } else {
            externalMismatch();
          }
        } else {
          showMessage("PreAuth: " + response.getResult(), Toast.LENGTH_LONG);
        }
        SystemClock.sleep(3000);
        recordMessage("ShowWelcomeScreen");
        cloverConnector.showWelcomeScreen();
      }

      @Override
      public void onIncrementPreAuthResponse(IncrementPreauthResponse response) {
        Log.d(TAG, "onIncrementPreAuthResponse: " + response.toString());
        recordMessage(response);

        if (!response.isSuccess()) {
          showMessage(" Increase Pre-auth: %@" + response.getResult(), Toast.LENGTH_SHORT);
          return;
        }


        if (response.getAuthorization() == null) {
          showMessage(" Increase Pre-auth: didn't return a valid Pre-auth", Toast.LENGTH_SHORT);
          return;
        }

        Payment preauthPayment = response.getAuthorization().getPayment();
        if (preauthPayment == null) {
          showMessage("Increase Pre-auth: didn't return a valid payment", Toast.LENGTH_SHORT);
          return;
        }

        if (!preauthPayment.getExternalPaymentId().equals(store.getCurrentOrder().getPendingPaymentId())) {
          externalMismatch();
          return;
        }

        CardTransaction cardTransaction = preauthPayment.getCardTransaction();
        String cardDetails = cardTransaction.getCardType().toString() + " " + cardTransaction.getLast4();
        long cashback = preauthPayment.getCashbackAmount() == null ? 0 : preauthPayment.getCashbackAmount();
        long tip = preauthPayment.getTipAmount() == null ? 0 : preauthPayment.getTipAmount();

        POSPayment payment = new POSPayment(preauthPayment.getAmount(), cardDetails, cardTransaction.getCardType(), new Date(preauthPayment.getCreatedTime()), preauthPayment.getId(), preauthPayment.getTender().getLabel(),
            "Auth", cardTransaction.getType(), false, cardTransaction.getEntryType(), cardTransaction.getState(), cashback, preauthPayment.getOrder().getId(), preauthPayment.getExternalPaymentId(), preauthPayment.getTaxAmount(), tip, preauthPayment.getAdditionalCharges());
        setPaymentStatus(payment, response);
        payment.setResult(response.getResult() == ResultCode.SUCCESS ? Result.SUCCESS : Result.FAIL);

        if (preauthPayment.isNotEmptyIncrements()) {
          payment.setIncrements(preauthPayment.getIncrements());
        }

        showMessage("Increment Preauth successfully processed.", Toast.LENGTH_SHORT);

        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            store.getCurrentOrder().setPreAuth(payment);
            store.updateTransaction(payment);
            showPreauthInfo(payment);
          }
        });
      }

      @Override
      public void onRetrievePendingPaymentsResponse(RetrievePendingPaymentsResponse response) {
        Log.d(TAG, "onRetrievePendingPaymentsResponse: " + response.toString());
        recordMessage(response);
        if (!response.isSuccess()) {
          store.setPendingPayments(null);
        } else {
          store.setPendingPayments(response.getPendingPayments());
        }
      }

      @Override
      public void onTipAdjustAuthResponse(TipAdjustAuthResponse response) {
        Log.d(TAG, "onTipAdjustAuthResponse: " + response.toString());
        recordMessage(response);
        if (response.isSuccess()) {
          boolean updatedTip = false;
          for (POSOrder order : store.getOrders()) {
            for (POSTransaction exchange : order.getPayments()) {
              if (exchange instanceof POSPayment) {
                POSPayment posPayment = (POSPayment) exchange;
                if (exchange.getId().equals(response.getPaymentId())) {
                  posPayment.setTipAmount(response.getTipAmount());
                  updatePaymentDetailsTip(posPayment);
                  // TODO: should the stats be updated?
                  updatedTip = true;
                  break;
                }
              }
            }
            if (updatedTip) {
              showMessage("Tip successfully adjusted", Toast.LENGTH_LONG);
              break;
            }
          }
        } else {
          showMessage("Tip adjust failed", Toast.LENGTH_LONG);
        }
      }

      @Override
      public void onCapturePreAuthResponse(final CapturePreAuthResponse response) {
        Log.d(TAG, "onCapturePreAuthResponse: " + response);
        recordMessage(response);

        if (!response.isSuccess()) {
          showMessage("PreAuth Capture Error: Payment failed with response code = " + response.getResult() + " and reason: " + response.getReason(), Toast.LENGTH_LONG);
        }

        for (final POSOrder order : store.getOrders()) {
          final POSPayment payment = order.getPreAuth();
          if (payment != null) {
            if (payment.getId().equals(response.getPaymentId())) {
              final long paymentAmount = response.getAmount();
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  order.setPreAuth(null);
                  payment.setTransactionState(CardTransactionState.CLOSED);
                  store.addPaymentToOrder(payment, store.getCurrentOrder());
                  payment.setPaymentStatus(POSPayment.Status.AUTHORIZED);
                  payment.setAmount(paymentAmount);
                  payment.setTipAmount(response.getTipAmount());
                  store.updateTransaction(payment);
                  showMessage("Sale successfully processing using Pre Authorization", Toast.LENGTH_LONG);
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      store.createOrder(false);
                      RegisterFragment registerFragment = (RegisterFragment) getSupportFragmentManager().findFragmentByTag("REGISTER");
                      registerFragment.setOrder(store.getCurrentOrder());
                      registerFragment.clearPreAuth();
                      recordMessage("ShowWelcomeScreen");
                      cloverConnector.showWelcomeScreen();
                      showRegister(null);
                    }
                  });
                }
              });
              break;
            } else {
              showMessage("PreAuth Capture: Payment received does not match any of the stored PreAuth records", Toast.LENGTH_LONG);
            }
          }
        }
      }


      @Override
      public void onVerifySignatureRequest(final VerifySignatureRequest request) {
        Log.d(TAG, "onVerifySignatureRequest: " + request.toString());
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            FragmentManager fm = getSupportFragmentManager();
            SignatureFragment signatureFragment = SignatureFragment.newInstance(request, cloverConnector);
            signatureFragment.show(fm, "fragment_signature");
          }
        });
      }

      @Override
      public void onMessageFromActivity(MessageFromActivity message) {
        Log.d(TAG, "onMessageFromActivity: " + message.toString());
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("CUSTOM");
        ((CustomActivitiesFragment) fragment).addMessage(message);
      }

      @Override
      public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
        Log.d(TAG, "onConfirmPaymentRequest: " + request.toString());
        if (request.getPayment() == null || request.getChallenges() == null) {
          showMessage("Error: The ConfirmPaymentRequest was missing the payment and/or challenges.", Toast.LENGTH_LONG);
        } else {
          currentPayment = request.getPayment();
          currentChallenges = request.getChallenges();
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              showPaymentConfirmation(paymentConfirmationListener, currentChallenges[0], 0);
            }
          });
        }
      }

      @Override
      public void onCloseoutResponse(CloseoutResponse response) {
        Log.d(TAG, "onCloseoutResponse: " + response.toString());
        recordMessage(response);
        if (response.isSuccess()) {
          showMessage("Closeout is scheduled.", Toast.LENGTH_SHORT);
        } else {
          showMessage("Error scheduling closeout: " + response.getResult(), Toast.LENGTH_LONG);
        }
      }

      @Override
      public void onSaleResponse(final SaleResponse response) {
        Log.d(TAG, "onSaleResponse: " + response.toString());
        if (response != null) {
          recordMessage(response);
          if (response.isSuccess()) { // Handle cancel response
            if (response.getPayment() != null) {
              Payment _payment = response.getPayment();
              CardTransaction cardTransaction = _payment.getCardTransaction();
              String cardDetails;
              if (cardTransaction.getCardType() != null) {
                cardDetails = cardTransaction.getCardType().toString() + " " + cardTransaction.getLast4();
              } else {
                //This is for custom tenders
                cardDetails = _payment.getTender().getLabel();
              }
              long cashback = _payment.getCashbackAmount() == null ? 0 : _payment.getCashbackAmount();
              long tip = _payment.getTipAmount() == null ? 0 : _payment.getTipAmount();
              Log.d(TAG, "payment external: " + _payment.getExternalPaymentId());
              if (_payment.getExternalPaymentId().equals(store.getCurrentOrder().getPendingPaymentId())) {
                POSPayment payment = new POSPayment(_payment.getAmount(), cardDetails, cardTransaction.getCardType(), new Date(_payment.getCreatedTime()), _payment.getId(), _payment.getTender().getLabel(),
                    "Payment", cardTransaction.getType(), false, cardTransaction.getEntryType(), cardTransaction.getState(), cashback, _payment.getOrder().getId(), _payment.getExternalPaymentId(), _payment.getTaxAmount(), tip, _payment.getAdditionalCharges());
                setPaymentStatus(payment, response);
                payment.setResult(response.getResult() == ResultCode.SUCCESS ? Result.SUCCESS : Result.FAIL);
                if (_payment.isNotEmptyIncrements()) {
                  payment.setIncrements(_payment.getIncrements());
                }

                store.addPaymentToOrder(payment, store.getCurrentOrder());
                store.addTransaction(payment);
                showMessage("Sale successfully processed", Toast.LENGTH_SHORT);
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    RegisterFragment registerFragment = (RegisterFragment) getSupportFragmentManager().findFragmentByTag("REGISTER");
                    if(store.getCurrentOrder().isCompleted()) {
                      store.createOrder(false);
                      registerFragment.setOrder(store.getCurrentOrder());
                      showRegister(null);
                    } else {
                      registerFragment.onPartiallyPaid();
                    }
                  }
                });
              } else {
                externalMismatch();
              }
            } else { // Handle null payment
              showMessage("Error: Sale response was missing the payment", Toast.LENGTH_LONG);
            }
          } else {
            if (response.getResult() != ResultCode.CANCEL) {
              showMessage("Sale error:" + response.getResult().toString() + ":" + response.getReason() + "  " + response.getMessage(), Toast.LENGTH_LONG);

              String msg = "There was a problem processing the transaction";
              JsonObject jsonMsg = new JsonObject();
              jsonMsg.addProperty("message", msg);
              recordMessage(new POSMessage("ShowMessage", POSMessage.MessageSrc.POS, jsonMsg));
              cloverConnector.showMessage(msg);
            } else {
              showMessage("Request was cancelled", Toast.LENGTH_LONG);
            }
          }
        } else { //Handle null payment response
          showMessage("Error: Null SaleResponse", Toast.LENGTH_LONG);
        }
        SystemClock.sleep(3000);
        recordMessage("ShowWelcomeScreen");
        cloverConnector.showWelcomeScreen();
      }

      @Override
      public void onManualRefundResponse(final ManualRefundResponse response) {
        Log.d(TAG, "onManualRefundResponse: " + response.toString());

        recordMessage(response);
        if (response.isSuccess()) {
          Credit credit = response.getCredit();
          CardTransaction cardTransaction = credit.getCardTransaction();
          String cardDetails;
          if (cardTransaction.getCardType() != null) {
            cardDetails = cardTransaction.getCardType().toString() + " " + cardTransaction.getLast4();
          } else {
            //This is for custom tenders
            cardDetails = response.getCredit().getTender().getLabel();
          }
          final POSTransaction nakedRefund = new POSTransaction(credit.getAmount(), cardDetails, cardTransaction.getCardType(), new Date(credit.getCreatedTime()), credit.getId(),
              credit.getTender().getLabel(), "Manual Refund", cardTransaction.getType(), true, cardTransaction.getEntryType(), cardTransaction.getState());
          nakedRefund.setEmployee(credit.getEmployee().getId());
          nakedRefund.setResult(response.getResult() == ResultCode.SUCCESS ? Result.SUCCESS : Result.FAIL);
          store.addTransaction(nakedRefund);
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              store.addRefund(nakedRefund);
              showMessage("Manual Refund successfully processed", Toast.LENGTH_SHORT);
            }
          });
        } else if (response.getResult() == ResultCode.CANCEL) {
          showMessage("User canceled the Manual Refund", Toast.LENGTH_SHORT);
        } else {
          showMessage("Manual Refund Failed with code: " + response.getResult() + " - " + response.getMessage(), Toast.LENGTH_LONG);
        }
      }

      @Override
      public void onRefundPaymentResponse(final RefundPaymentResponse response) {
        Log.d(TAG, "onRefundPaymentResponse: " + response.toString());
        recordMessage(response);
        if (response.isSuccess()) {
          POSRefund refund = new POSRefund(response.getRefund().getId(), response.getRefund().getPayment().getId(), response.getOrderId(), "DEFAULT", response.getRefund().getAmount());
          refund.setDate(new Date(response.getRefund().getCreatedTime()));
          boolean done = false;

          for (POSOrder order : store.getOrders()) {
            for (POSTransaction payment : order.getPayments()) {
              if (payment instanceof POSPayment) {
                if (payment.getId().equals(response.getRefund().getPayment().getId())) {
                  refund.setCardDetails(payment.getCardDetails());
                  refund.setCardType(payment.getCardType());
                  refund.setTender(payment.getTender());
                  refund.setResult(response.getResult() == ResultCode.SUCCESS ? Result.SUCCESS : Result.FAIL);
                  refund.setTransactionTitle("Refund");
                  store.addTransaction(refund);
                  store.addRefundToOrder(refund, order);
                  ((POSPayment) payment).setRefundId(refund.getId());
                  updatePaymentDetailsRefund(refund, refund.getAmount());
                  showMessage("Payment successfully refunded", Toast.LENGTH_SHORT);
                  done = true;
                  break;
                }
              }
            }
            if (done) {
              break;
            }
          }
        } else {
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              AlertDialog.Builder builder = new AlertDialog.Builder(ExamplePOSActivity.this);
              builder.setTitle("Refund Error").setMessage("There was an error refunding the payment");
              builder.create().show();
              Log.d(getClass().getName(), "Got refund response of " + response.getReason());
            }
          });
        }
      }


      @Override
      public void onTipAdded(TipAddedMessage message) {
        Log.d(TAG, "onTipAdded: " + message.toString());
        if (message.tipAmount > 0) {
          tipAdded(message.tipAmount);
          showMessage("Tip successfully added: " + CurrencyUtils.format(message.tipAmount, Locale.getDefault()), Toast.LENGTH_SHORT);
        }
      }

      @Override
      public void onVoidPaymentResponse(VoidPaymentResponse response) {
        Log.d(TAG, "onVoidPaymentResponse: " + response.toString());
        recordMessage(response);
        if (response.isSuccess()) {
          boolean done = false;
          for (POSOrder order : store.getOrders()) {
            for (POSTransaction payment : order.getPayments()) {
              if (payment instanceof POSPayment) {
                if (payment.getId().equals(response.getPaymentId())) {
                  ((POSPayment) payment).setPaymentStatus(POSPayment.Status.VOIDED);
                  showMessage("Payment was voided", Toast.LENGTH_SHORT);
                  updatePaymentDetailsVoided(payment);
                  done = true;
                  break;
                }
              }
            }
            if (done) {
              break;
            }
          }
        } else {
          showMessage(getClass().getName() + ":Got VoidPaymentResponse of " + response.getResult() + " : " + response.getMessage(), Toast.LENGTH_LONG);
        }
      }

      /**
       * Called in response to a void payment refund request
       *
       * @param response The response
       */
      @Override
      public void onVoidPaymentRefundResponse(final VoidPaymentRefundResponse response) {
        Log.d(TAG, "onVoidPaymentRefundResponse: " + response.toString());
        recordMessage(response);
        if (response.isSuccess()) {
          boolean done = false;

          for (POSOrder order : store.getOrders()) {
            for (POSTransaction payment : order.getPayments()) {
              if (payment instanceof POSPayment) {
                if (((POSPayment) payment).getRefundId().equals(response.getRefundId())) {
                  updatePaymentDetailsRefundVoid(payment, response.getRefundId());
                  showMessage("Payment Refund successfully voided", Toast.LENGTH_SHORT);
                  done = true;
                  break;
                }
              }
            }
            if (done) {
              break;
            }
          }
        } else {
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              AlertDialog.Builder builder = new AlertDialog.Builder(ExamplePOSActivity.this);
              builder.setTitle("Void Payment Refund Error").setMessage("There was an error voiding the payment refund");
              builder.create().show();
              Log.d(getClass().getName(), "Got void payment refund response of " + response.getReason());
            }
          });
        }
        recordMessage("ShowWelcomeScreen");
        cloverConnector.showWelcomeScreen();
      }

      @Override
      public void onVaultCardResponse(final VaultCardResponse response) {
        Log.d(TAG, "onVaultCardResponse" + response.toString());
        recordMessage(response);
        if (response.isSuccess()) {
          POSCard card = new POSCard();
          card.setFirst6(response.getCard().getFirst6());
          card.setLast4(response.getCard().getLast4());
          card.setName(response.getCard().getCardholderName());
          card.setMonth(response.getCard().getExpirationDate().substring(0, 2));
          card.setYear(response.getCard().getExpirationDate().substring(2, 4));
          card.setToken(response.getCard().getToken());
          store.addCard(card);
          showMessage("Card successfully vaulted", Toast.LENGTH_SHORT);
        } else {
          if (response.getResult() == ResultCode.CANCEL) {
            showMessage("User canceled the operation", Toast.LENGTH_SHORT);
            recordMessage("ShowWelcomeScreen");
            cloverConnector.showWelcomeScreen();
          } else {
            showMessage("Error capturing card: " + response.getResult(), Toast.LENGTH_LONG);
            String msg = "Card was not saved";
            JsonObject jsonMsg = new JsonObject();
            jsonMsg.addProperty("message", msg);
            recordMessage(new POSMessage("ShowMessage", POSMessage.MessageSrc.POS, jsonMsg));
            cloverConnector.showMessage(msg);
            SystemClock.sleep(4000); //wait 4 seconds
            recordMessage("ShowWelcomeScreen");
            cloverConnector.showWelcomeScreen();
          }
        }
      }

      @Override
      public void onPrintJobStatusResponse(PrintJobStatusResponse response) {
        Log.d(TAG, "onPrintJobStatusResponse: " + response.toString());
        recordMessage(response);
        showMessage("PrintJobStatus: " + response.getStatus(), Toast.LENGTH_SHORT);
      }

      @Override
      public void onRetrievePrintersResponse(RetrievePrintersResponse response) {
        Log.d(TAG, "onRetrievePrintersResponse: " + response.toString());
        recordMessage(response);
        printers = response.getPrinters();
        if (printers != null) {
          printer = printers.get(0);
        }
      }

      @Override
      public void onPrintManualRefundReceipt(PrintManualRefundReceiptMessage pcm) {
        Log.d(TAG, "onPrintManualRefundReceipt: " + pcm.toString());
        showMessage("Print Request for ManualRefund", Toast.LENGTH_SHORT);
      }

      @Override
      public void onPrintManualRefundDeclineReceipt(PrintManualRefundDeclineReceiptMessage pcdrm) {
        Log.d(TAG, "onPrintManualRefundDeclineReceipt: " + pcdrm.toString());
        showMessage("Print Request for Declined ManualRefund", Toast.LENGTH_SHORT);
      }

      @Override
      public void onPrintPaymentReceipt(PrintPaymentReceiptMessage pprm) {
        Log.d(TAG, "onPrintPaymentReceipt: " + pprm.toString());
        showMessage("Print Request for Payment Receipt", Toast.LENGTH_SHORT);
      }

      @Override
      public void onPrintPaymentDeclineReceipt(PrintPaymentDeclineReceiptMessage ppdrm) {
        Log.d(TAG, "onPrintPaymentDeclineReceipt: " + ppdrm.toString());
        showMessage("Print Request for DeclinedPayment Receipt", Toast.LENGTH_SHORT);
      }

      @Override
      public void onPrintPaymentMerchantCopyReceipt(PrintPaymentMerchantCopyReceiptMessage ppmcrm) {
        Log.d(TAG, "onPrintPaymentMerchantCopyReceipt: " + ppmcrm.toString());
        showMessage("Print Request for MerchantCopy of a Payment Receipt", Toast.LENGTH_SHORT);
      }

      @Override
      public void onPrintRefundPaymentReceipt(PrintRefundPaymentReceiptMessage pprrm) {
        Log.d(TAG, "onPrintRefundPaymentReceipt: " + pprrm.toString());
        showMessage("Print Request for RefundPayment Receipt", Toast.LENGTH_SHORT);
      }

      @Override
      public void onCustomActivityResponse(CustomActivityResponse response) {
        Log.d(TAG, "onCustomActivityResponse: " + response.toString());
        recordMessage(response);
        String message = "";
        if (response.isSuccess()) {
          message = "Success! Got: " + response.getPayload() + " from CustomActivity" + response.getAction();
          showMessage(message, Toast.LENGTH_LONG);
        } else {
          if (response.getResult().equals(ResultCode.CANCEL)) {
            message = "Failure! Custom activity: " + response.getAction() + " failed. Reason: " + response.getReason();
            showMessage(message, Toast.LENGTH_LONG);
          } else {
            message = "Custom activity: " + response.getAction() + " was canceled. Reason: " + response.getReason();
            showMessage(message, Toast.LENGTH_LONG);
          }
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("CUSTOM");
        ((CustomActivitiesFragment) fragment).onFinalMessage(response.getPayload());
      }

      @Override
      public void onRetrieveDeviceStatusResponse(RetrieveDeviceStatusResponse response) {
        Log.d(TAG, "onRetrieveDeviceStatusResponse: " + response.toString());
        recordMessage(response);
        showPopupMessage("Device Status", new String[]{response.isSuccess() ? "Success!" : "Failed!",
            "State: " + response.getState(), "ExternalActivityId: " + response.getData().toString(), "Reason: " + response.getReason()}, false);
      }

      @Override
      public void onInvalidStateTransitionResponse(InvalidStateTransitionResponse invalidStateTransitionResponse) {
        Log.d(TAG, "onInvalidStateTransitionResponse: " + invalidStateTransitionResponse.toString());
        recordMessage(invalidStateTransitionResponse);
        showPopupMessage("Invalid State Transition", new String[]{invalidStateTransitionResponse.isSuccess() ? "Success!" : "Failed!",
            "State: " + invalidStateTransitionResponse.getState(), "RequestedTransition: " + invalidStateTransitionResponse.getRequestedTransition(), "Reason: " + invalidStateTransitionResponse.getReason()}, false);
      }


      @Override
      public void onResetDeviceResponse(ResetDeviceResponse response) {
        Log.d(TAG, "onResetDeviceResponse: " + response.toString());
        recordMessage(response);
        showPopupMessage("Reset Device", new String[]{response.isSuccess() ? "Success!" : "Failed!", "State: " + response.getState(), "Reason: " + response.getReason()}, false);
      }

      @Override
      public void onRetrievePaymentResponse(RetrievePaymentResponse response) {
        Log.d(TAG, "onRetrievePaymentResponse: " + response.toString());
        recordMessage(response);
        if (response.isSuccess()) {
          showPopupMessage("Retrieve Payment", new String[]{"Retrieve Payment successful for Payment ID: " + response.getExternalPaymentId(),
              " QueryStatus: " + response.getQueryStatus(),
              " Payment: " + response.getPayment(),
              " reason: " + response.getReason()}, false);
        } else {
          showPopupMessage(null, new String[]{"Retrieve Payment error: " + response.getResult()}, false);
        }
      }

      @Override
      public void onCustomerProvidedData(CustomerProvidedDataEvent event) {

      }

      @Override
      public void onDisplayReceiptOptionsResponse(DisplayReceiptOptionsResponse response) {
        Log.d(TAG, "onDisplayReceiptOptionsResponse: " + response.toString());
        recordMessage(response);
        showMessage("Display Receipt Options", Toast.LENGTH_SHORT);
      }

      @Override
      public void onRequestSignatureResponse(SignatureResponse response) {
        Log.d(TAG, "onRequestSignatureResponse: " + response.toString());
        recordMessage(response);
        showMessage(String.format("Signature Response with %d segments", response.getSignature().strokes.size()), Toast.LENGTH_SHORT);

      }

      @Override
      public void onCheckBalanceResponse(CheckBalanceResponse response) {

      }

      @Override
      public void onRequestTipResponse(TipResponse response) {
        Log.d(TAG, "onRequestTipResponse: " + response.toString());
        recordMessage(response);
        showMessage(String.format("Tip Selected: %d", response.getTipAmount()), Toast.LENGTH_SHORT);

      }
    };
    cloverConnector.addCloverConnectorListener(ccListener);
    cloverConnector.initializeConnection();
    updateComponentsWithNewCloverConnector();
  }

  private void setPaymentStatus(POSPayment payment, PaymentResponse response) {
    if (response.isSale()) {
      payment.setPaymentStatus(POSPayment.Status.PAID);
    } else if (response.isAuth()) {
      payment.setPaymentStatus(POSPayment.Status.AUTHORIZED);
    } else if (response.isPreAuth()) {
      payment.setPaymentStatus(POSPayment.Status.PREAUTHORIZED);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (cloverConnector != null) {
      cloverConnector.dispose();
    }
  }

  @Override
  public void onFragmentInteraction(Uri uri) {

  }

  private void showPaymentConfirmation(PaymentConfirmationListener listenerIn, final Challenge challengeIn, int challengeIndexIn) {
    final int challengeIndex = challengeIndexIn;
    final Challenge challenge = challengeIn;
    final PaymentConfirmationListener listener = listenerIn;
    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(this);
    confirmationDialog.setTitle("Payment Confirmation");
    confirmationDialog.setCancelable(false);
    confirmationDialog.setMessage(challenge.message);
    confirmationDialog.setNegativeButton("Reject", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        Log.d(TAG, "Rejecting Challenge: " + challengeIn.toString());
        listener.onRejectClicked(challenge);
        dialog.dismiss();
      }
    });
    confirmationDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        Log.d(TAG, "Accepting Challenge: " + challengeIn.toString());
        listener.onAcceptClicked(challengeIndex);
        dialog.dismiss();
      }
    });
    confirmationDialog.show();
  }


  private void showMessage(final String msg, final int duration) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(ExamplePOSActivity.this, msg, duration).show();
      }
    });
  }

  protected void showPopupMessage(final String title, final String[] content, final boolean monospace) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        PopupMessageFragment popupMessageFragment = PopupMessageFragment.newInstance(title, content, monospace);
        FragmentManager fm = getSupportFragmentManager();
        popupMessageFragment.show(fm, "fragment_popup_message");
      }
    });
  }

  public void showSettings(MenuItem item) {
    if (!usb) {
      Intent intent = new Intent(this, ExamplePOSSettingsActivity.class);
      startActivityForResult(intent, WS_ENDPOINT_ACTIVITY);
    }
  }

  private void updateComponentsWithNewCloverConnector() {
    FragmentManager fragmentManager = getSupportFragmentManager();

    RegisterFragment refFragment = (RegisterFragment) fragmentManager.findFragmentByTag("REGISTER");
    if (refFragment != null) {
      refFragment.setCloverConnector(cloverConnector);
    }
    OrdersFragment ordersFragment = (OrdersFragment) fragmentManager.findFragmentByTag("ORDERS");
    if (ordersFragment != null) {
      ordersFragment.setCloverConnector(cloverConnector);
    }
    ManualRefundsFragment manualRefundsFragment = (ManualRefundsFragment) fragmentManager.findFragmentByTag("REFUNDS");
    if (manualRefundsFragment != null) {
      manualRefundsFragment.setCloverConnector(cloverConnector);
    }
    CardsFragment cardsFragment = (CardsFragment) fragmentManager.findFragmentByTag("CARDS");
    if (cardsFragment != null) {
      cardsFragment.setCloverConnector(cloverConnector);
    }
    HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentByTag("HOME");
    if (homeFragment != null) {
      homeFragment.setCloverConnector(cloverConnector);
    }
    RecoveryOptionsFragment recoveryFragment = (RecoveryOptionsFragment) fragmentManager.findFragmentByTag("RECOVERY");
    if (recoveryFragment != null) {
      recoveryFragment.setCloverConnector(cloverConnector);
    }
    DeviceFragment deviceFragment = (DeviceFragment) fragmentManager.findFragmentByTag("DEVICE");
    if (deviceFragment != null) {
      deviceFragment.setCloverConnector(cloverConnector);
    }
    CustomActivitiesFragment customFragment = (CustomActivitiesFragment) fragmentManager.findFragmentByTag("CUSTOM");
    if (customFragment != null) {
      customFragment.setCloverConnector(cloverConnector);
    }
    TransactionsFragment transactionsFragment = (TransactionsFragment) fragmentManager.findFragmentByTag("TRANSACTIONS");
    if (transactionsFragment != null) {
      transactionsFragment.setCloverConnector(cloverConnector);
    }
    PaymentDetailsFragment paymentDetailsFragment = (PaymentDetailsFragment) fragmentManager.findFragmentByTag("PAYMENT_DETAILS");
    if (paymentDetailsFragment != null) {
      paymentDetailsFragment.setCloverConnector(cloverConnector);
    }
    CurrentOrderFragment currentOrderFragment = (CurrentOrderFragment) fragmentManager.findFragmentByTag("CURRENT_ORDER");
    if (currentOrderFragment != null) {
      currentOrderFragment.setCloverConnector(cloverConnector);
    }
  }

  public void showPreauthInfo(@Nullable POSPayment payment) {
    //if payment wasn't passed in, try to get it from the store
    if (payment == null) {
      payment = store.getCurrentOrder().getPreAuth();
    }

    //...if for some reason we still don't have a payment, there's nothing else to do
    if (payment == null) {
      return;
    }

    LinearLayout preAuthInfo = findViewById(R.id.PreAuthInfo);
    preAuthInfo.setVisibility(View.VISIBLE);
    LinearLayout current = findViewById(R.id.CurrentOrder);
    current.setVisibility(View.GONE);
    ImageView preAuthTender = findViewById(R.id.PreAuthTender);
    preAuthTender.setImageResource(ImageUtil.getCardTypeImage(payment.getCardType()));
    TextView preAuthAmount = findViewById(R.id.PreAuthAmount);
    preAuthAmount.setText(CurrencyUtils.convertToString(payment.getAmount()));
    TextView preAuthCardType = findViewById(R.id.PreAuthCardType);
    preAuthCardType.setText("Card: " + payment.getCardDetails());
  }

  public void showOrders(View view) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    hideFragments(fragmentManager, fragmentTransaction);

    Fragment fragment = fragmentManager.findFragmentByTag("ORDERS");
    if (fragment == null) {
      fragment = OrdersFragment.newInstance(store, cloverConnector);
      ((OrdersFragment) fragment).setCloverConnector(cloverConnector);
      fragmentTransaction.add(R.id.contentContainer, fragment, "ORDERS");
    } else {
      ((OrdersFragment) fragment).setCloverConnector(cloverConnector);
      fragmentTransaction.show(fragment);
    }
    fragmentTransaction.addToBackStack("ORDERS");
    fragmentTransaction.commit();
  }

  public void showRegister(View view) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    hideFragments(fragmentManager, fragmentTransaction);

    Fragment fragment = fragmentManager.findFragmentByTag("REGISTER");
    if (fragment == null) {
      fragment = RegisterFragment.newInstance(store, cloverConnector, false);
      fragmentTransaction.add(R.id.contentContainer, fragment, "REGISTER");
    } else {
      ((RegisterFragment) fragment).setCloverConnector(cloverConnector);
      ((RegisterFragment) fragment).setPreAuth(false);
      ((RegisterFragment) fragment).setVaulted(false);
      ((RegisterFragment) fragment).setVaultedCard(null);
      fragmentTransaction.show(fragment);
    }
    fragmentTransaction.addToBackStack("REGISTER");
    fragmentTransaction.commitAllowingStateLoss();
  }

  public void showHome(View view) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    hideFragments(fragmentManager, fragmentTransaction);

    Fragment fragment = fragmentManager.findFragmentByTag("HOME");
    if (fragment == null) {
      fragment = HomeFragment.newInstance(cloverConnector);
      fragmentTransaction.add(R.id.contentContainer, fragment, "HOME");
    } else {
      ((HomeFragment) fragment).setCloverConnector(cloverConnector);
      fragmentTransaction.show(fragment);
    }
    fragmentTransaction.commit();
  }

  public void showMessageLog() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    hideFragments(fragmentManager, fragmentTransaction);

    Fragment fragment = fragmentManager.findFragmentByTag("MESSAGES");
    if(fragment == null) {
      fragment = MessageLogFragment.newInstance(store);
      fragmentTransaction.add(R.id.contentContainer, fragment, "MESSAGES");
    } else {
      ((MessageLogFragment) fragment).updateLogs();
      fragmentTransaction.show(fragment);
    }
    fragmentTransaction.addToBackStack("MESSAGES");
    fragmentTransaction.commit();
  }

  public void showRecoveryOptions(View view) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    hideFragments(fragmentManager, fragmentTransaction);

    Fragment fragment = fragmentManager.findFragmentByTag("RECOVERY");
    if (fragment == null) {
      fragment = RecoveryOptionsFragment.newInstance(store, cloverConnector);
      fragmentTransaction.add(R.id.contentContainer, fragment, "RECOVERY");
    } else {
      ((RecoveryOptionsFragment) fragment).setCloverConnector(cloverConnector);
      fragmentTransaction.show(fragment);
    }
    fragmentTransaction.addToBackStack("RECOVERY");
    fragmentTransaction.commit();
  }

  public void showDevice(View view) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    hideFragments(fragmentManager, fragmentTransaction);

    Fragment fragment = fragmentManager.findFragmentByTag("DEVICE");
    if (fragment == null) {
      fragment = DeviceFragment.newInstance(cloverConnector);
      fragmentTransaction.add(R.id.contentContainer, fragment, "DEVICE");
    } else {
      ((DeviceFragment) fragment).setCloverConnector(cloverConnector);
      fragmentTransaction.show(fragment);
    }
    fragmentTransaction.addToBackStack("DEVICE");
    fragmentTransaction.commit();
  }

  public void showCustomActivities(View view) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    hideFragments(fragmentManager, fragmentTransaction);

    Fragment fragment = fragmentManager.findFragmentByTag("CUSTOM");
    if (fragment == null) {
      fragment = CustomActivitiesFragment.newInstance(cloverConnector);
      fragmentTransaction.add(R.id.contentContainer, fragment, "CUSTOM");
    } else {
      ((CustomActivitiesFragment) fragment).setCloverConnector(cloverConnector);
      fragmentTransaction.show(fragment);
    }
    fragmentTransaction.addToBackStack("CUSTOM");
    fragmentTransaction.commit();
  }

  public void showRefunds(View view) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    hideFragments(fragmentManager, fragmentTransaction);

    Fragment fragment = fragmentManager.findFragmentByTag("REFUNDS");
    if (fragment == null) {
      fragment = ManualRefundsFragment.newInstance(store, cloverConnector);
      fragmentTransaction.add(R.id.contentContainer, fragment, "REFUNDS");
    } else {
      ((ManualRefundsFragment) fragment).setCloverConnector(cloverConnector);
      fragmentTransaction.show(fragment);
    }
    fragmentTransaction.addToBackStack("REFUNDS");
    fragmentTransaction.commit();
  }

  public void showCards(View view) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    hideFragments(fragmentManager, fragmentTransaction);

    Fragment fragment = fragmentManager.findFragmentByTag("CARDS");
    if (fragment == null) {
      fragment = CardsFragment.newInstance(store, cloverConnector);
      ((CardsFragment) fragment).setCloverConnector(cloverConnector);
      fragmentTransaction.add(R.id.contentContainer, fragment, "CARDS");
    } else {
      ((CardsFragment) fragment).setCloverConnector(cloverConnector);
      fragmentTransaction.show(fragment);
    }
    fragmentTransaction.addToBackStack("CARDS");
    fragmentTransaction.commit();
  }

  public void showTransactions(View view) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    hideFragments(fragmentManager, fragmentTransaction);

    Fragment fragment = fragmentManager.findFragmentByTag("TRANSACTIONS");
    if (fragment == null) {
      fragment = TransactionsFragment.newInstance(cloverConnector, store);
      fragmentTransaction.add(R.id.contentContainer, fragment, "TRANSACTIONS");
    } else {
      ((TransactionsFragment) fragment).setCloverConnector(cloverConnector);
      fragmentTransaction.show(fragment);
    }
    fragmentTransaction.addToBackStack("TRANSACTIONS");
    fragmentTransaction.commit();
  }


  public void showPaymentDetails(POSTransaction transaction) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    hideFragments(fragmentManager, fragmentTransaction);

    androidx.fragment.app.Fragment fragment = fragmentManager.findFragmentByTag("PAYMENT_DETAILS");
    if (fragment == null) {
      fragment = PaymentDetailsFragment.newInstance(transaction, cloverConnector, store);
      fragmentTransaction.add(R.id.contentContainer, fragment, "PAYMENT_DETAILS");
    } else {
      ((PaymentDetailsFragment) fragment).setCloverConnector(cloverConnector);
      ((PaymentDetailsFragment) fragment).setTransaction(transaction);
      fragmentTransaction.show(fragment);
    }
    fragmentTransaction.addToBackStack("PAYMENT_DETAILS");
    fragmentTransaction.commit();
  }

  public void hidePreAuth() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment fragment = fragmentManager.findFragmentByTag("REGISTER");
    ((RegisterFragment) fragment).clearPreAuth();
  }


  public void startPreAuth(View view) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    hideFragments(fragmentManager, fragmentTransaction);

    Fragment fragment = fragmentManager.findFragmentByTag("REGISTER");
    if (fragment == null) {
      fragment = RegisterFragment.newInstance(store, cloverConnector, true);
      fragmentTransaction.add(R.id.contentContainer, fragment, "REGISTER");
    } else {
      ((RegisterFragment) fragment).setCloverConnector(cloverConnector);
      ((RegisterFragment) fragment).setPreAuth(true);

      fragmentTransaction.show(fragment);
    }
    fragmentTransaction.addToBackStack("REGISTER");
    fragmentTransaction.commitAllowingStateLoss();
  }

  public void startVaulted(POSCard vaultedCard) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    hideFragments(fragmentManager, fragmentTransaction);

    Fragment fragment = fragmentManager.findFragmentByTag("REGISTER");
    if (fragment == null) {
      fragment = RegisterFragment.newInstance(store, cloverConnector, true, vaultedCard);
      fragmentTransaction.add(R.id.contentContainer, fragment, "REGISTER");
    } else {
      ((RegisterFragment) fragment).setCloverConnector(cloverConnector);
      ((RegisterFragment) fragment).setVaulted(true);
      ((RegisterFragment) fragment).setVaultedCard(vaultedCard);
      fragmentTransaction.show(fragment);
    }
    fragmentTransaction.addToBackStack("REGISTER");
    fragmentTransaction.commitAllowingStateLoss();
  }

  private void hideFragments(FragmentManager fragmentManager, FragmentTransaction fragmentTransaction) {
    Fragment fragment = fragmentManager.findFragmentByTag("ORDERS");
    if (fragment != null) {
      fragmentTransaction.hide(fragment);
    }
    fragment = fragmentManager.findFragmentByTag("REGISTER");
    if (fragment != null) {
      fragmentTransaction.hide(fragment);
    }
    fragment = fragmentManager.findFragmentByTag("SIGNATURE");
    if (fragment != null) {
      fragmentTransaction.hide(fragment);
    }
    fragment = fragmentManager.findFragmentByTag("CARDS");
    if (fragment != null) {
      fragmentTransaction.hide(fragment);
    }
    fragment = fragmentManager.findFragmentByTag("REFUNDS");
    if (fragment != null) {
      fragmentTransaction.hide(fragment);
    }
    fragment = fragmentManager.findFragmentByTag("HOME");
    if (fragment != null) {
      fragmentTransaction.hide(fragment);
    }
    fragment = fragmentManager.findFragmentByTag("RECOVERY");
    if (fragment != null) {
      fragmentTransaction.hide(fragment);
    }
    fragment = fragmentManager.findFragmentByTag("DEVICE");
    if (fragment != null) {
      fragmentTransaction.hide(fragment);
    }
    fragment = fragmentManager.findFragmentByTag("CUSTOM");
    if (fragment != null) {
      fragmentTransaction.hide(fragment);
    }
    fragment = fragmentManager.findFragmentByTag("TRANSACTIONS");
    if (fragment != null) {
      fragmentTransaction.hide(fragment);
    }
    fragment = fragmentManager.findFragmentByTag("PAYMENT_DETAILS");
    if (fragment != null) {
      fragmentTransaction.hide(fragment);
    }
    fragment = fragmentManager.findFragmentByTag("CURRENT_ORDER");
    if (fragment != null) {
      fragmentTransaction.hide(fragment);
    }
    fragment = fragmentManager.findFragmentByTag("MESSAGES");
    if (fragment != null) {
      fragmentTransaction.hide(fragment);
    }
  }

  private int getNextPrintRequestId() {
    return ++printRequestId;
  }

  public void onManualRefundClick(View view) {
    CharSequence val = ((TextView) findViewById(R.id.ManualRefundTextView)).getText();
    try {
      long refundAmount = CurrencyUtils.convertToLong(val.toString());
      String externalId = IdUtils.getNextId();
      ManualRefundRequest request = new ManualRefundRequest(refundAmount, externalId);
      request.setCardEntryMethods(store.getCardEntryMethods());
      request.setDisablePrinting(store.getDisablePrinting());
      request.setDisableReceiptSelection(store.getDisableReceiptOptions());
      Log.d(TAG, "ManualRefundRequest: " + request.toString());
      recordMessage(request);
      cloverConnector.manualRefund(request);
    } catch (NumberFormatException nfe) {
      showMessage("Invalid value. Must be an integer.", Toast.LENGTH_LONG);
    }
  }

  public void printTextClick(View view) {
    String[] textLines = ((TextView) findViewById(R.id.PrintTextText)).getText().toString().split("\n");
    List<String> lines = Arrays.asList(textLines);
    if (printer != null) {
      PrintRequest pr = new PrintRequest(lines);
      lastPrintRequestId = String.valueOf(getNextPrintRequestId());
      pr.setPrintRequestId(lastPrintRequestId);
      pr = new PrintRequest(lines, lastPrintRequestId, printer.getId());
      Log.d(TAG, "PrintRequest - Print Text: " + pr.toString());
      recordMessage(pr);
      cloverConnector.print(pr);
    }
  }

  public void printImageURLClick(View view) {
    String URL = ((TextView) findViewById(R.id.PrintImageURLText)).getText().toString();
    if (printer != null) {
      PrintRequest pr = new PrintRequest(URL);
      lastPrintRequestId = String.valueOf(getNextPrintRequestId());
      pr.setPrintRequestId(lastPrintRequestId);
      pr = new PrintRequest(URL, lastPrintRequestId, printer.getId());
      Log.d(TAG, "PrintRequest - Print Image URL: " + pr.toString());
      recordMessage(pr);
      cloverConnector.print(pr);
    }
  }

  public void queryPrintStatusClick(View view) {
    PrintJobStatusRequest pjsr = new PrintJobStatusRequest(lastPrintRequestId);
    Log.d(TAG, "PrintJobStatusRequest: " + pjsr.toString());
    recordMessage(pjsr);
    cloverConnector.retrievePrintJobStatus(pjsr);
  }

  private void tipAdded(long tipAmount) {
    RegisterFragment registerFragment = (RegisterFragment) getSupportFragmentManager().findFragmentByTag("REGISTER");
    registerFragment.tipAdded(tipAmount);
  }

  private void updatePaymentDetailsTip(POSTransaction payment) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment fragment = fragmentManager.findFragmentByTag("PAYMENT_DETAILS");
    ((PaymentDetailsFragment) fragment).setTip(((POSPayment) payment).getTipAmount());
  }

  private void updatePaymentDetailsRefund(POSRefund payment, long amount) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment fragment = fragmentManager.findFragmentByTag("PAYMENT_DETAILS");
    ((PaymentDetailsFragment) fragment).addRefund(payment, amount);
  }

  private void updatePaymentDetailsVoided(POSTransaction payment) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment fragment = fragmentManager.findFragmentByTag("PAYMENT_DETAILS");
    ((PaymentDetailsFragment) fragment).paymentVoided(payment);
  }

  private void updatePaymentDetailsRefundVoid(POSTransaction payment, String refundId) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment fragment = fragmentManager.findFragmentByTag("PAYMENT_DETAILS");
    ((PaymentDetailsFragment) fragment).refundVoided(payment, refundId);
  }

  public void showMessageClick(View view) {
    String msg = ((TextView) findViewById(R.id.ShowMessageText)).getText().toString();
    JsonObject jsonMsg = new JsonObject();
    jsonMsg.addProperty("message", msg);
    recordMessage(new POSMessage("ShowMessage", POSMessage.MessageSrc.POS, jsonMsg));
    cloverConnector.showMessage(msg);
  }

  public void showWelcomeMessageClick(View view) {
    recordMessage("ShowWelcomeScreen");
    cloverConnector.showWelcomeScreen();
  }

  public void showThankYouClick(View view) {
    recordMessage("ShowThankYouScreen");
    cloverConnector.showThankYouScreen();
  }

  public void onOpenCashDrawerClick(View view) {
    OpenCashDrawerRequest ocdr = new OpenCashDrawerRequest("Test");
    if (printer != null) {
      ocdr.setDeviceId(printer.getId());
    }
    Log.d(TAG, "OpenCashDrawerRequest: " + ocdr.toString());
    recordMessage(ocdr);
    cloverConnector.openCashDrawer(ocdr);
  }

  public void preauthCardClick(View view) {
    String externalPaymentID = IdUtils.getNextId();
    Log.d(TAG, "ExternalPaymentID:" + externalPaymentID);
    store.getCurrentOrder().setPendingPaymentId(externalPaymentID);
    PreAuthRequest request = new PreAuthRequest(5000L, externalPaymentID);
    request.setCardEntryMethods(store.getCardEntryMethods());
    request.setDisablePrinting(store.getDisablePrinting());
    request.setDisableReceiptSelection(store.getDisableReceiptOptions());
    request.setDisableDuplicateChecking(store.getDisableDuplicateChecking());
    Log.d(TAG, "PreAuthRequest: " + request.toString());
    recordMessage(request);
    cloverConnector.preAuth(request);
  }

  public void onClickCloseout(View view) {
    CloseoutRequest request = new CloseoutRequest();
    request.setAllowOpenTabs(false);
    request.setBatchId(null);
    Log.d(TAG, "CloseoutRequest: " + request.toString());
    recordMessage(request);
    cloverConnector.closeout(request);
  }


  public void printImageClick(View view) {
    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
  }

  public void setPrinter(Printer printer) {
    this.printer = printer;
  }

  public void printImage(String imgDecodableString) {
    Bitmap bitmap = BitmapFactory.decodeFile(imgDecodableString);
    if (this.printer != null) {
      PrintRequest pr = new PrintRequest(bitmap);
      lastPrintRequestId = String.valueOf(getNextPrintRequestId());
      pr.setPrintRequestId(lastPrintRequestId);
      pr = new PrintRequest(bitmap, lastPrintRequestId, printer.getId());
      Log.d(TAG, "PrintRequest - Print Image: " + pr.toString());
      recordMessage(pr);
      cloverConnector.print(pr);
    }
  }

  public void onResetDeviceClick(View view) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        new AlertDialog.Builder(ExamplePOSActivity.this)
            .setTitle("Reset Device")
            .setMessage("Are you sure you want to reset the device? Warning: You may lose any pending transaction information.")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Resetting Device");
                recordMessage("ResetDevice");
                cloverConnector.resetDevice();
              }
            })
            .setNegativeButton("No", null)
            .show();
      }
    });
  }

  public void onReadCardDataClick(View view) {
    ReadCardDataRequest readCardDataRequest = new ReadCardDataRequest(store.getCardEntryMethods());
    Log.d(TAG, "ReadCardDataRequest: " + readCardDataRequest);
    recordMessage(readCardDataRequest);
    cloverConnector.readCardData(readCardDataRequest);
  }

  public void onGetDeviceStatusClick(View view) {
    RetrieveDeviceStatusRequest request = new RetrieveDeviceStatusRequest(false);
    Log.d(TAG, "RetrieveDeviceStatusRequest: " + request.toString());
    recordMessage(request);
    cloverConnector.retrieveDeviceStatus(request);
  }

  public void onGetDeviceStatusCBClick(View view) {
    RetrieveDeviceStatusRequest request = new RetrieveDeviceStatusRequest(true);
    Log.d(TAG, "RetrieveDeviceStatusRequest: " + request.toString());
    recordMessage(request);
    cloverConnector.retrieveDeviceStatus(new RetrieveDeviceStatusRequest(true));
  }

  public void refreshPendingPayments(View view) {
    Log.d(TAG, "Retrieving Pending Payments");
    recordMessage("RetrievePendingPayments");
    cloverConnector.retrievePendingPayments();
  }

  public void externalMismatch() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        AlertDialog externalIDMismatch;
        AlertDialog.Builder builder = new AlertDialog.Builder(ExamplePOSActivity.this);
        builder.setTitle("Error");
        builder.setMessage("External Payment Ids do not match");
        externalIDMismatch = builder.create();
        externalIDMismatch.show();
      }
    });
  }

  @Override
  public void onSaleTypeChoice(String choice) {

  }

  @Override
  public void onContinue(String name, String amount) {

  }

  @Override
  public void onContinue(long amount) {

  }

  @Override
  public void onSave(long tipAmount) {

  }

  @Override
  public void onContinue(String name) {

  }

  @Override
  public void onLookup(String paymentId) {

  }

  @Override
  public void makePartialRefund(long amount) {

  }

  @Override
  public void makeFullRefund() {

  }

  @Override
  public void onSaveTipSuggestions(TipSuggestion tipSuggestion1, TipSuggestion tipSuggestion2, TipSuggestion tipSuggestion3, TipSuggestion tipSuggestion4) {

  }

  public void onClickRequestTip(View view) {
    TipRequest request = new TipRequest(1001L, null);
    recordMessage(request);
    cloverConnector.requestTip(request);
  }

  public void onClickRequestSignature(View view) {
    SignatureRequest request = new SignatureRequest();
    recordMessage(request);
    cloverConnector.requestSignature(request);
  }

  public void recordMessage(Object reqRes) {
    recordMessage(new POSMessage(reqRes));
  }
  public void recordMessage(String msgName) { recordMessage(new POSMessage(msgName));}
  public void recordMessage(POSMessage msg) { store.addMessage(msg); }
}
