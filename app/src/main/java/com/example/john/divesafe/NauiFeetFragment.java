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
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;


/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NauiFeetFragment.OnDoneButtonListener} interface
 * to handle interaction events.
 * Use the {@link NauiFeetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NauiFeetFragment extends Fragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public ArrayList<Dive> currentDive = new ArrayList<Dive>();
    ArrayList<DataPoint> dp = new ArrayList<DataPoint>();
    public LineGraphSeries series = new LineGraphSeries<>();
    private EditText depthNum, bottomNum, diveName;
    private TextView pressureGroup, decompressStop, Sit;
    private Button buttonDone;
    private Button buttonUndo;
    private Button buttonAdd;
    private Button buttonHome;
    private GraphView graph;

    private OnUpdateSITListener updateSIT;
    private OnDiveAddedListener diveAddedListener;
    private OnDiveDeletedListener diveDeletedListener;
    private OnDiveCompletedListener diveDoneListener;
    private OnDoneButtonListener mListener;

    private DiveOperations diveDBoperation;

    public static NauiFeetFragment newInstance() {
        NauiFeetFragment fragment = new NauiFeetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NauiFeetFragment() {
        // Required empty public constructor
    }

    // Container Activity must implement this interface
    public interface OnDiveAddedListener {
        public void OnDiveAdded(int diveID, int SIT);
    }

    public interface OnDiveDeletedListener {
        public void OnDiveDeleted();
    }

    public interface OnUpdateSITListener {
        public void OnUpdateSIT(int SIT[]);
    }

    public interface OnDiveCompletedListener {
        public void OnDiveCompleted(String name, String EPG, String metric);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        Activity a = getActivity();
        diveDBoperation = new DiveOperations(a);
        diveDBoperation.open();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_naui_feet, container, false);

        diveName = (EditText) view.findViewById(R.id.diveName);
        depthNum = (EditText) view.findViewById(R.id.depthNum);
        bottomNum = (EditText) view.findViewById(R.id.bottomNum);
        Sit = (EditText) view.findViewById(R.id.SIT);
        pressureGroup = (TextView) view.findViewById(R.id.pressureGroup);
        decompressStop = (TextView) view.findViewById(R.id.decompressStop);
        buttonDone = (Button) view.findViewById(R.id.buttonDone);
        buttonUndo = (Button) view.findViewById(R.id.undo);
        buttonAdd = (Button) view.findViewById(R.id.buttonAdd);
        buttonHome = (Button) view.findViewById(R.id.home);
        graph = (GraphView) view.findViewById(R.id.graph);

        buttonDone.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        buttonUndo.setOnClickListener(this);
        buttonHome.setOnClickListener(this);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (diveAddedListener != null) {
        }
        if (diveDeletedListener != null) {
        }
        if (diveDoneListener != null) {
        }
        if (updateSIT != null) {
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            diveAddedListener = (OnDiveAddedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDiveAddedListener");
        }
        try {
            diveDeletedListener = (OnDiveDeletedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDiveAddedListener");
        }
        try {
            diveDoneListener = (OnDiveCompletedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDiveCompletedListener");
        }
        try {
            updateSIT = (OnUpdateSITListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDiveCompletedListener");
        }
        try {
            mListener = (OnDoneButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        diveAddedListener = null;
        diveDeletedListener = null;
        diveDoneListener = null;
        updateSIT = null;
        mListener = null;
    }

    @Override
    public void onClick(View view){
        //check for invalid input
        final double depth, bottom;
        NAUIDiveTable DT = new NAUIDiveTable();

        switch(view.getId()){
            case R.id.buttonAdd:
            case R.id.buttonDone:


                if(!(TextUtils.isEmpty(depthNum.getText()) | TextUtils.isEmpty(bottomNum.getText()))) {

                    try {
                        depth = Double.parseDouble(depthNum.getText().toString());
                        bottom = Double.parseDouble(bottomNum.getText().toString());
                    } catch (NumberFormatException e) {

                        CharSequence text = "Please Enter a Number";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(getActivity(), text, duration);
                        toast.show();

                        depthNum.setText("");
                        bottomNum.setText("");

                        break;
                    }

                   /* if (currentDive.size() != 0 && !currentDive.get(currentDive.size()-1).isSIT){
                        Dive reqSIT = new Dive();
                        reqSIT.bottomTime = 10;
                        reqSIT.depth = 0;
                        reqSIT.isSIT = true;
                        currentDive.add(reqSIT);
                    }*/

                    Log.d("NFF ", "Depth: " + (int) depth + " Bottom: " + (int) bottom);
                    //Do stuff here
                    Dive add = new Dive();
                    add.depth = depth;
                    add.bottomTime = bottom;
                    mListener.onDoneButtonListener(1, add);
                    int decompressTime = 0;

                    //add to current dive
                    currentDive.add(add);


                    //determine Current letter group and decompression stop data
                    char PG = DT.getLetterGroupFirstDiveFeet((int) currentDive.get(0).depth, (int) currentDive.get(0).bottomTime);
                    Log.d("NFF ", "PG: First "+PG);
                    decompressTime = DT.decompressionStopMinutesFirstDiveFeet((int) currentDive.get(0).depth, (int) currentDive.get(0).bottomTime);

                    if (PG == '1' || decompressTime == -1) {
                        depthNum.setText("");
                        bottomNum.setText("");

                        CharSequence text = "Error: Dive out of recommended scope";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(getActivity(), text, duration);
                        toast.show();
                        currentDive.remove(currentDive.size()-1);
                        break;
                    }

                    if (currentDive.size() > 1) {
                        PG = DT.getLetterGroupFirstDiveFeet((int) currentDive.get(0).depth, (int) currentDive.get(0).bottomTime); //get first dive's ending PG
                        decompressTime = DT.decompressionStopMinutesFirstDiveFeet((int) currentDive.get(0).depth, (int) currentDive.get(0).bottomTime); //get first dive's ending DT
                        for (int i = 1; i < currentDive.size(); i++) {
                            if (currentDive.get(i).isSIT) {
                                PG = DT.getLetterGroupSurfaceIntervalTime(PG, (int) currentDive.get(i).bottomTime);
                                continue;
                            }
                            decompressTime = DT.decompressionStopMinutesRepetitiveDiveFeet(PG, (int) currentDive.get(i).depth, (int) currentDive.get(i).bottomTime);
                            Log.d("NFF ", "Decomp Time: " + decompressTime);
                            PG = DT.getLetterGroupRepetitiveDiveFeet(PG, (int) currentDive.get(i).depth, (int) currentDive.get(i).bottomTime);
                            Log.d("NFF ", "PG ALoop: " + Character.toString(PG));

                        }

                        if (PG == '1' || decompressTime == -1) {
                            depthNum.setText("");
                            bottomNum.setText("");

                            CharSequence text = "Error: Dive out of recommended scope";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(getActivity(), text, duration);
                            toast.show();
                            currentDive.remove(currentDive.size() - 1);
                            break;
                        }
                    }

                    int diveNum = currentDive.size() - 1;

                    //remove data in the EditText fields so they can be used again
                    depthNum.setText("");
                    bottomNum.setText("");


                    //add single dive to single dive table
                    SingleDive sd = diveDBoperation.addDive((int) currentDive.get(diveNum).depth,
                            (int) currentDive.get(diveNum).bottomTime, Character.toString(PG),
                            decompressTime);
                    int diveID = sd.getId();

                    pressureGroup.setText(Character.toString(PG));

                    //Add data to the Graph!
                    checkSIT();
                    double tottime  = 0;
                    for (int i = 0; i < currentDive.size(); i++) {
                        if (i != 0) {
                            dp.add(new DataPoint(tottime, currentDive.get(i).depth));
                            tottime = currentDive.get(i).bottomTime + tottime;
                            dp.add(new DataPoint(tottime, currentDive.get(i).depth));
                        } else {
                            dp.add(new DataPoint(0, 0));
                            dp.add(new DataPoint(0, currentDive.get(i).depth));
                            dp.add(new DataPoint(currentDive.get(i).bottomTime, currentDive.get(i).depth));
                            tottime = currentDive.get(i).bottomTime;
                        }
                    }
                    if (currentDive.size() == 1) {
                        dp.add(new DataPoint(currentDive.get(0).bottomTime, 0));
                    }

                    series.resetData(dp.toArray(new DataPoint[dp.size()]));
                    dp.clear();
                    graph.addSeries(series);

                    if(decompressTime != 0){
                        decompressStop.setText("15 Ft for "+ Integer.toString(decompressTime)+" Min");
                    } else {
                        decompressStop.setText("N/A"); //blank Decompression stop
                    }

                    char newPG = PG;
                    if(!TextUtils.isEmpty(Sit.getText())){
                        int surfaceTime = 0;
                        try {
                            surfaceTime = Integer.parseInt(Sit.getText().toString());
                        } catch (NumberFormatException e) {
                            CharSequence text = "Please enter a number in Minutes";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(getActivity(), text, duration);
                            toast.show();
                        }

                        newPG = DT.getLetterGroupSurfaceIntervalTime(PG, surfaceTime);

                        if (newPG == '2') {
                            CharSequence text = "Error: Wait time is above 24 Hours";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(getActivity(), text, duration);
                            toast.show();
                            Sit.setText("");
                        }
                        if (newPG == '1') {
                            CharSequence text = "Error: Wait time must be above 10 Minutes";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(getActivity(), text, duration);
                            toast.show();
                            Sit.setText("");
                        }

                        Dive sitDive = new Dive();
                        sitDive.depth = 0;
                        sitDive.bottomTime = surfaceTime;
                        sitDive.isSIT = true;

                        pressureGroup.setText(Character.toString(newPG));
                        //add to curdive
                        if(currentDive.get(currentDive.size()-1).bottomTime == 10 && currentDive.get(currentDive.size()-1).isSIT)
                            currentDive.remove(currentDive.size()-1);

                        currentDive.add(sitDive);

                        //save single dive id and surface interval time if exists, or 0 if not
                        diveAddedListener.OnDiveAdded(diveID, surfaceTime);
                        tottime  = 0;
                        //Add data to the Graph!
                        checkSIT();
                        for (int i = 0; i < currentDive.size(); i++) {
                            if (i != 0) {
                                dp.add(new DataPoint(tottime, currentDive.get(i).depth));
                                Log.d("DSA ", " curr bottom time is: " + currentDive.get(i).bottomTime);
                                tottime = currentDive.get(i).bottomTime + tottime;
                                Log.d ("DSA ", " new bottom time is: " + currentDive.get(i).bottomTime);
                                dp.add(new DataPoint(tottime, currentDive.get(i).depth));
                            } else {
                                dp.add(new DataPoint(0, 0));
                                dp.add(new DataPoint(0, currentDive.get(i).depth));
                                dp.add(new DataPoint(currentDive.get(i).bottomTime, currentDive.get(i).depth));
                                tottime = currentDive.get(i).bottomTime;
                            }
                        }

                        if (currentDive.size() == 1) {
                            dp.add(new DataPoint(currentDive.get(0).bottomTime, 0));
                        }

                        series.resetData(dp.toArray(new DataPoint[dp.size()]));
                        dp.clear();
                        graph.addSeries(series);
                        Sit.setText("");
                    } else {
                        //save single dive id and 10 minute surface interval time
                        Dive reqSIT = new Dive();
                        reqSIT.bottomTime = 10;
                        reqSIT.depth = 0;
                        reqSIT.isSIT = true;
                        currentDive.add(reqSIT);
                        diveAddedListener.OnDiveAdded(diveID, 10);
                    }


                    if (view.getId() == R.id.buttonDone) {
                        diveDoneListener.OnDiveCompleted(diveName.getText().toString(), newPG + "", "Feet");
                    }

                    break;

                }else{
                    if(!TextUtils.isEmpty(Sit.getText()) && currentDive.size() >= 1 && (TextUtils.isEmpty(depthNum.getText()) && TextUtils.isEmpty(bottomNum.getText()))){
                        //add in the SIT
                        int surfaceTime = 0;
                        char PG;
                        try{
                            surfaceTime = Integer.parseInt(Sit.getText().toString());
                        }catch (NumberFormatException e){
                            CharSequence text = "Please enter a number in Minutes";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(getActivity(), text, duration);
                            toast.show();
                        }

                        Dive sitDive = new Dive();
                        sitDive.depth = 0;
                        sitDive.bottomTime = surfaceTime;
                        sitDive.isSIT = true;

                        //add to curdive and remove 10 min required SIT
                        if(currentDive.get(currentDive.size()-1).bottomTime == 10 && currentDive.get(currentDive.size()-1).isSIT)
                            currentDive.remove(currentDive.size()-1);

                        currentDive.add(sitDive);

                        //display curDive
                        double tottime  = 0;
                        //Add data to the Graph!
                        checkSIT();
                        PG = DT.getLetterGroupFirstDiveFeet((int) currentDive.get(0).depth, (int) currentDive.get(0).bottomTime );
                        if (currentDive.size() > 1) {
                            for (int i = 1; i < currentDive.size(); i++) {
                                if (currentDive.get(i).isSIT) {
                                    PG = DT.getLetterGroupSurfaceIntervalTime(PG, (int) currentDive.get(i).bottomTime);
                                    continue;
                                }

                                PG = DT.getLetterGroupRepetitiveDiveFeet(PG, (int) currentDive.get(i).depth, (int) currentDive.get(i).bottomTime);
                                Log.d("NFF ", "PG: L " + Character.toString(PG));
                            }
                        }

                        if(PG == '2'){
                            CharSequence text = "Error: Wait time is above 24 Hours";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(getActivity(), text, duration);
                            toast.show();
                            Sit.setText("");
                        }
                        if(PG == '1'){
                            CharSequence text = "Error: Wait time must be above 10 Minutes";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(getActivity(), text, duration);
                            toast.show();
                            Sit.setText("");
                        }

                        pressureGroup.setText(Character.toString(PG));

                        tottime = 0;
                        for (int i = 0; i < currentDive.size(); i++) {
                            if (i != 0) {
                                dp.add(new DataPoint(tottime, currentDive.get(i).depth));
                                Log.d("DSA ", " curr bottom time is: " + currentDive.get(i).bottomTime);
                                tottime = currentDive.get(i).bottomTime + tottime;
                                Log.d ("DSA ", " new bottom time is: " + currentDive.get(i).bottomTime);
                                dp.add(new DataPoint(tottime, currentDive.get(i).depth));
                            } else {
                                dp.add(new DataPoint(0, 0));
                                dp.add(new DataPoint(0, currentDive.get(i).depth));
                                dp.add(new DataPoint(currentDive.get(i).bottomTime, currentDive.get(i).depth));
                                tottime = currentDive.get(i).bottomTime;
                            }
                        }
                        if(currentDive.size() == 1){
                            dp.add( new DataPoint(currentDive.get(0).bottomTime, 0));
                        }

                        series.resetData(dp.toArray(new DataPoint[dp.size()]));
                        dp.clear();
                        graph.addSeries(series);
                        Sit.setText("");
                        checkSIT(); //combine SIT times if they are left next to one another. so RNT calculates correctly

                        if (view.getId() == R.id.buttonDone) {
                            diveDoneListener.OnDiveCompleted(diveName.getText().toString(), PG+"", "Feet");
                        }
                        break;
                    }

                    if (view.getId() != R.id.buttonDone) {
                        Log.d("NFF ", "Depth or Bottom Empty");
                        CharSequence text = "Depth or Bottom Time field empty";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(getActivity(), text, duration);
                        toast.show();
                    } else {
                        diveDoneListener.OnDiveCompleted(diveName.getText().toString(), pressureGroup.getText().toString(), "Feet");
                    }
                    break;
                }


            case R.id.undo:
                //check array is not empty
                if(!currentDive.isEmpty()) {
                    // if is not a sit...
                    // erase dive
                    /*if (currentDive.get(currentDive.size() - 1).isSIT == false && currentDive.size() > 1 && currentDive.get(currentDive.size() - 2).bottomTime == 10) {
                        currentDive.remove(currentDive.size() - 1);
                        currentDive.remove(currentDive.size() - 1);
                    } else
                    if (currentDive.get(currentDive.size() - 1).isSIT == false) {
                        currentDive.remove(currentDive.size() - 1);
                    } else
                    // if sit != 10 min...
                    // erase sit replace with 10 minute minimum
                    if(currentDive.get(currentDive.size() - 1).isSIT == true && currentDive.get(currentDive.size() - 1).bottomTime != 10){
                       currentDive.remove(currentDive.size() - 1);
                        Dive reqSIT = new Dive();
                        reqSIT.bottomTime = 10;
                        reqSIT.depth = 0;
                        reqSIT.isSIT = true;
                        currentDive.add(reqSIT);
                    } else
                    // if sit == 10 min....
                    // erase sit and dive
                    if(currentDive.get(currentDive.size() - 1).isSIT == true && currentDive.get(currentDive.size() - 1).bottomTime == 10){
                        currentDive.remove(currentDive.size() - 1);
                        currentDive.remove(currentDive.size() - 1);
                    }*/
                    if(currentDive.size() > 1) {
                        currentDive.remove(currentDive.size() - 1);
                        currentDive.remove(currentDive.size() - 1);
                    } else if(currentDive.size() == 1){
                        currentDive.remove(currentDive.size() - 1);
                    } else {
                        //do nothing
                    }

                    if(currentDive.size() != 0) {
                        // recalculate Pressure group,decompress stop
                        int decompressTime = DT.decompressionStopMinutesFirstDiveFeet((int) currentDive.get(0).depth, (int) currentDive.get(0).bottomTime);
                        char PG = DT.getLetterGroupFirstDiveFeet((int) currentDive.get(0).depth, (int) currentDive.get(0).bottomTime);

                        if (PG == '1' || decompressTime == -1) {
                            depthNum.setText("");
                            bottomNum.setText("");

                            CharSequence text = "Error: Dive out of recommended scope";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(getActivity(), text, duration);
                            toast.show();
                            break;
                        }

                        if (currentDive.size() > 1) {
                            for (int i = 1; i < currentDive.size(); i++) {
                                if (currentDive.get(i).isSIT) {
                                    PG = DT.getLetterGroupSurfaceIntervalTime(PG, (int) currentDive.get(i).bottomTime);
                                    continue;
                                }

                                PG = DT.getLetterGroupRepetitiveDiveFeet(PG, (int) currentDive.get(i).depth, (int) currentDive.get(i).bottomTime);
                                decompressTime = DT.decompressionStopMinutesRepetitiveDiveFeet(PG, (int) currentDive.get(i).depth, (int) currentDive.get(i).bottomTime);
                            }
                        }

                        if (PG == '2') {
                            CharSequence text = "Error: Wait time is above 24 Hours";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(getActivity(), text, duration);
                            toast.show();
                            Sit.setText("");
                        }
                        if (PG == '1') {
                            CharSequence text = "Error: Wait time must be above 10 Minutes";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(getActivity(), text, duration);
                            toast.show();
                            Sit.setText("");
                        }

                        if (decompressTime != 0) {
                            decompressStop.setText("15 Ft for " + Integer.toString(decompressTime) + " Min");
                        } else {
                            decompressStop.setText("N/A"); //blank Decompression stop
                        }
                        pressureGroup.setText(Character.toString(PG));

                        // redisplay graph
                        //Add data to the Graph!
                        checkSIT();
                        double tottime = 0;
                        for (int i = 0; i < currentDive.size(); i++) {
                            if (i != 0) {
                                dp.add(new DataPoint(tottime, currentDive.get(i).depth));
                                Log.d("DSA ", " curr bottom time is: " + currentDive.get(i).bottomTime);
                                tottime = currentDive.get(i).bottomTime + tottime;
                                Log.d("DSA ", " new bottom time is: " + currentDive.get(i).bottomTime);
                                dp.add(new DataPoint(tottime, currentDive.get(i).depth));
                            } else {
                                dp.add(new DataPoint(0, 0));
                                dp.add(new DataPoint(0, currentDive.get(i).depth));
                                dp.add(new DataPoint(currentDive.get(i).bottomTime, currentDive.get(i).depth));
                                tottime = currentDive.get(i).bottomTime;
                            }
                        }

                        if (currentDive.size() == 1) {
                            dp.add(new DataPoint(currentDive.get(0).bottomTime, 0));
                        }

                        series.resetData(dp.toArray(new DataPoint[dp.size()]));
                        dp.clear();
                        graph.addSeries(series);
                    } // end after erase empty check
                    else { // the array IS empty...
                        pressureGroup.setText("N/A");
                        decompressStop.setText("N/A");
                        dp.clear();
                        series.resetData(dp.toArray(new DataPoint[dp.size()]));
                        graph.addSeries(series);
                    }

                    //Delete Dive from DB
                    diveDeletedListener.OnDiveDeleted();

                } // end outer empty check

                break;

            case R.id.home:
                Intent intent = new Intent(getActivity(), SafeDivePlanner.class);
                getActivity().startActivity(intent);
                break;

            default:
                throw new RuntimeException("Unknown Button");
        }

    }



    public interface OnDoneButtonListener {
        // passes the pressure group back to the activity
        // note: this is mostly just a test. later all relevant data will be
        // passed back to make fragment switching easier for the user.
        public void onDoneButtonListener(int id, Dive d);
    }

    public void checkSIT(){
        int newSIT[] = new int [4];
        int j = 0;
        boolean update = false;
        for (int i=0; i<currentDive.size()-1; i++){
            if(currentDive.get(i).isSIT && currentDive.get(i+1).isSIT){
                Dive combine = new Dive();
                combine.bottomTime = currentDive.get(i).bottomTime + currentDive.get(i+1).bottomTime;
                combine.depth = 0;
                combine.isSIT = true;

                currentDive.remove(i+1);
                currentDive.remove(i);

                newSIT[j++] = (int) combine.bottomTime;
                update = true;

                currentDive.add(combine);
            } else if (currentDive.get(i).isSIT) {
                newSIT[j++] = (int) currentDive.get(i).bottomTime;
            }
        }
        if (update)
            updateSIT.OnUpdateSIT(newSIT);
    }

}
