package com.sarita.insideout;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BookAdapter extends FirestoreRecyclerAdapter<ModelBooks, BookAdapter.BookHolder> {


    private ClickListener listener;
    private Context context;
    private String query = "";


    public BookAdapter(@NonNull FirestoreRecyclerOptions<ModelBooks> options, ClickListener listener, Context context ) {
        super(options);
        this.listener = listener;
        this.context = context;
        Log.e("Book", "adapter constr");

    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("Adapter error", e.getMessage());
    }

    @Override
    protected void onBindViewHolder(@NonNull BookHolder holder, final int position, @NonNull final ModelBooks model) {

        if(model.getTitle()==null) return;
        if(query.length()>0){//if query is set, filter the children
            Log.e("query", query);
            if(!model.getTitle().toLowerCase().contains(query.toLowerCase())){
                holder.ll.setVisibility(View.GONE);
            }

        }else {
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
            holder.ll.setVisibility(View.VISIBLE);
            holder.title.setText(model.getTitle());

            Log.e("Note", model.getTitle());
            holder.timestamp.setText(sfd.format(model.getTimestamp().toDate()));
            Log.e("Note", "bindviewholder");
        }

    }

    public void setQuery(String s){
        query = s;
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_row, parent, false);
        Log.e("Book", "createviewholder");
        return new BookHolder(v, listener, context);
    }

    class BookHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        TextView title, timestamp;
        LinearLayout ll;
        Context context;


        private WeakReference<ClickListener> listenerRef;

        public BookHolder(View v, ClickListener listener, Context context){
            super(v);
            listenerRef = new WeakReference<>(listener);
            v.setOnClickListener(this);
            this.context = context;
            title = v.findViewById(R.id.textView_title);
            timestamp = v.findViewById(R.id.textView_time);
            ll = v.findViewById(R.id.ll_book_row);

            v.findViewById(R.id.view_background).setVisibility(View.GONE);

            Log.e("Book", "holder constr");
        }

        @Override
        public void onClick(View v) {

            ModelBooks model = getItem(getAdapterPosition());
            Log.e("hihih", model.getTitle());
            final Intent intent;
            intent =  new Intent(context, ShowNotebook.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.putExtra("Notebook", model.getTitle());
            context.startActivity(intent);

        }


    }

    public interface ClickListener {

        void onPositionClicked(int position);

    }


}