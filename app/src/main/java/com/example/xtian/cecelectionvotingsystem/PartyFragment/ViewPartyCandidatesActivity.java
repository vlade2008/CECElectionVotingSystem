package com.example.xtian.cecelectionvotingsystem.PartyFragment;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xtian.cecelectionvotingsystem.DBConnection;
import com.example.xtian.cecelectionvotingsystem.R;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Xtian on 11/29/2015.
 */
public class ViewPartyCandidatesActivity extends AppCompatActivity {

    //textview candidate
    TextView viewname;
    TextView viewposition;
    TextView viewParty;
    TextView viewVote;
    ImageView viewcandidate_img;


    //intent displayView
    private static Integer IntentdisplyViews;
    String intent_studentID;
    String intent_CandidateParty;
    String intent_partyID;


    //candidate name and ID
    String Candidate_Name;
    String Candidate_Id;
    String Candidate_PositionName;
    String Candidate_PartyName;
    String Candidate_VoteNumber;
    Bitmap candidate_bitmap;

    //statement and async for database
    boolean authenticated = false;
    String statement;
    DBConnection dburl = new DBConnection();
    Connection conn;
    boolean connectionlost = true;
    private ProgressDialog pDialog;



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewPartyCandidatesActivity.this, Party_view_candidates.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("IntentdisplyView", IntentdisplyViews);
        intent.putExtra("intent_partyID",intent_partyID);
        intent.putExtra("intent_partyName", intent_CandidateParty);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_candidates);
        //action header bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        viewname = (TextView)findViewById(R.id.viewname);
        viewParty = (TextView)findViewById(R.id.viewParty);
        viewposition = (TextView)findViewById(R.id.viewposition);
        viewVote = (TextView)findViewById(R.id.viewVote);
        viewcandidate_img = (ImageView)findViewById(R.id.viewcandidate_img);


        //get data from student
        Intent intent = getIntent();
        intent_studentID = intent.getStringExtra("intent_studentID");
        IntentdisplyViews = intent.getIntExtra("IntentdisplyView", 0);
        
        intent_CandidateParty = intent.getStringExtra("intent_partyName");
        intent_partyID = intent.getStringExtra("intent_partyID");


        CandidateViewAsync candidateViewAsync = new CandidateViewAsync();
        candidateViewAsync.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                deleteDialog();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(ViewPartyCandidatesActivity.this,EditPartyCandidatesAcitivity.class);
                intent.putExtra("intent_studentID",intent_studentID);
                intent.putExtra("intent_positionName",Candidate_PositionName);
                intent.putExtra("intent_partyName",Candidate_PartyName);
                intent.putExtra("IntentdisplyView", (int) 2);

                intent.putExtra("intent_partyID",intent_partyID);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //AlertDialog Confirmation to delete Candidate
    private void deleteDialog(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                ViewPartyCandidatesActivity.this);

        // Setting Dialog Title
        alertDialog2.setTitle("Confirm choose...");

        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure want to delete "+Candidate_Name + "?");


        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        deleteCandidatetAsync deleteCandidatetAsync = new deleteCandidatetAsync();
                        deleteCandidatetAsync.execute();

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


    //*
    // --------
    // sync all candidate, position , party
    class  CandidateViewAsync extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            statement = "SELECT * FROM candidate_details where student_ID= '" + intent_studentID + "'";
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        Candidate_Id = rs.getString("student_ID");
                        Candidate_Name = rs.getString("student_Name");
                        Candidate_PositionName = rs.getString("position_Name");
                        Candidate_PartyName = rs.getString("party_Name");
                        Candidate_VoteNumber = rs.getString("vote_Number");
                        Blob b = (Blob) rs.getBlob("candidate_img");
                        byte[] temp = b.getBytes(1, (int) b.length());
                        //candidate_bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                        //for decodebytearray memory
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPurgeable = true;
                        candidate_bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length,options);
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
            pDialog = new ProgressDialog(ViewPartyCandidatesActivity.this);
            pDialog.setMessage("Loading Candidate. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            if (connectionlost) {
                //result
                viewname.setText(Candidate_Name);
                viewParty.setText(Candidate_PartyName);
                viewVote.setText(Candidate_VoteNumber);
                viewposition.setText(Candidate_PositionName);
                viewcandidate_img.setImageBitmap(candidate_bitmap);
                getSupportActionBar().setTitle(Candidate_Name);
            } else {
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(ViewPartyCandidatesActivity.this);
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


    //*
    //------
    // delete candidate Sync
    class deleteCandidatetAsync extends  AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            statement = "DELETE FROM candidates WHERE student_ID='"+Candidate_Id+"'";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    st.executeUpdate(statement);


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
            pDialog = new ProgressDialog(ViewPartyCandidatesActivity.this);
            pDialog.setMessage("Deleting Candidate. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            if (connectionlost){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewPartyCandidatesActivity.this);
                alertDialog.setTitle("Candidate..");
                alertDialog.setMessage("deleted!");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        onBackPressed();
                    }
                });
                alertDialog.show();
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(ViewPartyCandidatesActivity.this);
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
