package com.sarita.insideout;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<String> mUploads;
    private String bname, title;

    private StorageReference storageRef;

    public ImageAdapter(Context context, List<String> uploads, String bname, String title) {
        mContext = context;
        mUploads = uploads;
        this.bname = bname;
        this.title = title;
        Log.e("img ada", "in ada constr" +uploads);

        storageRef = FirebaseStorage.getInstance().getReference();

    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_row, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        String uploadCurrent = mUploads.get(position);
        Log.e("img ada", "in bind");

        storageRef.child(bname).child(title +"/" + uploadCurrent).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.get()
                        .load(uri)
                        .into(holder.imageView);
                Log.e("img ada", "gs://inside-out-6093a.appspot.com/"+ bname+ "/" + title + "/" );
            }
        });




    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
//        public ImageView videoView;
//        public ImageView textView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            Log.e("img ada", "in constr");
            imageView = itemView.findViewById(R.id.img_view);


        }
    }
}
