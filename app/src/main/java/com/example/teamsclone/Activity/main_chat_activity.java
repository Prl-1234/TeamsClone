package com.example.teamsclone.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamsclone.Adapter.Main_chat_adapter;
import com.example.teamsclone.MainChatModel;
import com.example.teamsclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class main_chat_activity extends AppCompatActivity {
    ImageView back,send,block,unblock;
    TextView name;
    ListView chat_view;
    String email, myemail;
    EditText write_msg;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Main_chat_adapter mchatadapter2;
    ImageView user_image;
    ArrayList<MainChatModel> chat_ada2;
    boolean isBlocked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_chat_activity);
        db=FirebaseFirestore.getInstance();
        block=(ImageView) findViewById(R.id.block_chat);
        back=(ImageView) findViewById(R.id.back_from_chat);
        user_image=(ImageView) findViewById(R.id.user_image);
        unblock=(ImageView) findViewById(R.id.unblock_chat);
        block.setVisibility(View.GONE);
        unblock.setVisibility(View.GONE);
        isBlocked=false;

        // Go to welcome activity
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(main_chat_activity.this, WelcomeActivity.class);
                startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
            }
        });
        name=(TextView) findViewById(R.id.name_chat_act);
        email=getIntent().getStringExtra("email");
        send=(ImageView) findViewById(R.id.send_msg);
        send.setVisibility(View.GONE);
        mAuth=FirebaseAuth.getInstance();
        chat_view=(ListView) findViewById(R.id.chat_personal);
        chat_ada2=new ArrayList<>();
        write_msg=(EditText) findViewById(R.id.write_msg);

        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                myemail=(task.getResult().getString("email"));
                SetUpInitialChat();
                SetUpInitialDetails();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(main_chat_activity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(write_msg.getText().toString().length()>0){
                    final Map<String,String> one=new HashMap<>();
                    one.put(myemail,write_msg.getText().toString());
                    String msg=write_msg.getText().toString();
                    final String time=Timestamp.now().toString();
                     final ArrayList<Map<String,String>> chat =new ArrayList<>();
                    db.collection("chats")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        int x=0;
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                            if((documentSnapshot.getString("us1").equals(email)&&
                                                                    documentSnapshot.getString("us2").equals(myemail)
                                                    )){
                                                x=1;
                                                Map<String, Object> chat_document = new HashMap<>();
                                                chat_document.put("talk", msg);
                                                chat_document.put("user", myemail);
                                                db.collection("chats")
                                                        .document( email+ myemail)
                                                        .update("time",time);
                                                db.collection("chats")
                                                        .document(email+ myemail)
                                                        .collection("all chats")
                                                        .document(time)
                                                        .set(chat_document);
                                                break;
                                            }
                                        }
                                        if(x==0){
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                if((documentSnapshot.getString("us2").equals(email)&&
                                                        documentSnapshot.getString("us1").equals(myemail)
                                                )){
                                                    x=1;
                                                    Map<String, Object> chat_document = new HashMap<>();
                                                    chat_document.put("talk", msg);
                                                    chat_document.put("user", myemail);
                                                    db.collection("chats")
                                                            .document( myemail+email)
                                                            .update("time",time);
                                                    db.collection("chats")
                                                            .document( myemail+email)
                                                            .collection("all chats")
                                                            .document(time)
                                                            .set(chat_document);
                                                    break;
                                                }
                                            }
                                        }
                                        if(x==0){
                                            final String[] myname = new String[1];
                                            final String[] othername = new String[1];
                                            final String[] p1 = new String[1];
                                            final String[] p2 = new String[1];
                                            db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    myname[0] =task.getResult().getString("name");
                                                    p2[0]=task.getResult().getString("photo");
                                                    db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            QuerySnapshot querySnapshot=task.getResult();
                                                            for(DocumentSnapshot documentSnapshot:querySnapshot){
                                                                if(documentSnapshot.getString("email").equals(email)){
                                                                    othername[0] =documentSnapshot.getString("name");
                                                                    p1[0]=documentSnapshot.getString("photo");
                                                                    Map<String, Object> two = new HashMap<>();
                                                                    two.put("us1", email);
                                                                    two.put("us2", myemail);
                                                                    two.put("name2", myname[0]);
                                                                    two.put("name1", othername[0]);
                                                                    two.put("p2", p2[0]);
                                                                    two.put("p1", p1[0]);
                                                                    two.put("time",time);
                                                                    two.put("block by 1",false);
                                                                    two.put("block by 2",false);
                                                                    db.collection("chats").document(email+ myemail).set(two)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Map<String, Object> chat_document = new HashMap<>();
                                                                                    chat_document.put("talk", msg);
                                                                                    chat_document.put("user", myemail);
                                                                                    db.collection("chats")
                                                                                            .document(email+ myemail)
                                                                                            .collection("all chats")
                                                                                            .document(time)
                                                                                            .set(chat_document).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            SetUpInitialChat();
                                                                                        }
                                                                                    });
                                                                                }
                                                                            });

                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    });

                                                }
                                            });


                                        }

                                    } else {
                                        //Log.w(TAG, "Error getting documents.", task.getException());
                                    }
                                }
                            });
                    write_msg.setText("");
                }
            }
        });

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int x=0;
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if ((documentSnapshot.getString("us1").equals(email) &&
                                        documentSnapshot.getString("us2").equals(myemail)
                                )) {
                                    db.collection("chats")
                                        .document(email+myemail)
                                        .update("block by 2",true);
                                    SettingVisibilityOfBlock();

                                    x=1;

                                    break;
                                }
                            }
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if ((documentSnapshot.getString("us2").equals(email) &&
                                        documentSnapshot.getString("us1").equals(myemail)
                                )) {

                                    db.collection("chats")
                                            .document(myemail+email)
                                            .update("block by 1",true);
                                    SettingVisibilityOfBlock();
                                    x=1;

                                    break;
                                }
                            }
                        }
                    }
                });
            }
        });
        unblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int x=0;
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if ((documentSnapshot.getString("us1").equals(email) &&
                                        documentSnapshot.getString("us2").equals(myemail)
                                )) {
                                    db.collection("chats")
                                            .document(email+myemail)
                                            .update("block by 2",false);
                                    SettingVisibilityOfUnblock();
                                    x=1;
                                    break;
                                }
                            }
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if ((documentSnapshot.getString("us2").equals(email) &&
                                        documentSnapshot.getString("us1").equals(myemail)
                                )) {

                                    db.collection("chats")
                                            .document(myemail+email)
                                            .update("block by 1",false);
                                    SettingVisibilityOfUnblock();
                                    x=1;
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    private void SetUpInitialDetails(){
        db.collection("chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int x=0;
                    // Toast.makeText(main_chat_activity.this, myemail+email, Toast.LENGTH_SHORT).show();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if ((documentSnapshot.getString("us1").equals(email) &&
                                documentSnapshot.getString("us2").equals(myemail)
                        )) {
                            String name_f=documentSnapshot.getString("name1");
                            name.setText(name_f);
                            Picasso.get().load(Uri.parse(documentSnapshot.getString("p1"))).into(user_image);
                            if((!documentSnapshot.getBoolean("block by 1"))&&(!documentSnapshot.getBoolean("block by 2"))){
                                send.setVisibility(View.VISIBLE);
                            }
                            if(documentSnapshot.getBoolean("block by 1")){
                                isBlocked=true;
                            }
                            if(documentSnapshot.getBoolean("block by 2")){
                                unblock.setVisibility(View.VISIBLE);
                                block.setVisibility(View.GONE);
                            }
                            else{
                                unblock.setVisibility(View.GONE);
                                block.setVisibility(View.VISIBLE);
                            }
                            x=1;
                            name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    IntentToUserProfile(email);
                                }
                            });

                            break;
                        }
                    }

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if ((documentSnapshot.getString("us2").equals(email) &&
                                documentSnapshot.getString("us1").equals(myemail)
                        )) {
                            String name_f=documentSnapshot.getString("name2");
                            name.setText(name_f);
                            Picasso.get().load(Uri.parse(documentSnapshot.getString("p2"))).into(user_image);
                            if((!documentSnapshot.getBoolean("block by 2"))&&(!documentSnapshot.getBoolean("block by 1"))){
                                send.setVisibility(View.VISIBLE);
                            }
                            if(documentSnapshot.getBoolean("block by 2")){
                                isBlocked=true;
                            }
                            if(documentSnapshot.getBoolean("block by 1")){
                                unblock.setVisibility(View.VISIBLE);
                                block.setVisibility(View.GONE);
                            }
                            else{
                                unblock.setVisibility(View.GONE);
                                block.setVisibility(View.VISIBLE);
                            }
                            x=1;
                            name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    IntentToUserProfile(email);
                                }
                            });

                            break;
                        }
                    }

                    if(x==0){
                        name.setText(email);
                        unblock.setVisibility(View.GONE);
                        send.setVisibility(View.VISIBLE);
                        block.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    private void SetUpInitialChat() {
        db.collection("chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int x=0;
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if ((documentSnapshot.getString("us1").equals(email) &&
                                documentSnapshot.getString("us2").equals(myemail)
                        )) {
                            db.collection("chats").document(email+myemail).collection("all chats").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    for(DocumentChange dc:value.getDocumentChanges()){
                                        if(dc.getType()==DocumentChange.Type.ADDED){
                                            chat_ada2.add(dc.getDocument().toObject(MainChatModel.class));
                                        }
                                    }
                                    mchatadapter2.notifyDataSetChanged();
                                }
                            });
                            mchatadapter2=new Main_chat_adapter(main_chat_activity.this,chat_ada2);
                            mchatadapter2.notifyDataSetChanged();
                            chat_view.setAdapter(mchatadapter2);
                            mchatadapter2.notifyDataSetChanged();
                            break;

                        }
                    }
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if ((documentSnapshot.getString("us2").equals(email) &&
                                documentSnapshot.getString("us1").equals(myemail)
                        )) {
                            x=1;
                            db.collection("chats").document(myemail+email).collection("all chats").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    for(DocumentChange dc:value.getDocumentChanges()){
                                        if(dc.getType()==DocumentChange.Type.ADDED){
                                            chat_ada2.add(dc.getDocument().toObject(MainChatModel.class));
                                        }
                                    }
                                    mchatadapter2.notifyDataSetChanged();
                                }
                            });
                            mchatadapter2=new Main_chat_adapter(main_chat_activity.this,chat_ada2);
                            mchatadapter2.notifyDataSetChanged();
                            chat_view.setAdapter(mchatadapter2);
                            mchatadapter2.notifyDataSetChanged();
                            break;

                        }
                    }

                }
            }
        });
    }

    private void IntentToUserProfile(String emailid){
        Intent intent=new Intent(main_chat_activity.this,UserProfileActivity.class);
        intent.putExtra("email",emailid);
        startActivity(intent);
    }

    private void SettingVisibilityOfBlock(){
        block.setVisibility(View.GONE);
        unblock.setVisibility(View.VISIBLE);
        send.setVisibility(View.GONE);

    }

    private void SettingVisibilityOfUnblock(){
        unblock.setVisibility(View.GONE);
        block.setVisibility(View.VISIBLE);
        if(!isBlocked){
            send.setVisibility(View.VISIBLE);
        }
    }
}
