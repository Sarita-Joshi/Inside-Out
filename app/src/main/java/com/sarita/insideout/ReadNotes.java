package com.sarita.insideout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadNotes extends AppCompatActivity {

    private String book_name, note_title;

    private String src,name,by,bookn;

    private TextView notebookName, title, content;

    private RecyclerView recyclerView;

    private List<String> fileNameList;

    private UploadListAdapter uploadListAdapter;

    private StorageReference mStorage;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_notes);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
         name = extras.getString("Note");
         by = extras.getString("by");
         bookn = extras.getString("notebook");
        src = extras.getString("source");

        ActionBar ab = getSupportActionBar();
        ab.setTitle(bookn);
        ab.setSubtitle(by);


        mStorage = FirebaseStorage.getInstance().getReference();
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);

        fileNameList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference docref=db.collection("Notebooks").document(bookn).collection("Notes")
                .document(name);
        Source source= Source.CACHE;

        docref.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {

                    DocumentSnapshot document=task.getResult();
                    if(document.exists())
                    {


                        title.setText("Title: " + name);
                        content.setText(document.getString("description"));
                        List<String> list = (List<String>) document.get("media");

                        if(list!=null) {
                            //set image rv
                            recyclerView = findViewById(R.id.rv);
                            ImageAdapter adapter = new ImageAdapter(ReadNotes.this, list, bookn, name);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ReadNotes.this));
                            recyclerView.setAdapter(adapter);
                        }

                    }
                }
                else
                {

                    title.setText(name);
                    content.setText("Fetching from db");
                }
            }
        });
    }


    private void edit() {

        final Button b = findViewById(R.id.edit_btn);
        final EditText e = findViewById(R.id.edit_tv);

        e.setText(content.getText().toString());
        content.setVisibility(View.GONE);
        e.setVisibility(View.VISIBLE);
        b.setVisibility(View.VISIBLE);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> data = new HashMap<>();
                data.put("description", e.getText().toString());

                db.collection("Notebooks").document(bookn).collection("Notes").document(name)
                        .set(data, SetOptions.merge());

                db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                        .collection("Notes").document(name)
                        .set(data, SetOptions.merge());

                content.setText(e.getText().toString());
                e.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                b.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        if(src.equals("home")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_edit:
                edit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
