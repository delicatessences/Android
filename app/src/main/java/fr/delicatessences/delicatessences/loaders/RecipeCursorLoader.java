package fr.delicatessences.delicatessences.loaders;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.sql.SQLException;

import fr.delicatessences.delicatessences.activities.OrmLiteBaseActionBarActivity;
import fr.delicatessences.delicatessences.adapters.RecipeListItem;
import fr.delicatessences.delicatessences.model.Recipe;
import fr.delicatessences.delicatessences.model.Use;
import fr.delicatessences.delicatessences.model.DatabaseHelper;


public class RecipeCursorLoader extends AsyncTaskLoader<Cursor> {

    private final ForceLoadContentObserver mObserver;
    private final Context mActivity;
    private final int mCategory;
    private final boolean mOnlyFavorites;
    private Cursor mCursor;


    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {
        Cursor cursor = null;

        if (mActivity instanceof OrmLiteBaseActionBarActivity) {
            try {
                OrmLiteBaseActionBarActivity ormActivity = (OrmLiteBaseActionBarActivity) mActivity;
                DatabaseHelper helper = (DatabaseHelper) ormActivity.getHelper();
                Dao<Recipe, Integer> dao = helper.getRecipeDao();
                String query = "SELECT " + DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.ID_FIELD_NAME + ", "
                        + DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.NAME_FIELD_NAME + ", "
                        + DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.CATEGORY_FIELD_NAME + ", "
                        + DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.FAVORITE_FIELD_NAME + ", "
                        + DatabaseHelper.USE_TABLE_NAME + "." + Use.NAME_FIELD_NAME
                        + " FROM " + DatabaseHelper.RECIPE_TABLE_NAME
                        + " LEFT JOIN " + DatabaseHelper.USE_TABLE_NAME
                        + " ON " + DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.USE_FIELD_NAME
                        + "=" + DatabaseHelper.USE_TABLE_NAME + "." + Use.ID_FIELD_NAME
                        + (mCategory > 0 || mOnlyFavorites ? " WHERE " : "")
                        + (mCategory > 0 ? DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.CATEGORY_FIELD_NAME
                        + "=" + mCategory : "")
                        + (mCategory > 0 && mOnlyFavorites ? " AND " : "")
                        + (mOnlyFavorites ? DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.FAVORITE_FIELD_NAME
                        + "=1" : "")
                        + " ORDER BY " + DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.NAME_FIELD_NAME;

                GenericRawResults<RecipeListItem> items = dao.queryRaw(query, new RawRowMapper<RecipeListItem>() {
                    public RecipeListItem mapRow(String[] columnNames,
                                                 String[] resultColumns) {
                        return new RecipeListItem(Integer.valueOf(resultColumns[0]),
                                resultColumns[1],
                                Integer.valueOf(resultColumns[2]),
                                Boolean.valueOf(resultColumns[3]),
                                resultColumns[4]);
                    }
                });
                CloseableIterator<RecipeListItem> iterator = items.closeableIterator();
                AndroidDatabaseResults results = (AndroidDatabaseResults) iterator.getRawResults();
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




    public RecipeCursorLoader(Context context, int category, boolean onlyFavorites) {
        super(context);
        mActivity = context;
        mObserver = new ForceLoadContentObserver();
        mCategory = category;
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