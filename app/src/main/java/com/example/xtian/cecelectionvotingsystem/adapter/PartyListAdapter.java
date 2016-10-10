package com.example.xtian.cecelectionvotingsystem.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.xtian.cecelectionvotingsystem.R;
import com.example.xtian.cecelectionvotingsystem.fragment.partyItem;

import java.util.ArrayList;

/**
 * Created by Xtian on 11/25/2015.
 */
public class PartyListAdapter  extends ArrayAdapter<partyItem>{

    Context context;
    int layoutResourceId;
    ArrayList<partyItem> data = new ArrayList<partyItem>();

    public PartyListAdapter(Context context, int layoutResourceId, ArrayList<partyItem> data) {
        super(context,layoutResourceId,data);
        this.layoutResourceId = layoutResourceId;
        this.context=context;
        this.data=data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View rowView = inflater.inflate(layoutResourceId, parent, false);

        TextView textPartyId = (TextView) rowView.findViewById(R.id.textPartyId);
        TextView textPartyName = (TextView) rowView.findViewById(R.id.textPartyName);

        partyItem partyItem = data.get(position);
        textPartyId.setText(partyItem.getPartyId());
        textPartyId.setAlpha(0f);
        textPartyName.setText(partyItem.getPartyName());

        return rowView;
    }
}
