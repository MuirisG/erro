package com.example.erro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import android.os.Bundle;

public class selectZone extends AppCompatActivity implements View.OnClickListener{

    TextView btn_zone1, btn_zone2, btn_zone3, btn_zone4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_zone);

        Button testAPIBtn = findViewById(R.id.test_api_btn);
        testAPIBtn.setOnClickListener(this);
        btn_zone1 = (TextView) findViewById(R.id.btn_zone1);
        btn_zone2 = (TextView) findViewById(R.id.btn_zone2);
        btn_zone3 = (TextView) findViewById(R.id.btn_zone3);
        btn_zone4 = (TextView) findViewById(R.id.btn_zone4);
        btn_zone1.setOnClickListener(this);
        btn_zone2.setOnClickListener(this);
        btn_zone3.setOnClickListener(this);
        btn_zone4.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        if(v==btn_zone1 || v == btn_zone2 || v == btn_zone3 || v == btn_zone4) {
            Intent intent = new Intent(getApplicationContext(),yourLocation.class);
            startActivity(intent);
        } else if( v.getId()==R.id.test_api_btn) {
            Intent intent = new Intent(getApplicationContext(),TestApiActivity.class);
            startActivity(intent);
        }
    }
}
