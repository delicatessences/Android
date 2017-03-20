package fr.delicatessences.delicatessences.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.fragments.ListEssentialOilFragment;
import fr.delicatessences.delicatessences.fragments.ListRecipeFragment;
import fr.delicatessences.delicatessences.fragments.ListVegetalOilFragment;
import fr.delicatessences.delicatessences.fragments.ViewType;

public class FavoritePagerAdpater extends FragmentPagerAdapter {

    private static final int NUMBER_OF_PAGES = 3;
    private final Context mContext;

    public FavoritePagerAdpater(Context context, FragmentManager mgr) {
        super(mgr);
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return(NUMBER_OF_PAGES);
    }


    @Override
    public Fragment getItem(int position) {

        Fragment fragment;

        ViewType viewType = getViewType(position);
        switch (viewType){


            case ESSENTIAL_OILS: {
                fragment = ListEssentialOilFragment.newInstance(true);
                break;
            }

            case VEGETAL_OILS: {
                fragment = ListVegetalOilFragment.newInstance(true);
                break;
            }


            case RECIPES:
            default:
                fragment = ListRecipeFragment.newInstance(0, true);
                break;
        }

        return fragment;
    }


    @Override
    public String getPageTitle(int position) {
        Resources resources = mContext.getResources();
        String[] titles = resources.getStringArray(R.array.favorites);
        String title = resources.getString(R.string.unknown);

        try {
            title = titles[position];
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return title.toUpperCase();
    }



    private static ViewType getViewType(int position){
        ViewType viewType;
        switch (position){
            case 0:
                viewType=ViewType.RECIPES;
                break;

            case 1:
                viewType=ViewType.ESSENTIAL_OILS;
                break;

            case 2:
                viewType=ViewType.VEGETAL_OILS;
                break;

            default:
                viewType=ViewType.RECIPES;
                break;
        }

        return viewType;
    }
}