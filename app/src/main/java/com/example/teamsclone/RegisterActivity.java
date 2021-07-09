package com.example.teamsclone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private Button signUpBtn;
    private EditText mEmail;
    private EditText mUsername;
    private EditText mpassword;
    private String Email,cpi;
    private String Username;
    private String Password;
    private StorageReference storage;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        signUpBtn=(Button) findViewById(R.id.signUpbtn);
        mEmail=(EditText) findViewById(R.id.email);
        mUsername=(EditText) findViewById(R.id.name);
        mpassword=(EditText) findViewById(R.id.password);
        storage = FirebaseStorage.getInstance().getReference();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Email=mEmail.getText().toString();
                Password=mpassword.getText().toString();
                Username=mUsername.getText().toString();
                    setUpFirebase(Email,Password,Username);

            }
        });
    }


    private void setUpFirebase(final String email, final   String password, final   String name){
        if(email.equals("")||password.equals("")){
            Toast.makeText(RegisterActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
        }
        else{
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser new_user = mAuth.getCurrentUser();
                                if(new_user!=null){
                                    final String user_id=new_user.getUid();
                                    new_user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        final StorageReference reference=storage.child("profile.png");
                                                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                Map<String, Object> user = new HashMap<>();
                                                                user.put("photo",uri.toString());
                                                                user.put("email", email);
                                                                user.put("name", name);
                                                                user.put("user_id",user_id);
                                                                db.collection("users").document(user_id).set(user);
                                                            }
                                                        });

                                                    }
                                                    else{
                                                        Toast.makeText(RegisterActivity.this, "Could Not send verification Link", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                                Toast.makeText(RegisterActivity.this, "Sending Verification link", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterActivity.this, "Failed Authentication", Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }


}