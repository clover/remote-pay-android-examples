package com.clover.example;

import com.clover.sdk.v3.merchant.TipSuggestion;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class TipSuggestionFragment extends DialogFragment {
  private View view;
  private EditText tip1percent, tip1label, tip2percent, tip2label, tip3percent, tip3label, tip4percent, tip4label;
  private CheckBox tip1Enabled, tip2Enabled, tip3Enabled, tip4Enabled;
  private Button save;
  private List<TipSuggestionListener> listeners = new ArrayList<>(5);
  private TipSuggestion tipSuggestion1, tipSuggestion2, tipSuggestion3, tipSuggestion4;
  public static TipSuggestionFragment newInstance(TipSuggestion tipSuggestion1, TipSuggestion tipSuggestion2, TipSuggestion tipSuggestion3, TipSuggestion tipSuggestion4){
    TipSuggestionFragment fragment = new TipSuggestionFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    fragment.setTipSuggestions(tipSuggestion1, tipSuggestion2, tipSuggestion3, tipSuggestion4);
    return fragment;
  }
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, R.style.CustomDialog);
  }
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_tip_suggestions, container, false);
    tip1percent = (EditText) view.findViewById(R.id.tip_suggestion_1_percentage);
    tip1label = (EditText) view.findViewById(R.id.tip_suggestion_1_label);
    tip1Enabled = (CheckBox) view.findViewById(R.id.tip_suggestion_1_enabled);
    initFields(tipSuggestion1,tip1Enabled, tip1percent, tip1label);
    tip2percent = (EditText) view.findViewById(R.id.tip_suggestion_2_percentage);
    tip2label = (EditText) view.findViewById(R.id.tip_suggestion_2_label);
    tip2Enabled = (CheckBox) view.findViewById(R.id.tip_suggestion_2_enabled);
    initFields(tipSuggestion2,tip2Enabled, tip2percent, tip2label);
    tip3percent = (EditText) view.findViewById(R.id.tip_suggestion_3_percentage);
    tip3label = (EditText) view.findViewById(R.id.tip_suggestion_3_label);
    tip3Enabled = (CheckBox) view.findViewById(R.id.tip_suggestion_3_enabled);
    initFields(tipSuggestion3,tip3Enabled, tip3percent, tip3label);
    tip4percent = (EditText) view.findViewById(R.id.tip_suggestion_4_percentage);
    tip4label = (EditText) view.findViewById(R.id.tip_suggestion_4_label);
    tip4Enabled = (CheckBox) view.findViewById(R.id.tip_suggestion_4_enabled);
    initFields(tipSuggestion4,tip4Enabled, tip4percent, tip4label);
    save = (Button) view.findViewById(R.id.save_tip_suggestions_button);
    save.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        tipSuggestion1 = saveTip(tip1Enabled, tip1percent, tip1label);
        tipSuggestion2 = saveTip(tip2Enabled, tip2percent, tip2label);
        tipSuggestion3 = saveTip(tip3Enabled, tip3percent, tip3label);
        tipSuggestion4 = saveTip(tip4Enabled, tip4percent, tip4label);
        onSaveTipSuggestions(tipSuggestion1, tipSuggestion2, tipSuggestion3, tipSuggestion4);
      }
    });
    return view;
  }
  private void initFields(TipSuggestion tipSuggestion, CheckBox tipEnabled, EditText tipPercentage, EditText tipLabel){
    if(tipSuggestion != null){
      if(tipSuggestion.getPercentage() != null) {
        tipPercentage.setText(tipSuggestion.getPercentage().toString());
      }
      else{
        tipPercentage.setText("");
      }
      if(tipSuggestion.getName() != null) {
        tipLabel.setText(tipSuggestion.getName());
      }
      else{
        tipLabel.setText("");
      }
      tipEnabled.setChecked(tipSuggestion.getIsEnabled());
    }
  }
  private TipSuggestion saveTip(CheckBox tipEnabled, EditText percentage, EditText label){
    TipSuggestion tipSuggestion = new TipSuggestion();
    if(percentage.getText().length() > 0) {
      tipSuggestion.setPercentage(Long.parseLong(percentage.getText().toString()));
    }
    if(label.getText().length() > 0) {
      tipSuggestion.setName(label.getText().toString());
    }
    tipSuggestion.setIsEnabled(tipEnabled.isChecked());
    return tipSuggestion;
  }
  private void setTipSuggestions(TipSuggestion tipSuggestion1, TipSuggestion tipSuggestion2, TipSuggestion tipSuggestion3, TipSuggestion tipSuggestion4){
    this.tipSuggestion1 = tipSuggestion1;
    this.tipSuggestion2 = tipSuggestion2;
    this.tipSuggestion3 = tipSuggestion3;
    this.tipSuggestion4 = tipSuggestion4;
  }
  public void onSaveTipSuggestions(TipSuggestion tipSuggestion1, TipSuggestion tipSuggestion2, TipSuggestion tipSuggestion3, TipSuggestion tipSuggestion4){
    dismiss();
    for(TipSuggestionListener listener: listeners){
      listener.onSaveTipSuggestions(tipSuggestion1, tipSuggestion2, tipSuggestion3, tipSuggestion4);
    }
  }
  public interface TipSuggestionListener {
    public abstract void onSaveTipSuggestions(TipSuggestion tipSuggestion1, TipSuggestion tipSuggestion2, TipSuggestion tipSuggestion3, TipSuggestion tipSuggestion4);
  }
  public void addListener(TipSuggestionListener listener){
    listeners.add(listener);
  }
  private TipSuggestionListener mListener;
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      this.mListener = (TipSuggestionListener) activity;
    }
    catch (final ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement TipSuggestionListener");
    }
  }
}