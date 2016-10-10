package com.example.xtian.cecelectionvotingsystem.ResultFragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.example.xtian.cecelectionvotingsystem.adapter.CountResultListAdapater;
import com.example.xtian.cecelectionvotingsystem.getSetterItem.ResultItem;
import com.example.xtian.cecelectionvotingsystem.home;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Xtian on 12/30/2015.
 */
public class Result_activity extends AppCompatActivity {

    String student_ID ;    //listview display candidate ID
    String student_Name ;  //listview display candidate name
    String position_Name; //listview display candidate position_Name
    String party_Name;  //listview display candidate party_Name
    String candidate_vote;  //listview display candidate party_Name
    String coin;
    String tmp_candidate_vote;
    int position_coin_remove;
    int voters_count;
    String ID_COIN;

    ListView list;
    TextView empty,textview_notes,textview_ops;
    RelativeLayout result_Rlayout,result_Rlayout_notStart;
    FrameLayout result_frame;
    ImageView image_note;

    ProgressBar pb;
    private ProgressDialog pDialog;


    //intent displayView
    private static Integer IntentdisplyViews;
    String intent_positionName;

    ArrayList<ResultItem> resultArray;
    CountResultListAdapater countResultListAdapater;

    //statement and async for database
    boolean authenticated = false;
    boolean authenticated_2 = false;
    String statement;
    DBConnection dburl = new DBConnection();
    Connection conn;
    boolean connectionlost = true;


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Result_activity.this, home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("IntentdisplyView", IntentdisplyViews);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_4, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidates_win_result);
        //action header bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);


        pb = (ProgressBar)findViewById(R.id.candidate_br);
        pb.setVisibility(View.GONE);
        list=(ListView)findViewById(R.id.listviewcandidate);
        empty = (TextView)findViewById(R.id.candidate_empty);
        list.setEmptyView(empty);

        //get data from student
        Intent intent = getIntent();
        intent_positionName = intent.getStringExtra("intent_positionName");
        IntentdisplyViews = intent.getIntExtra("IntentdisplyView", 0);
        //name action abar
        getSupportActionBar().setTitle(intent_positionName);

        Coin_winner coin_winner = new Coin_winner();
        coin_winner.execute();

    }

    //add coin
    private void add_coin(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                Result_activity.this);

        // Setting Dialog Title
        alertDialog2.setTitle("Confirm choose...");

        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want to win this candidate? ");



        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        Add_coin_candidates add_coin_candidates = new  Add_coin_candidates();
                        add_coin_candidates.execute();


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
    //remove coin
    private void remove_coin(){

        if (position_coin_remove==0){
            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                    Result_activity.this);

            // Setting Dialog Title
            alertDialog2.setTitle("Confirm choose...");

            // Setting Dialog Message
            alertDialog2.setMessage("Are you sure you want to remove the coin? ");



            // Setting Positive "Yes" Btn
            alertDialog2.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog
                            Remove_coin_candidates remove_coin_candidates = new  Remove_coin_candidates();
                            remove_coin_candidates.execute();


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
        }else {
            //nothing
        }
    }
    //====================== Async task class
    // =====================================
    // 0--------------------------------------------- ===================================================



    class Coin_winner extends AsyncTask<Void,Integer,Void>{


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
                        voters_count = rs.getInt("COUNT_VOTE");

                        statement = "SELECT COUNT(*) as COUNT_COIN FROM candidate_details where position_Name= '"+intent_positionName+"'AND candidate_coin='1'";
                        rs = st.executeQuery(statement);

                        //win or not
                        while (rs.next()){
                            //winner by coin
                            if (rs.getString("COUNT_COIN").equals("1")){
                                statement = "SELECT * FROM candidate_details where position_Name= '"+intent_positionName+"' ORDER BY candidate_coin DESC";
                                rs = st.executeQuery(statement);
                                while (rs.next()) {
                                    student_ID = rs.getString("student_ID");
                                    student_Name = rs.getString("student_Name");
                                    position_Name = rs.getString("position_Name");
                                    party_Name = rs.getString("party_Name");
                                    candidate_vote = rs.getString("vote_Number");
                                    Blob b = (Blob) rs.getBlob("candidate_img");
                                    byte[] temp = b.getBytes(1, (int) b.length());
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPurgeable = true;
                                    Bitmap imageIcon = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
                                    coin = rs.getString("candidate_coin");
                                    resultArray.add(new ResultItem(student_ID, student_Name, position_Name, party_Name, candidate_vote, imageIcon, coin, voters_count));
                                    authenticated = true;
                                }
                                //no winner by coin
                            }else{
                                //Vote winner async
                            }
                        }

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    connectionlost = false;
                }
            } catch (InstantiationException e) {
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
            pDialog = new ProgressDialog(Result_activity.this);
            pDialog.setMessage("Loading Candidates. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (connectionlost){
                //result
                if (authenticated) {
                    pDialog.dismiss();
                    countResultListAdapater = new CountResultListAdapater(Result_activity.this, R.layout.count_result_list, resultArray);
                    list.setAdapter((ListAdapter) countResultListAdapater);
                    list.setDivider(null);
                    list.setDividerHeight(0);
                    final Animation anim_alpha = AnimationUtils.loadAnimation(Result_activity.this, R.anim.anim_alpha);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            view.startAnimation(anim_alpha);
                            ID_COIN = ((TextView) view.findViewById(R.id.textviewCandidate_ID)).getText()
                                    .toString();
                            position_coin_remove = i;
                            remove_coin();

                        }
                    });
                }else{
                    Vote_winner vote_winner = new Vote_winner();
                    vote_winner.execute();
                }
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Result_activity.this);
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

    class Vote_winner extends AsyncTask<Void,Integer,Void>{
        @Override
        protected Void doInBackground(Void... params) {

            statement = "SELECT * FROM candidate_details where position_Name= '"+intent_positionName+"' ORDER BY vote_Number DESC LIMIT 1";
            resultArray = new ArrayList<ResultItem>();
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    //count the leading candidate vote
                    while (rs.next()) {

                         tmp_candidate_vote = rs.getString("vote_Number");

                        //second statement
                        String statement2 = "SELECT COUNT(*) AS COUNT FROM candidate_details where vote_Number= '"+tmp_candidate_vote+"' AND position_Name ='"+intent_positionName+"'";
                        rs =st.executeQuery(statement2);
                        while (rs.next()) {
                            if (rs.getString("COUNT").equals("1")) {
                                //winner by vote
                                statement = "SELECT * FROM candidate_details where position_Name= '"+intent_positionName+"' ORDER BY vote_Number DESC";
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
                                    authenticated_2 = true;
                                }
                            } else {
                                //tied
                                authenticated_2 = false;
                            }
                        }

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    connectionlost = false;
                }
            } catch (InstantiationException e) {
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
            if (connectionlost){
                //result
                if (authenticated_2) {
                    pDialog.dismiss();
                    countResultListAdapater = new CountResultListAdapater(Result_activity.this, R.layout.count_result_list, resultArray);
                    list.setAdapter((ListAdapter) countResultListAdapater);
                    list.setDivider(null);
                    list.setDividerHeight(0);
                }else{
                    //tied
                    Tie_candidate tie_candidate = new Tie_candidate();
                    tie_candidate.execute();
                }
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Result_activity.this);
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

    class Tie_candidate extends AsyncTask<Void,Integer,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            statement = "SELECT * FROM candidate_details where vote_Number= '"+tmp_candidate_vote+"' AND position_Name ='"+intent_positionName+"'";
            resultArray = new ArrayList<ResultItem>();

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    //tied candidates
                    while (rs.next()) {
                        student_ID = rs.getString("student_ID");
                        student_Name = rs.getString("student_Name");
                        position_Name = "tied";
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
                } catch (SQLException e) {
                    e.printStackTrace();
                    connectionlost = false;
                }
            } catch (InstantiationException e) {
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
            if (connectionlost){
                    pDialog.dismiss();
                    countResultListAdapater = new CountResultListAdapater(Result_activity.this, R.layout.count_result_list, resultArray);
                    list.setAdapter((ListAdapter) countResultListAdapater);
                    list.setDivider(null);
                    list.setDividerHeight(0);
                    final Animation anim_alpha = AnimationUtils.loadAnimation(Result_activity.this, R.anim.anim_alpha);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            view.startAnimation(anim_alpha);
                             ID_COIN = ((TextView) view.findViewById(R.id.textviewCandidate_ID)).getText()
                                    .toString();
                            //Toast.makeText(getActivity(), "" + titre, Toast.LENGTH_SHORT).show();
                            add_coin();

                        }
                        });

                        AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Result_activity.this);
                        alertConnectionLost.setTitle("Select a winner..");
                        alertConnectionLost.setMessage("Choose only one.");
                        alertConnectionLost.setCancelable(false);
                        alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertConnectionLost.show();
                }else{
                    //lost Connection
                    AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Result_activity.this);
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

    //add coin
    class Add_coin_candidates extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);
                            //if name 0 row = og ala pay name sa party

                    PreparedStatement stInsert = conn.prepareStatement("UPDATE candidates SET candidate_coin=? WHERE student_ID=? ");
                    stInsert.setInt(1, 1);
                    stInsert.setInt(2, Integer.parseInt(ID_COIN));
                    stInsert.executeUpdate();


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
            pDialog = new ProgressDialog(Result_activity.this);
            pDialog.setMessage("Updating Candidates. Please wait..");
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

                    AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Result_activity.this);
                    alertConnectionLost.setTitle("Success..");
                    alertConnectionLost.setMessage("Update Successful");
                    alertConnectionLost.setCancelable(false);
                    alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            onBackPressed();
                        }
                    });
                    alertConnectionLost.show();
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Result_activity.this);
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
    //remove coin
    class Remove_coin_candidates extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);
                    //if name 0 row = og ala pay name sa party

                    PreparedStatement stInsert = conn.prepareStatement("UPDATE candidates SET candidate_coin=? WHERE student_ID=? ");
                    stInsert.setString(1, null);
                    stInsert.setInt(2, Integer.parseInt(ID_COIN));
                    stInsert.executeUpdate();


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
            pDialog = new ProgressDialog(Result_activity.this);
            pDialog.setMessage("Updating Candidates. Please wait..");
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

                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Result_activity.this);
                alertConnectionLost.setTitle("Success..");
                alertConnectionLost.setMessage("Update Successful");
                alertConnectionLost.setCancelable(false);
                alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Coin_winner coin_winner = new Coin_winner();
                        coin_winner.execute();
                    }
                });
                alertConnectionLost.show();
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Result_activity.this);
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
}
