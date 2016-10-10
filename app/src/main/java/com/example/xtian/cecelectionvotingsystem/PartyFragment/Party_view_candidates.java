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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xtian.cecelectionvotingsystem.DBConnection;
import com.example.xtian.cecelectionvotingsystem.R;
import com.example.xtian.cecelectionvotingsystem.adapter.PartyCandidateListAdapater;
import com.example.xtian.cecelectionvotingsystem.getSetterItem.candidateItem;
import com.example.xtian.cecelectionvotingsystem.home;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Xtian on 12/18/2015.
 */
public class Party_view_candidates extends AppCompatActivity {

    //intent displayView
    private static Integer IntentdisplyViews;
    String intent_partyID;
    String intent_partytName;

    ListView list;
    Button buttonCreate;
    TextView empty,viewparty;

    ProgressBar pb;
    private ProgressDialog pDialog;

    String student_ID ;    //listview display candidate ID
    String student_Name ;  //listview display candidate name
    String position_Name; //listview display candidate position_Name
    String party_Name;  //listview display candidate party_Name
    String candidate_vote;  //listview display candidate party_Name

    String Candidate_value;     //value of create new candidate name
    String textCandidateId;     //update and delete candidate ID
    String textCandidateName;  //update and delete candidate Name
    String textCandidatePosition;
    String textCandidateParty;

    //ArrayList<partyitem> partyArray;
    ArrayList<candidateItem> candidateArray;
    PartyCandidateListAdapater partyCandidateListAdapater;
    //CandidateListAdapater partyCandidateListAdapater;

