package com.example.xtian.cecelectionvotingsystem.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xtian.cecelectionvotingsystem.R;
import com.example.xtian.cecelectionvotingsystem.getSetterItem.ResultItem;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Xtian on 12/1/2015.
 */
public class ResultListAdapater extends ArrayAdapter<ResultItem> {

    Context context;
    int layoutResourceId;
    ArrayList<ResultItem> ResultArray = new ArrayList<ResultItem>();

    public ResultListAdapater(Context context, int layoutResourceId, ArrayList<ResultItem> ResultArray) {
        super(context,layoutResourceId,ResultArray);
        this.layoutResourceId = layoutResourceId;
        this.context=context;
        this.ResultArray=ResultArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View rowView = inflater.inflate(layoutResourceId, parent, false);

        LinearLayout some_layout_item = (LinearLayout)rowView.findViewById(R.id.some_layout_item);

        TextView tied = (TextView) rowView.findViewById(R.id.textView_tied);
        TextView position_name = (TextView) rowView.findViewById(R.id.textviewPosition);
        //TextView position_ID = (TextView)rowView.findViewById(R.id.textviewCandidate_Position);
        TextView Name = (TextView) rowView.findViewById(R.id.textviewCandidate_name);
        TextView Party = (TextView) rowView.findViewById(R.id.textviewParty_name);
        ProgressBar progressBar = (ProgressBar)rowView.findViewById(R.id.progress_candidate);
        TextView percent_value = (TextView)rowView.findViewById(R.id.progressPercent);


        TextView vote = (TextView) rowView.findViewById(R.id.vote_count);
        TextView ID = (TextView) rowView.findViewById(R.id.textviewCandidate_ID);//student ID
        ImageView Image = (ImageView) rowView.findViewById(R.id.candidateImage);
        ImageView image_coin = (ImageView)rowView.findViewById(R.id.image_coin);


        //setting the tie,coin,progressbar,percent
        tied.setVisibility(View.INVISIBLE);
        image_coin.setVisibility(View.INVISIBLE);
        percent_value.setText("0%");
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);


        ResultItem resultItem = ResultArray.get(position);

        //tie statement
        if (resultItem.getCandidate_vote().equals("tied")){
            tied.setVisibility(View.VISIBLE);
            //animation for blink
            final Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(1000);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            some_layout_item.startAnimation(animation);

        }else{
            vote.setText(resultItem.getCandidate_vote());
            Image.setImageBitmap(resultItem.getImageIcon());
            //declare the variable
            float num1 = Float.parseFloat(String.valueOf(resultItem.getVoters_count()));
            float num2 = Integer.parseInt(resultItem.getCandidate_vote());
            int percentage_bar = Math.round(GetPercentValue(num1, num2));
            DecimalFormat df = new DecimalFormat("###.##");
            percent_value.setText(""+df.format(GetPercentValue(num1,num2))+"%");
            progressBar.setSecondaryProgress(percentage_bar);
        }
        //coin statement
        if (resultItem.getCoin()==null){
            image_coin.setVisibility(View.INVISIBLE);
        }else{
            image_coin.setVisibility(View.VISIBLE);
        }

        position_name.setText(resultItem.getPosition_Name());
//        if (ResultArray.get(0).getPosition_Name().equals(resultItem.getPosition_Name())){
//            position_name.setText("WINNEr");
//        }
        Name.setText(resultItem.getStudent_Name());
        Party.setText(resultItem.getParty_Name());
        ID.setText(resultItem.getStudent_ID());


//        Name.setText(candidateItem.getStudent_Name());
//        Party.setText(candidateItem.getParty_Name());
//        Position.setText(candidateItem.getPosition_Name());
//        Vote.setText(candidateItem.getCandidate_vote());
//        ID.setText(candidateItem.getStudent_ID());
//        Image.setImageBitmap(candidateItem.getImageIcon());
//        Position.setAlpha(0f);
//        ID.setAlpha(0f);

        return rowView;
    }

    private float GetPercentValue(float num1, float num2) {

        float percentage = 0;
        float total_num2_percentage;
        //total of percentage
        total_num2_percentage = num2 *100;
        percentage = total_num2_percentage/num1;

        return percentage;
    }


}
