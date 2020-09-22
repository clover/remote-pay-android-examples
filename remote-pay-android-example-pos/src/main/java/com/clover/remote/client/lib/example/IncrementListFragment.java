package com.clover.remote.client.lib.example;

import com.clover.remote.client.lib.example.model.POSTransaction;
import com.clover.sdk.v3.payments.IncrementalAuthorization;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/**
 * Used to display a list of increments for a Preauth whose value has been increased.
 */
public class IncrementListFragment extends DialogFragment {
  private ArrayList<IncrementalAuthorization> increments;

  /**
   * The default tag for use in the fragment manager when showing this fragment.
   */
  public final String DEFAULT_FRAGMENT_TAG = "increment_fragment_tag";

  public IncrementListFragment() {
    // Required empty public constructor
  }

  /**
   * Constructor for the dialog to show an preauth's increments
   * @param transaction The transaction for which to view the increments
   * @return
   */
  public static IncrementListFragment newInstance(POSTransaction transaction) {
    IncrementListFragment fragment = new IncrementListFragment();
    fragment.increments = new ArrayList<>(transaction.getIncrements());

    //accumulate the sum of all the increments. This is necessary because we need to subtract the increments from the
    //amount of the transaction to arrive at the value of the original preauth.
    long sumOfIncrements = 0;
    for (IncrementalAuthorization increment: fragment.increments) {
      sumOfIncrements += increment.getAmount();
    }

    long originalAmount = transaction.getAmount() - sumOfIncrements;
    IncrementalAuthorization originalAuth = new IncrementalAuthorization();
    originalAuth.setAmount(originalAmount);

    //put the original auth before its increments in the list
    fragment.increments.add(0, originalAuth);

    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_increment_list, container, false);

    getDialog().setTitle(R.string.increments_view_title);

    RecyclerView incrementsRecyclerView = view.findViewById(R.id.incrementsRecyclerView);
    IncrementListAdapter adapter = new IncrementListAdapter(increments);
    incrementsRecyclerView.setAdapter(adapter);
    incrementsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

    Button okButton = view.findViewById(R.id.DoneButton);
    okButton.setOnClickListener(v -> dismiss());

    return view;
  }
}
