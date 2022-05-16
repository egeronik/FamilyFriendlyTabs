package com.example.familyfriendlytabs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

public class myListAdapter extends ArrayAdapter<String> {
    private static final String TAG = "myListAdapter";

    private Context mContext;
    int mResource;
    int mNorm;

    public myListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects, Integer normeze) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mNorm = normeze;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView nameTv = (TextView) convertView.findViewById(R.id.lvlNameView);
        Button playBtn = (Button) convertView.findViewById(R.id.PlayButton);

        nameTv.setText(getItem(position));
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, Integer.toString(position));
                Intent intent = new Intent(mContext, PuzzleActivity.class);
                if (position >= mNorm) {
                    intent.putExtra("LevelName", "H" + Integer.toString(position - mNorm + 1));

                } else {
                    intent.putExtra("LevelName", "P" + Integer.toString(position));
                }
                intent.putExtra("LevelTitle", getItem(position));
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }
}
