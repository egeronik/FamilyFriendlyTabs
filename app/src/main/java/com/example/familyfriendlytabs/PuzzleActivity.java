package com.example.familyfriendlytabs;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PuzzleActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String DEBUG_TAG = "PuzleAct";


    private float x1, x2, y1, y2;
    private int X = 3, Y = 3;//Pos of blank piece
    static final int MIN_DISTANCE = 150;

    TableLayout tableLayout;
    List<ArrayList<Drawable>> pieces;

    List<ArrayList<Integer>> ides;

    MediaPlayer mediaPlayer;

    AssetFileDescriptor assetFileDescriptor;

    private Timer mTimer;
    private TextView timerView;
    private long startTime;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    Random random;

    boolean isReleased = true;

    private enum soundType {
        MOVE,
        START,
        WIN,
        ENTER,
        GACHI_ENTER,
        GACHI_MOVE
    }

    private void shuffle(List<ArrayList<Integer>> a, int n) {
        Random random = new Random();
        int x = 3;
        int y = 3;
        for (int i = 0; i < n; i++) {
            int move = random.nextInt(4);
            int tmp = 0;
            switch (move) {
                case 0: //UP
                    if (y > 0) {
                        tmp = a.get(x).get(y);
                        a.get(x).set(y, a.get(x).get(y - 1));
                        a.get(x).set(y - 1, tmp);
                        y--;
                    }
                    break;
                case 1: //DOWN
                    if (y < 3) {
                        tmp = a.get(x).get(y);
                        a.get(x).set(y, a.get(x).get(y + 1));
                        a.get(x).set(y + 1, tmp);
                        y++;
                    }
                    break;
                case 2: //LEFT
                    if (x > 0) {
                        tmp = a.get(x).get(y);
                        a.get(x).set(y, a.get(x - 1).get(y));
                        a.get(x - 1).set(y, tmp);
                        x--;
                    }
                    break;
                case 3: //RIGHT
                    if (x < 3) {
                        tmp = a.get(x).get(y);
                        a.get(x).set(y, a.get(x + 1).get(y));
                        a.get(x + 1).set(y, tmp);
                        x++;
                    }
                    break;
            }
        }
    }

    private List<ArrayList<Drawable>> makeDrawabls(String folderName) {

        folderName = "levelParts/" + folderName;
        ides = new ArrayList<>();
        String imagePattern = "image_part_0";
        int imgNum = 1;
        for (int i = 0; i < 4; i++) {
            ArrayList<Integer> tmpID = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                tmpID.add(imgNum);
                imgNum++;
            }
            ides.add(tmpID);
        }


        shuffle(ides, 10000);
        debugIdes();
        List<ArrayList<Drawable>> ans = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ArrayList<Drawable> tmp = new ArrayList<>();

            for (int j = 0; j < 4; j++) {
                String imageName;
                imgNum = ides.get(i).get(j);
                if (imgNum == 16) {
                    X = i;
                    Y = j;
                }
                if (imgNum < 10) {
                    imageName = folderName + imagePattern + '0' + String.valueOf(imgNum) + ".jpg";
                } else {
                    imageName = folderName + imagePattern + String.valueOf(imgNum) + ".jpg";
                }
                try (InputStream inputStream = getApplicationContext().getAssets().open(imageName)) {
                    Drawable drawable = Drawable.createFromStream(inputStream, null);
                    tmp.add(drawable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ans.add(tmp);

        }
        return ans;
    }


    private void fillGrid(TableLayout tableLayout, List<ArrayList<Drawable>> pieces) {

        for (int i = 0; i < pieces.size(); i++) {

            LinearLayout row = (LinearLayout) tableLayout.getChildAt(i);
            for (int j = 0; j < pieces.get(i).size(); j++) {
                ImageView imageView = (ImageView) row.getChildAt(j);
                imageView.setImageDrawable(null);
                if (i != X || j != Y) {
                    imageView.setImageDrawable(pieces.get(i).get(j));
                }


            }


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        sharedPref = this.getSharedPreferences("settings", MODE_PRIVATE);
        editor = sharedPref.edit();
        //TODO StartSound
        tableLayout = findViewById(R.id.puzzleGrid);
        getSupportActionBar().setTitle(getIntent().getStringExtra("LevelTitle"));
        Log.d(DEBUG_TAG, getIntent().getStringExtra("LevelName").toString() + "/");
        pieces = makeDrawabls(getIntent().getStringExtra("LevelName").toString() + "/");
        ImageButton playButton = findViewById(R.id.backButton);
        ImageButton shuffleButton = findViewById(R.id.shuffleButton);
        timerView = findViewById(R.id.timerView);
        random = new Random();
        mediaPlayer = new MediaPlayer();
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                X = 3;
                Y = 3;
                pieces = makeDrawabls(getIntent().getStringExtra("LevelName").toString() + "/");
                fillGrid(tableLayout, pieces);
                startTimer();
            }
        });
        fillGrid(tableLayout, pieces);


        startTimer();


    }


    private void startTimer() {
        if (mTimer != null)
            mTimer.cancel();


        startTime = System.currentTimeMillis();
        mTimer = new Timer();
        TimerTask mMyTimerTask = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                Date date = Calendar.getInstance().getTime();

                runOnUiThread(new Runnable() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        long time = System.currentTimeMillis() - startTime;
                        timerView.setText(Long.toString(time / 1000));
                    }
                });
            }
        };

        mTimer.schedule(mMyTimerTask, 0, 100);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                float deltaX = x2 - x1;
                float deltaY = y2 - y1;
                if (Math.abs(deltaX) > MIN_DISTANCE || Math.abs(deltaY) > MIN_DISTANCE) {
                    AssetFileDescriptor descriptor;
                    if (sharedPref.getBoolean("gachiMode", false)) {
                        descriptor = getRandomSound(soundType.GACHI_MOVE);
                    } else {
                        descriptor = getRandomSound(soundType.MOVE);
                    }

                    if (descriptor != null  && isReleased) {

                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            isReleased=false;
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    mediaPlayer.release();
                                    isReleased=true;
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        if (deltaX < 0) {
                            if (Y < 3) {
                                Collections.swap(pieces.get(X), Y, Y + 1);
                                Collections.swap(ides.get(X), Y, Y + 1);
                                Y++;
                            }
                        } else {

                            if (Y > 0) {
                                Collections.swap(pieces.get(X), Y, Y - 1);
                                Collections.swap(ides.get(X), Y, Y - 1);

                                Y--;

                            }
                        }
                    } else {
                        Drawable tmp = pieces.get(X).get(Y).getConstantState().newDrawable();
                        Integer tmpID = ides.get(X).get(Y);

                        if (deltaY < 0) {


                            if (X < 3) {
                                pieces.get(X).set(Y, pieces.get(X + 1).get(Y));
                                pieces.get(X + 1).set(Y, tmp);
                                ides.get(X).set(Y, ides.get(X + 1).get(Y));
                                ides.get(X + 1).set(Y, tmpID);


                                X++;
                            }
                        } else {
                            //Toast.makeText(this, "up", Toast.LENGTH_SHORT).show ();
                            if (X > 0) {

                                pieces.get(X).set(Y, pieces.get(X - 1).get(Y));
                                pieces.get(X - 1).set(Y, tmp);
                                ides.get(X).set(Y, ides.get(X - 1).get(Y));
                                ides.get(X - 1).set(Y, tmpID);
                                X--;
                            }
                        }
                    }
//                    Log.d(DEBUG_TAG, String.valueOf(X) + String.valueOf(Y));


                    fillGrid(tableLayout, pieces);
                    if (checkWin()) {
                        descriptor = getRandomSound(soundType.WIN);
                        if (descriptor != null) {
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                isReleased=false;
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        mediaPlayer.release();
                                        isReleased=true;
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        mTimer.cancel();
                        Toast toast = Toast.makeText(getApplicationContext(), "Вы прошли уровень!!!", Toast.LENGTH_LONG);
                        toast.show();
                        SharedPreferences sharedPreferences = getSharedPreferences("levelLIst", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(getIntent().getStringExtra("LevelName"), true);
                        editor.apply();

                    }
                }
                break;
        }

        return super.onTouchEvent(event);
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

    private void debugIdes() {
        for (int i = 0; i < 4; i++) {
            StringBuilder tmpS = new StringBuilder();
            for (int j = 0; j < 4; j++) {
                tmpS.append(ides.get(i).get(j).toString()).append(' ');
            }
            Log.d(DEBUG_TAG, tmpS.toString());
        }
    }

    private boolean checkWin() {

        Integer num = 1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (!ides.get(i).get(j).equals(num)) return false;
                num++;
            }
        }

        return true;

    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        Log.d(DEBUG_TAG, "onScroll: " + e1.toString() + e2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }


}