package com.example.erro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class selectSurface extends AppCompatActivity implements View.OnClickListener {

    TextView tvTarmac, tvGrassVerge, tvConcreateFoothpath, tvHra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_surface);

        tvTarmac = findViewById(R.id.btn_tarmac);
        tvGrassVerge = findViewById(R.id.btn_grassVerge);
        tvConcreateFoothpath = findViewById(R.id.btn_concreteFoothpath);
        tvHra = findViewById(R.id.btn_hra);

        tvTarmac.setOnClickListener(this);
        tvGrassVerge.setOnClickListener(this);
        tvConcreateFoothpath.setOnClickListener(this);
        tvHra.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == tvTarmac || view == tvGrassVerge || view == tvConcreateFoothpath || view == tvHra) {
            Intent intent = new Intent(getApplicationContext(),uploadpictureActivity.class);
            startActivity(intent);
        }
    }
}
