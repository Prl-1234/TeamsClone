package com.example.teamsclone;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class StartCallActivity extends AppCompatActivity {
    private static final int PERMISSION_REQ_ID=22;
    private static final String[] REQUESTED_PERMISSIONS={
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private FrameLayout mLocalContainer;
    private SurfaceView mLocalView;
    private RelativeLayout mRemoteContainer;
    private SurfaceView mRemoteView;
    private RequestQueue mRequestQueue;
    private ImageView btn_call,btn_mute,btn_switch,video_off;
    private boolean mCallEnd;
    private boolean mMuted;
    private boolean mVideoOn;
    private String channelname,channelcode;
    private TextView copycode;
    private ClipboardManager clipboardManager;
    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler=new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora","success");
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora","User offline");
                    removeRemoteVideo();
                }
            });
        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed);
            if(state==Constants.REMOTE_VIDEO_STATE_STARTING){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("agora","Remote Video Starting");
                        setupRemoteVideo(uid);
                    }
                });
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startcall);
        mRequestQueue= Volley.newRequestQueue(StartCallActivity.this);
        Intent intent=getIntent();
        channelname=intent.getStringExtra("channel");
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        initui();
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)&&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            initEngineAndJoinChannel();
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!mCallEnd){
            leaveChannel();
        }
        RtcEngine.destroy();
    }
    public void onLocalAudioMuteClicked(View view){
        mMuted=!mMuted;
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res=mMuted?R.drawable.ic_mute:R.drawable.ic_turn_camera;
        btn_mute.setImageResource(res);
    }

    public void onVideoOffClicked(View view){
        mVideoOn=!mVideoOn;
        if(mVideoOn){
            mRtcEngine.disableVideo();
        }
        else{
            mRtcEngine.enableVideo();
        }
        int res=mVideoOn?R.drawable.ic_video_on:R.drawable.ic_video_off;
        video_off.setImageResource(res);
    }
    public void onSwitchCamera(View view) {
        mRtcEngine.switchCamera();
    }

    public void onCallClicked(View view) {
        if(mCallEnd){
            startCall();
            mCallEnd=false;
            btn_call.setImageResource(R.drawable.ic_endcall);
        }else{
            endCall();
            mCallEnd=true;
            btn_call.setImageResource(R.drawable.ic_pick);
        }
        showButtons(!mCallEnd);
    }
    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }
    private void startCall() {
        setupLocalVideo();
        joinChannel();
    }
    private void endCall() {
        removeLocalVideo();
        removeRemoteVideo();
        leaveChannel();
    }
    private void removeLocalVideo() {
        if(mLocalView!=null){
            mLocalContainer.removeView(mLocalView);
        }
        mLocalView=null;
    }
    private void showButtons(boolean show){
        int visibility=show?View.VISIBLE:View.GONE;
        btn_mute.setVisibility(visibility);
        btn_switch.setVisibility(visibility);
        video_off.setVisibility(visibility);
    }

    private void initui(){
        mLocalContainer=findViewById(R.id.local_video_view_container);
        mRemoteContainer=findViewById(R.id.remote_video_view_container);
        btn_call=findViewById(R.id.btn_call);
        btn_mute=findViewById(R.id.btn_mute);
        video_off=findViewById(R.id.video_off);
        btn_switch=findViewById(R.id.switch_camera);
        copycode=findViewById(R.id.copy_code);
        copycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clip = ClipData.newPlainText("code", channelcode);
                clipboardManager.setPrimaryClip(clip);
                Toast.makeText(StartCallActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
        //    Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }
    private void setUpVideoConfig(){
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }
    private void setupLocalVideo() {

        // Enable the video module.
        mRtcEngine.enableVideo();


        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        mLocalContainer.addView(mLocalView);
        // Set the local video view.
        VideoCanvas localVideoCanvas = new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(localVideoCanvas);
    }
    private void joinChannel() {

        // Join a channel with a token.
//        mRtcEngine.joinChannel("00624d90b7061a24ef28ff84d5947d46ad6IADLdHcPwaB+T6WM0rxWLZ6St477aJuw7trqYNTJdsy1kKDfQtYAAAAAEACqPfBqMijTYAEAAQAyKNNg",
//                "demo", "", 0);
        String url = "https://teamscloone.herokuapp.com/access_token?channel="+channelname;

        final JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token;
                    token =response.getString("token");
                    mRtcEngine.joinChannel(token,channelname, "", 0);
                    channelcode=channelname+" "+token;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mRequestQueue.add(request);
    }

    private void initEngineAndJoinChannel() {
        initializeEngine();
        setUpVideoConfig();
        setupLocalVideo();
        joinChannel();
    }
    private void setupRemoteVideo(int uid) {
        int count=mRemoteContainer.getChildCount();
        View view=null;
        for (int i=0;i<count;i++){
            View v=mRemoteContainer.getChildAt(i);
            if(v.getTag() instanceof Integer&&((int) v.getTag())==uid){
                view=v;
            }
        }
        if(view!=null){
            return;
        }
        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteContainer.addView(mRemoteView);
        // Set the remote video view.
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        mRemoteView.setTag(uid);

    }
    //@NonNull @org.jetbrains.annotations.NotNull
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull  String[] permissions,@NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQ_ID:{
                if (grantResults[0]!=PackageManager.PERMISSION_GRANTED||
                        grantResults[1]!=PackageManager.PERMISSION_GRANTED){
                    break;
                }
                initEngineAndJoinChannel();
                break;
            }
        }
    }

    private void removeRemoteVideo() {
        if(mRemoteView!=null){
            mRemoteContainer.removeView(mRemoteView);
        }
        mRemoteView=null;
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }


}