package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private Button btnSave, btnGetAllData, btnTransition;
    private EditText edtName, edtPunchSpeed, edtPunchPower, edtKickPower, edtKickSpeed;
    private TextView txtGetData;

    private String allKickBoxers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSave = findViewById(R.id.btnSave);
        edtName = findViewById(R.id.edtName);
        edtPunchSpeed = findViewById(R.id.edtPunchSpeed);
        edtPunchPower = findViewById(R.id.edtPunchPower);
        edtKickPower = findViewById(R.id.edtKickPower);
        edtKickSpeed = findViewById(R.id.edtKickSpeed);
        txtGetData = findViewById(R.id.txtGetData);
        btnGetAllData = findViewById(R.id.btnGetAllData);
        btnTransition = findViewById(R.id.btnNextActivity);

        txtGetData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("KickBoxer");
                //getInBackground if we want to get one object at a time
                parseQuery.getInBackground("sqRjPiW5kM", new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (object != null && e == null) {
//                            FancyToast.makeText(SignUp.this, "Data is not saved to server", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                            txtGetData.setText(object.get("name") + " - "+ "Punch Power: "+object.get("punch_power")+ " - "+ "Punch Speed: "+object.get("punch_speed")+ " - "+ "Kick Power: "+object.get("kick_power")+ " - "+ "kick Speed: "+object.get("kick_speed"));
                        }
                    }
                });
            }
        });
        btnSave.setOnClickListener(SignUp.this);

        btnGetAllData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                allKickBoxers = "";

                ParseQuery<ParseObject> queryAll = ParseQuery.getQuery("KickBoxer");

//                queryAll.whereGreaterThan("punch_power","50"); //whereGreaterThan API to get only the punch power having greater then 100;
                queryAll.whereGreaterThanOrEqualTo("punch_power","50");
                queryAll.setLimit(1);
                //we want all the data of the class name KickBoxer
                queryAll.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() > 0){
                                for (ParseObject kickboxer : objects) {
                                    allKickBoxers = allKickBoxers + kickboxer.get("name") + "\n";
                                }

                                FancyToast.makeText(SignUp.this, allKickBoxers, FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                            } else {
                                FancyToast.makeText(SignUp.this, "error", FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                            }
                        }
                    }
                });

            }
        });

        btnTransition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        try {
            final ParseObject kickBoxer = new ParseObject("KickBoxer");
            kickBoxer.put("name", edtName.getText().toString());
            kickBoxer.put("punch_speed", Integer.parseInt(edtPunchSpeed.getText().toString()));
            kickBoxer.put("punch_power", Integer.parseInt(edtPunchPower.getText().toString()));
            kickBoxer.put("kick_speed", Integer.parseInt(edtKickSpeed.getText().toString()));
            kickBoxer.put("kick_power", Integer.parseInt(edtKickPower.getText().toString()));
            kickBoxer.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
//                    Toast.makeText(SignUp.this, kickBoxer.get("name") +": "+"object is saved successfully",Toast.LENGTH_SHORT).show();
                        FancyToast.makeText(SignUp.this, kickBoxer.get("name") +": "+ "is saved to server", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                    } else {
//                    Toast.makeText(SignUp.this, "object is not saved",Toast.LENGTH_SHORT).show();
                        FancyToast.makeText(SignUp.this, "Data is not saved to server", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                    }
                }
            });
        } catch (Exception e){

            FancyToast.makeText(SignUp.this,  e.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
        }

    }


//    public void helloWorldTapped(View view){
////        ParseObject boxer = new ParseObject("Boxer");
////        boxer.put("punch_speed","200");
////        boxer.saveInBackground(new SaveCallback() {
////            @Override
////            public void done(ParseException e) {
////                if (e == null) {
////                    Toast.makeText(SignUp.this,"boxer object is saved succeessfully",Toast.LENGTH_SHORT).show();
////                }
////            }
////        });
//
//        final ParseObject kickBoxeroxer = new ParseObject("KickBoxer");
//        kickBoxeroxer.put("name","John");
//        kickBoxeroxer.put("punch_speed","1000");
//        kickBoxeroxer.put("punch_power","2000");
//        kickBoxeroxer.put("kick_speed","3000");
//        kickBoxeroxer.put("kick_power","4000");
//        kickBoxeroxer.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    Toast.makeText(SignUp.this, kickBoxeroxer.get("name") +"object is saved successfully",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
}
