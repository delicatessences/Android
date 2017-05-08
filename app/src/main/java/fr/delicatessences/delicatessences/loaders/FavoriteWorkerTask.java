package fr.delicatessences.delicatessences.loaders;

import android.os.AsyncTask;
import android.view.MenuItem;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.lang.ref.WeakReference;
import java.sql.SQLException;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.activities.OrmLiteBaseActionBarActivity;
import fr.delicatessences.delicatessences.fragments.ViewType;
import fr.delicatessences.delicatessences.model.EssentialOil;
import fr.delicatessences.delicatessences.model.Recipe;
import fr.delicatessences.delicatessences.model.VegetalOil;
import fr.delicatessences.delicatessences.model.DatabaseHelper;

public class FavoriteWorkerTask extends AsyncTask<Integer, Void, Integer> {
    private final WeakReference<MenuItem> menuItemReference;
    private final OrmLiteBaseActionBarActivity mActivity;
    private final ViewType mViewType;

    public FavoriteWorkerTask(OrmLiteBaseActionBarActivity context, ViewType viewType, MenuItem menuItem) {
        mActivity = context;
        mViewType = viewType;
        menuItemReference = new WeakReference<>(menuItem);
    }


    @Override
    protected Integer doInBackground(Integer... params) {
        int id = params[0];
        DatabaseHelper helper = (DatabaseHelper) mActivity.getHelper();

        switch (mViewType) {
            case ESSENTIAL_OILS:
                try {
                    Dao<EssentialOil, Integer> dao = helper.getEssentialOilDao();
                    EssentialOil essentialOil = dao.queryForId(id);
                    boolean favorite = !essentialOil.isFavorite();
                    UpdateBuilder<EssentialOil, Integer> updateBuilder = dao.updateBuilder();
                    updateBuilder.where().eq(EssentialOil.ID_FIELD_NAME, id);
                    updateBuilder.updateColumnValue(EssentialOil.FAVORITE_FIELD_NAME, favorite);
                    updateBuilder.update();
                    return favorite ? 1 : 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case VEGETAL_OILS:
                try {
                    Dao<VegetalOil, Integer> dao = helper.getVegetalOilDao();
                    VegetalOil vegetalOil = dao.queryForId(id);
                    boolean favorite = !vegetalOil.isFavorite();
                    UpdateBuilder<VegetalOil, Integer> updateBuilder = dao.updateBuilder();
                    updateBuilder.where().eq(VegetalOil.ID_FIELD_NAME, id);
                    updateBuilder.updateColumnValue(VegetalOil.FAVORITE_FIELD_NAME, favorite);
                    updateBuilder.update();
                    return favorite ? 1 : 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;


            case RECIPES:
                try {
                    Dao<Recipe, Integer> dao = helper.getRecipeDao();
                    Recipe recipe = dao.queryForId(id);
                    boolean favorite = !recipe.isFavorite();
                    UpdateBuilder<Recipe, Integer> updateBuilder = dao.updateBuilder();
                    updateBuilder.where().eq(Recipe.ID_FIELD_NAME, id);
                    updateBuilder.updateColumnValue(Recipe.FAVORITE_FIELD_NAME, favorite);
                    updateBuilder.update();
                    return favorite ? 1 : 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e){
                    e.printStackTrace();
                }
                break;
        }

        return 0;
    }


    @Override
    protected void onPostExecute(Integer result) {
        MenuItem menuItem = menuItemReference.get();
        if (menuItem != null) {
            menuItem.setIcon(result > 0 ? R.drawable.ic_star_24dp : R.drawable.ic_star_outline_24dp);
        }
    }
}