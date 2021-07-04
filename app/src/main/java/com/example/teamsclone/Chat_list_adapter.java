package com.example.teamsclone;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Chat_list_adapter extends ArrayAdapter<Pair<String,String>> {
    public Chat_list_adapter(@NonNull Context context, @NonNull List<Pair<String,String>> objects) {
        super(context, 0, objects);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView=((Activity)getContext()).getLayoutInflater().inflate(R.layout.one_chat,parent,false);
        }
        ImageView user_image=(ImageView) convertView.findViewById(R.id.user_image);
        TextView one_chat=(TextView) convertView.findViewById(R.id.one_chat);

      //  String main_chat=getItem(position).first;
        one_chat.setText(getItem(position).first);
        Picasso.get().load(Uri.parse(getItem(position).second)).into(user_image);

        return convertView;

        //  one_chat.setText(main_chat.get(main_chat.keySet()));
    }
}