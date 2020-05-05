package com.sarita.insideout;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarita.insideout.EnterNumberFragment;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       // getSupportActionBar().hide();
        getSupportActionBar().hide();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //start prof

            FirebaseFirestore.getInstance().collection("User")
                    .document(firebaseAuth.getCurrentUser().getPhoneNumber()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            User.createUser(doc.get("name").toString(), doc.get("email").toString(), doc.get("password").toString(), doc.get("image").toString(), doc.getId());
                        }
                    });


            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }


        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.main_frame_1, new EnterNumberFragment(), "");
        ft1.commit();


    }

}