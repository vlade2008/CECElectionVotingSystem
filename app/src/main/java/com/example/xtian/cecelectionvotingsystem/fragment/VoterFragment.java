package com.example.xtian.cecelectionvotingsystem.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xtian.cecelectionvotingsystem.CSVreader.FileChooser;
import com.example.xtian.cecelectionvotingsystem.DBConnection;
import com.example.xtian.cecelectionvotingsystem.R;
import com.example.xtian.cecelectionvotingsystem.adapter.VoterListAdapter;
import com.example.xtian.cecelectionvotingsystem.getSetterItem.item;
import com.example.xtian.cecelectionvotingsystem.home;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Xtian on 11/18/2015.
 */
public class VoterFragment extends android.support.v4.app.Fragment {



    String name ;
    String studentId ;
    String course ;
    String studentpassword ;


    ListView list;
    Button buttonUpload,buttonDeleteStudent;
    TextView empty;


    ProgressBar pb;
    private ProgressDialog pDialog;


    ArrayList<item> studentArray;
    VoterListAdapter adapter;

    //statement and async for database
    boolean authenticated = false;
    String statement;
    DBConnection dburl = new DBConnection();
    Connection conn;
    boolean connectionlost = true;



    public VoterFragment(){}



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_voter, container, false);

        final Animation anim_blink = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
        pb = (ProgressBar)rootView.findViewById(R.id.br);

        //list view
        //adapter=new VoterListAdapter(this.getActivity(),R.layout.voterlist,studentArray);
        list=(ListView)rootView.findViewById(R.id.listView1);
         empty = (TextView)rootView.findViewById(R.id.empty);
        list.setEmptyView(empty);
        //list.setAdapter((ListAdapter) adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String titre = ((TextView) view.findViewById(R.id.textName)).getText()
                        .toString();
                Toast.makeText(getActivity(), "" + titre, Toast.LENGTH_SHORT).show();
            }
        });

        //upload csv file button
        buttonUpload = (Button)rootView.findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(anim_blink);
                new csvFileCheckAsync().execute();

            }
        });
        //delete csv file buton
        buttonDeleteStudent = (Button)rootView.findViewById(R.id.buttonDeleteStudent);
        buttonDeleteStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(anim_blink);
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                        getActivity());

                // Setting Dialog Title
                alertDialog2.setTitle("Confirm choose...");

                // Setting Dialog Message
                alertDialog2.setMessage("Are you sure you want to delete all student?");



                // Setting Positive "Yes" Btn
                alertDialog2.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                new deleteStudentAsync().execute();
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


        //async student
        new studentAsync().execute();
        return rootView;
    }
    private void deleteDialog(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Confirm choose...");

        // Setting Dialog Message
        alertDialog2.setMessage("Students are already upload. Do you want to upload new csv file ?");



        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        new deleteStudentAsync().execute();
                        Intent i = new Intent(getActivity(), FileChooser.class);
                        startActivity(i);
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

    //student data async in listview
    private class studentAsync extends AsyncTask<Void,Integer,Void> {

        @Override
        protected Void doInBackground(Void... params) {


            statement = "SELECT COUNT(*)AS COUNT FROM students";
            studentArray = new ArrayList<item>();

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
                            statement = "SELECT * FROM students";
                            rs = st.executeQuery(statement);
                            while (rs.next()) {
                                studentId = rs.getString("student_ID");
                                name = rs.getString("student_Name");
                                course = rs.getString("student_Course");
                                studentpassword = rs.getString("student_Password");
                                studentArray.add(new item(studentId, name, course, studentpassword));

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
            pb.setVisibility(getView().VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            pb.setVisibility(getView().INVISIBLE);
            //connection status
            if (connectionlost){
                //result
                if (getActivity()!=null) {
                    adapter=new VoterListAdapter(getActivity(),R.layout.voterlist,studentArray);
                    list.setAdapter((ListAdapter) adapter);
                    if (authenticated) {
                        //true student
                        empty.setText("No student Upload");
                    } else {
                        // false student
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

    //delete data async
    private class deleteStudentAsync extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            statement = "TRUNCATE TABLE students";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    //Statement  = st.executeUpdate(statement);
                    st.executeUpdate("TRUNCATE TABLE candidates");
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
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Delete student . Please wait..");
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Student..");
                alertDialog.setMessage("All student has been deleted!");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(getActivity(), home.class);
                        intent.putExtra("IntentdisplyView",(int)1);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                        new studentAsync().execute();

                    }
                });
                alertDialog.show();
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

    //check student csv
    private class csvFileCheckAsync extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            statement = "SELECT COUNT(*)AS COUNT FROM students ";

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
            pDialog.setMessage("Loading File. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //connection status
            pDialog.dismiss();
            if (connectionlost){
                //result
                if (authenticated){
                    //true student
                    Intent i = new Intent(getActivity(), FileChooser.class);
                    startActivity(i);

                }else{
                    // false student
                    deleteDialog();
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
}
