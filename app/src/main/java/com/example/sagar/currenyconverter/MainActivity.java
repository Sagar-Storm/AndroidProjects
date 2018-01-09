package com.example.sagar.currenyconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void convertToRupee(View view) {
        EditText dollar = (EditText) findViewById(R.id.dollars);
        String rupee = dollar.getText().toString();
        Long rupeeNumber = Long.valueOf(rupee) * 66;
        Toast.makeText(MainActivity.this, "Rupees are " + rupeeNumber, Toast.LENGTH_SHORT).show();
    }
}
