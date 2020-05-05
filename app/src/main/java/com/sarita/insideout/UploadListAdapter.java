package com.sarita.insideout;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder>{

    public List<String> fileNameList;
    public List<String> fileDoneList;
    public List<Uri> filePreviewList;
    Context context;


    public UploadListAdapter(List<String> fileNameList, List<String> fileDoneList, List<Uri> filePreviewList, Context context){

        this.fileDoneList = fileDoneList;
        this.fileNameList = fileNameList;
        this.filePreviewList = filePreviewList;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String fileName = fileNameList.get(position);
        holder.fileNameView.setText(fileName);
        String fileDone = fileDoneList.get(position);

        if(fileDone.equals("uploading")){
            holder.fileDoneView.setImageResource(R.drawable.progress);
        } else {
            holder.fileDoneView.setImageResource(R.drawable.checked);
        }

        try {
            Uri filePath = filePreviewList.get(position);
            Bitmap bitmap = MediaStore
                    .Images
                    .Media
                    .getBitmap(
                            context.getContentResolver(),
                            filePath);
            holder.upload_preview.setImageBitmap(bitmap);

        }catch(Exception e){
            Log.e("upload error" , e.getMessage());
        }
    }

    public void set(int finalI){
        fileDoneList.remove(finalI);
        fileDoneList.add(finalI, "done");
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView fileNameView;
        public ImageView fileDoneView;
        public ImageView upload_preview;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            fileNameView = (TextView) mView.findViewById(R.id.upload_filename);
            fileDoneView = (ImageView) mView.findViewById(R.id.upload_loading);
            upload_preview = (ImageView) mView.findViewById(R.id.upload_icon);

        }

    }

}
