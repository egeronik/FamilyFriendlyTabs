package com.example.familyfriendlytabs;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class PuzzleActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String DEBUG_TAG = "PuzleAct";


    private float x1, x2, y1, y2;
    private int X = 3, Y = 3;//Pos of blank piece
    static final int MIN_DISTANCE = 150;

    TableLayout tableLayout;
    List<ArrayList<Drawable>> pieces;

    List<ArrayList<Integer>> ides;


    private List<ArrayList<Drawable>> makeDrawabls(String folderName) {
        Random random = new Random();
        List<ArrayList<Drawable>> ans = new ArrayList<>();
        ides = new ArrayList<>();
        HashSet<Integer> st = new HashSet<>();
        String imagePattern = "image_part_0";
        int imgNum = 1;
        for (int i = 0; i < 4; i++) {
            ArrayList<Drawable> tmp = new ArrayList<>();
            ArrayList<Integer> tmpID = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                String imageName;
                if (i == 3 && j == 3) {
                    imgNum = 16;
                } else {
                    imgNum = 1 + random.nextInt(15);
                    while (st.contains(imgNum)) {
                        imgNum = 1 + random.nextInt(15);
                    }
                }

                Log.d("ImgNum", String.valueOf(imgNum));
                st.add(imgNum);
                if (imgNum < 10) {
                    imageName = folderName + imagePattern + '0' + String.valueOf(imgNum) + ".jpg";
                } else {
                    imageName = folderName + imagePattern + String.valueOf(imgNum) + ".jpg";
                }

                try (InputStream inputStream = getApplicationContext().getAssets().open(imageName)) {
                    Drawable drawable = Drawable.createFromStream(inputStream, null);
                    tmp.add(drawable);
                    tmpID.add(imgNum);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ans.add(tmp);
            ides.add(tmpID);
        }
        return ans;
    }


    private void fillGrid(TableLayout tableLayout, List<ArrayList<Drawable>> pieces) {

        for (int i = 0; i < pieces.size(); i++) {

            LinearLayout row = (LinearLayout) tableLayout.getChildAt(i); //Maybe kostil
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
        tableLayout = findViewById(R.id.puzzleGrid);
        Log.d(DEBUG_TAG, getIntent().getStringExtra("LevelName").toString() + "/");
        pieces = makeDrawabls(getIntent().getStringExtra("LevelName").toString() + "/");
        Button playButton = findViewById(R.id.backButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fillGrid(tableLayout, pieces);

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
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        if (deltaX > 0) {
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

                        if (deltaY > 0) {


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
                    if (checkWin()){
                        finish();
                        Toast toast = Toast.makeText(getApplicationContext(),"Вы прошли уровень!!!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
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