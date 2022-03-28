package com.clover.remote.client.lib.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clover.remote.client.lib.example.R;
import com.clover.remote.client.lib.example.model.POSLineItem;
import com.clover.remote.client.lib.example.model.POSMessage;
import com.clover.remote.client.lib.example.utils.CurrencyUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageListViewAdapter extends ArrayAdapter<POSMessage> {

    public MessageListViewAdapter(Context context, int resource) {
        super(context, resource);
    }

    public MessageListViewAdapter(Context context, int resource, List<POSMessage> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.message_row, null);
        }

        POSMessage posMsg = getItem(position);

        if (posMsg != null) {
            TextView timeColumn = (TextView) v.findViewById(R.id.MessageRowTimeColumn);
            TextView srcColumn = (TextView) v.findViewById(R.id.MessageRowSrcColumn);
            TextView messageColumn = (TextView) v.findViewById(R.id.MessageRowMessageColumn);

            timeColumn.setText(new SimpleDateFormat("HH:mm:ss\nyyyy-MM-dd").format(posMsg.time));
            srcColumn.setText(posMsg.src.toString());
            messageColumn.setText(posMsg.toString());
        }

        return v;
    }
}
