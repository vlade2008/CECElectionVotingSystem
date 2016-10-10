package com.example.xtian.cecelectionvotingsystem.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xtian.cecelectionvotingsystem.R;
import com.example.xtian.cecelectionvotingsystem.getSetterItem.candidateItem;

import java.util.ArrayList;

/**
 * Created by Xtian on 12/1/2015.
 */
public class PartyCandidateListAdapater extends ArrayAdapter<candidateItem> {

    Context context;
    int layoutResourceId;
    ArrayList<candidateItem> candidateArray = new ArrayList<candidateItem>();

    public PartyCandidateListAdapater(Context context, int layoutResourceId, ArrayList<candidateItem> candidateArray) {
        super(context,layoutResourceId,candidateArray);
        this.layoutResourceId = layoutResourceId;
        this.context=context;
        this.candidateArray=candidateArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View rowView = inflater.inflate(layoutResourceId, parent, false);

        TextView Name = (TextView) rowView.findViewById(R.id.textviewCandidate_Name);
        TextView Party = (TextView) rowView.findViewById(R.id.textviewCandidate_Party);
        TextView Position = (TextView) rowView.findViewById(R.id.textviewCandidate_Position);
        TextView Vote = (TextView) rowView.findViewById(R.id.textviewCanidate_Vote);
        TextView ID = (TextView) rowView.findViewById(R.id.textviewCandidate_ID);
        ImageView Image = (ImageView) rowView.findViewById(R.id.candidateImage);

        candidateItem candidateItem = candidateArray.get(position);
        Bitmap scale;
        scale = Bitmap.createScaledBitmap(candidateItem.getImageIcon(),100,100,true);

        Name.setText(candidateItem.getStudent_Name());
        Party.setText(candidateItem.getPosition_Name());
        Position.setText(candidateItem.getPosition_Name());
        Vote.setText(candidateItem.getCandidate_vote());
        ID.setText(candidateItem.getStudent_ID());
        Image.setImageBitmap(candidateItem.getImageIcon());

        Position.setAlpha(0f);
        ID.setAlpha(0f);

        return rowView;
    }
}
