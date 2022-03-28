package com.clover.remote.client.lib.example;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.clover.remote.client.lib.example.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckoutFragment extends DialogFragment implements RadioGroup.OnCheckedChangeListener {

    private long fullAmount;
    private View v;
    private RadioGroup splitTypeGroup_1, splitTypeGroup_2;
    private List<SplitTypeListener> listeners = new ArrayList<>(5);
    private Button payBtn, cancelBtn;
    private long splitType = 1;
    private TextView amountText;
    private long splitAmount;
    private TextView splitAmountTitle;

    public CheckoutFragment() {
        // Required empty public constructor
    }

    public static CheckoutFragment newInstance(long fullAmount) {
        CheckoutFragment fragment = new CheckoutFragment();
        fragment.setFullAmount(fullAmount);
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
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_checkout, container, false);

        payBtn = (Button) v.findViewById(R.id.CheckoutPayButton);
        cancelBtn = (Button) v.findViewById(R.id.CheckoutCancelButton);
        splitTypeGroup_1 = (RadioGroup) v.findViewById(R.id.SplitTypeGroup1);
        splitTypeGroup_2 = (RadioGroup) v.findViewById(R.id.SplitTypeGroup2);
        amountText = (TextView) v.findViewById(R.id.SplitAmountVal);
        splitAmountTitle = (TextView) v.findViewById(R.id.SplitAmountTitle);
        splitTypeGroup_1.setOnCheckedChangeListener(this);
        splitTypeGroup_2.setOnCheckedChangeListener(this);

        ((RadioButton) v.findViewById(R.id.SplitTypeFull)).setChecked(true);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSplitTypeAmount(splitAmount);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }

    public void handleSplitTypChange(RadioGroup splitTypeGroup, RadioButton selectedType) {
        if(splitTypeGroup.getId() == R.id.SplitTypeGroup1) {
            splitTypeGroup_2.clearCheck();
        } else {
            splitTypeGroup_1.clearCheck();
        }
        splitType = Long.parseLong((String) selectedType.getTag());
        updateAmount();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == View.NO_ID) return;

        RadioButton radio = v.findViewById(checkedId);
        if(radio != null && radio.isChecked()) handleSplitTypChange(group, radio);
    }

    public void updateAmount() {
        splitAmount = fullAmount / splitType;
        amountText.setText(CurrencyUtils.format(splitAmount, Locale.getDefault()));
        if(splitType == 1) {
            splitAmountTitle.setText(getResources().getString(R.string.splitFull));
        } else {
            splitAmountTitle.setText(String.format(getResources().getString(R.string.splitTextTemplate), splitType));
        }
    }
    public void setFullAmount(long amount) {
        fullAmount = amount;
    }

    public void onSplitTypeAmount(long amount){
        dismiss();
        for (CheckoutFragment.SplitTypeListener listener : listeners){
            listener.onSplitTypeAmount(amount);
        }
    }


    public void addListener(SplitTypeListener listener){
        listeners.add(listener);
    }

    public interface SplitTypeListener {
        public abstract void onSplitTypeAmount(long amount);
    }
}