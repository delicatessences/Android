package fr.delicatessences.delicatessences.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.activities.MainActivity;
import fr.delicatessences.delicatessences.adapters.FavoritePagerAdpater;
import fr.delicatessences.delicatessences.interfaces.Reloadable;
import fr.delicatessences.delicatessences.views.SlidingTabLayout;

public class FavoritePagerFragment extends Fragment implements Reloadable{

    private final static int PAGER_OFFSCREEN_LIMIT = 2;
    private FragmentManager mFragmentManager;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_pager, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        mFragmentManager = getChildFragmentManager();
        FavoritePagerAdpater mAdapter = new FavoritePagerAdpater(getActivity(), mFragmentManager);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(PAGER_OFFSCREEN_LIMIT);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(viewPager);
    }


    public static FavoritePagerFragment newInstance() {
        FavoritePagerFragment fragment = new FavoritePagerFragment();

        Bundle args = new Bundle();
        args.putInt(MainActivity.EXTRA_VIEW_TYPE, ViewType.FAVORITES.ordinal());
        fragment.setArguments(args);

        return fragment;
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MainActivity activity = (MainActivity) getActivity();
        activity.setDrawerIndicatorEnabled(true);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayShowTitleEnabled(true);
            String[] titles = getResources().getStringArray(R.array.drawer_items);
            actionBar.setTitle(titles[ViewType.FAVORITES.ordinal()]);
        }

    }

    @Override
    public void reload() {
        for (Fragment f : mFragmentManager.getFragments()){
            Reloadable fragment = (Reloadable) f;
            fragment.reload();
        }
    }
}