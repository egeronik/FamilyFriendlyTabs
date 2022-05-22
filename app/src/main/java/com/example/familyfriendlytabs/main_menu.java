package com.example.familyfriendlytabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class main_menu extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }


    public void showLevels(View view) {
        Intent intent = new Intent(this, ScrollingActivity.class);
        startActivity(intent);
    }
}