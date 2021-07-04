package com.example.teamsclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignOutActivity extends AppCompatActivity {
    private Button signout;
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_out);
        signout=(Button) findViewById(R.id.signoutbtn);
        mAuth=FirebaseAuth.getInstance();
        setUpFirebase();
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent=new Intent(SignOutActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void setUpFirebase(){
        mAuth=FirebaseAuth.getInstance();

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();

                if(user!=null){

                }
                else{
                    Intent intent=new Intent(SignOutActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            Intent intent=new Intent(SignOutActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener!=null){
            mAuth.addAuthStateListener(mAuthListener);
        }
    }
}
