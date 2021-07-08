package com.example.teamsclone;

import android.content.Intent;
import android.net.Uri;
import android.opengl.ETC1;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Chat extends Fragment {
    ListView listView;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ArrayList<String> no_list;
    ArrayList<Pair<String,String>> chat_list;
    ImageView search;
    EditText searchbar;
    String myemail;
    int x=0;
    Chat_list_adapter adapter;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_chat, container, false);
      //  final View view=inflater.inflate(R.layout.chat_fragment,container,false);
        db=FirebaseFirestore.getInstance();
        listView=(ListView) view.findViewById(R.id.chat_list);
        chat_list=new ArrayList<Pair<String,String>>();
        no_list=new ArrayList<>();
        mAuth=FirebaseAuth.getInstance();
        searchbar=(EditText) view.findViewById(R.id.search_text);
        search=(ImageView) view.findViewById(R.id.search);

        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                myemail=(task.getResult().getString("email"));
                adap();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    x=0;
                    final String number=searchbar.getText().toString();
                    //  Toast.makeText(MainActivity.this, "e"+number+mynumber, Toast.LENGTH_LONG).show();
                    if(!number.equals(myemail)&&number.length()!=0){
                      //  DocumentReference docIdRef = db.collection("users").document(number);
                        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull  Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot document = task.getResult();
                                    boolean exists=false;
                                    for(DocumentSnapshot documentSnapshot:document){
                                        if(documentSnapshot.getString("email").equals(number)){
                                            Intent intent=new Intent(getContext(),main_chat_activity.class);
                                            intent.putExtra("email",number);
                                            startActivity(intent);
                                            exists=true;
                                            break;
                                        }
                                    }
                                    if(!exists){
                                        Toast.makeText(getContext(), "User is not registered", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

            }
        });
        return view;
    }
    public void adap() {
        db.collection("chats").orderBy("time");
        db.collection("chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int x = 0;
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("us2").equals(myemail)) {
                            chat_list.add(new Pair <String,String> (document.getString("name1"), document.getString("p1")));
                            no_list.add(document.getString("us1"));
                            adapter = new Chat_list_adapter(getContext(), chat_list);
                            adapter.notifyDataSetChanged();
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    //String clk = (String) adapter.getItem(i);
                                    String clk=(String) no_list.get(i);
                                    Intent intent = new Intent(getContext(), main_chat_activity.class);
                                    intent.putExtra("email", clk);
                                    startActivity(intent);
                                }
                            });
                            adapter.notifyDataSetChanged();
                            //    mynumber = document.getString("name1");
                           // break;
                        }

                    }
                    x=0;
                    if(x==0){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("us1").equals(myemail)) {
                                chat_list.add(new Pair <String,String> (document.getString("name2"), document.getString("p2")));
                                no_list.add(document.getString("us2"));
                                adapter = new Chat_list_adapter(getContext(), chat_list);
                                adapter.notifyDataSetChanged();
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        //   String clk = (String) adapter.getItem(i);
                                        String clk=(String) no_list.get(i);
                                        Intent intent = new Intent(getContext(), main_chat_activity.class);
                                        intent.putExtra("email", clk);
                                        startActivity(intent);
                                    }
                                });
                                x = 1;
                                adapter.notifyDataSetChanged();
                                //   mynumber = document.getString("name1");
                              //  break;
                            }

                        }
                    }

                }
                else {

                }
            }
        });
    }
}
