package com.example.john.divesafe;

/*
*
*	NOTICE - THIS CODE IS NOT TO BE USED FOR REAL DIVING. IT IS A SCHOOL PROJECT ONLY.
*	DO NOT USE THIS APP FOR REAL DIVING. YOUR SAFETY IS NOT GUARANTEED.
*	THIS IS NOT SAFETY CRITICAL SOFTWARE DEVELOPED BY PROFESSIONALS.
*	IT IS AN APP CREATED BY COLLEGE STUDENTS FOR CLASSWORK ONLY.
*
*/

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;


public class ShowSavedDive extends Activity {
    public FullDive currentDive = new FullDive ();
    //    private Button buttonDone;
    private TextView dive;
    int diveID;

    private FullDiveOperations fullDiveDBoperation;
    private DiveOperations diveDBoperation;

    public ShowSavedDive() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_dive);

        fullDiveDBoperation = new FullDiveOperations(this);
        fullDiveDBoperation.open();
        diveDBoperation = new DiveOperations(this);
        diveDBoperation.open();

        getParameters();

        final Button buttonDelete = (Button) findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                fullDiveDBoperation.deleteDive(currentDive);
                finish();
            }
        });

        final Button buttonHome = (Button) findViewById(R.id.buttonHome);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ShowSavedDive.this, SafeDivePlanner.class);
                startActivity(intent);
            }
        });

        final Button buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                finish();
            }
        });

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_show_saved_dive, container, false);
        dive = (TextView) view.findViewById(R.id.diveInfo);

        dive.setText (currentDive.toString());

        return view;
    }

    public void getParameters() {

        if (ShowSavedDive.this.getIntent().getExtras() != null)
        {
            Bundle bundle = this.getIntent().getExtras();

            TextView diveInfo = (TextView) findViewById(R.id.diveInfo);
            diveID = bundle.getInt("diveID");
            Log.d("DSA ", "dive ID is: " + diveID);

            List dives = diveDBoperation.getAllDives();
            List values = fullDiveDBoperation.merge(dives);

            for (int i = 0; i < values.size(); i++) {
                FullDive d = (FullDive) values.get(i);
                if (d.getId() == diveID) {
                    currentDive = d;
                    break;
                }
            }

            diveInfo.setText(currentDive.toString());
            TextView title = (TextView) findViewById(R.id.diveTitle);
            title.setText (currentDive.getName()); //display name as title
        }

    }

}
