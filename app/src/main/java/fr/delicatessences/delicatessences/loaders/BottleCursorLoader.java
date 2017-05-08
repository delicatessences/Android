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
import fr.delicatessences.delicatessences.adapters.BottleListItem;
import fr.delicatessences.delicatessences.model.Bottle;
import fr.delicatessences.delicatessences.model.EssentialOil;
import fr.delicatessences.delicatessences.model.DatabaseHelper;


public class BottleCursorLoader extends AsyncTaskLoader<Cursor> {

    private final ForceLoadContentObserver mObserver;
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
                Dao<Bottle, Integer> dao = helper.getBottleDao();
                String query = "SELECT " + DatabaseHelper.BOTTLE_TABLE_NAME + "." + Bottle.ID_FIELD_NAME + ", "
                        + DatabaseHelper.BOTTLE_TABLE_NAME + "." + Bottle.BRAND_FIELD_NAME + ", "
                        + DatabaseHelper.BOTTLE_TABLE_NAME + "." + Bottle.EXPIRATION_DATE_FIELD_NAME + ", "
                        + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.ID_FIELD_NAME + ", "
                        + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.NAME_FIELD_NAME + ", "
                        + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.IMAGE_FIELD_NAME
                        + " FROM " + DatabaseHelper.BOTTLE_TABLE_NAME
                        + " INNER JOIN " + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME
                        + " ON " + DatabaseHelper.BOTTLE_TABLE_NAME + "." + Bottle.OIL_FIELD_NAME
                        + "=" + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.ID_FIELD_NAME
                        + " ORDER BY " + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.NAME_FIELD_NAME;

                GenericRawResults<BottleListItem> items = dao.queryRaw(query, new RawRowMapper<BottleListItem>() {
                    public BottleListItem mapRow(String[] columnNames,
                                                 String[] resultColumns) {
                        return new BottleListItem(Integer.valueOf(resultColumns[0]),
                                resultColumns[1],
                                resultColumns[2],
                                Integer.valueOf(resultColumns[3]),
                                resultColumns[4],
                                resultColumns[5]);
                    }
                });

                CloseableIterator<BottleListItem> iterator = items.closeableIterator();
                AndroidDatabaseResults results = (AndroidDatabaseResults)iterator.getRawResults();
                cursor = results.getRawCursor();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IllegalStateException e){
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




    public BottleCursorLoader(Context context) {
        super(context);
        mActivity = context;
        mObserver = new ForceLoadContentObserver();
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