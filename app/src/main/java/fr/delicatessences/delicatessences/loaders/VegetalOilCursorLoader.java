package fr.delicatessences.delicatessences.loaders;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.sql.SQLException;

import fr.delicatessences.delicatessences.activities.OrmLiteBaseActionBarActivity;
import fr.delicatessences.delicatessences.model.VegetalOil;
import fr.delicatessences.delicatessences.model.DatabaseHelper;


public class VegetalOilCursorLoader extends AsyncTaskLoader<Cursor> {

    private final ForceLoadContentObserver mObserver;
    private final boolean mOnlyFavorites;
    private final Context mActivity;
    private Cursor mCursor;


    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {
        Cursor cursor = null;

        if (mActivity instanceof OrmLiteBaseActionBarActivity) {
            try {
                OrmLiteBaseActionBarActivity ormActivity = (OrmLiteBaseActionBarActivity) mActivity;
                DatabaseHelper helper = (DatabaseHelper) ormActivity.getHelper();
                Dao<VegetalOil, Integer> dao = helper.getVegetalOilDao();
                QueryBuilder<VegetalOil, Integer> queryBuilder = dao.queryBuilder();
                if (mOnlyFavorites) {
                    queryBuilder.where().eq(VegetalOil.FAVORITE_FIELD_NAME, true);
                }
                queryBuilder.orderBy(VegetalOil.NAME_FIELD_NAME, true);
                PreparedQuery<VegetalOil> preparedQuery = queryBuilder.prepare();
                CloseableIterator<VegetalOil> iterator = dao.iterator(preparedQuery);
                AndroidDatabaseResults results = (AndroidDatabaseResults)iterator.getRawResults();
                cursor = results.getRawCursor();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else{
            throw new IllegalStateException(mActivity.toString() + " does not implement " +
                    "OrmLiteBaseActionBarActivity");
        }

        if (cursor != null) {
            // Ensure the cursor window is filled
            cursor.getCount();
            registerContentObserver(cursor);
        }
        return cursor;
    }




    /**
     * Registers an observer to get notifications from the content provider
     * when the cursor needs to be refreshed.
     */
    private void registerContentObserver(Cursor cursor) {
        cursor.registerContentObserver(mObserver);
    }




    /* Runs on the UI thread */
    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
// An async query came in while the loader is stopped
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;
        if (isStarted()) {
            super.deliverResult(cursor);
        }
        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }




    public VegetalOilCursorLoader(Context context, boolean onlyFavorites) {
        super(context);
        mActivity = context;
        mObserver = new ForceLoadContentObserver();
        mOnlyFavorites = onlyFavorites;
    }




    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }




    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
// Attempt to cancel the current load task if possible.
        cancelLoad();
    }





    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }




    @Override
    protected void onReset() {
        super.onReset();
// Ensure the loader is stopped
        onStopLoading();
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mCursor = null;
    }





    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix);
        writer.print("mCursor=");
        writer.println(mCursor);
    }
}