package com.example.xtian.cecelectionvotingsystem.CSVreader;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.xtian.cecelectionvotingsystem.DBConnection;
import com.example.xtian.cecelectionvotingsystem.R;
import com.example.xtian.cecelectionvotingsystem.home;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileChooser extends ListActivity {

    private ProgressDialog pDialog;


    private String pathfile;
    private File currentDir;
    private FileArrayAdapter adapter;


    boolean authenticated = false;
    boolean connectionlost = true;
    String statement;


    DBConnection dburl = new DBConnection();
    Connection conn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDir = new File("/sdcard/");
        fill(currentDir);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FileChooser.this, home.class);
        intent.putExtra("IntentdisplyView", (int) 1);
        startActivity(intent);
        finish();

    }



    private void fill(File f)
    {
        File[]dirs = f.listFiles();
        this.setTitle("Current Dir: "+f.getName());
        List<Option>dir = new ArrayList<Option>();
        List<Option>fls = new ArrayList<Option>();
        try{
            for(File ff: dirs)
            {
                if(ff.isDirectory())
                    dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
                else
                {
                    fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
                }
            }
        }catch(Exception e)
        {

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if(!f.getName().equalsIgnoreCase("sdcard"))
            dir.add(0,new Option("..","Parent Directory",f.getParent()));

        adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,dir);
        this.setListAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Option o = adapter.getItem(position);
        if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
            currentDir = new File(o.getPath());
            fill(currentDir);
        }
        else
        {
            onFileClick(o);

        }
    }
    private void onFileClick(final Option o)
    {
        final String path = o.getPath();

        pathfile = path;
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                FileChooser.this);

        // Setting Dialog Title
        alertDialog2.setTitle("Confirm choose...");

        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want choose this file?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog

                        //Toast.makeText(getApplicationContext(),
                        //"File Clicked: "+path, Toast.LENGTH_SHORT)
                        //.show();
                        String substring = path.length() > 2 ? path.substring(path.length() - 3) : path;
                        if (substring.equals("csv")) {
                            //true csv file
                            File file = new File(path);
                            if (file.exists()) {
                                authenticated = false;
                                statement = "SELECT * from administrator where username='user'";
                                CsvAsyncTask csvAsyncTask = new CsvAsyncTask();
                                csvAsyncTask.execute();

                            } else {
                                Toast.makeText(getApplication(), "File not exists", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            //false csv file
                            Toast.makeText(getApplicationContext(),"Not csv File "+path ,Toast.LENGTH_SHORT).show();
                        }


                    }
                });
        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        Toast.makeText(getApplicationContext(),
                                "You clicked on NO", Toast.LENGTH_SHORT)
                                .show();
                        dialog.cancel();
                    }
                });

        // Showing Alert Dialog
        alertDialog2.show();

        //Toast.makeText(this, "File Clicked: "+o.getPath(), Toast.LENGTH_LONG).show();



    }

    //csv file asynctask
    private class CsvAsyncTask extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            try {
                BufferedReader reader = new BufferedReader(new FileReader(pathfile));
                String csvLine;

                try {
                    while ((csvLine = reader.readLine()) != null) {
                        List<String> items = Arrays.asList(csvLine.split(","));

                         String id = items.get(0).substring(1, items.get(0).length() - 1);//csv sbstr the = ""
                         String firstname = items.get(2).substring(1, items.get(2).length() - 1);
                         String lastname = items.get(1).substring(1, items.get(1).length() - 1)+","+firstname;
                         String course = items.get(3).substring(1, items.get(3).length() - 1);
                         String password = items.get(4).substring(1, items.get(4).length() - 1);

                        try {
                            Class.forName("com.mysql.jdbc.Driver").newInstance();

                            try {
                                conn = DriverManager.getConnection(dburl.getUrl());
                                Statement st = conn.createStatement();
                                ResultSet rs = st.executeQuery(statement);

                                ResultSetMetaData rsmd = rs.getMetaData();

                                while (rs.next()) {
                                        authenticated = true;
                                }

                                if (!authenticated){
                                    PreparedStatement stInsert = conn.prepareStatement("INSERT INTO students(student_ID,student_Name,student_Course,student_Password,flag) " +
                                            "VALUES(?,?,?,?,?)");
                                    stInsert.setString(1, id);
                                    stInsert.setString(2, lastname);
                                    stInsert.setString(3, course);
                                    stInsert.setString(4, password);
                                    stInsert.setInt(5, 0);
                                    stInsert.executeUpdate();
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

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Error csv file" ,Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FileChooser.this);
            pDialog.setMessage("Loading CSV file. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            //connection status
            if (connectionlost){
                //result
                    if (authenticated){
                        //if account and associated password do not exist, notify user
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FileChooser.this);
                        alertDialog.setTitle("Failed..");
                        alertDialog.setMessage("Error csv file. Please try again.");

                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }else {
                        //notify user of incoming email with account details
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FileChooser.this);
                        alertDialog.setTitle("Success");
                        alertDialog.setMessage("Upload success.");

                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                //go back to StartActivity
                                Intent intent = new Intent(FileChooser.this, home.class);
                                intent.putExtra("IntentdisplyView",(int)1);
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                finish();

                            }
                        });
                        alertDialog.show();
                    }
            }else {
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(FileChooser.this);
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