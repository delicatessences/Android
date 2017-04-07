package fr.delicatessences.delicatessences.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.sql.SQLException;
import java.util.List;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.activities.OrmLiteBaseActionBarActivity;
import fr.delicatessences.delicatessences.fragments.ListRecipeFragment;
import fr.delicatessences.delicatessences.model.DatabaseHelper;

public class RecipePagerAdpater extends FragmentPagerAdapter {

    private static final int NUMBER_OF_PAGES = 5;
    private final Context mContext;
    private ListRecipeFragment mCurrentFragment;

    private ListRecipeFragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((ListRecipeFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }


    public RecipePagerAdpater(Context context, FragmentManager mgr) {
        super(mgr);
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return(NUMBER_OF_PAGES);
    }


    @Override
    public Fragment getItem(int position) {
        return(ListRecipeFragment.newInstance(position, false));
    }


    @Override
    public String getPageTitle(int position) {
        if (position > 0){
            if (mContext instanceof OrmLiteBaseActionBarActivity) {
                OrmLiteBaseActionBarActivity activity = (OrmLiteBaseActionBarActivity) mContext;
                DatabaseHelper helper = (DatabaseHelper) activity.getHelper();
                try {
                    List<String> categories = helper.getCategories();
                    return categories.get(position - 1);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
        }else{
            return mContext.getResources().getString(R.string.all);
        }

        return mContext.getResources().getString(R.string.unknown);
    }
}