package com.example.familyfriendlytabs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Debug;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familyfriendlytabs.databinding.ActivityScrollingBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    private static String TAG = "ScrollingActivity";

    private ActivityScrollingBinding binding;

    boolean oneMode = false;

    ArrayList<String> names;
    ArrayList<String> nude;


    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listView = findViewById(R.id.lvlNameView);
        names = new ArrayList<String>(
                Arrays.asList(
                        "Белый синий",
                        "Ловушка",
                        "Байт",
                        "Движение броуна",
                        "Кринж",
                        "Дуплет",
                        "17.6+",
                        "Сенко",
                        "Mda starou",
                        "17.8+")
        );
        nude = new ArrayList<String>(
                Arrays.asList("Не надо, семпай",
                        "Заправка",
                        "Пайзури",
                        "Даблкилл",
                        "Blindfold",
                        "Сахарная пудра",
                        "Вареник со смятаной",
                        "Смотри в глаза")
        );

        Switch sw = findViewById(R.id.oneHandSwitch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    oneMode = true;
                    Toast toast = Toast.makeText(getApplicationContext(),"Включен режим управления одной рукой", Toast.LENGTH_LONG);
                    toast.show();
                    populateListView();
                } else {
                    oneMode = false;
                    Toast toast = Toast.makeText(getApplicationContext(),"Режим управления одной рукой выключен", Toast.LENGTH_LONG);
                    toast.show();
                    populateListView();
                }
            }
        });
        populateListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateListView();
    }

    public void populateListView() {
        Log.d("ListDataActivity", "populateListView: dispalying data in listView");
        ArrayList<String> cur = new ArrayList<>();
        cur.addAll(names);
        if(oneMode)
            cur.addAll(nude);
        myListAdapter adapter = new myListAdapter(this, R.layout.recycl_item, cur, names.size());
        Log.d(TAG, this.toString());
        listView.setAdapter(adapter);

    }


}