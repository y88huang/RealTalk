package com.serindlabs.realtalk;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import static com.serindlabs.realtalk.Utility.KillApplicationDialog;
import static com.serindlabs.realtalk.Utility.isNetworkStatusAvailable;

public class RealTalk extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    RealTalkPageAdapter realTalkPageAdapter;
    ImageButton backButton;
    Tracker mTracker;
    VolleyApplication analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.real_talk);

        if (!isNetworkStatusAvailable(this)) {
            KillApplicationDialog(getString(R.string.connectionError), this);
        }

        analytics = (VolleyApplication) this.getApplication();

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.realtalkTab));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.nextStepTab));

        viewPager = (ViewPager) findViewById(R.id.pager);
        realTalkPageAdapter = new RealTalkPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(realTalkPageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isNetworkStatusAvailable(this)) {
            KillApplicationDialog(getString(R.string.connectionError), this);
        }
    }

    class RealTalkPageAdapter extends FragmentStatePagerAdapter {
        int numOfTabs;

        public RealTalkPageAdapter(FragmentManager fm, int nOfTabs) {
            super(fm);
            numOfTabs = nOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Fragment tab1 = new RealTalkFragment();
                    mTracker = analytics.getDefaultTracker();
                    mTracker.setScreenName("Real Talk Details");
                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                    return tab1;
                case 1:
                    NextStepsFragment tab2 = new NextStepsFragment();
                    mTracker = analytics.getDefaultTracker();
                    mTracker.setScreenName("Resources");
                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }
}

