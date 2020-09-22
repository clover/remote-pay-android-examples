package com.clover.remote.client.lib.example;

import com.clover.remote.client.lib.example.utils.CurrencyUtils;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;

import java.text.NumberFormat;

public class IncrementAuthorizationFragment extends DialogFragment {

  private EditText incAuthAmountText;
  private IncrementAuthorizationListener listener;

  public static IncrementAuthorizationFragment newInstance(IncrementAuthorizationListener listener){
    IncrementAuthorizationFragment fragment = new IncrementAuthorizationFragment();
    fragment.listener = listener;
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, R.style.CustomDialog);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_increment_authorization, container, false);

    incAuthAmountText = (EditText) view.findViewById(R.id.IncrementAuthAmount);
    incAuthAmountText.setSelection(incAuthAmountText.getText().length());
    incAuthAmountText.addTextChangedListener(new TextWatcher(){
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        String current = "";
        if(!s.toString().equals(current)){
          incAuthAmountText.removeTextChangedListener(this);

          String cleanString = s.toString().replaceAll("[$,.]", "");

          double parsed = Double.parseDouble(cleanString);
          String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));
          incAuthAmountText.setText(formatted);
          incAuthAmountText.setSelection(formatted.length());
          incAuthAmountText.addTextChangedListener(this);
        }
      }
      @Override
      public void afterTextChanged(Editable arg0) { }
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    });

    Button incAuthButton = view.findViewById(R.id.IncrementAuthButton);
    incAuthButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();

        long incrementAmount = CurrencyUtils.convertToLong(incAuthAmountText.getText().toString());
        if (listener != null) {
          listener.onIncrementAuthorization(incrementAmount);
        }
      }
    });

    return view;
  }

  public interface IncrementAuthorizationListener {
    void onIncrementAuthorization(long incrementAmount);
  }
}
