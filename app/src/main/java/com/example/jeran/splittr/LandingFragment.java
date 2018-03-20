package com.example.jeran.splittr;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Abhi on 18-Mar-18.
 */

public class LandingFragment extends Fragment {

    private View landingView;
    private MyFragmentPagerAdapter landingAdapter, activitiesAdapter, chartAdapter;
    private ViewPager pager;
    private TabLayout tabs;

    public LandingFragment() {
        // Required empty public constructor
    }

    public static LandingFragment newInstance() {
        LandingFragment fragment = new LandingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        landingView = inflater.inflate(R.layout.fragment_landing, container, false);
        findViewsById();
        setUpTabs();

        return landingView;
    }

    private void setUpTabs() {
        landingAdapter = new MyFragmentPagerAdapter(getFragmentManager());
        pager.setAdapter(landingAdapter);

        activitiesAdapter = new MyFragmentPagerAdapter(getFragmentManager());
        pager.setAdapter(activitiesAdapter);

        chartAdapter = new MyFragmentPagerAdapter(getFragmentManager());
        pager.setAdapter(chartAdapter);

        tabs.setupWithViewPager(pager);
    }

    private void findViewsById() {
        pager = (ViewPager) landingView.findViewById(R.id.pagerLanding);
        tabs = (TabLayout) landingView.findViewById(R.id.tabsLanding);
    }

    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = SummaryFragment.newInstance();
                    break;
                case 1:
                    fragment = ActivitiesFragment.newInstance();
                    break;
                case 2:
                    fragment = ChartFragment.newInstance();
                    break;

            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // TODO Auto-generated method stub
            String title = "";
            switch (position) {
                case 0:
                    title = "Summary";
                    break;
                case 1:
                    title = "Activities";
                    break;
                case 2:
                    title = "Dashboard";
                    break;
            }

            return title;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 3;
        }

        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

}
