package com.example.xtian.cecelectionvotingsystem.PartyFragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xtian.cecelectionvotingsystem.DBConnection;
import com.example.xtian.cecelectionvotingsystem.R;
import com.example.xtian.cecelectionvotingsystem.getSetterItem.partyItem;
import com.example.xtian.cecelectionvotingsystem.getSetterItem.positionItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Xtian on 11/29/2015.
 */
public class NewPartyCandidatesActivity extends AppCompatActivity {

    //position array,geterandseter,spinner
    ArrayList<String> postion_spinner_string;
    ArrayList<positionItem> positionItems;
    ArrayAdapter<String> positionAdapter;
    Spinner positionSpinner;
    String position_value_spinner;

    //party array,geterandseter,spinner
    ArrayList<String> party_spinner_string;
    ArrayList<partyItem> partyItems;
    ArrayAdapter<String> partyAdapter;
    Spinner party_spinner;
    String party_value_spinner;

    String spinner_edit;
    TextView activityMainProfileName;

    //image
    private int PICK_IMAGE_REQUEST = 1;
    private ImageView candidate_img;
    private Bitmap bitmap;
    private Uri filePath;


    //intent displayView
    private static Integer IntentdisplyViews;
    String intent_studentID;
    String intent_candidateTitle;
    String intent_CandidateParty;
    String intent_partyID;


    //student name and ID
    String name;
    String studentId;

    //statement and async for database
    boolean authenticated = false;
    String statement;
    DBConnection dburl = new DBConnection();
    Connection conn;
    boolean connectionlost = true;
    private ProgressDialog pDialog;


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(NewPartyCandidatesActivity.this, Party_view_candidates.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("IntentdisplyView",(int) 2);
        intent.putExtra("intent_partyID",intent_partyID);
        intent.putExtra("intent_partyName", intent_CandidateParty);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_edit_candidates);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        final Animation anim_alpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        //get data from student
        Intent intent = getIntent();
        intent_studentID = intent.getStringExtra("intent_studentID");
        IntentdisplyViews = intent.getIntExtra("IntentdisplyView", 0);
        intent_candidateTitle = intent.getStringExtra("intent_candidateTitle");
        intent_CandidateParty = intent.getStringExtra("intent_partyName");
        intent_partyID = intent.getStringExtra("intent_partyID");

        getSupportActionBar().setTitle(intent_candidateTitle);

        activityMainProfileName = (TextView) findViewById(R.id.activityMainProfileName);//name of student


