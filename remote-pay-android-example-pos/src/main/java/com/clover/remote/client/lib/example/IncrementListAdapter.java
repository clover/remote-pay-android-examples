package com.clover.remote.client.lib.example;

import com.clover.remote.client.lib.example.utils.CurrencyUtils;
import com.clover.sdk.v3.payments.IncrementalAuthorization;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IncrementListAdapter extends RecyclerView.Adapter<IncrementListAdapter.IncrementViewHolder> {

  List<IncrementalAuthorization> increments;

  public IncrementListAdapter(List<IncrementalAuthorization> increments) {
    this.increments = increments;
  }

  @NonNull
  @Override
  public IncrementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_increment_list_row, parent, false);
    IncrementViewHolder incrementViewHolder = new IncrementViewHolder(view);
    return incrementViewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull IncrementViewHolder holder, int position) {
    //only notate the original transaction. All others are known to be subsequent increments by the user's context
    if (position == 0) {
      holder.title.setText(R.string.original_auth);
    } else {
      holder.title.setText("");
    }

    IncrementalAuthorization increment = increments.get(position);
    holder.amount.setText(CurrencyUtils.convertToString(increment.getAmount()));
  }

  @Override
  public int getItemCount() {
    return increments.size();
  }

  public class IncrementViewHolder extends RecyclerView.ViewHolder {

    TextView title, amount;
    RelativeLayout incrementRow;

    public IncrementViewHolder(@NonNull View itemView) {
      super(itemView);

      incrementRow = itemView.findViewById(R.id.incrementRow);
      title = itemView.findViewById(R.id.title);
      amount = itemView.findViewById(R.id.amount);
    }
  }
}
