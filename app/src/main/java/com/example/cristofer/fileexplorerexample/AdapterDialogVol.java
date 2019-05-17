package com.example.cristofer.fileexplorerexample;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class AdapterDialogVol extends BaseAdapter {

    private Activity activity;
    private ArrayList<String> names;

    public AdapterDialogVol() {
    }

    public AdapterDialogVol(Activity activity, ArrayList<String> names) {
        this.activity = activity;
        this.names = names;
    }


    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        final String name;
        final TextView comicName;
        final TextView number;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.comic_explorer_item, null);
        }

        comicName = (TextView) v.findViewById(R.id.nameVol);
        number = (TextView) v.findViewById(R.id.numberVol);

        name = names.get(position);
        comicName.setText(name);
        number.setText("#" + String.valueOf(position + 1));
        return v;
    }

}
