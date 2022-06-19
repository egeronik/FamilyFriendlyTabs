package com.example.familyfriendlytabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.Arrays;

public class main_menu extends AppCompatActivity {

    private static final String LOG_TAG = "Main menu";

    SharedPreferences sharedPref;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        relativeLayout = findViewById(R.id.bgnd);

        sharedPref = this.getSharedPreferences("settings", MODE_PRIVATE);
        if (sharedPref.getBoolean("hentaiMode",false)){
            relativeLayout.setBackground(getDrawable(R.drawable.dark));
        }else{
            relativeLayout.setBackground(getDrawable(R.drawable.clear));
        }



/*
        AssetManager manager = getAssets();
        try {
            Log.d(LOG_TAG, Arrays.toString(manager.list("H1")));
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPref.getBoolean("hentaiMode",false)){
            relativeLayout.setBackground(getDrawable(R.drawable.dark));
        }else{
            relativeLayout.setBackground(getDrawable(R.drawable.clear));
        }
    }

    public void showLevels(View view) {
        Intent intent = new Intent(this, ScrollingActivity.class);
        startActivity(intent);
    }

    public void showSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}