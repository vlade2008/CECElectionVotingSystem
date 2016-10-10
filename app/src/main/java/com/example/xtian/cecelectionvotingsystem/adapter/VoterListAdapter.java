package com.example.xtian.cecelectionvotingsystem.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.xtian.cecelectionvotingsystem.R;
import com.example.xtian.cecelectionvotingsystem.getSetterItem.item;

import java.util.ArrayList;

/**
 * Created by Xtian on 11/17/2015.
 */
public class VoterListAdapter extends ArrayAdapter<item> {

    Context context;
    int layoutResourceId;
    ArrayList<item> data = new ArrayList<item>();



    public VoterListAdapter(Context context, int layoutResourceId, ArrayList<item> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View rowView = inflater.inflate(layoutResourceId, parent, false);

        TextView textStudentid = (TextView) rowView.findViewById(R.id.textStudentId);
        TextView textName = (TextView) rowView.findViewById(R.id.textName);
        TextView textCoursePassword = (TextView) rowView.findViewById(R.id.textCoursePassword);


        item item = data.get(position);
        textName.setText(item.getName() +"   "+ item.getCourse());
        textStudentid.setText(item.getStudentId());
        textCoursePassword.setText("Password: "+item.getStudentpassword());


        return rowView;
    }

}
