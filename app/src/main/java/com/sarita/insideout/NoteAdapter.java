package com.sarita.insideout;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteAdapter extends FirestoreRecyclerAdapter<ModelNotes, NoteAdapter.NoteHolder> {


    private NoteAdapter.ClickListener listener;
    private String bookn;
    private String source;
    private String query = "";
    private int c=0;


    public NoteAdapter(@NonNull FirestoreRecyclerOptions<ModelNotes> options, String name, String src, NoteAdapter.ClickListener listener ) {
        super(options);
        this.listener = listener;
        this.bookn = name;
        this.source = src;

        Log.e("Note", "adapter constr");

    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("Adapter error", e.getMessage());
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteAdapter.NoteHolder holder, final int position, @NonNull final ModelNotes model) {


        if(query.length()>0){//if query is set, filter the children
            Log.e("query", query);
            if(!model.getTitle().toLowerCase().contains(query.toLowerCase())){
                holder.ll.setVisibility(View.GONE);
                holder.viewBackground.setVisibility(View.GONE);
                holder.viewForeground.setVisibility(View.GONE);
            }else{

                holder.ll.setVisibility(View.VISIBLE);
                holder.viewBackground.setVisibility(View.VISIBLE);
                holder.viewForeground.setVisibility(View.VISIBLE);
                holder.title.setText(model.getTitle());
                holder.timestamp.setText(model.getOwner());
                Log.e("Note", "bindviewholder");
            }

        }else {

            holder.ll.setVisibility(View.VISIBLE);
            holder.viewBackground.setVisibility(View.VISIBLE);
            holder.viewForeground.setVisibility(View.VISIBLE);
            holder.title.setText(model.getTitle());
            holder.timestamp.setText(model.getOwner());
            Log.e("Note", "bindviewholder");
        }

    }


    @Override
    public int getItemCount() {
        return super.getItemCount() ;
    }

    @NonNull
    @Override
    public NoteAdapter.NoteHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_row, parent, false);
        Log.e("Note", "createviewholder");
        return new NoteAdapter.NoteHolder(v, listener);
    }

    public void setQuery(String s){
        query = s;
    }


    class NoteHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        TextView title, timestamp;
        LinearLayout ll;
        //CardView c;
        Context context;
        RelativeLayout viewForeground;
        RelativeLayout viewBackground;

        private WeakReference<NoteAdapter.ClickListener> listenerRef;

        public NoteHolder(View v, NoteAdapter.ClickListener listener){
            super(v);
            listenerRef = new WeakReference<>(listener);
            v.setOnClickListener(this);
            context = itemView.getContext();
            title = v.findViewById(R.id.textView_title);
            timestamp = v.findViewById(R.id.textView_time);
            ll = v.findViewById(R.id.ll_book_row);

            viewBackground = v.findViewById(R.id.view_background);
            viewForeground = v.findViewById(R.id.view_foreground);
            Log.e("Note", "holder constr");


        }

        @Override
        public void onClick(View v) {

            ModelNotes model = getItem(getAdapterPosition());
            Log.e("hihih", model.getTitle());
            final Intent intent;
            intent =  new Intent(context, ReadNotes.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.putExtra("Note", model.getTitle());
            intent.putExtra("by", model.getOwner());
            intent.putExtra("source", source);

            if(!bookn.equals("")) {
                intent.putExtra("notebook", bookn);
            }else{
                intent.putExtra("notebook", model.getOwner());
            }
            context.startActivity(intent);
        }


    }

    public void deleteItem(int position) {

        final ModelNotes ticket = getItem(position);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("User")
                .document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .collection("Notes").document(ticket.getTitle()).delete();

        db.collection("Notebooks")
                .document(ticket.getOwner())
                .collection("Notes")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //check if colleciton is empty
                if(queryDocumentSnapshots.size()<=1){
                    db.collection("Notebooks")
                            .document(ticket.getOwner())
                            .delete();
                }else{
                    db.collection("Notebooks")
                            .document(ticket.getOwner())
                            .collection("Notes")
                            .document(ticket.getTitle()).delete();
                }
            }
        });






        StorageReference photoRef = FirebaseStorage.getInstance().getReference()
                .child(ticket.getOwner()).child(ticket.getTitle());

        if(photoRef!=null) {
            photoRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference item : listResult.getItems()) {
                        // All the items under listRef.
                        item.delete();
                    }
                }
            });
        }
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }


    public interface ClickListener {
        void onPositionClicked(int position);
    }



}
