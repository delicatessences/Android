package fr.delicatessences.delicatessences.loaders;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.lang.ref.WeakReference;
import java.sql.SQLException;

import fr.delicatessences.delicatessences.activities.OrmLiteBaseActionBarActivity;
import fr.delicatessences.delicatessences.fragments.HomeFragment;
import fr.delicatessences.delicatessences.model.Configuration;
import fr.delicatessences.delicatessences.model.DatabaseHelper;

public class HideWelcomeWorkerTask extends AsyncTask<Integer, Void, Boolean> {
    private final WeakReference<HomeFragment> weakReference;
    private final Context mContext;

    public HideWelcomeWorkerTask(Context context, HomeFragment fragment) {
        mContext = context;
        weakReference = new WeakReference<>(fragment);
    }


    @Override
    protected Boolean doInBackground(Integer... params) {
        if (mContext instanceof OrmLiteBaseActionBarActivity){
            OrmLiteBaseActionBarActivity activity = (OrmLiteBaseActionBarActivity) mContext;
            DatabaseHelper helper = (DatabaseHelper) activity.getHelper();
            try {
                Dao<Configuration, Integer> dao = helper.getConfigurationDao();
                UpdateBuilder<Configuration, Integer> updateBuilder = dao.updateBuilder();
                updateBuilder.where().eq(Configuration.ID_FIELD_NAME, 1);
                updateBuilder.updateColumnValue(Configuration.WELCOME_MESSAGE_FIELD_NAME, false);
                updateBuilder.update();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }



    @Override
    protected void onPostExecute(Boolean hideWelcomeCard) {
        HomeFragment homeFragment = weakReference.get();
        if (homeFragment != null && hideWelcomeCard != null) {
            homeFragment.hideWelcomeCard();
        }
    }
}