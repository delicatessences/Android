package fr.delicatessences.delicatessences.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.melnykov.fab.FloatingActionButton;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.activities.MainActivity;
import fr.delicatessences.delicatessences.adapters.RecipePagerAdpater;
import fr.delicatessences.delicatessences.interfaces.Reloadable;
import fr.delicatessences.delicatessences.views.SlidingTabLayout;


public class RecipePagerFragment extends Fragment implements Reloadable{

    private static final String RECIPE_DISPLAY_COUNT_PREF = "recipeDisplayCount";
    private final static int RECIPE_DISPLAY_COUNT = 2;
    private final static int PAGER_OFFSCREEN_LIMIT = 2;
    private FragmentManager mFragmentManager;
    private boolean needReload;
    private ShowcaseView mShowcaseView;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_pager, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);


        Resources resources = getResources();
        FloatingActionButton button = (FloatingActionButton) getView().findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mShowcaseView != null && mShowcaseView.isShown()){
                    mShowcaseView.hide();
                }
                MainActivity activity = (MainActivity) getActivity();
                activity.newItem(view);
            }
        });

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        int displayCount = preferences.getInt(RECIPE_DISPLAY_COUNT_PREF, 1);

        if (displayCount == RECIPE_DISPLAY_COUNT) {
            mShowcaseView = new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    .setTarget(new ViewTarget(button))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setContentTitle(resources.getString(R.string.recipe_showcase_title))
                    .setContentText(resources.getString(R.string.recipe_showcase_content))
                    .replaceEndButton(R.layout.view_custom_button)
                    .build();
            mShowcaseView.setButtonPosition(lps);
        }

        SharedPreferences.Editor e = preferences.edit();
        e.putInt(RECIPE_DISPLAY_COUNT_PREF, displayCount + 1);
        e.apply();


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mFragmentManager = getChildFragmentManager();
        RecipePagerAdpater mAdapter = new RecipePagerAdpater(getActivity(), mFragmentManager);
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(PAGER_OFFSCREEN_LIMIT);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                switch (state){
                    case ViewPager.SCROLL_STATE_IDLE:
                        fab.show();
                        break;

                    case ViewPager.SCROLL_STATE_DRAGGING:
                        fab.hide();
                        break;

                    case ViewPager.SCROLL_STATE_SETTLING:
                        break;
                }
            }
        });

        needReload = false;
    }


    @Override
    public void onStop() {
        super.onStop();
        needReload = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (needReload) {
            reload();
        }
    }

    public static RecipePagerFragment newInstance() {
        RecipePagerFragment fragment = new RecipePagerFragment();

        Bundle args = new Bundle();
        args.putInt(MainActivity.EXTRA_VIEW_TYPE, ViewType.RECIPES.ordinal());
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
            actionBar.setTitle(titles[ViewType.RECIPES.ordinal()]);
        }

    }

    @Override
    public void reload() {
        for (Fragment f : mFragmentManager.getFragments()){
            Reloadable fragment = (Reloadable) f;
            if (f.isAdded()) {
                fragment.reload();
            }
        }
    }
}