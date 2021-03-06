package com.example.teamsclone;

import android.content.Intent;
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

import com.example.teamsclone.Activity.main_chat_activity;
import com.example.teamsclone.Adapter.Chat_list_adapter;
import com.example.teamsclone.Adapter.Main_chat_adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

        //Getting my details and setting up chat users
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                myemail=(task.getResult().getString("email"));
                SetUpChatListOfUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });


        //Search if searched user has been registered on our app
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                x=0;
                final String number=searchbar.getText().toString();

                if(!number.equals(myemail)&&number.length()!=0){
                    db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull  Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot document = task.getResult();
                                boolean exists=false;
                                for(DocumentSnapshot documentSnapshot:document){
                                    if(documentSnapshot.getString("email").equals(number)){
                                        Intent intent=new Intent(getContext(), main_chat_activity.class);
                                        intent.putExtra("email",number);
                                        searchbar.setText("");
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

    /**
     * Checks if logged in user is user1
     * or user2 in firebase "chats" node
     * and sends appropriate result to setUpAdapter().
     */
    public void SetUpChatListOfUser() {
        db.collection("chats").orderBy("time").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentChange dc:value.getDocumentChanges()){
                    if(dc.getType()==DocumentChange.Type.ADDED){
                        if(dc.getDocument().getString("us1").equals(myemail)){
                            chat_list.add(new Pair <String,String> (dc.getDocument().getString("name2"), dc.getDocument().getString("p2")));
                            no_list.add(dc.getDocument().getString("us2"));

                        }
                        else if(dc.getDocument().getString("us2").equals(myemail)){
                            chat_list.add(new Pair <String,String> (dc.getDocument().getString("name1"), dc.getDocument().getString("p1")));
                            no_list.add(dc.getDocument().getString("us1"));

                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
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
    }


}
