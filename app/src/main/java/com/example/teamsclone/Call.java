package com.example.teamsclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class Call extends Fragment {


    Button start_new_call,join_call,join_final_call;
    EditText enter_code;
    ImageView profile;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_call,container,false);
        start_new_call=(Button) view.findViewById(R.id.start_call);
        join_call=(Button) view.findViewById(R.id.join_call);
        join_final_call=(Button) view.findViewById(R.id.join_final_call);
        enter_code=(EditText) view.findViewById(R.id.enter_code);
        profile=(ImageView) view.findViewById(R.id.profile);
        join_final_call.setVisibility(View.GONE);
        enter_code.setVisibility(View.GONE);
        mAuth=FirebaseAuth.getInstance();
        start_new_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                join_final_call.setVisibility(View.GONE);
                enter_code.setVisibility(View.GONE);
                Intent intent=new Intent(getActivity(),StartCallActivity.class);
                intent.putExtra("channel", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
            }
        });
        join_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                join_final_call.setVisibility(View.VISIBLE);
                enter_code.setVisibility(View.VISIBLE);
            }
        });
        join_final_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enter_code.getText().toString().length()!=144||enter_code.getText().toString().charAt(28)!=' '){
                    Toast.makeText(getActivity(), "Enter Valid Code", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent=new Intent(getActivity(),JoinCallActivity.class);
                    intent.putExtra("channel code", enter_code.getText().toString());
                    startActivity(intent);
                }
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ProfileActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
