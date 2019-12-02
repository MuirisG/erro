package com.example.erro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class selectDirectorate extends AppCompatActivity implements View.OnClickListener{

    TextView btnYounghal, btnCobh, btnGlanmire, btnBlarney, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_directorate);

        btnYounghal = findViewById(R.id.btn_youghal);
        btnCobh = findViewById(R.id.btn_cobh);
        btnGlanmire = findViewById(R.id.btn_glanmire);
        btnBlarney = findViewById(R.id.btn_blarney);
        btnNext = findViewById(R.id.btn_next);
        btnYounghal.setOnClickListener(this);
        btnCobh.setOnClickListener(this);
        btnGlanmire.setOnClickListener(this);
        btnBlarney.setOnClickListener(this);
        btnNext.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if(view == btnYounghal) {
            Intent intent = new Intent(getApplicationContext(),selectSurface.class);
            intent.putExtra("directorate","youghal");
            startActivity(intent);
        } else if(view == btnCobh) {
            Intent intent = new Intent(getApplicationContext(),selectSurface.class);
            intent.putExtra("directorate","cobh");
            startActivity(intent);
        } else if(view == btnGlanmire) {
            Intent intent = new Intent(getApplicationContext(),selectSurface.class);
            intent.putExtra("directorate","glanmire");
            startActivity(intent);
        } else if(view == btnBlarney) {
            Intent intent = new Intent(getApplicationContext(),selectSurface.class);
            intent.putExtra("directorate","blarney");
            startActivity(intent);
        } else if(view == btnNext) {
            Intent intent = new Intent(getApplicationContext(),selectSurface.class);
            startActivity(intent);
        }
    }
}
