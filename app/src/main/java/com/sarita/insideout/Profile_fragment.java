package com.sarita.insideout;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class Profile_fragment extends Fragment {

    private static final int RESULT_LOAD_IMAGE = 1;

    FirebaseFirestore db;
    DocumentReference docref;
    ImageView iv;

    private StorageReference storageRef;

    public Profile_fragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v=inflater.inflate(R.layout.fragment_profile_fragment,container,false);

        storageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        docref =db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());


        Button btn = v.findViewById(R.id.next_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        iv = v.findViewById(R.id.profile_picture);
        ImageButton im = v.findViewById(R.id.edit_profile_picture);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose();
            }
        });


        Source source= Source.CACHE;
        docref.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {

                    DocumentSnapshot document=task.getResult();
                    if(document.exists())
                    {
                        TextView name=(TextView)v.findViewById(R.id.name_value);
                        TextView email=(TextView)v.findViewById(R.id.email_value);
                        TextView phone=(TextView)v.findViewById(R.id.phone_value);
                        name.setText(document.getString("name"));
                        email.setText(document.getString("email"));
                        phone.setText(document.getId());
                        if(document.get("image")!=null){
                            loadFile(document.get("image").toString());
                        }
                    }
                }
                else
                {
                    TextView name=(TextView)v.findViewById(R.id.name_value);
                    TextView email=(TextView)v.findViewById(R.id.email_value);
                    TextView phone=(TextView)v.findViewById(R.id.phone_value);
                    name.setText("Fetching from db");
                    email.setText("Fetching from db");
                    phone.setText("Fetching from db");


                }
            }
        });

        return v;
    }

    private void choose() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK) {

            if (data.getData() != null) {

                Toast.makeText(getActivity(), "Selected one File", Toast.LENGTH_SHORT).show();
                Uri fileUri = data.getData();
                final String fileName = getFileName(fileUri);

                //delete current dp from storage
                if(User.currentUser.getImage()!=null){
                    Log.e("image", User.currentUser.getImage());
                    storageRef.child("images").child(User.currentUser.getImage()).delete();
                    User.currentUser.setImage(fileName);
                }

                //upload to firebase
                Map<String, Object> dp = new HashMap<>();
                dp.put("image", fileName.toString());
                docref.set(dp, SetOptions.merge());

                Log.e("hihi", "uploaded to fb");

                //upload to storage
                StorageReference fileToUpload = storageRef.child("images").child(fileName);

                fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       loadFile(fileName);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Profile picture not updated.", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }


    private void loadFile(String image){

        storageRef.child("images").child(image).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        Picasso.get()
                                .load(uri)
                                .into(iv);
                    }
                });
    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
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



    public void logout()
    {
        FirebaseAuth firebaseAuth;
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    //Do anything here which needs to be done after signout is complete
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                else {
                }
            }
        };

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseAuth.signOut();
    }




}
