package com.example.realtalk.realtalk;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by alexgomes on 2015-09-29.
 */
public class Profile extends AppCompatActivity {

    TextView userName;
    TabLayout tabLayout;
    ImageButton btnBackButton;
    ViewPager profilePager;
    ProfilePageAdapter profilePageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);

        btnBackButton = (ImageButton)findViewById(R.id.backButton);
        btnBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userName = (TextView) findViewById(R.id.userName);
        userName.setTypeface(FontManager.setFont(this, FontManager.Font.MontSerratRegular));
        userName.setText("User Name");

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.bookMarks));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.nextSteps));

        profilePager = (ViewPager) findViewById(R.id.profilePager);
        profilePageAdapter = new ProfilePageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());

        profilePager.setAdapter(profilePageAdapter);
        profilePager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                profilePager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}

class ProfilePageAdapter extends FragmentStatePagerAdapter {
    int numOfTabs;

    public ProfilePageAdapter(FragmentManager fm, int nOfTabs) {
        super(fm);
        numOfTabs = nOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                ProfileBookMarksFragment bookMarksFragment = new ProfileBookMarksFragment();
                return bookMarksFragment;
            case 1:
                ProfileNextStepsFragment nextStepsFragment = new ProfileNextStepsFragment();
                return nextStepsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}