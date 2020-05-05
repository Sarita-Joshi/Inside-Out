package com.sarita.insideout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Help_fragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{


    TextView tv;
    FirebaseFirestore db;
    CollectionReference ModelNotesRef;
    NoteAdapter adapter;
    RecyclerView recyclerView;


    public Help_fragment()
    {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         final View v=inflater.inflate(R.layout.fragment_help_fragment,container,false);

        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
        recyclerView = v.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        setUpRecyclerView();

        return v;
    }

    private void setUpRecyclerView() {
        Log.e("My notes", "setting up recycler view");
        Query query =
                db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .collection("Notes");


        FirestoreRecyclerOptions<ModelNotes> options = new FirestoreRecyclerOptions.Builder<ModelNotes>()
                .setQuery(query, ModelNotes.class)
                .build();

        adapter = new NoteAdapter(options, "", "help", new NoteAdapter.ClickListener() {
            @Override public void onPositionClicked(int position) {
                // callback performed on click
            }
        });

        recyclerView.setAdapter(adapter);

    }

        @Override
    public void onStart() {
        Log.e("ModelBooks", "on start");
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        Log.e("ModelBooks", "on stop");
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        adapter.deleteItem(viewHolder.getAdapterPosition());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_home, menu);
        MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.setQuery(s);
                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.setQuery(s);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

    //handle option clicks

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_add: {

                startActivity(new Intent(getActivity(), UploadImage.class));
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


}
