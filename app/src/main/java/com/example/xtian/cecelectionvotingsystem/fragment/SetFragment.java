package com.example.xtian.cecelectionvotingsystem.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xtian.cecelectionvotingsystem.DBConnection;
import com.example.xtian.cecelectionvotingsystem.R;
import com.example.xtian.cecelectionvotingsystem.home;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Xtian on 11/18/2015.
 */
public class SetFragment extends android.support.v4.app.Fragment {

    Button btnStartTime,buttonStopTime,buttonFinishTime,buttonNewTime,buttonResume;
    ProgressBar mProgressBar;

    int time_progress; // time progress election
    int time_elapsed;  // time elapsed  election
    int hour_value; // admin set time

    private TextView textViewShowTime; // will show the time
    private CountDownTimer countDownTimer; // built in android class
    // CountDownTimer
    private long totalTimeCountInMilliseconds; // total count down time in
    // milliseconds
    private long timeBlinkInMilliseconds; // start time of start blinking
    private boolean blink; // controls the blinking .. on and off

    //statement and async for database
    private ProgressDialog pDialog;
    boolean authenticated = false;
    String statement;
    DBConnection dburl = new DBConnection();
    Connection conn;
    boolean connectionlost = true;

    public SetFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_set, container, false);

        btnStartTime = (Button)rootView.findViewById(R.id.btnStartTime);
        buttonStopTime = (Button)rootView.findViewById(R.id.btnStopTime);
        buttonFinishTime = (Button)rootView.findViewById(R.id.btnFinishTime);
        buttonResume = (Button)rootView.findViewById(R.id.btnResume);
        buttonNewTime = (Button)rootView.findViewById(R.id.btnNewTime);
        textViewShowTime = (TextView)rootView.findViewById(R.id.tvTimeCount);
        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progressbar);


        new flagElectionAsync().execute(); // check flag election


        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDialog();
            }
        });
        buttonStopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopElection();

            }
        });
        buttonFinishTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishElection();
            }
        });
        buttonResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDialog_resume();
            }
        });
        buttonNewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newElection();
            }
        });


        return rootView;
    }

    //stop election
    private void newElection(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                getActivity());
        // Setting Dialog Title
        alertDialog2.setTitle("Confirm choose...");
        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want set new election? All vote will be 0.");
        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        new newTimerElectionAsync().execute();

                    }
                });
        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });
        // Showing Alert Dialog
        alertDialog2.show();
    }
    //stop election
    private void stopElection(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                getActivity());
        // Setting Dialog Title
        alertDialog2.setTitle("Confirm choose...");
        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want stop the election? All vote will be 0.");
        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        new stopTimerElectionAsync().execute();

                    }
                });
        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });
        // Showing Alert Dialog
        alertDialog2.show();
    }
    //finish election
    private void finishElection(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                getActivity());
        // Setting Dialog Title
        alertDialog2.setTitle("Confirm choose...");
        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want to finish the election.");
        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        new finishTimerElectionAsync().execute();

                    }
                });
        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });
        // Showing Alert Dialog
        alertDialog2.show();
    }
    //input hours election dialog
    private void inputDialog(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Choose Hour");

        NumberPicker myNumberPicker = new NumberPicker(getActivity());
        myNumberPicker.setMaxValue(24);
        myNumberPicker.setMinValue(0);
        myNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int change_value) {
                hour_value = change_value;
            }
        };
        myNumberPicker.setOnValueChangedListener(myValChangedListener);
        alertDialog2.setView(myNumberPicker);

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        //new deleteStudentAsync().execute();
                        if (hour_value==0){ // zero hour
                            Toast.makeText(getActivity(), "No hours selected!", Toast.LENGTH_SHORT).show();
                        }else{ // start election
                            new SetCandiatesAsync().execute();
                            //new setElectionAsync().execute();
                            //startTimer();
                        }
                    }
                });

        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();

                    }
                });

        // Showing Alert Dialog
        alertDialog2.show();
    }
    //input hours election dialog
    private void inputDialog_resume(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Choose Hour");

        NumberPicker myNumberPicker = new NumberPicker(getActivity());
        myNumberPicker.setMaxValue(24);
        myNumberPicker.setMinValue(0);
        myNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int change_value) {
                hour_value = change_value;
            }
        };
        myNumberPicker.setOnValueChangedListener(myValChangedListener);
        alertDialog2.setView(myNumberPicker);

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        //new deleteStudentAsync().execute();
                        if (hour_value==0){ // zero hour
                            Toast.makeText(getActivity(), "No hours selected!", Toast.LENGTH_SHORT).show();
                        }else{ // start election
                            new ResumeElectionAsync().execute();
                            //new setElectionAsync().execute();
                            //startTimer();
                        }
                    }
                });

        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();

                    }
                });

        // Showing Alert Dialog
        alertDialog2.show();
    }
    //countdown timer class
    private void startTimer() {

            int input_value = time_elapsed;// second
            totalTimeCountInMilliseconds =  input_value * 1000; // second to milisecond
            mProgressBar.setMax(time_progress);
            mProgressBar.setProgress((int) (time_progress - time_elapsed));
            timeBlinkInMilliseconds = 30 * 1000;

        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds,100) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {

                mProgressBar.setProgress((int) (leftTimeInMilliseconds / 1000));

                if (leftTimeInMilliseconds < timeBlinkInMilliseconds) {
//                    textViewShowTime.setTextAppearance(getApplicationContext(),
//                            R.style.blinkText);
                    // change the style of the textview .. giving a red
                    // alert style

                    if (blink) {
                        textViewShowTime.setVisibility(View.VISIBLE);
                        // if blink is true, textview will be visible
                    } else {
                        textViewShowTime.setVisibility(View.INVISIBLE);
                    }

                    blink = !blink; // toggle the value of blink
                }
                int days = (int) ((leftTimeInMilliseconds / 1000) / 86400);
                int hours = (int) (((leftTimeInMilliseconds / 1000)
                        - (days * 86400)) / 3600);
                int minutes = (int) (((leftTimeInMilliseconds / 1000)
                        - (days * 86400) - (hours * 3600)) / 60);
                int secondss = (int) ((leftTimeInMilliseconds / 1000) % 60);

                String format_time = String.format("%02dh %02dm %02ds",
                        hours, minutes, secondss);

//                textViewShowTime.setText(String.format("%02d", seconds / 60)
//                        + ":" + String.format("%02d", seconds % 60));
                textViewShowTime.setText(format_time);

                // format the textview to show the easily readable format
            }

            @Override
            public void onFinish() {
//                Notification n =
//                        new Notification(R.mipmap.ic_launcher, getString(R.string.noticeMe),
//                                System.currentTimeMillis());
//
//                Intent notIntent = new Intent(getActivity(), com.example.xtian.cecelectionvotingsystem.splash.class);
//
//                PendingIntent wrappedIntent = PendingIntent.getActivity(getActivity(), 0, notIntent,0);
//
//                n.setLatestEventInfo(getContext(), getString(R.string.title), getString(R.string.message), wrappedIntent);
//                n.flags |= Notification.FLAG_AUTO_CANCEL;
//                n.flags |= Notification.DEFAULT_SOUND;
//                n.flags |= Notification.DEFAULT_VIBRATE;
//                n.ledARGB = 0xff0000ff;
//                n.flags |= Notification.FLAG_SHOW_LIGHTS;
//
//                // Now invoke the Notification Service
//                String notifService = Context.NOTIFICATION_SERVICE;
//                NotificationManager mgr =
//                        (NotificationManager) getActivity().getSystemService(notifService);
//                mgr.notify(0, n);
                new StartTimerElectionAsync().execute();
            }
        }.start();


    }

    class flagElectionAsync extends AsyncTask<Void,Integer,Void>{

        boolean flag_NotStart = false;
        boolean flag_Start = false;
        boolean flag_finish = false;

        @Override
        protected Void doInBackground(Void... params) {
            statement = "SELECT * FROM flag";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        //set time for the election
                        int flag_election = rs.getInt("set_election");
                        if (flag_election==0){
                            flag_NotStart = true;
                        }else if (flag_election==1){
                            flag_Start = true;
                        }else if (flag_election==2){
                            flag_finish = true;
                        }

                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    connectionlost = false;
                }
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Election. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //connection status
            if (connectionlost){
                if (flag_NotStart){

                    pDialog.dismiss();
                    hour_value = 0;
                    textViewShowTime.setText("00:00");
                    textViewShowTime.setVisibility(View.VISIBLE);
                    btnStartTime.setVisibility(View.VISIBLE);
                    buttonNewTime.setVisibility(View.GONE);
                    buttonResume.setVisibility(View.GONE);
                    buttonStopTime.setVisibility(View.GONE);
                    buttonFinishTime.setVisibility(View.GONE);
                    mProgressBar.setProgress(0);

                }else if (flag_Start){
                    new StartTimerElectionAsync().execute();
                }else if (flag_finish){
                    pDialog.dismiss();
                    hour_value = 0;
                    textViewShowTime.setText("Time up!");
                    textViewShowTime.setVisibility(View.VISIBLE);
                    btnStartTime.setVisibility(View.GONE);
                    buttonStopTime.setVisibility(View.GONE);
                    buttonFinishTime.setVisibility(View.GONE);
                    buttonNewTime.setVisibility(View.VISIBLE);
                    buttonResume.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(0);
                }
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                alertConnectionLost.setTitle("Connection..");
                alertConnectionLost.setMessage("No connection in database!");
                alertConnectionLost.setCancelable(false);
                alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        System.exit(0);
                    }
                });
                alertConnectionLost.show();
            }
        }
    }

    //candidates sync
    class SetCandiatesAsync  extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            statement = "SELECT COUNT(*)AS COUNT FROM candidates";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);
                    while (rs.next()) {
                        if (rs.getInt("COUNT") == 0){
                            authenticated = true;
                        }else {
                            authenticated =false;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    connectionlost = false;
                }
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Start time. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //connection status
            if (connectionlost){
                //result
                if (authenticated){
                    //true student
                    pDialog.dismiss();
                    AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                    alertConnectionLost.setTitle("Attention..");
                    alertConnectionLost.setMessage("No candidates has been run!");
                    alertConnectionLost.setCancelable(false);
                    alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertConnectionLost.show();
                }else{
                    // false student
                    new setElectionAsync().execute();
                }
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                alertConnectionLost.setTitle("Connection..");
                alertConnectionLost.setMessage("No connection in database!");
                alertConnectionLost.setCancelable(false);
                alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        System.exit(0);
                    }
                });
                alertConnectionLost.show();
            }
        }
    }

    class setElectionAsync extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            statement = "SELECT UNIX_TIMESTAMP() as time_span";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        //set time for the election
                        int current_time = rs.getInt("time_span");
                        int end_election = (hour_value*3600) + current_time;
                        PreparedStatement stInsert = conn.prepareStatement("UPDATE election SET start_election=?,end_election=? WHERE id=? ");
                        stInsert.setInt(1,current_time);
                        stInsert.setInt(2, end_election);
                        stInsert.setInt(3, 1);
                        stInsert.executeUpdate();
                        // flag the student to vote
                        stInsert = conn.prepareStatement("UPDATE flag SET set_election=?");
                        stInsert.setInt(1, 1);
                        stInsert.executeUpdate();
                        authenticated = true;
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    connectionlost = false;
                }
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();

            //connection status
            if (connectionlost){
                //result
                if (authenticated){
                    //true create
                    Intent intent = new Intent(getActivity(), home.class);
                    intent.putExtra("IntentdisplyView", (int) 4);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    //new StartTimerElectionAsync().execute();

                }else{
                    //false create time
                }
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                alertConnectionLost.setTitle("Connection..");
                alertConnectionLost.setMessage("No connection in database!");
                alertConnectionLost.setCancelable(false);
                alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        System.exit(0);
                    }
                });
                alertConnectionLost.show();
            }
        }
    }

    class ResumeElectionAsync extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            statement = "SELECT UNIX_TIMESTAMP() as time_span";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        //set time for the election
                        int current_time = rs.getInt("time_span");
                        int end_election = (hour_value*3600) + current_time;
                        PreparedStatement stInsert = conn.prepareStatement("UPDATE election SET start_election=?,end_election=? WHERE id=? ");
                        stInsert.setInt(1,current_time);
                        stInsert.setInt(2, end_election);
                        stInsert.setInt(3, 1);
                        stInsert.executeUpdate();
                        // flag the student to vote
                        stInsert = conn.prepareStatement("UPDATE flag SET set_election=?");
                        stInsert.setInt(1, 1);
                        stInsert.executeUpdate();
                        authenticated = true;
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    connectionlost = false;
                }
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();

            //connection status
            if (connectionlost){
                //result
                if (authenticated){
                    //true create
                    Intent intent = new Intent(getActivity(), home.class);
                    intent.putExtra("IntentdisplyView",(int)4);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    //new StartTimerElectionAsync().execute();

                }else{
                    //false create time
                }
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                alertConnectionLost.setTitle("Connection..");
                alertConnectionLost.setMessage("No connection in database!");
                alertConnectionLost.setCancelable(false);
                alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        System.exit(0);
                    }
                });
                alertConnectionLost.show();
            }
        }
    }

    class StartTimerElectionAsync  extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            statement = "SELECT UNIX_TIMESTAMP() as time_span";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        //set time for the election
                        int current_time = rs.getInt("time_span");
                        rs = st.executeQuery("SELECT * FROM election");
                        while (rs.next()){
                            int end_time = rs.getInt("end_election");
                            time_progress = end_time-rs.getInt("start_election");
                            if (end_time <= current_time){ // times up
                                // flag the student to vote
                                PreparedStatement stInsert = conn.prepareStatement("UPDATE flag SET set_election=?");
                                stInsert.setInt(1, 2);
                                stInsert.executeUpdate();
                                authenticated = false;
                            }else{ // start election
                                authenticated = true;
                                time_elapsed = end_time - current_time;
                            }
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    connectionlost = false;
                }
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();

            //connection status
            if (connectionlost){
                //result
                if (authenticated){
                    //election start
                    startTimer();
                    buttonStopTime.setVisibility(View.VISIBLE);
                    buttonFinishTime.setVisibility(View.VISIBLE);
                    btnStartTime.setVisibility(View.GONE);
                    buttonNewTime.setVisibility(View.GONE);
                    buttonResume.setVisibility(View.GONE);
                    textViewShowTime.setVisibility(View.VISIBLE);

                }else{
                    //election times up
                    startTimer();
                    time_elapsed = 0;
                    time_progress=0;
                    countDownTimer.cancel();
                    hour_value = 0;
                    textViewShowTime.setText("Time up!");
                    textViewShowTime.setVisibility(View.VISIBLE);
                    btnStartTime.setVisibility(View.GONE);
                    buttonStopTime.setVisibility(View.GONE);
                    buttonFinishTime.setVisibility(View.GONE);
                    buttonNewTime.setVisibility(View.VISIBLE);
                    buttonResume.setVisibility(View.VISIBLE);
                }
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                alertConnectionLost.setTitle("Connection..");
                alertConnectionLost.setMessage("No connection in database!");
                alertConnectionLost.setCancelable(false);
                alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        System.exit(0);
                    }
                });
                alertConnectionLost.show();
            }

        }
    }

    class stopTimerElectionAsync extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    //set election time to 0
                    PreparedStatement stInsert = conn.prepareStatement("UPDATE election SET start_election=?,end_election=? WHERE id=? ");
                    stInsert.setInt(1,0);
                    stInsert.setInt(2, 0);
                    stInsert.setInt(3, 1);
                    stInsert.executeUpdate();
                    // flag election to 0
                    stInsert = conn.prepareStatement("UPDATE flag SET set_election=?");
                    stInsert.setInt(1, 0);
                    stInsert.executeUpdate();

                    //set vote_number to 0
                    stInsert = conn.prepareStatement("UPDATE candidates SET vote_Number=?,candidate_coin=?");
                    stInsert.setInt(1, 0);
                    stInsert.setString(2,null);
                    stInsert.executeUpdate();

                    authenticated = true;

                } catch (SQLException e) {
                    e.printStackTrace();
                    connectionlost = false;
                }
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Stopping election. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();

            //connection status
            if (connectionlost){
                //result
                if (authenticated){
                    //true student
                    countDownTimer.cancel();
                    AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                    alertConnectionLost.setTitle("Election Stop..");
                    alertConnectionLost.setMessage("The election has been stop!");
                    alertConnectionLost.setCancelable(false);
                    alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            Intent intent = new Intent(getActivity(), home.class);
                            intent.putExtra("IntentdisplyView", (int) 4);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                            //new flagElectionAsync().execute();
                        }
                    });
                    alertConnectionLost.show();

                }else{}
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                alertConnectionLost.setTitle("Connection..");
                alertConnectionLost.setMessage("No connection in database!");
                alertConnectionLost.setCancelable(false);
                alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        System.exit(0);
                    }
                });
                alertConnectionLost.show();
            }
        }
    }

    class newTimerElectionAsync extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    //set election time to 0
                    PreparedStatement stInsert = conn.prepareStatement("UPDATE election SET start_election=?,end_election=? WHERE id=? ");
                    stInsert.setInt(1,0);
                    stInsert.setInt(2, 0);
                    stInsert.setInt(3, 1);
                    stInsert.executeUpdate();
                    // flag election to 0
                    stInsert = conn.prepareStatement("UPDATE flag SET set_election=?");
                    stInsert.setInt(1, 0);
                    stInsert.executeUpdate();

                    //set vote_number to 0
                    stInsert = conn.prepareStatement("UPDATE candidates SET vote_Number=?,candidate_coin=?");
                    stInsert.setInt(1, 0);
                    stInsert.setString(2,null);
                    stInsert.executeUpdate();

                    authenticated = true;

                } catch (SQLException e) {
                    e.printStackTrace();
                    connectionlost = false;
                }
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Set new time election. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();

            //connection status
            if (connectionlost){
                //result
                if (authenticated){
                    Intent intent = new Intent(getActivity(), home.class);
                    intent.putExtra("IntentdisplyView",(int)4);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    //new flagElectionAsync().execute();
                }else{}
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                alertConnectionLost.setTitle("Connection..");
                alertConnectionLost.setMessage("No connection in database!");
                alertConnectionLost.setCancelable(false);
                alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        System.exit(0);
                    }
                });
                alertConnectionLost.show();
            }
        }
    }

    class finishTimerElectionAsync extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            statement = "SELECT UNIX_TIMESTAMP() as time_span";
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        //set time for the election
                        int current_time = rs.getInt("time_span");
                        PreparedStatement stInsert = conn.prepareStatement("UPDATE election SET end_election=? WHERE id=? ");
                        stInsert.setInt(1, current_time);
                        stInsert.setInt(2, 1);
                        stInsert.executeUpdate();
                        // flag the student to vote
                        stInsert = conn.prepareStatement("UPDATE flag SET set_election=?");
                        stInsert.setInt(1, 2);
                        stInsert.executeUpdate();
                        authenticated = true;
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    connectionlost = false;
                }
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Finishing Election. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();

            //connection status
            if (connectionlost){
                //result
                if (authenticated){
                    //true student
//                    Notification n =
//                        new Notification(R.mipmap.ic_launcher, getString(R.string.noticeMe),
//                                System.currentTimeMillis());
//
//                    Intent notIntent = new Intent(getActivity(), com.example.xtian.cecelectionvotingsystem.splash.class);
//
//                    PendingIntent wrappedIntent = PendingIntent.getActivity(getActivity(), 0, notIntent,0);
//
//                    n.setLatestEventInfo(getContext(), getString(R.string.title), getString(R.string.message), wrappedIntent);
//                    n.flags |= Notification.FLAG_AUTO_CANCEL;
//                    //n.flags |= Notification.DEFAULT_SOUND;
//                    n.sound=(Settings.System.DEFAULT_NOTIFICATION_URI);
//                    //n.flags |= Notification.DEFAULT_VIBRATE;
//                    n.ledARGB = 0xff0000ff;
//                    n.flags |= Notification.FLAG_SHOW_LIGHTS;
//
//                    // Now invoke the Notification Service
//                    String notifService = Context.NOTIFICATION_SERVICE;
//                    NotificationManager mgr =
//                        (NotificationManager) getActivity().getSystemService(notifService);
//                    mgr.notify(0, n);
                    countDownTimer.cancel();
                    AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                    alertConnectionLost.setTitle("Election end..");
                    alertConnectionLost.setMessage("The election has been end!");
                    alertConnectionLost.setCancelable(false);
                    alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(getActivity(), home.class);
                            intent.putExtra("IntentdisplyView",(int)4);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                            //new flagElectionAsync().execute();

                        }
                    });
                    alertConnectionLost.show();


                }else{}
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                alertConnectionLost.setCancelable(false);
                alertConnectionLost.setTitle("Connection..");
                alertConnectionLost.setMessage("No connection in database!");

                alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        System.exit(0);
                    }
                });
                alertConnectionLost.show();
            }
        }
    }
}
