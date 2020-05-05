package com.sarita.insideout;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] maintitle;
    private final String[] subtitle;
    private final Integer[] imgid;

    public MyListAdapter(Activity context, String[] maintitle,String[] subtitle, Integer[] imgid) {
        super(context, R.layout.list_item_single, maintitle);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.maintitle=maintitle;
        this.subtitle=subtitle;
        this.imgid=imgid;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item_single, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.upload_filename);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.upload_icon);
        ImageView subtitleText =  rowView.findViewById(R.id.upload_loading);

        titleText.setText(maintitle[position]);
        imageView.setImageResource(imgid[position]);
        imageView.setImageResource(imgid[position]);

        return rowView;

    };
}