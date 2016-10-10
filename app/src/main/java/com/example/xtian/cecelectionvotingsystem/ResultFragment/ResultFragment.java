package com.example.xtian.cecelectionvotingsystem.ResultFragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xtian.cecelectionvotingsystem.DBConnection;
import com.example.xtian.cecelectionvotingsystem.R;
import com.example.xtian.cecelectionvotingsystem.adapter.ResultListAdapater;
import com.example.xtian.cecelectionvotingsystem.getSetterItem.ResultItem;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Xtian on 11/18/2015.
 */
public class ResultFragment extends android.support.v4.app.Fragment {

    String student_ID ;    //listview display candidate ID
    String student_Name ;  //listview display candidate name
    String position_Name; //listview display candidate position_Name
    String party_Name;  //listview display candidate party_Name
    String candidate_vote;  //listview display candidate party_Name
    String coin;
    int voters_count;


    ListView list;
    TextView empty,textview_notes,textview_ops;
    RelativeLayout result_Rlayout,result_Rlayout_notStart;
    FrameLayout result_frame;
    ImageView image_note;

    ProgressBar pb;
    private ProgressDialog pDialog;

    //ArrayList<partyitem> partyArray;
    ArrayList<ResultItem> resultArray;
    ResultListAdapater resultListAdapater;

    //statement and async for database
    boolean authenticated = false;
    boolean authenticated_2 = false;
    String statement;
    DBConnection dburl = new DBConnection();
    Connection conn;
    boolean connectionlost = true;


