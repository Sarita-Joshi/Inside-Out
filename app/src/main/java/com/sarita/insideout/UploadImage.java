package com.sarita.insideout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadImage extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;

    private String book_name, note_title;

    private EditText notebookName, title, content;
    private TextView empty_tv;
    private Button mUploadBtn;
    private Spinner s;
    private RecyclerView mUploadList;

    private List<String> fileNameList;
    private List<String> fileDoneList;
    private List<Uri> filePreviewList;
    final List<String> books=new ArrayList<>();

    private UploadListAdapter uploadListAdapter;

    private StorageReference mStorage;
    private  FirebaseFirestore db;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        mStorage = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(UploadImage.this);

        empty_tv = findViewById(R.id.empty_view);
        notebookName = findViewById(R.id.notebookName);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        mUploadBtn = (Button) findViewById(R.id.upload_btn);
        s = findViewById(R.id.book_spinner);
        mUploadList = (RecyclerView) findViewById(R.id.upload_list);

        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();
        filePreviewList = new ArrayList<>();

        uploadListAdapter = new UploadListAdapter(fileNameList, fileDoneList, filePreviewList, UploadImage.this);


        books.add("New Notebook");
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(UploadImage.this,R.layout.spinner_item,books);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        db.collection("Notebooks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document:task.getResult())
                    {
                        books.add(document.getId());
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                book_name=s.getSelectedItem().toString();
                mUploadBtn.setEnabled(true);
                if(s.getSelectedItem().toString().equals("New Notebook")){
                    notebookName.setVisibility(View.VISIBLE);
                }else{
                    notebookName.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Toast.makeText(getActivity(),"Fail",Toast.LENGTH_SHORT).show();
                    mUploadBtn.setEnabled(false);
                return;

            }
        });

        //RecyclerView
        mUploadList.setLayoutManager(new LinearLayoutManager(this));
        mUploadList.setHasFixedSize(true);
        mUploadList.setAdapter(uploadListAdapter);

        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });


    }

    private void choose(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);

    }

    private synchronized void upload() {

        if( TextUtils.isEmpty(title.getText())) {
            title.setError("title is required!");
            return ;
        }
        if( TextUtils.isEmpty(content.getText())) {
            content.setError("content is required!");
            return ;
        }
        if(notebookName.getVisibility()==View.VISIBLE && TextUtils.isEmpty(notebookName.getText())){
            notebookName.setError("name is required!");
            return ;
        }

        if(books.contains(notebookName.getText().toString())){
            notebookName.setError("Book Already exists!");
            return ;
        }

        if(notebookName.getVisibility()==View.VISIBLE ){
            book_name = notebookName.getText().toString();
        }

            db.collection("Notebooks").document(s.getSelectedItem().toString()).
                    collection("Notes").document(title.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot snapShot = task.getResult();
                    if (snapShot.exists() && snapShot.get("title").toString().equalsIgnoreCase(title.getText().toString())){
                        Log.e("loga", s.getSelectedItem().toString() + title.getText().toString() + snapShot.getId());
                        title.setError("Note title already exists");
                        return ;
                    }else{

                        progressDialog.setMessage("Uploading your note, please wait.");
                        progressDialog.show();


                        final Map<String, Object> map = new HashMap<>();
                        map.put("title", title.getText().toString());
                        map.put("description",content.getText().toString());
                        map.put("timestamp", FieldValue.serverTimestamp());
                        map.put("media", fileNameList);
                        map.put("owner", User.currentUser.getName());


                        if(notebookName.getVisibility()==View.VISIBLE){

                            Map<String, Object> map1 = new HashMap<>();
                            map1.put("title", book_name);
                            map1.put("timestamp", FieldValue.serverTimestamp());
                            map1.put("owner", User.currentUser.getName());
                            db.collection("Notebooks").document(book_name).set(map1)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.e("hihihihihih", "done");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("hihihihihih", "done");
                                }
                            });

                        }




                        // add entry to firestore notebooks
                        db.collection("Notebooks").document(book_name).collection("Notes").
                                document(title.getText().toString()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //add to user
                                map.remove("owner");
                                map.put("owner", book_name);

                                db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                        .collection("Notes").document(title.getText().toString()).set(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                int i;
                                                for (i = 0; i < filePreviewList.size(); i++) {

                                                    StorageReference fileToUpload = mStorage.child(book_name).child(title.getText().toString()).child(fileNameList.get(i));

                                                    final int finalI = i;
                                                    fileToUpload.putFile(filePreviewList.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                                                            fileDoneList.remove(finalI);
//                                                            fileDoneList.add(finalI, "done");
                                                            uploadListAdapter.set(finalI);
                                                            uploadListAdapter.notifyDataSetChanged();

                                                        }
                                                    });

                                                }
                                                            progressDialog.dismiss();

                                                try {
                                                    Thread.sleep(1000);
                                                }catch (Exception e){

                                                }

                                                    Toast.makeText(UploadImage.this, "note uploaded", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                    startActivity(new Intent(UploadImage.this, MainActivity.class));


                                            }
                                        });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UploadImage.this, "Error!", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }
            });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        empty_tv.setVisibility(View.GONE);
        mUploadList.setVisibility(View.VISIBLE);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

            if (data.getClipData() != null) {

                int totalItemsSelected = data.getClipData().getItemCount();

                for (int i = 0; i < totalItemsSelected; i++) {

                    Uri fileUri = data.getClipData().getItemAt(i).getUri();

                    String fileName = getFileName(fileUri);

                    fileNameList.add(fileName);
                    fileDoneList.add("uploading");
                    filePreviewList.add(fileUri);
                    uploadListAdapter.notifyDataSetChanged();
                }

                //Toast.makeText(MainActivity.this, "Selected Multiple Files", Toast.LENGTH_SHORT).show();

            } else if (data.getData() != null) {

                Toast.makeText(UploadImage.this, "Selected Single File", Toast.LENGTH_SHORT).show();
                Uri fileUri = data.getData();
                String fileName = getFileName(fileUri);

                fileNameList.add(fileName);
                fileDoneList.add("uploading");
                filePreviewList.add(fileUri);
                uploadListAdapter.notifyDataSetChanged();

            }else{
                empty_tv.setVisibility(View.VISIBLE);
                mUploadList.setVisibility(View.GONE);
            }

        }

    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attach, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_attach:
                choose();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}


