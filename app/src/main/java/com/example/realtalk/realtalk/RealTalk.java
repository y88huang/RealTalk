package com.example.realtalk.realtalk;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class RealTalk extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    RealTalkPageAdapter realTalkPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.real_talk);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.realtalkTab));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.nextStepTab));

        viewPager = (ViewPager) findViewById(R.id.pager);
        realTalkPageAdapter = new RealTalkPageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());

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
}
class RealTalkPageAdapter extends FragmentStatePagerAdapter{
    int numOfTabs;

    public RealTalkPageAdapter(FragmentManager fm, int nOfTabs) {
        super(fm);
        numOfTabs = nOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                Fragment tab1 = new RealTalkFragment();
                return tab1;
            case 1:
                NextStepsFragment tab2 = new NextStepsFragment();
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