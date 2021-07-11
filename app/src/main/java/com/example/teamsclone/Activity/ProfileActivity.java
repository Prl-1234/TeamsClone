package com.example.teamsclone.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamsclone.Login.LoginActivity;
import com.example.teamsclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ImageView photo,logout;
    private Button save;
    private TextView change_photo;
    private TextInputEditText name;
    private static final int IMG_REQUEST_ID=10;
    private FirebaseAuth mAuth;
    private String myemail;
    private FirebaseFirestore db;
    private StorageReference storage;
    private Uri uri;

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth=FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance().getReference();
        db=FirebaseFirestore.getInstance();
        initUi();
        setUpInitialDetails();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(mAuth.getCurrentUser().getUid()).update("name",name.getText().toString());
                Toast.makeText(ProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();

            }
        });
        change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_change();
            }
        });
    }

    private void setUpInitialDetails() {
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                name.setText(task.getResult().getString("name"));
                myemail=task.getResult().getString("email");
                Picasso.get().load(Uri.parse(task.getResult().getString("photo"))).into(photo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUi() {
        photo=findViewById(R.id.photo);
        change_photo=findViewById(R.id.cngphoto);
        name=findViewById(R.id.name);
        logout=findViewById(R.id.logout);
        save=findViewById(R.id.save);
    }
    private void image_change(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),IMG_REQUEST_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQUEST_ID&&resultCode==RESULT_OK
                &&data!=null
                &&data.getData()!=null){
            uri=data.getData();
            Picasso.get().load(uri).into(photo);
            photo.setImageURI(uri);
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();
            final StorageReference reference=storage.child("pictures").child(mAuth.getCurrentUser().getUid().toString());
            try{
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        change_photo.setVisibility(View.VISIBLE);
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final Uri firebaseUri = uri;
                                db.collection("users").document(mAuth.getCurrentUser().getUid()).update("photo", firebaseUri.toString());
                                db.collection("chats")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    int x=0;
                                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                        if((documentSnapshot.getString("us2").equals(myemail))){
                                                            x=1;
                                                            db.collection("chats").document(documentSnapshot.getId()).update("p2",firebaseUri.toString());
                                                            break;
                                                        }
                                                    }
                                                    x=0;
                                                    if(x==0){
                                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                            if((documentSnapshot.getString("us1").equals(myemail))){
                                                                x=1;
                                                                db.collection("chats").document(documentSnapshot.getId()).update("p1",firebaseUri.toString());
                                                                break;
                                                            }
                                                        }
                                                    }

                                                }
                                            }
                                        });
                                Toast.makeText(ProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (Exception e){
            }

        }
    }
}
