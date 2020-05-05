

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
import android.widget.SearchView;
import android.widget.TextView;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;




public class Home_fragment extends Fragment {

    TextView tv;
    FirebaseFirestore db;
    CollectionReference ModelBooksRef;
    BookAdapter adapter;
    RecyclerView recyclerView;


    public Home_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_fragment, container, false);

        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.rv);
        setUpRecyclerView();

        return view;
    }

    private void setUpRecyclerView() {
        Log.e("ModelBooks", "setting up recycler view");
        Query query =
                db.collection("Notebooks");

        FirestoreRecyclerOptions<ModelBooks> options = new FirestoreRecyclerOptions.Builder<ModelBooks>()
                .setQuery(query, ModelBooks.class)
                .build();

        adapter = new BookAdapter(options, new BookAdapter.ClickListener() {
            @Override public void onPositionClicked(int position) {
            }
        }, getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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