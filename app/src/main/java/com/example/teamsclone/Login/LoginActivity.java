package com.example.teamsclone.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamsclone.R;
import com.example.teamsclone.Activity.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private Button signinBtn;
    private EditText mEmail;
    private EditText mpassword;
    private String Email;
    private String Password;
    private FirebaseAuth mAuth;
    private TextView register;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("login activity","login");
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        signinBtn=(Button) findViewById(R.id.signinbtn);
        mEmail=(EditText) findViewById(R.id.email);
        mpassword=(EditText) findViewById(R.id.password);
        register=(TextView) findViewById(R.id.create);
        if(mAuth.getCurrentUser()!=null&&mAuth.getCurrentUser().isEmailVerified()){
            Intent intent=new Intent(LoginActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Email=mEmail.getText().toString();
                Password=mpassword.getText().toString();
                setUpFirebase(Email,Password);
            }
        });
    }

    /**
     *
     *  Takes in input email and password from user
     *  and send user to WelcomeActivity
     *  if user is registered and verified.
     * @param email
     * @param password
     */
    private void setUpFirebase(String email,String password){
        if(email.equals("")||password.equals("")){
            Toast.makeText(LoginActivity.this, "Enter all fields", Toast.LENGTH_SHORT).show();
        }
        else{
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Failed To Authenticate", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser user=mAuth.getCurrentUser();
                            try {
                                if(user.isEmailVerified()){

                                    Intent intent=new Intent(LoginActivity.this,WelcomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Email is not verified", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                }
                            }catch (NullPointerException e){

                            }

                        }
                    }
                });
        }

    }

}