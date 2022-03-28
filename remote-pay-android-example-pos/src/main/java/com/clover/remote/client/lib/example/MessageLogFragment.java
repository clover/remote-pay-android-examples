package com.clover.remote.client.lib.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.clover.remote.PendingPaymentEntry;
import com.clover.remote.client.lib.example.adapter.MessageListViewAdapter;
import com.clover.remote.client.lib.example.model.POSCard;
import com.clover.remote.client.lib.example.model.POSMessage;
import com.clover.remote.client.lib.example.model.POSOrder;
import com.clover.remote.client.lib.example.model.POSPayment;
import com.clover.remote.client.lib.example.model.POSStore;
import com.clover.remote.client.lib.example.model.POSTransaction;
import com.clover.remote.client.lib.example.model.StoreObserver;

import java.util.List;

public class MessageLogFragment extends Fragment {
    private View v;
    private ListView rawOutput;
    private POSStore store;

    public MessageLogFragment() { }

    public static MessageLogFragment newInstance(POSStore store) {
        MessageLogFragment fragment = new MessageLogFragment();
        fragment.setStore(store);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_message_log, container, false);
        rawOutput = v.findViewById(R.id.raw_output);
        _updateLogs();
        return v;
    }

    private void _updateLogs() {
        MessageListViewAdapter messageListViewAdapter = new MessageListViewAdapter(v.getContext(), R.id.raw_output, store.getMessages());
        rawOutput.setAdapter(messageListViewAdapter);
        rawOutput.setSelection(messageListViewAdapter.getCount() - 1);
    }

    public void updateLogs() {
        getView().post(new Runnable() {
            @Override
            public void run() {
                _updateLogs();
            }
        });
    }

    public void setStore(POSStore store) {
        this.store = store;

        store.addStoreObserver(new StoreObserver() {
            @Override
            public void onCurrentOrderChanged(POSOrder currentOrder) {}
            @Override
            public void newOrderCreated(POSOrder order, boolean userInitiated) {}
            @Override
            public void cardAdded(POSCard card) {}
            @Override
            public void refundAdded(POSTransaction refund) {}
            @Override
            public void preAuthAdded(POSPayment payment) {}
            @Override
            public void preAuthRemoved(POSPayment payment) {}
            @Override
            public void pendingPaymentsRetrieved(List<PendingPaymentEntry> pendingPayments) {}
            @Override
            public void transactionsChanged(List<POSTransaction> transactions) {}
            @Override
            public void newMessageAdded(POSMessage msg) {
                updateLogs();
            }
        });
    }
}