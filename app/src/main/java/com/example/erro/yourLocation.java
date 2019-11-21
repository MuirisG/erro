package com.example.erro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class yourLocation extends AppCompatActivity implements View.OnClickListener {

    EditText input_a, input_b;
    TextView tv_result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_location);
        TextView calculate;
        calculate = findViewById(R.id.calculate);
        tv_result = findViewById(R.id.text_c);
        input_a = findViewById(R.id.input_a);
        input_b = findViewById(R.id.input_b);
        calculate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int a,b,c;
        a=Integer.valueOf(input_a.getText().toString());
        b=Integer.valueOf(input_b.getText().toString());
        c=a*b;
        tv_result.setText(String.valueOf(c));
    }
}
