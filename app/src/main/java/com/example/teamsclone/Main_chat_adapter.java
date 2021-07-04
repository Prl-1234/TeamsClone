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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Main_chat_adapter extends ArrayAdapter<Map<String,String>> {
    private LayoutInflater mInflater;
    private int layoutResource;
    FirebaseFirestore db;
    private Context mContext;

    public Main_chat_adapter(@NonNull Context context, @NonNull ArrayList<Map<String,String>> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView=((Activity)getContext()).getLayoutInflater().inflate(R.layout.chat_personal,parent,false);
        }

        final TextView one_chat=(TextView) convertView.findViewById(R.id.chat_personal);
        Map<String,String> main_chat=getItem(position);
        Iterator myVeryOwnIterator = main_chat.keySet().iterator();
        while(myVeryOwnIterator.hasNext()) {
            final String key=(String)myVeryOwnIterator.next();
            final String[] mynumber = new String[1];
            db=FirebaseFirestore.getInstance();
            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getString("user_id").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        mynumber[0] =document.getString("email");
                                        if(key.equals(mynumber[0])){
                                            one_chat.setBackgroundResource(R.color.white);
                                            one_chat.setGravity(Gravity.RIGHT);
                                        }
                                        else{
                                            one_chat.setBackgroundResource(R.color.lightPurple);
                                            one_chat.setGravity(Gravity.LEFT);
                                        }
                                        break;
                                    }
                                }
                            } else {

                            }
                        }
                    });


            String value=(String)main_chat.get(key);
            one_chat.setText(value);

        }
        return convertView;

      //  one_chat.setText(main_chat.get(main_chat.keySet()));
    }
}