    //statement and async for database
    boolean authenticated = false;
    boolean authenticated_2 = false;
    String statement;
    DBConnection dburl = new DBConnection();
    Connection conn;
    boolean connectionlost = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_party_candidates);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        //get data from student
        Intent intent = getIntent();
        intent_partyID = intent.getStringExtra("intent_partyID");
        intent_partytName = intent.getStringExtra("intent_partyName");
        IntentdisplyViews = intent.getIntExtra("IntentdisplyView", 0);
        getSupportActionBar().setTitle(intent_partytName);

        pb = (ProgressBar)findViewById(R.id.candidate_br);
        final Animation anim_alpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        final Animation anim_blink = AnimationUtils.loadAnimation(this, R.anim.blink);

        //viewparty = (TextView)findViewById(R.id.viewparty);
        //viewparty.setText(intent_partytName);
        list=(ListView)findViewById(R.id.listviewcandidate);
        empty = (TextView)findViewById(R.id.candidate_empty);
        list.setEmptyView(empty);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //animation
                view.startAnimation(anim_alpha);
                //ID and name of Candidate for delete and update
                textCandidateId = ((TextView) view.findViewById(R.id.textviewCandidate_ID)).getText().toString();
                textCandidateName = ((TextView) view.findViewById(R.id.textviewCandidate_Name)).getText().toString();
                textCandidatePosition = ((TextView) view.findViewById(R.id.textviewCandidate_Position)).getText().toString();
                textCandidateParty = ((TextView) view.findViewById(R.id.textviewCandidate_Party)).getText().toString();

                //Show Dialog
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                        Party_view_candidates.this);
                // Setting Dialog Title
                alertDialog2.setTitle("Choose an Option");

                //list of item of select button
                final CharSequence[] items={"View","Edit","Delete"};

                //select edit and delete list
                alertDialog2.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //Toast.makeText(getActivity(), "" + i, Toast.LENGTH_SHORT).show();
                        switch (i){
                            case 0:
                                Intent intent = new Intent(Party_view_candidates.this,ViewPartyCandidatesActivity.class);
                                intent.putExtra("intent_studentID",textCandidateId);
                                intent.putExtra("IntentdisplyView", (int) 2);
                                intent.putExtra("intent_partyID",intent_partyID);
                                intent.putExtra("intent_partyName",intent_partytName);
                                startActivity(intent);
                                finish();

                                break;
                            case 1:
                                intent = new Intent(Party_view_candidates.this,EditPartyCandidatesAcitivity.class);
                                intent.putExtra("intent_studentID",textCandidateId);
                                intent.putExtra("intent_positionName",textCandidateParty);
                                intent.putExtra("intent_partyName",intent_partytName);
                                intent.putExtra("intent_partyID",intent_partyID);
                                intent.putExtra("IntentdisplyView", (int) 2);
                                startActivity(intent);
                                break;
                            case 2:
                                deleteDialog();
                                break;
                        }
                    }
                });
                // Setting Negative "NO" Btn
                alertDialog2.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                dialog.cancel();
                            }
                        });

                // Showing Alert Dialog
                alertDialog2.show();
            }
        });


        //buttons
        buttonCreate =(Button)findViewById(R.id.buttonCreate_Canidate);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(anim_blink);
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                        Party_view_candidates.this);

                // Setting Dialog Title
                alertDialog2.setTitle("Create New...");

                // Setting Dialog Message
                alertDialog2.setMessage("Input Student ID");

                final EditText input = new EditText(Party_view_candidates.this);
                alertDialog2.setView(input);

                // Setting Positive "Yes" Btn
                alertDialog2.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                //new deleteStudentAsync().execute();
                                Candidate_value = input.getText().toString();
                                if(TextUtils.isEmpty(Candidate_value)){
                                    //statement result
                                    Toast.makeText(Party_view_candidates.this, "Please fill  input text ", Toast.LENGTH_SHORT).show();
                                    return;
                                }else {
                                    createNewCandidate createNewCandidate = new createNewCandidate();
                                    createNewCandidate.execute();
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
        });

        CandidateAsync candidateAsync = new CandidateAsync();
        candidateAsync.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Party_view_candidates.this, home.class);
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

    //delete party dialog
    private void deleteDialog(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                Party_view_candidates.this);

        // Setting Dialog Title
        alertDialog2.setTitle("Confirm choose...");

        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want to delete " + textCandidateName + "?");



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

    //==============================  Async class =========================================

    // --- disply all candidate sync
    // ========================================
    class  CandidateAsync extends AsyncTask<Void,Integer,Void> {

        @Override
        protected Void doInBackground(Void... params) {

            statement = "SELECT COUNT(*)AS COUNT FROM candidate_details where party_ID= '"+ intent_partyID +"'";
            candidateArray = new ArrayList<candidateItem>();

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
                            statement = "SELECT * FROM candidate_details where party_ID= '"+ intent_partyID +"'  ORDER BY position_ID ASC";
                            rs = st.executeQuery(statement);
                            while (rs.next()) {
                                // partyId = rs.getString("party_ID");
                                //partyName = rs.getString("party_Name");
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

                                candidateArray.add(new candidateItem(student_ID,student_Name,position_Name,party_Name,candidate_vote,imageIcon));


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
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pb.setVisibility(View.INVISIBLE);
            partyCandidateListAdapater = new PartyCandidateListAdapater(Party_view_candidates.this,R.layout.party_candidates_list,candidateArray);
            list.setAdapter((ListAdapter) partyCandidateListAdapater);
            list.setDivider(null);
            list.setDividerHeight(0);

            //connection status
            if (connectionlost){
                //result
                if (authenticated){
                    //true student
                    empty.setText("No Candidate");
                }else{
                    // false student
                }
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Party_view_candidates.this);
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

    class  createNewCandidate extends  AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            statement = "SELECT COUNT(*)AS COUNT FROM students where student_ID='"+Candidate_value+"'";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        if (rs.getInt("COUNT") == 1){
                            //if name 1 row = og naay name sa students table
                            authenticated = true;

                            statement = "SELECT COUNT(*)AS COUNT FROM candidates where student_ID='"+Candidate_value+"'";
                            rs = st.executeQuery(statement);
                            while (rs.next()){
                                if (rs.getInt("COUNT")==0){
                                    //COUNT = 0 if ala
                                    authenticated_2 = true;
                                }else{
                                    //COUNT = 1 if naa
                                    authenticated_2 = false;
                                }
                            }
                        }else {
                            //if name 0 row = og alay name sa students table
                            authenticated = false;
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
            pDialog = new ProgressDialog(Party_view_candidates.this);
            pDialog.setMessage("Checking student ID. Please wait..");
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
                    if (authenticated_2){
                        Intent intent = new Intent(Party_view_candidates.this,NewPartyCandidatesActivity.class);
                        intent.putExtra("intent_studentID",Candidate_value);
                        intent.putExtra("intent_candidateTitle","New Candidate");
                        intent.putExtra("intent_partyID",intent_partyID);
                        intent.putExtra("intent_partyName",intent_partytName);
                        intent.putExtra("IntentdisplyView", (int) 2);
                        startActivity(intent);


                    }else{
                        // false student
                        AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Party_view_candidates.this);
                        alertConnectionLost.setTitle("Student..");
                        alertConnectionLost.setMessage("Student already exist in candidates!");

                        alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                //new CandidateAsync().execute();
                            }
                        });
                        alertConnectionLost.show();

                    }

                }else{
                    // false student
                    AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Party_view_candidates.this);
                    alertConnectionLost.setTitle("Student..");
                    alertConnectionLost.setMessage("Student not exist!");
                    alertConnectionLost.setCancelable(false);
                    alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            CandidateAsync candidateAsync = new CandidateAsync();
                            candidateAsync.execute();
                        }
                    });
                    alertConnectionLost.show();

                }
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Party_view_candidates.this);
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

    //-- delete Candidate Sync
    class deleteCandidatetAsync extends  AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            statement = "DELETE FROM candidates WHERE student_ID='"+textCandidateId+"'";

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
            pDialog = new ProgressDialog(Party_view_candidates.this);
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Party_view_candidates.this);
                alertDialog.setTitle("Candidate..");
                alertDialog.setMessage("deleted!");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
//                        Intent intent = new Intent(Party_view_candidates.this, Party_view_candidates.class);
//                        intent.putExtra("IntentdisplyView",(int)2);
//                        intent.putExtra("intent_partyID",intent_partyID);
//                        intent.putExtra("intent_partyName",intent_partytName);
//                        startActivity(intent);
//                        finish();
//                        Party_view_candidates.this.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                        new CandidateAsync().execute();
                    }
                });
                alertDialog.show();
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Party_view_candidates.this);
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
