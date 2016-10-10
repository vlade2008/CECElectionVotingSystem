package com.example.xtian.cecelectionvotingsystem.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.xtian.cecelectionvotingsystem.PartyFragment.Party_view_candidates;
import com.example.xtian.cecelectionvotingsystem.R;
import com.example.xtian.cecelectionvotingsystem.adapter.PartyListAdapter;
import com.example.xtian.cecelectionvotingsystem.home;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Xtian on 11/18/2015.
 */
public class PartyFragment extends android.support.v4.app.Fragment {

    String partyId ;    //listview display party ID
    String partyName ;  //listview display party name
    String party_value; //value of create new party name
    String textPartyId; //update and delete party ID
    String textPartyName; //update and delete party Name

    ListView list;
    Button buttonCreate;
    TextView empty;

    ProgressBar pb;
    private ProgressDialog pDialog;

    //ArrayList<partyitem> partyArray;
    ArrayList<partyItem> partyArray;
    PartyListAdapter partyListAdapter;

    //statement and async for database
    boolean authenticated = false;
    String statement;
    DBConnection dburl = new DBConnection();
    Connection conn;
    boolean connectionlost = true;


    public PartyFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_party, container, false);

        final Animation anim_blink = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
        pb = (ProgressBar)rootView.findViewById(R.id.party_br);


        //list view
        //adapter=new VoterListAdapter(this.getActivity(),R.layout.voterlist,studentArray);
        list=(ListView)rootView.findViewById(R.id.listviewparty);
        empty = (TextView)rootView.findViewById(R.id.party_empty);
        list.setEmptyView(empty);
        //list.setAdapter((ListAdapter) adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //ID and name of party for delete and update
                textPartyId = ((TextView) view.findViewById(R.id.textPartyId)).getText().toString();
                textPartyName = ((TextView) view.findViewById(R.id.textPartyName)).getText().toString();

                //Show Dialog
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                        getActivity());
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
                                    Intent intent = new Intent(getActivity(),Party_view_candidates.class);
                                    intent.putExtra("intent_partyID",textPartyId);
                                    intent.putExtra("intent_partyName",textPartyName);
                                    intent.putExtra("IntentdisplyView", (int) 2);
                                    startActivity(intent);
                                    break;
                                case 1:
                                    dialog.cancel();
                                    updateDialog();
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


        //Create new Party
        buttonCreate = (Button)rootView.findViewById(R.id.buttonCreate);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(anim_blink);
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                        getActivity());

                // Setting Dialog Title
                alertDialog2.setTitle("Create New...");

                // Setting Dialog Message
                alertDialog2.setMessage("Create new Party");

                final EditText input = new EditText(getActivity());
                alertDialog2.setView(input);

                // Setting Positive "Yes" Btn
                alertDialog2.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                //new deleteStudentAsync().execute();
                                 party_value = input.getText().toString();
                                if(TextUtils.isEmpty(party_value)){
                                    //statement result
                                    Toast.makeText(getActivity(), "Please fill  input text ", Toast.LENGTH_SHORT).show();
                                    return;
                                }else {
                                    new createpartyAsync().execute();
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


        new partyAsync().execute();
        return rootView;
    }
    //update party dialog
    private void updateDialog(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Update Party");

        // Setting Dialog Message
        alertDialog2.setMessage("Update Party");

        final EditText input = new EditText(getActivity());
        input.setText(textPartyName);
        alertDialog2.setView(input);

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        //new deleteStudentAsync().execute();
                        party_value = input.getText().toString();

                        if(TextUtils.isEmpty(party_value)){
                            //statement result
                            Toast.makeText(getActivity(), "Update failed (Empty field)", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else {
                            if (party_value.equals(textPartyName)){
                                //do nothing
                            }else{
                                //do update
                                new updatepartyAsync().execute();
                            }
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
    //delete party dialog
    private void deleteDialog(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Confirm choose...");

        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want to delete " + textPartyName+"? \n All candidates will is also deleted!");



        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        new deletePartytAsync().execute();
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

    //Party sync all data in listview
    class partyAsync  extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            statement = "SELECT COUNT(*)AS COUNT FROM party";
            partyArray = new ArrayList<partyItem>();

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
                            statement = "SELECT * FROM party";
                            rs = st.executeQuery(statement);
                            while (rs.next()) {
                                partyId = rs.getString("party_ID");
                                partyName = rs.getString("party_Name");
                                partyArray.add(new partyItem(partyId,partyName));


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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pb.setVisibility(getView().INVISIBLE);


            //connection status
            if (connectionlost){
                //result
                if (getActivity()!=null){
                    partyListAdapter=new PartyListAdapter(getActivity(),R.layout.partlist_layout,partyArray);
                    list.setAdapter((ListAdapter)partyListAdapter);
                    list.setDivider(null);
                    list.setDividerHeight(0);
                    if (authenticated){
                        //true student
                        empty.setText("No party");
                    }else{
                        // false student
                    }
                }else{

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
    //create sync new party
    class createpartyAsync extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            statement = "SELECT COUNT(*)AS COUNT FROM party where party_Name='"+party_value+"'";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        if (rs.getInt("COUNT") == 0){
                            //if name 0 row = og ala pay name sa party
                            PreparedStatement stInsert = conn.prepareStatement("INSERT INTO party(party_Name) " +
                                    "VALUES(?)");
                            stInsert.setString(1, party_value);
                            stInsert.executeUpdate();
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
            pDialog.setMessage("Create New Party. Please wait..");
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
                    AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                    alertConnectionLost.setTitle("Success..");
                    alertConnectionLost.setMessage("Success full");
                    alertConnectionLost.setCancelable(false);
                    alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(getActivity(), home.class);
                            intent.putExtra("IntentdisplyView",(int)2);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                            new partyAsync().execute();
                        }
                    });
                    alertConnectionLost.show();

                }else{
                    // false student
                    AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                    alertConnectionLost.setTitle("Database..");
                    alertConnectionLost.setMessage("Name already exist!");
                    alertConnectionLost.setCancelable(false);
                    alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertConnectionLost.show();
                    new partyAsync().execute();
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
    //update sync party
    class updatepartyAsync extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            statement = "SELECT COUNT(*)AS COUNT FROM party where party_Name='"+party_value+"'";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        if (rs.getInt("COUNT") == 0){
                            //if name 0 row = og ala pay name sa party
                            PreparedStatement stInsert = conn.prepareStatement("UPDATE party SET party_Name=? WHERE party_ID=? ");
                            stInsert.setString(1, party_value);
                            stInsert.setInt(2, Integer.parseInt(textPartyId));
                            stInsert.executeUpdate();
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
            pDialog.setMessage("Updating Party. Please wait..");
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
                    AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                    alertConnectionLost.setTitle("Success..");
                    alertConnectionLost.setMessage("Update Successful");
                    alertConnectionLost.setCancelable(false);
                    alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(getActivity(), home.class);
                            intent.putExtra("IntentdisplyView",(int)2);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                            new partyAsync().execute();
                        }
                    });
                    alertConnectionLost.show();

                }else{
                    // false student
                    AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(getActivity());
                    alertConnectionLost.setTitle("Database..");
                    alertConnectionLost.setMessage("Name already exist!");
                    alertConnectionLost.setCancelable(false);
                    alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            new partyAsync().execute();
                        }
                    });
                    alertConnectionLost.show();

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
    //delete sync party
    class deletePartytAsync extends AsyncTask<Void,Integer,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            statement = "DELETE FROM party WHERE party_ID='"+textPartyId+"'";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    st.executeUpdate("DELETE FROM candidates WHERE party_ID='"+textPartyId+"'");
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
            pDialog.setMessage("Deleting Party. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            if (connectionlost){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Party..");
                alertDialog.setMessage("deleted!");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(getActivity(), home.class);
                        intent.putExtra("IntentdisplyView",(int)2);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                        //new partyAsync().execute();
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


}
