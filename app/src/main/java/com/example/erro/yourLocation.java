package com.example.erro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class yourLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_location);
        final TextView tv_result;
        final EditText input_a, input_b;
        Button calculate;
        calculate = findViewById(R.id.calculate);
        tv_result = findViewById(R.id.text_c);
        input_a = findViewById(R.id.input_a);
        input_b = findViewById(R.id.input_b);
        calculate.setOnclickListener(view) {
            int a,b,c;
            a=Integer.valueOf(input_a.getText().toString());
            b=Integer.valueOf(input_b.getText().toString());
            c=a*b;
            tv_result.setText(String.valueOf(c));
        }
    }
}
