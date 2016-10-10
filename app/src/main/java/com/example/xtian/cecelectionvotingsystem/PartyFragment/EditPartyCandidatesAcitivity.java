package com.example.xtian.cecelectionvotingsystem.PartyFragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.xtian.cecelectionvotingsystem.DBConnection;
import com.example.xtian.cecelectionvotingsystem.R;
import com.example.xtian.cecelectionvotingsystem.getSetterItem.partyItem;
import com.example.xtian.cecelectionvotingsystem.getSetterItem.positionItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Xtian on 12/6/2015.
 */
public class EditPartyCandidatesAcitivity extends AppCompatActivity {

    //position array,geterandseter,spinner
    ArrayList<String> postion_spinner_string;
    ArrayList<positionItem> positionItems;
    ArrayAdapter<String> positionAdapter;
    Spinner positionSpinner;
    String position_value_spinner;
    String position_name_spinner;

    //party array,geterandseter,spinner
    ArrayList<String> party_spinner_string;
    ArrayList<partyItem> partyItems;
    ArrayAdapter<String> partyAdapter;
    Spinner party_spinner;
    String party_value_spinner;
    String party_name_spinner;

    //student name and ID
    String name;
    String studentId;
    Bitmap candidate_bitmap;

    //image
    private int PICK_IMAGE_REQUEST = 1;
    private ImageView candidate_img;
    private Bitmap bitmap;
    private Uri filePath;

    //textView
    TextView activityMainProfileName;


    //intent displayView
    private static Integer IntentdisplyViews;
    String intent_studentID;
    String intent_candidateTitle;
    String intent_CandidatePosition;
    String intent_CandidateParty;
    String intent_partyID;

    //statement and async for database
    boolean authenticated = false;
    String statement;
    String statement_update;
    DBConnection dburl = new DBConnection();
    Connection conn;
    boolean connectionlost = true;
    private ProgressDialog pDialog;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditPartyCandidatesAcitivity.this, Party_view_candidates.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("IntentdisplyView", IntentdisplyViews);
        //party view id and party name
        intent.putExtra("intent_partyID", intent_partyID);
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
        intent_CandidateParty = intent.getStringExtra("intent_partyName");
        intent_CandidatePosition = intent.getStringExtra("intent_positionName");
        IntentdisplyViews = intent.getIntExtra("IntentdisplyView", 0);
        intent_candidateTitle = intent.getStringExtra("intent_candidateTitle");

        //party view id and party name
        intent_partyID = intent.getStringExtra("intent_partyID");


        activityMainProfileName = (TextView) findViewById(R.id.activityMainProfileName);//name of student

        //position Spinner
        positionSpinner = (Spinner) findViewById(R.id.position_spinner);
        positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Toast.makeText(adapterView.getContext(), "ID:" + positionItems.get(position).getId() + " NAME:" + positionItems.get(position).getName(), Toast.LENGTH_LONG).show();
                position_value_spinner = positionItems.get(position).getId();
                position_name_spinner = positionItems.get(position).getName();
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
                party_name_spinner = partyItems.get(position).getName();
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

