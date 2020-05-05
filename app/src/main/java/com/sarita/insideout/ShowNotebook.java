package com.sarita.insideout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ShowNotebook extends AppCompatActivity {


    TextView tv;
    FirebaseFirestore db;
    CollectionReference ModelNotesRef;
    NoteAdapter adapter;
    RecyclerView recyclerView;



    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notebook);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        name = extras.getString("Notebook");

        ActionBar ab = getSupportActionBar();
        ab.setTitle(name);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.rv);


        Log.e("nonono", name);


           setUpRecyclerView(name);
    }

    private void setUpRecyclerView(String name) {
        Log.e("ModelBooks", "setting up recycler view");
        Query query =
                db.collection("Notebooks").document(name).collection("Notes");

        FirestoreRecyclerOptions<ModelNotes> options = new FirestoreRecyclerOptions.Builder<ModelNotes>()
                .setQuery(query, ModelNotes.class)
                .build();

        adapter = new NoteAdapter(options, name, "home", new NoteAdapter.ClickListener() {
            @Override public void onPositionClicked(int position) {
                // callback performed on click
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_add).setVisible(false);
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
