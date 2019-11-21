package com.example.clover.remotepayandroidsimpleexample;
import com.clover.remote.client.CloverConnector;
import com.clover.remote.client.CloverDeviceConfiguration;
import com.clover.remote.client.DefaultCloverConnectorListener;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.MerchantInfo;
import com.clover.remote.client.USBCloverDeviceConfiguration;
import com.clover.remote.client.WebSocketCloverDeviceConfiguration;
import com.clover.remote.client.messages.ConfirmPaymentRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * This example class illustrates how to create and terminate a connection to a clover device.
 * The dialog which occurs is:
 *    1) Create CloverConnector based upon the specified configuration
 *    2) Register the CloverConnectorListener with the CloverConnector
 *    3) Initialize the CloverConnector via initializeConnection() method
 *      a) If network connection configured, input the pairing code provided by the device callback
 *    4) Handle onDeviceReady() callback from device indicating connection was made
 *    5) Close the underlying connection via dispose() method
 */


public class MainActivity extends Activity {

  private final String TAG = MainActivity.class.getSimpleName();
  private TextView textConnect;
  private static ICloverConnector cloverConnector;

  private static final String APP_ID = "com.cloverconnector.java.simple.sample:2.0.0";
  private static final String POS_NAME = "Clover Simple Sample Java";
  private static final String DEVICE_NAME = "Clover Device";
  private AlertDialog pairingCodeDialog;

  private static final SecureRandom random = new SecureRandom();
  private static final char[] vals = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'}; // Crockford's base 32 chars

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    textConnect = (TextView)findViewById(R.id.connector_text);
    cloverConnector = null;
  }

  @Override
  protected void onResume(){
    super.onResume();
    if(cloverConnector == null) {
      connect();
    }
  }

  @Override
  protected void onDestroy(){
    super.onDestroy();
    exit();
  }

  private void connect(){
    Log.d(TAG, "connecting.....");
    cloverConnector = new CloverConnector(getUSBConfiguration());
    //cloverConnector = new CloverConnector(getNetworkConfiguration("Your Clover device ip address here",12345));
    cloverConnector.addCloverConnectorListener(new TestListener(cloverConnector));
    cloverConnector.initializeConnection();
  }

  private void connected(){
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        textConnect.setText(R.string.connection_successful);
      }
    });
  }

  private void exit(){
    Log.d(TAG,"exiting");
    cloverConnector.dispose();
    textConnect.setText(R.string.disconnected);
    synchronized (this) {
      notifyAll();
    }
  }

  private final class TestListener extends DefaultCloverConnectorListener {
    public TestListener(ICloverConnector cloverConnector) {
      super(cloverConnector);
    }

    @Override
    public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
    }


    @Override
    public void onDeviceReady(MerchantInfo merchantInfo) {
      super.onDeviceReady(merchantInfo);
      Log.d(TAG, "device ready");
      connected();
    }
  }

  public CloverDeviceConfiguration getUSBConfiguration() {
    return new USBCloverDeviceConfiguration(this, APP_ID);
  }

  public CloverDeviceConfiguration getNetworkConfiguration(String ip) {
    return getNetworkConfiguration(ip, null);
  }

  public CloverDeviceConfiguration getNetworkConfiguration(String ip, Integer port) {
    Integer dvcPort = port != null ? port : Integer.valueOf(12345);
    try {
      URI endpoint = new URI("wss://" + ip + ":" + dvcPort + "/remote_pay");
      KeyStore trustStore  = createTrustStore();

      // For WebSocket configuration, we must handle the device pairing via callback
      return new WebSocketCloverDeviceConfiguration(endpoint,APP_ID, trustStore, POS_NAME, DEVICE_NAME, null) {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if (pairingCodeDialog != null && pairingCodeDialog.isShowing()) {
                pairingCodeDialog.dismiss();
                pairingCodeDialog = null;
              }
              textConnect.setText("Pairing Successful!");
            }
          });
        }
      };
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    System.err.println("Error creating CloverDeviceConfiguration");
    return null;
  }

  private static KeyStore createTrustStore() {
    try {
      String storeType = KeyStore.getDefaultType();
      KeyStore trustStore = KeyStore.getInstance(storeType);
      char[] TRUST_STORE_PASSWORD = "clover".toCharArray();
      trustStore.load(null, TRUST_STORE_PASSWORD);

      // Load the old "dev" cert.  This should be valid for all target environments (dev, stg, sandbox, prod).
      Certificate cert = loadCertificateFromResource("/certs/device_ca_certificate.crt");
      trustStore.setCertificateEntry("dev", cert);

      // Now load the environment specific cert (e.g. prod).  Always retrieving this cert from prod as it is really
      // only valid in prod at this point, and we also don't have a mechanism within the SDK of specifying the target
      // environment.
      cert = loadCertificateFromResource("/certs/env_device_ca_certificate.crt");
      trustStore.setCertificateEntry("prod", cert);

      return trustStore;
    } catch(Throwable t) {
      t.printStackTrace();
    }
    return null;
  }

  private static Certificate loadCertificateFromResource(String name) {
    System.out.println("Loading cert:  " + name);

    InputStream is = null;
    try {
      is = MainActivity.class.getResourceAsStream(name);

      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      return cf.generateCertificate(is);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (Exception ex) {
          // NO-OP
        }
      }
    }
  }

  public static String getNextId() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 13; i++) {
      int idx = random.nextInt(vals.length);
      sb.append(vals[idx]);
    }
    return sb.toString();
  }

  public void showMessage(View view){
    Log.d(TAG, "show message called");
    Intent intent = new Intent();
    intent.setClass(this, ShowMessageActivity.class);
    startActivity(intent);
  }

  public void makeSale(View view){
    Log.d(TAG, "make sale called");
    Intent intent = new Intent();
    intent.setClass(this, MakeSaleActivity.class);
    startActivity(intent);
  }

  public void makeRefund(View view){
    Log.d(TAG, "make refund called");
    Intent intent = new Intent();
    intent.setClass(this, MakeRefundActivity.class);
    startActivity(intent);
  }

  public void sendDeviceLogs(View view){
    Log.d(TAG, "send device logs clicked");
    cloverConnector.sendDebugLog("because");
  }

  public static ICloverConnector getCloverConnector (){
    return cloverConnector;
  }

}
