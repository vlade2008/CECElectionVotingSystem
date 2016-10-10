package com.example.xtian.cecelectionvotingsystem.Edit_View_Candidates;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.xtian.cecelectionvotingsystem.R;
import com.example.xtian.cecelectionvotingsystem.getSetterItem.positionItem;
import com.example.xtian.cecelectionvotingsystem.home;

import java.util.ArrayList;

/**
 * Created by Xtian on 11/29/2015.
 */
public class EditCandidatesActivityBackup extends AppCompatActivity {

    ArrayList<String> user_spinner_string;
    ArrayList<positionItem> users;
    String spinner_edit;

    //intent displayView
    private static Integer IntentdisplyViews;
    String intent_studentID;


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditCandidatesActivityBackup.this, home.class);
        intent.putExtra("IntentdisplyView",IntentdisplyViews);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_edit_candidates);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get data from student
        Intent intent = getIntent();
        intent_studentID = intent.getStringExtra("intent_studentID");
        IntentdisplyViews = intent.getIntExtra("IntentdisplyViews",0);

        getSupportActionBar().setTitle("Edit Candidates");




        users = new ArrayList<positionItem>();
        user_spinner_string = new ArrayList<String>();

        positionItem user = new positionItem();
        user.setId("1");
        user.setName("rohit");

        users.add(user);
        user_spinner_string.add("rohit");

        user = new positionItem();
        user.setId("2");
        user.setName("Vaibhav");
        users.add(user);

        user_spinner_string.add("Vaibhav");

        user = new positionItem();
        user.setId("3");
        user.setName("vivek");
        users.add(user);

        user_spinner_string.add("vivek");

        user = new positionItem();
        user.setId("4");
        user.setName("Shubham");
        users.add(user);

        user_spinner_string.add("Shubham");

        final Spinner mySpinner = (Spinner) findViewById(R.id.position_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditCandidatesActivityBackup.this,android.R.layout.simple_spinner_dropdown_item,user_spinner_string);
        mySpinner.setAdapter(adapter);

        mySpinner.post(new Runnable() {
            @Override
            public void run() {
                spinner_edit = "Vaibhav";
                mySpinner.setSelection(getIndex(mySpinner,spinner_edit));
            }
        });

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(adapterView.getContext(), "ID:" + users.get(position).getId() +" NAME:" + users.get(position).getName(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
}

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
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
                Toast.makeText(this, "check", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
