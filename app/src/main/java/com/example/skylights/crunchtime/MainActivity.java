package com.example.skylights.crunchtime;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Context;
import android.widget.AdapterView.OnItemSelectedListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private EditText inputtedNum;
    private TextView calResult;
    private TextView repsMinsResult;
    private Timer timer = new Timer();
    private final long DELAY = 1000; // in ms
    private String selectedExercise;
    private double calPerRepMin;
    private String totalCals;
    private String totalRepsMins;
    private int result;
    private List<Integer> allEquivRepsMins;

    repsMinsClass [] repsMinsObj  = {
            new repsMinsClass("Push-ups", "reps", R.drawable.pushup),
            new repsMinsClass("Sit-ups", "reps", R.drawable.situp),
            new repsMinsClass("Squats", "reps", R.drawable.squat),
            new repsMinsClass("Leg-lifts", "minutes", R.drawable.leglift),
            new repsMinsClass("Planks", "minutes", R.drawable.plank),
            new repsMinsClass("Jumping Jacks", "minutes", R.drawable.jumpingjacks),
            new repsMinsClass("Pull-ups", "reps", R.drawable.pullup),
            new repsMinsClass("Cycling", "minutes", R.drawable.cycling),
            new repsMinsClass("Walking", "minutes", R.drawable.walking),
            new repsMinsClass("Jogging", "minutes", R.drawable.running),
            new repsMinsClass("Swimming", "minutes", R.drawable.swimming),
            new repsMinsClass("Stair Climbing", "minutes", R.drawable.stairclimb),
    };
    TextView repsOrMins;

    Map<String, Integer> exerciseValsMap = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner customSpinner = (Spinner) findViewById(R.id.static_spinner);
        inputtedNum = (EditText) findViewById(R.id.edit_number);
        calResult = (TextView) findViewById(R.id.calories);

        exerciseValsMap.put("Push-ups", 350);
        exerciseValsMap.put("Sit-ups", 200);
        exerciseValsMap.put("Squats", 225);
        exerciseValsMap.put("Leg-lifts", 25);
        exerciseValsMap.put("Planks", 25);
        exerciseValsMap.put("Jumping Jacks", 10);
        exerciseValsMap.put("Pull-ups", 100);
        exerciseValsMap.put("Cycling", 12);
        exerciseValsMap.put("Walking", 20);
        exerciseValsMap.put("Jogging", 12);
        exerciseValsMap.put("Swimming", 13);
        exerciseValsMap.put("Stair Climbing", 15);

        MySpinnerAdapter staticAdapter =
                new MySpinnerAdapter(MainActivity.this,
                        R.layout.spinner_layout, repsMinsObj);

        staticAdapter
                .setDropDownViewResource(R.layout.spinner_item);

        customSpinner.setAdapter(staticAdapter);
        customSpinner.setOnItemSelectedListener(onItemSelectedListener0);
        repsOrMins = (TextView)findViewById(R.id.repsmins);

        inputtedNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {
                if(timer != null)
                    timer.cancel();
            }
            @Override
            public void afterTextChanged(final Editable s) {
                if (s.length() >= 1) {

                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    calculateCalories();
                                    calResult.setText(totalCals);
                                    calculateRepsMins();
                                    for (int ind = 0; ind < 12; ind++) {
                                        String name = "equiv_rm" + ind;
                                        int id = getResources().getIdentifier(name, "id", getPackageName());
                                        if (id != 0) {
                                            TextView textView = (TextView) findViewById(id);
                                            String tempRepsOrMins;
                                            if (ind == 0 || ind == 1 || ind == 2 || ind == 6) {
                                                if (allEquivRepsMins.get(ind) == 1) {
                                                    tempRepsOrMins = " rep";
                                                } else {
                                                    tempRepsOrMins = " reps";
                                                }
                                            } else {
                                                if (allEquivRepsMins.get(ind) == 1) {
                                                    tempRepsOrMins = " minute";
                                                } else {
                                                    tempRepsOrMins = " minutes";
                                                }
                                            }
                                            String numRepsMins = allEquivRepsMins.get(ind) + tempRepsOrMins;
                                            textView.setText(Html.fromHtml(numRepsMins));
                                        }
                                    }
                            }
                            });
                        }
                    }, DELAY);
                }
            }
        });
    }

    private void calculateCalories() {
        double inputtedNumInt = Double.valueOf(inputtedNum.getText().toString());
        double selectedExerConv = exerciseValsMap.get(selectedExercise);
        calPerRepMin = 100.0 / selectedExerConv;
        double doubleAns = calPerRepMin * inputtedNumInt;
        result = (int) Math.round(doubleAns);
        totalCals = Integer.toString(result);
    }

    private void calculateRepsMins() {
        double tempCalPerRepMin;
        double calculatedConversion;
        int convCalculatedConversion;
        int theExerVal;
        allEquivRepsMins = new ArrayList<Integer>();
        int[] exerciseVals = new int[12];
        exerciseVals[0] = exerciseValsMap.get("Push-ups");
        exerciseVals[1] = exerciseValsMap.get("Sit-ups");
        exerciseVals[2] = exerciseValsMap.get("Squats");
        exerciseVals[3] = exerciseValsMap.get("Leg-lifts");
        exerciseVals[4] = exerciseValsMap.get("Planks");
        exerciseVals[5] = exerciseValsMap.get("Jumping Jacks");
        exerciseVals[6] = exerciseValsMap.get("Pull-ups");
        exerciseVals[7] = exerciseValsMap.get("Cycling");
        exerciseVals[8] = exerciseValsMap.get("Walking");
        exerciseVals[9] = exerciseValsMap.get("Jogging");
        exerciseVals[10] = exerciseValsMap.get("Swimming");
        exerciseVals[11] = exerciseValsMap.get("Stair Climbing");
        for (int i = 0; i < 12; i++) {
            theExerVal = exerciseVals[i];
            tempCalPerRepMin = 100.0 / theExerVal;
            calculatedConversion = result / tempCalPerRepMin;
            convCalculatedConversion = (int) Math.round(calculatedConversion);
            allEquivRepsMins.add(convCalculatedConversion);
        }
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener0 =
            new OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    repsMinsClass obj = (repsMinsClass)(parent.getItemAtPosition(position));
                    repsOrMins.setText(String.valueOf(obj.getMoreText()));
                    selectedExercise = String.valueOf(obj.getText());
                    String inputtedNumCheck = inputtedNum.getText().toString();

                    if (totalCals != null && !inputtedNumCheck.matches("")) {
                        calculateCalories();
                        calResult.setText(totalCals);
                        calculateRepsMins();
                        for (int ind = 0; ind < 12; ind++) {
                            String name = "equiv_rm" + ind;
                            int id2 = getResources().getIdentifier(name, "id", getPackageName());
                            if (id2 != 0) {
                                TextView textView = (TextView) findViewById(id2);
                                String tempRepsOrMins;
                                if (ind == 0 || ind == 1 || ind == 2 || ind == 6) {
                                    tempRepsOrMins = " reps";
                                } else {
                                    tempRepsOrMins = " minutes";
                                }
                                String numRepsMins = allEquivRepsMins.get(ind) + tempRepsOrMins;
                                textView.setText(Html.fromHtml(numRepsMins));
                            }
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}

            };

    public class repsMinsClass {

        private String text;
        private String moreText;
        private int exerciseImage;

        public repsMinsClass(String text, String moreText, int exerciseImage){
            this.text = text;
            this.moreText = moreText;
            this.exerciseImage = exerciseImage;
        }

        public void setText(String text){
            this.text = text;
        }

        public String getText(){
            return this.text;
        }

        public void setMoreText(String moreText){
            this.moreText = moreText;
        }

        public String getMoreText(){
            return this.moreText;
        }

        public int getExerciseImage() {
            return this.exerciseImage;
        }
    }

    public class MySpinnerAdapter extends ArrayAdapter<repsMinsClass>{

        private Context context;
        private repsMinsClass[] myObjs;

        public MySpinnerAdapter(Context context, int textViewResourceId,
                                repsMinsClass[] myObjs) {
            super(context, textViewResourceId, myObjs);
            this.context = context;
            this.myObjs = myObjs;
        }

        public int getCount(){
            return myObjs.length;
        }

        public repsMinsClass getItem(int position){
            return myObjs[position];
        }

        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_layout, parent, false);
            ImageView exercisePose = (ImageView) row.findViewById(R.id.imageIcon);
            if (exercisePose != null) {
                exercisePose.setImageDrawable(ContextCompat.getDrawable(context, myObjs[position].getExerciseImage()));
            }
            TextView label = (TextView)row.findViewById(R.id.spinnerLayout);
            label.setText(myObjs[position].getText());
            return row;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_item, parent, false);
            ImageView exercisePose = (ImageView) row.findViewById(R.id.imageIcon);
            if (exercisePose != null) {
                exercisePose.setImageDrawable(ContextCompat.getDrawable(context, myObjs[position].getExerciseImage()));
            }
            TextView label = (TextView)row.findViewById(R.id.exerciseItem);
            label.setText(myObjs[position].getText());
            return row;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
