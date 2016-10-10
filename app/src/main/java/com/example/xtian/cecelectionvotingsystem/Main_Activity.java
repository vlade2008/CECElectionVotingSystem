package com.example.xtian.cecelectionvotingsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Xtian on 11/10/2015.
 */
public class Main_Activity extends Activity {


    private Button BLogin;


     TextView tv;
     EditText TFusername,TFpassword;
    // Progress Dialog
    private ProgressDialog pDialog;

    boolean authenticated = false;
    boolean connectionlost = true;
    String username = "";
    String password = "";
    String adminstrator;
    String statement;

    DBConnection dburl = new DBConnection();

    Connection conn;


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                Main_Activity.this);

        // Setting Dialog Title
        alertDialog2.setTitle("Confirm choose...");

        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want close this application?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        finish();
                        System.exit(0);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //connectionStatus();//check connection

        BLogin = (Button) this.findViewById(R.id.BLogin);
        TFpassword = (EditText)findViewById(R.id.password);
        TFusername = (EditText)findViewById(R.id.username);

        TFusername.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        TFpassword.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);


        BLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = TFusername.getText().toString() ;
                password = TFpassword.getText().toString();

                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                    //statement result
                    Toast.makeText(getApplicationContext(), "Please fill all input Text ", Toast.LENGTH_SHORT).show();
                    TFusername.requestFocus();
                    return;
                }else {
                    statement = "select * from administrator where username='" + username + "' and password='" + password + "'";
                    ConnectAsyncTask newTask = new ConnectAsyncTask();
                    newTask.execute();
                }
            }
        });
    }

    //username and password AsyncTask
    private class ConnectAsyncTask extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    ResultSetMetaData rsmd = rs.getMetaData();

                    while (rs.next()) {
                        authenticated = true;
                        adminstrator = rs.getString("username");
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
            pDialog = new ProgressDialog(Main_Activity.this);
            pDialog.setMessage("Loading username. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            //connection status
            if (connectionlost) {
                //result
                if(authenticated) {
                    //validate username and password
                    Intent intent = new Intent(Main_Activity.this, home.class);
                    startActivity(intent);
                }
                else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main_Activity.this);
                    alertDialog.setTitle("Login Failed");
                    alertDialog.setMessage("The username and password were not found. Please Try Again.");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            TFusername.setText(null);
                            TFpassword.setText(null);
                            TFusername.requestFocus();
                        }
                    });
                    alertDialog.show();

                }
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(Main_Activity.this);
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



    //check connection
    public void connectionStatus(){
            //check connection
            try {

                StrictMode.ThreadPolicy policy=
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(Config.url,Config.user,Config.pass);

                String result = "Database Connection success\n";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select * from administrator");
                while (rs.next()){
                    String id = rs.getString("id");
                }
            }
            catch (Exception e){
                e.printStackTrace();
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Connection..");
                alertDialog.setMessage("No connection in database");
                alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        System.exit(0);
                    }
                });
                alertDialog.show();
            }
        }
    }