        canidateNameSync canidateNameSync = new canidateNameSync();
        canidateNameSync.execute();

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
                    if (party_name_spinner.equals(intent_CandidateParty) &&  position_name_spinner.equals(intent_CandidatePosition)){
                        //Toast.makeText(getApplicationContext(), "image update only:", Toast.LENGTH_LONG).show();
                        statement_update ="UPDATE candidates SET candidate_img=? WHERE student_ID=?";
                        pDialog = new ProgressDialog(EditPartyCandidatesAcitivity.this);
                        pDialog.setMessage("Updating Candidate. Please wait..");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(false);
                        pDialog.show();
                        updateCandidate updateCandidate = new updateCandidate();
                        updateCandidate.execute();
                    }else{
                        //Toast.makeText(getApplicationContext(), "image update and candidate and position:", Toast.LENGTH_LONG).show();
                        statement_update ="UPDATE candidates SET position_ID=?,party_ID=?,candidate_img=? WHERE student_ID=?";
                        checkCandidateParty checkCandidateParty = new checkCandidateParty();
                        checkCandidateParty.execute();
                    }
                } else {
                    if (party_name_spinner.equals(intent_CandidateParty) && position_name_spinner.equals(intent_CandidatePosition)){
                        onBackPressed();
                    }else{
                        statement_update ="UPDATE candidates SET position_ID=?,party_ID=? WHERE student_ID=?";
                        checkCandidateParty checkCandidateParty = new checkCandidateParty();
                        checkCandidateParty.execute();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
    class canidateNameSync extends AsyncTask<Void, Integer, Void> {

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
                        studentId = rs.getString("student_ID");
                        name = rs.getString("student_Name");
                        Blob b = (Blob) rs.getBlob("candidate_img");
                        byte[] temp = b.getBytes(1, (int) b.length());
                        //candidate_bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);

                        //for decodebytearray memory
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPurgeable = true;
                        candidate_bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length,options);
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
            pDialog = new ProgressDialog(EditPartyCandidatesAcitivity.this);
            pDialog.setMessage("Loading Candidate. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (connectionlost) {
                //result
                if (authenticated) {
                    //true student
                    activityMainProfileName.setText(name);
                    candidate_img.setImageBitmap(candidate_bitmap);
                    getSupportActionBar().setTitle(name);
                    positionAsync positionAsync = new positionAsync();
                    positionAsync.execute();
                } else {
                    // false student
                }
            } else {
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(EditPartyCandidatesAcitivity.this);
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

    class positionAsync extends AsyncTask<Void, Integer, Void> {

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
            if (connectionlost) {
                //result
                positionAdapter = new ArrayAdapter<String>(EditPartyCandidatesAcitivity.this, android.R.layout.simple_spinner_dropdown_item, postion_spinner_string);
                positionSpinner.setAdapter(positionAdapter);
                positionSpinner.post(new Runnable() {
                    @Override
                    public void run() {
                        positionSpinner.setSelection(getIndex(positionSpinner, intent_CandidatePosition));
                    }
                });

                partyAsync partyAsync = new partyAsync();
                partyAsync.execute();

            } else {
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(EditPartyCandidatesAcitivity.this);
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

            statement = "SELECT * FROM party";
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
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            //connection status

            if (connectionlost) {
                //result
                partyAdapter = new ArrayAdapter<String>(EditPartyCandidatesAcitivity.this, android.R.layout.simple_spinner_dropdown_item, party_spinner_string);
                party_spinner.setAdapter(partyAdapter);
                party_spinner.post(new Runnable() {
                    @Override
                    public void run() {
                        party_spinner.setSelection(getIndex(party_spinner, intent_CandidateParty));
                    }
                });
            } else {
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(EditPartyCandidatesAcitivity.this);
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

    class checkCandidateParty extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            statement = "SELECT COUNT(*)AS COUNT FROM candidate_details where position_ID='" + position_value_spinner + "' AND  party_ID='" + party_value_spinner + "'";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        if (rs.getInt("COUNT") == 0) {
                            //if name 0 row = og ala pay name sa party
                            authenticated = true;
                        } else {
                            if (rs.getInt("COUNT") <= 1) {
                                authenticated = false;
                            } else {
                                authenticated = false;
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
            pDialog = new ProgressDialog(EditPartyCandidatesAcitivity.this);
            pDialog.setMessage("Updating Candidate. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //connection status
            //pDialog.dismiss();
            if (connectionlost) {
                //result
                if (authenticated) {
                    //true student
                    updateCandidate updateCandidate = new updateCandidate();
                    updateCandidate.execute();
                    //Toast.makeText(getApplicationContext(), "UPDATE", Toast.LENGTH_LONG).show();
                } else {
                    pDialog.dismiss();
                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                            EditPartyCandidatesAcitivity.this);
                    // Setting Dialog Title
                    alertDialog2.setTitle("Confirm choose...");

                    // Setting Dialog Message
                    alertDialog2.setMessage("Position is unavailable. Do you want to procceed? ");

                    // Setting Positive "Yes" Btn
                    alertDialog2.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to execute after dialog
                                    pDialog = new ProgressDialog(EditPartyCandidatesAcitivity.this);
                                    pDialog.setMessage("Loading Student. Please wait..");
                                    pDialog.setIndeterminate(false);
                                    pDialog.setCancelable(false);
                                    pDialog.show();
                                    updateCandidate updateCandidate = new updateCandidate();
                                    updateCandidate.execute();
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
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(EditPartyCandidatesAcitivity.this);
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

    class updateCandidate extends  AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    //Statement st = conn.createStatement();

                    //position and party Update
                    if (statement_update.equals("UPDATE candidates SET position_ID=?,party_ID=? WHERE student_ID=?")){
                        PreparedStatement stInsert = conn.prepareStatement(statement_update);
                        stInsert.setString(1,position_value_spinner);
                        stInsert.setString(2, party_value_spinner);
                        stInsert.setString(3,intent_studentID);
                        stInsert.executeUpdate();
                        authenticated = true;
                    }
                    //image upload only
                    if (statement_update.equals("UPDATE candidates SET candidate_img=? WHERE student_ID=?")){
                        PreparedStatement stInsert = conn.prepareStatement(statement_update);
                        File image = new File(String.valueOf(getPath(filePath)));
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(image);
                            stInsert.setBinaryStream(1, fis, (int) image.length());
                            stInsert.setString(2,intent_studentID);
                            stInsert.executeUpdate();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    //image, position , candidate update
                    if (statement_update.equals("UPDATE candidates SET position_ID=?,party_ID=?,candidate_img=? WHERE student_ID=?")){
                        PreparedStatement stInsert = conn.prepareStatement(statement_update);
                        File image = new File(String.valueOf(getPath(filePath)));
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(image);
                            stInsert.setString(1,position_value_spinner);
                            stInsert.setString(2,party_value_spinner);
                            stInsert.setBinaryStream(3, fis, (int) image.length());
                            stInsert.setString(4,intent_studentID);
                            stInsert.executeUpdate();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
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
            pDialog.dismiss();
            if (connectionlost) {
                //result
                onBackPressed();
            } else {
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(EditPartyCandidatesAcitivity.this);
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