    public ResultFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_result, container, false);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.icon_people_empty,options);


        result_Rlayout = (RelativeLayout)rootView.findViewById(R.id.result_Rlayout);
        result_Rlayout_notStart = (RelativeLayout)rootView.findViewById(R.id.result_Rlayout_notStart);
        result_frame = (FrameLayout)rootView.findViewById(R.id.result_frame);
        image_note = (ImageView)rootView.findViewById(R.id.image_note);
        textview_notes = (TextView)rootView.findViewById(R.id.textview_notes);
        textview_ops = (TextView)rootView.findViewById(R.id.textview_ops);

        pb = (ProgressBar)rootView.findViewById(R.id.candidate_br);
        pb.setVisibility(View.GONE);
        list=(ListView)rootView.findViewById(R.id.listviewcandidate);
        empty = (TextView)rootView.findViewById(R.id.candidate_empty);
        list.setEmptyView(empty);
        image_note.setImageBitmap(bm);



        new flagElectionAsync().execute();

        return rootView;
    }

    //==============================  Async class =========================================

    //flag election ,, if the election start,or finish
    //==================================================
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
                if (getActivity()!=null) {
                    if (flag_NotStart) {
                        pDialog.dismiss();
                    } else if (flag_Start) {
                        pDialog.dismiss();
                        textview_ops.setVisibility(View.GONE);
                        textview_notes.setText("Election has not yet finish.");
                    } else if (flag_finish) {
                        result_Rlayout_notStart.setVisibility(View.GONE);
                        result_Rlayout.setVisibility(View.VISIBLE);
                        result_frame.setVisibility(View.VISIBLE);
                        new ResultCandidateAsync().execute();
                    }
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



    // --- disply all candidate sync
    // ========================================
    class  ResultCandidateAsync extends AsyncTask<Void,Integer,Void> {
        int posistion_result;
        @Override
        protected Void doInBackground(Void... params) {

            statement = "SELECT COUNT(*)AS COUNT_VOTE FROM students";
            resultArray = new ArrayList<ResultItem>();

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);
                    while (rs.next()) {
                        voters_count=rs.getInt("COUNT_VOTE");

                        //president querty
                        posistion_result =1;
                        statement = "SELECT COUNT(*) as COUNT_COIN FROM candidate_details where position_ID= '"+posistion_result+"'AND candidate_coin='1'";
                        rs = st.executeQuery(statement);
                        //coint if have win by coin
                        while (rs.next()){
                            if (rs.getString("COUNT_COIN").equals("1")){
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' AND candidate_coin='1'";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");
                                    resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                }
                            }else{
                                //no coin is win
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' ORDER BY vote_Number DESC LIMIT 1";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");

                                    //resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                    //if tied statement
                                    String statement2 = "SELECT COUNT(*) AS COUNT FROM candidate_details where vote_Number= '"+candidate_vote+"' AND position_ID ='"+posistion_result+"'";
                                    rs =st.executeQuery(statement2);
                                    while (rs.next()) {
                                        if (rs.getString("COUNT").equals("1")) {
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        } else {
                                            candidate_vote = "tied";
                                            party_Name = "Not yet";
                                            student_Name = "Not yet";
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        }
                                    }
                                }
                            }

                        }//end president


                        //vice president
                        posistion_result =2;
                        statement = "SELECT COUNT(*) as COUNT_COIN FROM candidate_details where position_ID= '"+posistion_result+"'AND candidate_coin='1'";
                        rs = st.executeQuery(statement);
                        //coint if have win by coin
                        while (rs.next()){
                            if (rs.getString("COUNT_COIN").equals("1")){
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' AND candidate_coin='1'";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");
                                    resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                }
                            }else{
                                //no coin is win
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' ORDER BY vote_Number DESC LIMIT 1";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");

                                    //resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                    //if tied statement
                                    String statement2 = "SELECT COUNT(*) AS COUNT FROM candidate_details where vote_Number= '"+candidate_vote+"' AND position_ID ='"+posistion_result+"'";
                                    rs =st.executeQuery(statement2);
                                    while (rs.next()) {
                                        if (rs.getString("COUNT").equals("1")) {
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        } else {
                                            candidate_vote = "tied";
                                            party_Name = "Not yet";
                                            student_Name = "Not yet";
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        }
                                    }
                                }
                            }

                        }// end vice president

                        //secretary
                        posistion_result =3;
                        statement = "SELECT COUNT(*) as COUNT_COIN FROM candidate_details where position_ID= '"+posistion_result+"'AND candidate_coin='1'";
                        rs = st.executeQuery(statement);
                        //coint if have win by coin
                        while (rs.next()){
                            if (rs.getString("COUNT_COIN").equals("1")){
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' AND candidate_coin='1'";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");
                                    resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                }
                            }else{
                                //no coin is win
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' ORDER BY vote_Number DESC LIMIT 1";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");

                                    //resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                    //if tied statement
                                    String statement2 = "SELECT COUNT(*) AS COUNT FROM candidate_details where vote_Number= '"+candidate_vote+"' AND position_ID ='"+posistion_result+"'";
                                    rs =st.executeQuery(statement2);
                                    while (rs.next()) {
                                        if (rs.getString("COUNT").equals("1")) {
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        } else {
                                            candidate_vote = "tied";
                                            party_Name = "Not yet";
                                            student_Name = "Not yet";
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        }
                                    }
                                }
                            }

                        }// end secretary

                        //Treasure
                        posistion_result =4;
                        statement = "SELECT COUNT(*) as COUNT_COIN FROM candidate_details where position_ID= '"+posistion_result+"'AND candidate_coin='1'";
                        rs = st.executeQuery(statement);
                        //coint if have win by coin
                        while (rs.next()){
                            if (rs.getString("COUNT_COIN").equals("1")){
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' AND candidate_coin='1'";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");
                                    resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                }
                            }else{
                                //no coin is win
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' ORDER BY vote_Number DESC LIMIT 1";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");

                                    //resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                    //if tied statement
                                    String statement2 = "SELECT COUNT(*) AS COUNT FROM candidate_details where vote_Number= '"+candidate_vote+"' AND position_ID ='"+posistion_result+"'";
                                    rs =st.executeQuery(statement2);
                                    while (rs.next()) {
                                        if (rs.getString("COUNT").equals("1")) {
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        } else {
                                            candidate_vote = "tied";
                                            party_Name = "Not yet";
                                            student_Name = "Not yet";
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        }
                                    }
                                }
                            }

                        }// end Treasure


                        //Auditor
                        posistion_result =5;
                        statement = "SELECT COUNT(*) as COUNT_COIN FROM candidate_details where position_ID= '"+posistion_result+"'AND candidate_coin='1'";
                        rs = st.executeQuery(statement);
                        //coint if have win by coin
                        while (rs.next()){
                            if (rs.getString("COUNT_COIN").equals("1")){
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' AND candidate_coin='1'";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");
                                    resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                }
                            }else{
                                //no coin is win
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' ORDER BY vote_Number DESC LIMIT 1";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");

                                    //resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                    //if tied statement
                                    String statement2 = "SELECT COUNT(*) AS COUNT FROM candidate_details where vote_Number= '"+candidate_vote+"' AND position_ID ='"+posistion_result+"'";
                                    rs =st.executeQuery(statement2);
                                    while (rs.next()) {
                                        if (rs.getString("COUNT").equals("1")) {
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        } else {
                                            candidate_vote = "tied";
                                            party_Name = "Not yet";
                                            student_Name = "Not yet";
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        }
                                    }
                                }
                            }

                        }// end Auditor


                        //Pro-1
                        posistion_result =6;
                        statement = "SELECT COUNT(*) as COUNT_COIN FROM candidate_details where position_ID= '"+posistion_result+"'AND candidate_coin='1'";
                        rs = st.executeQuery(statement);
                        //coint if have win by coin
                        while (rs.next()){
                            if (rs.getString("COUNT_COIN").equals("1")){
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' AND candidate_coin='1'";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");
                                    resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                }
                            }else{
                                //no coin is win
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' ORDER BY vote_Number DESC LIMIT 1";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");

                                    //resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                    //if tied statement
                                    String statement2 = "SELECT COUNT(*) AS COUNT FROM candidate_details where vote_Number= '"+candidate_vote+"' AND position_ID ='"+posistion_result+"'";
                                    rs =st.executeQuery(statement2);
                                    while (rs.next()) {
                                        if (rs.getString("COUNT").equals("1")) {
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        } else {
                                            candidate_vote = "tied";
                                            party_Name = "Not yet";
                                            student_Name = "Not yet";
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        }
                                    }
                                }
                            }

                        }// end Pro-1

                        //Pro-2
                        posistion_result =7;
                        statement = "SELECT COUNT(*) as COUNT_COIN FROM candidate_details where position_ID= '"+posistion_result+"'AND candidate_coin='1'";
                        rs = st.executeQuery(statement);
                        //coint if have win by coin
                        while (rs.next()){
                            if (rs.getString("COUNT_COIN").equals("1")){
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' AND candidate_coin='1'";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");
                                    resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                }
                            }else{
                                //no coin is win
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' ORDER BY vote_Number DESC LIMIT 1";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");

                                    //resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                    //if tied statement
                                    String statement2 = "SELECT COUNT(*) AS COUNT FROM candidate_details where vote_Number= '"+candidate_vote+"' AND position_ID ='"+posistion_result+"'";
                                    rs =st.executeQuery(statement2);
                                    while (rs.next()) {
                                        if (rs.getString("COUNT").equals("1")) {
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        } else {
                                            candidate_vote = "tied";
                                            party_Name = "Not yet";
                                            student_Name = "Not yet";
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        }
                                    }
                                }
                            }

                        }// end Pro-2


                        //1st year Representative
                        posistion_result =8;
                        statement = "SELECT COUNT(*) as COUNT_COIN FROM candidate_details where position_ID= '"+posistion_result+"'AND candidate_coin='1'";
                        rs = st.executeQuery(statement);
                        //coint if have win by coin
                        while (rs.next()){
                            if (rs.getString("COUNT_COIN").equals("1")){
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' AND candidate_coin='1'";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");
                                    resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                }
                            }else{
                                //no coin is win
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' ORDER BY vote_Number DESC LIMIT 1";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");

                                    //resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                    //if tied statement
                                    String statement2 = "SELECT COUNT(*) AS COUNT FROM candidate_details where vote_Number= '"+candidate_vote+"' AND position_ID ='"+posistion_result+"'";
                                    rs =st.executeQuery(statement2);
                                    while (rs.next()) {
                                        if (rs.getString("COUNT").equals("1")) {
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        } else {
                                            candidate_vote = "tied";
                                            party_Name = "Not yet";
                                            student_Name = "Not yet";
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        }
                                    }
                                }
                            }

                        }// end 1st year Representative

                        //2nd year Representative
                        posistion_result =9;
                        statement = "SELECT COUNT(*) as COUNT_COIN FROM candidate_details where position_ID= '"+posistion_result+"'AND candidate_coin='1'";
                        rs = st.executeQuery(statement);
                        //coint if have win by coin
                        while (rs.next()){
                            if (rs.getString("COUNT_COIN").equals("1")){
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' AND candidate_coin='1'";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");
                                    resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                }
                            }else{
                                //no coin is win
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' ORDER BY vote_Number DESC LIMIT 1";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");

                                    //resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                    //if tied statement
                                    String statement2 = "SELECT COUNT(*) AS COUNT FROM candidate_details where vote_Number= '"+candidate_vote+"' AND position_ID ='"+posistion_result+"'";
                                    rs =st.executeQuery(statement2);
                                    while (rs.next()) {
                                        if (rs.getString("COUNT").equals("1")) {
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        } else {
                                            candidate_vote = "tied";
                                            party_Name = "Not yet";
                                            student_Name = "Not yet";
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        }
                                    }
                                }
                            }

                        }// end 2nd year Representative

                        //3rd year Representative
                        posistion_result =10;
                        statement = "SELECT COUNT(*) as COUNT_COIN FROM candidate_details where position_ID= '"+posistion_result+"'AND candidate_coin='1'";
                        rs = st.executeQuery(statement);
                        //coint if have win by coin
                        while (rs.next()){
                            if (rs.getString("COUNT_COIN").equals("1")){
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' AND candidate_coin='1'";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");
                                    resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                }
                            }else{
                                //no coin is win
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' ORDER BY vote_Number DESC LIMIT 1";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");

                                    //resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                    //if tied statement
                                    String statement2 = "SELECT COUNT(*) AS COUNT FROM candidate_details where vote_Number= '"+candidate_vote+"' AND position_ID ='"+posistion_result+"'";
                                    rs =st.executeQuery(statement2);
                                    while (rs.next()) {
                                        if (rs.getString("COUNT").equals("1")) {
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        } else {
                                            candidate_vote = "tied";
                                            party_Name = "Not yet";
                                            student_Name = "Not yet";
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        }
                                    }
                                }
                            }

                        }// end 3rd year Representative


                        //4th year Representative
                        posistion_result =11;
                        statement = "SELECT COUNT(*) as COUNT_COIN FROM candidate_details where position_ID= '"+posistion_result+"'AND candidate_coin='1'";
                        rs = st.executeQuery(statement);
                        //coint if have win by coin
                        while (rs.next()){
                            if (rs.getString("COUNT_COIN").equals("1")){
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' AND candidate_coin='1'";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");
                                    resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                }
                            }else{
                                //no coin is win
                                statement = "SELECT * FROM candidate_details where position_ID= '"+posistion_result+"' ORDER BY vote_Number DESC LIMIT 1";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {

                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    //Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                                    //for decodebytearray memory
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");

                                    //resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                    //if tied statement
                                    String statement2 = "SELECT COUNT(*) AS COUNT FROM candidate_details where vote_Number= '"+candidate_vote+"' AND position_ID ='"+posistion_result+"'";
                                    rs =st.executeQuery(statement2);
                                    while (rs.next()) {
                                        if (rs.getString("COUNT").equals("1")) {
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        } else {
                                            candidate_vote = "tied";
                                            party_Name = "Not yet";
                                            student_Name = "Not yet";
                                            resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                        }
                                    }
                                }
                            }

                        }// end 4th year Representative

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
            //pb.setVisibility(getView().VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //pb.setVisibility(getView().INVISIBLE);
            pDialog.dismiss();

            //connection status
            if (connectionlost){
                //result
                if (getActivity()!=null){
                    resultListAdapater= new ResultListAdapater(getActivity(),R.layout.result_list,resultArray);
                    list.setAdapter((ListAdapter)resultListAdapater);
                    list.setDivider(null);
                    list.setDividerHeight(0);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String position_name = ((TextView) view.findViewById(R.id.textviewPosition)).getText()
                                    .toString();
                            //Toast.makeText(getActivity(), "" + titre, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(),Result_activity.class);
                            intent.putExtra("intent_positionName",position_name);
                            intent.putExtra("IntentdisplyView", (int) 5);
                            startActivity(intent);
                        }
                    });
                }else{}
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                alertConnectionLost.setTitle("Connection..");
                alertConnectionLost.setMessage("No connection in database!  ");
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
}
