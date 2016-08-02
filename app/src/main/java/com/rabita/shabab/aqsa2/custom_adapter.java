package com.rabita.shabab.aqsa2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbookair on 8/29/15.
 */
public class custom_adapter extends ArrayAdapter {
    private ArrayList<String> headers;
    private ArrayList<String> contents;
    private ArrayList<String> dates;
    private Context context;
    private LayoutInflater inflater ;
    public static String TAG = "AqsaProject";



    public custom_adapter(Context applicationContext, List<String> headerArray,List<String> contents,List<String> dates) {
        super(applicationContext,R.layout.list_row,headerArray);
        this.headers = (ArrayList<String>) headerArray;
        this.contents = (ArrayList<String>) contents;
        this.dates = (ArrayList<String>) dates;

        this.context = applicationContext;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return headers.size();
    }

    @Override
    public Object getItem(int position) {
        return headers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_row,null);
        TextView headerText = (TextView)convertView.findViewById(R.id.header);
        TextView contentText = (TextView)convertView.findViewById(R.id.contentView);

        headerText.setText(headers.get(position));
        contentText.setText(contents.get(position));

        return convertView;
    }
}