        //position Spinner
        positionSpinner = (Spinner) findViewById(R.id.position_spinner);
        positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Toast.makeText(adapterView.getContext(), "ID:" + positionItems.get(position).getId() + " NAME:" + positionItems.get(position).getName(), Toast.LENGTH_LONG).show();
                position_value_spinner = positionItems.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //party Spinner
        party_spinner = (Spinner) findViewById(R.id.party_spinner);
        party_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Toast.makeText(adapterView.getContext(), "ID:" + partyItems.get(position).getId() + " NAME:" + partyItems.get(position).getName(), Toast.LENGTH_LONG).show();
                party_value_spinner = partyItems.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // image button
        candidate_img = (ImageView) findViewById(R.id.candidate_img);
        candidate_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(anim_alpha);
                showFileChooser();
            }
        });

        canidateNameSyncs canidateNameSyncs = new canidateNameSyncs();
        canidateNameSyncs.execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_close:
                onBackPressed();
                return true;
            case R.id.action_check:
                if (filePath != null) {
                    if (position_value_spinner != "0") {
                        if (party_value_spinner != "0") {
                            //insert new canditate
                            checkCandidateParty checkCandidateParty = new checkCandidateParty();
                            checkCandidateParty.execute();
                        } else {
                            //empty image
                            Toast.makeText(this, "No party selected", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        //empty position
                        Toast.makeText(this, "No position selected", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //empty image
                    Toast.makeText(this, "No image Upload", Toast.LENGTH_LONG).show();
                    final Animation animation = new AlphaAnimation(1, 0);
                    animation.setDuration(1000);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setRepeatCount(Animation.INFINITE);
                    animation.setRepeatMode(Animation.REVERSE);
                    candidate_img.startAnimation(animation);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //*
    // --------
    // image uploader to database
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                candidate_img.setImageBitmap(bitmap);
                filePath = data.getData();
                String selectedImagePath = getPath(filePath);
                File image = new File(selectedImagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Spinner auto value
    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }


    //*
    // --------
    // sync all student , position , party
     class canidateNameSyncs extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            statement = "SELECT * FROM students where student_ID='"+intent_studentID+"'";
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        studentId = rs.getString("student_ID");
                        name = rs.getString("student_Name");
                        authenticated = true;
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
            pDialog = new ProgressDialog(NewPartyCandidatesActivity.this);
            pDialog.setMessage("Loading Candidate. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //pDialog.dismiss();
            //connection status
            if (connectionlost) {
                //result
                if (authenticated) {
                    //true student
                    activityMainProfileName.setText(name);
                    positionAsyncsss positionAsyncsss = new positionAsyncsss();
                    positionAsyncsss.execute();
                } else {
                    // false student
                }
            } else {
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(NewPartyCandidatesActivity.this);
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

     class positionAsyncsss extends AsyncTask<Void, Integer, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            positionItems = new ArrayList<positionItem>();
            postion_spinner_string = new ArrayList<String>();
            positionItem positionItem = new positionItem();

            statement = "SELECT * FROM position ORDER BY position_ID ASC";
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        positionItem = new positionItem();
                        positionItem.setId(rs.getString("position_ID"));
                        positionItem.setName(rs.getString("position_Name"));
                        positionItems.add(positionItem);
                        postion_spinner_string.add(rs.getString("position_Name"));
                    }
                    positionItem = new positionItem();
                    positionItem.setId("0");
                    positionItem.setName("=========Select Position=========");
                    positionItems.add(positionItem);

                    postion_spinner_string.add("=========Select Position=========");

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
//            pDialog = new ProgressDialog(NewCandidatesActivity.this);
//            pDialog.setMessage("Loading Student. Please wait..");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //pDialog.dismiss();
            //connection status

            if (connectionlost) {
                //result
                positionAdapter = new ArrayAdapter<String>(NewPartyCandidatesActivity.this, android.R.layout.simple_spinner_dropdown_item, postion_spinner_string) {

                    @Override
                    public int getCount() {
                        return super.getCount() - 1;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                        if (position == getCount()) {
                            ((TextView) v.findViewById(android.R.id.text1)).setText("");
                            ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                        }

                        return v;
                    }
                };
                positionSpinner.setAdapter(positionAdapter);
                positionSpinner.setSelection(positionAdapter.getCount());

                partyAsync partyAsync = new partyAsync();
                partyAsync.execute();

            } else {
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(NewPartyCandidatesActivity.this);
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

     class partyAsync extends AsyncTask<Void, Integer, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            partyItems = new ArrayList<partyItem>();
            party_spinner_string = new ArrayList<String>();
            partyItem partyItem = new partyItem();

            statement = "SELECT * FROM party where party_Name ='"+intent_CandidateParty+"' ";
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        partyItem = new partyItem();
                        partyItem.setId(rs.getString("party_ID"));
                        partyItem.setName(rs.getString("party_Name"));
                        partyItems.add(partyItem);
                        party_spinner_string.add(rs.getString("party_Name"));
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
//            pDialog = new ProgressDialog(NewCandidatesActivity.this);
//            pDialog.setMessage("Loading Student. Please wait..");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            //connection status

            if (connectionlost) {
                //result
                partyAdapter = new ArrayAdapter<String>(NewPartyCandidatesActivity.this, android.R.layout.simple_spinner_dropdown_item, party_spinner_string);
                party_spinner.setAdapter(partyAdapter);
                party_spinner.post(new Runnable() {
                    @Override
                    public void run() {
                        party_spinner.setSelection(getIndex(party_spinner, intent_CandidateParty));
                    }
                });

            } else {
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(NewPartyCandidatesActivity.this);
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
    //---------------------------------------------
    // sync data to database
    private class addCandidateAsync extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {


            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    //Statement st = conn.createStatement();
                    PreparedStatement stInsert = conn.prepareStatement("INSERT INTO candidates(student_ID,position_ID," +
                            "party_ID,candidate_img,vote_Number) VALUES(?,?,?,?,?)");

                    File image = new File(String.valueOf(getPath(filePath)));
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(image);
                        stInsert.setString(1, studentId);
                        stInsert.setString(2, position_value_spinner);
                        stInsert.setString(3, party_value_spinner);
                        stInsert.setBinaryStream(4, fis, (int) image.length());
                        stInsert.setString(5, "0");
                        stInsert.executeUpdate();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
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
            pDialog.dismiss();
            if (connectionlost) {
                //result
//                Intent intent = new Intent(NewPartyCandidatesActivity.this, Party_view_candidates.class);
//                intent.putExtra("IntentdisplyView",(int) 2);
//                intent.putExtra("intent_partyID",intent_partyID);
//                intent.putExtra("intent_partyName", intent_CandidateParty);
//                startActivityForResult(intent, 0);
//                finish();
//                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                onBackPressed();
            } else {
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(NewPartyCandidatesActivity.this);
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

    private class checkCandidateParty extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            statement = "SELECT COUNT(*)AS COUNT FROM candidate_details where position_ID='"+position_value_spinner+"' AND  party_ID='"+party_value_spinner+"'";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        if (rs.getInt("COUNT") == 0){
                            //if name 0 row = og ala pay name sa party
                            authenticated = true;
                        }else {
                            //name have 1 row = og naay name na
                            authenticated = false;
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
            pDialog = new ProgressDialog(NewPartyCandidatesActivity.this);
            pDialog.setMessage("Saving Candidate. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //connection status
            if (connectionlost) {
                //result
                if (authenticated) {
                    //true student
                    addCandidateAsync addCandidateAsync = new addCandidateAsync();
                    addCandidateAsync.execute();
                } else {
                    pDialog.dismiss();
                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                            NewPartyCandidatesActivity.this);
                    // Setting Dialog Title
                    alertDialog2.setTitle("Confirm choose...");

                    // Setting Dialog Message
                    alertDialog2.setMessage("Position is unavailable. Do you want to procceed? ");

                    // Setting Positive "Yes" Btn
                    alertDialog2.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to execute after dialog
                                    pDialog = new ProgressDialog(NewPartyCandidatesActivity.this);
                                    pDialog.setMessage("Loading Student. Please wait..");
                                    pDialog.setIndeterminate(false);
                                    pDialog.setCancelable(false);
                                    pDialog.show();
                                    addCandidateAsync addCandidateAsync = new addCandidateAsync();
                                    addCandidateAsync.execute();
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
            } else {
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(NewPartyCandidatesActivity.this);
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


