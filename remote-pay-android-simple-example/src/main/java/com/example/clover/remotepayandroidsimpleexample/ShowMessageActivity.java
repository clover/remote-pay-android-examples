package com.example.clover.remotepayandroidsimpleexample;

import com.clover.remote.client.CloverConnector;
import com.clover.remote.client.ICloverConnector;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

/**
 * Created by rachel.antion on 7/13/17.
 */

public class ShowMessageActivity extends Activity {

  private static ICloverConnector cloverConnector;
  private final String TAG = ShowMessageActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_message);
    TextView showMessageText = (TextView)findViewById(R.id.show_message_text);
    showMessageText.setText("Calling Show Message...");
    cloverConnector = MainActivity.getCloverConnector();
    Log.d(TAG, "About to call show message");
    if (cloverConnector != null) {
      cloverConnector.showMessage("Hello World!");
    }
    else{
      Log.d(TAG, "Clover connector is null");
    }
  }

  private void exit(){
    Log.d(TAG,"exiting");
    cloverConnector.showWelcomeScreen();
    cloverConnector.dispose();
    synchronized (this) {
      notifyAll();
    }
  }


}
