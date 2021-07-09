package com.example.teamsclone;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Main_chat_adapter_2 extends ArrayAdapter<MainChatModel> {
    private LayoutInflater mInflater;
    private int layoutResource;
    FirebaseFirestore db;
    private Context mContext;

    public Main_chat_adapter_2(@NonNull Context context, @NonNull ArrayList<MainChatModel> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView=((Activity)getContext()).getLayoutInflater().inflate(R.layout.chat_personal,parent,false);
        }

        final TextView one_chat=(TextView) convertView.findViewById(R.id.chat_personal);

        String talk=getItem(position).getTalk();
        one_chat.setText(talk);
        db=FirebaseFirestore.getInstance();
       db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               String myemail ;
               myemail =task.getResult().getString("email");
               if(getItem(position).getUser().equals(myemail)){
                   one_chat.setBackgroundResource(R.color.white);
                   one_chat.setGravity(Gravity.RIGHT);
               }
               else{
                   one_chat.setBackgroundResource(R.color.lightPurple);
                   one_chat.setGravity(Gravity.LEFT);

               }
           }
       });


        return convertView;

    }
}
