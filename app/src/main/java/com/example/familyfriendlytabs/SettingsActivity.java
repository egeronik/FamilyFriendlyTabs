package com.example.familyfriendlytabs;

import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceFragmentCompat;

import java.io.IOException;
import java.util.Random;


public class SettingsActivity extends AppCompatActivity {

    SwitchCompat leftHandModeSwitch;
    SwitchCompat darkModeSwitch;
    SwitchCompat hentaiModeSwitch;
    SwitchCompat gachiSwitch;
    Button volumeButton;
    TextView volumeTextView;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private enum soundType {
        MOVE,
        START,
        WIN,
        ENTER,
        GACHI_ENTER,
        GACHI_MOVE
    }

    Random random;
    Random soundRand;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        sharedPref = this.getSharedPreferences("settings",MODE_PRIVATE);
        editor = sharedPref.edit();
        random = new Random();

        ImageButton button = findViewById(R.id.backSettingsButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        leftHandModeSwitch = findViewById(R.id.leftHandModeSwitch);
        leftHandModeSwitch.setChecked(sharedPref.getBoolean("leftHandMode", false));
        leftHandModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("leftHandMode", b);
                editor.apply();
            }
        });

        darkModeSwitch = findViewById(R.id.darkThemeSwitch);
        darkModeSwitch.setChecked(sharedPref.getBoolean("darkMode", false));
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("darkMode", b);
                editor.apply();
                if (b){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        hentaiModeSwitch = findViewById(R.id.hentaiModeSwitch);
        hentaiModeSwitch.setChecked(sharedPref.getBoolean("hentaiMode", false));
        hentaiModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("hentaiMode", b);
                editor.apply();
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
            }
        });


        gachiSwitch = findViewById(R.id.boyNextDoorSwitch);
        gachiSwitch.setChecked(sharedPref.getBoolean("gachiMode", false));
        gachiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("gachiMode", b);
                editor.apply();
                if(b){
                    MediaPlayer mediaPlayer;
                    AssetFileDescriptor descriptor = getRandomSound(soundType.GACHI_ENTER);
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
            }
        });




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