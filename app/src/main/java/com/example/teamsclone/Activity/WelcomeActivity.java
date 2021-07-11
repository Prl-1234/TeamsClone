package com.example.teamsclone.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.teamsclone.Call;
import com.example.teamsclone.Chat;
import com.example.teamsclone.R;
import com.example.teamsclone.Adapter.SectionPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setViewPager();
    }
    public void setViewPager(){
        SectionPagerAdapter adapter=new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Call());
        adapter.addFragment(new Chat());
        mViewPager=(ViewPager)findViewById(R.id.container);
        mViewPager.setAdapter(adapter);
        TabLayout tabLayout=(TabLayout) findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setText("Call");
        tabLayout.getTabAt(1).setText("Chat");
    }

}
