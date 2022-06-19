package com.example.familyfriendlytabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ScrollingActivity extends AppCompatActivity {

    private static String TAG = "ScrollingActivity";

    private ActivityScrollingBinding binding;

    boolean oneMode = false;

    ArrayList<String> names;
    ArrayList<String> nude;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private ListView listView;

    private enum soundType {
        MOVE,
        START,
        WIN,
        ENTER,
        GACHI_ENTER,
        GACHI_MOVE
    }

    Random random;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        random = new Random();

        listView = findViewById(R.id.lvlNameView);
        names = new ArrayList<String>(
                Arrays.asList(
                        "Белый синий",
                        "Ловушка",
                        "Байт",
                        "Движение броуна",
                        "Кринж",
                        "Дуплет",
                        "17,6+",
                        "Сенко",
                        "Mda starou",
                        "17,8+",
                        "Гляди какая лужа",
                        "Пельмень для господина",
                        "То, что тебе не светит :(",
                        "Недололя",
                        "Лампа Pixar",
                        "Люблю фурри :3",
                        "Угадай аниме",
                        "Мне надоело",
                        "Придумывать названия")
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

        sharedPref = this.getSharedPreferences("settings",MODE_PRIVATE);
        editor = sharedPref.edit();

        SwitchCompat sw = findViewById(R.id.oneHandSwitch);
        sw.setChecked(sharedPref.getBoolean("hentaiMode", false));
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("hentaiMode", b);
                editor.apply();
                Toast toast;
                if (b) {
                    if(b){
                        MediaPlayer mediaPlayer;
                        AssetFileDescriptor descriptor = getRandomSound(soundType.ENTER);
                        if (descriptor != null) {
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        mediaPlayer.release();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    toast = Toast.makeText(getApplicationContext(), "Включен режим управления одной рукой", Toast.LENGTH_LONG);
                } else {
                    toast = Toast.makeText(getApplicationContext(), "Режим управления одной рукой выключен", Toast.LENGTH_LONG);
                }
                toast.show();
                populateListView();

            }
        });
        populateListView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        SwitchCompat sw = findViewById(R.id.oneHandSwitch);
        sw.setChecked(sharedPref.getBoolean("hentaiMode", false));
        populateListView();
    }

    public void populateListView() {
        Log.d("ListDataActivity", "populateListView: dispalying data in listView");
        ArrayList<String> cur = new ArrayList<>(names);
        if (sharedPref.getBoolean("hentaiMode", false))
            cur.addAll(nude);
        myListAdapter adapter;
        if (sharedPref.getBoolean("leftHandMode", false)) {
            adapter = new myListAdapter(this, R.layout.recycl_item_left, cur, names.size());
        } else {
            adapter = new myListAdapter(this, R.layout.recycl_item, cur, names.size());
        }
        Log.d(TAG, this.toString());
        listView.setAdapter(adapter);

    }


    private AssetFileDescriptor getRandomSound(soundType type) {
        String soundFolder = "sounds";
        switch (type) {
            case MOVE:
                soundFolder += "/moveTileSounds";
                break;
            case START:
                soundFolder += "/startGameSounds";
                break;
            case WIN:
                soundFolder += "/youWinSounds";
                break;
            case ENTER:
                soundFolder += "/modeChangeSounds";
                break;
            case GACHI_ENTER:
                soundFolder += "/gachiChangeSounds";
                break;
            case GACHI_MOVE:
                soundFolder += "/gachiMoveSounds";

        }
        AssetManager manager = getAssets();
        try {
            int l = manager.list(soundFolder).length;
            String name = manager.list(soundFolder)[random.nextInt(l)];
            AssetFileDescriptor assetFileDescriptor;
            assetFileDescriptor = getAssets().openFd(soundFolder + "/" + name);
            return assetFileDescriptor;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}