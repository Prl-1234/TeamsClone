package com.example.teamsclone.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamsclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {
    ImageView image;
    TextView name,email;
    String email_id;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    /**
     * To see profile of other users--
     * it shows email, name, user profile image in detail.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        image=findViewById(R.id.user_image);
        name=findViewById(R.id.user_name);
        email=findViewById(R.id.user_email);
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        email_id =getIntent().getStringExtra("email");
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if ((documentSnapshot.getString("email").equals(email_id))) {
                            name.setText(documentSnapshot.getString("name"));
                            email.setText(documentSnapshot.getString("email"));
                            Picasso.get().load(Uri.parse(documentSnapshot.getString("photo"))).into(image);
                            break;
                        }
                    }
                }
        }
        });



    }
}
