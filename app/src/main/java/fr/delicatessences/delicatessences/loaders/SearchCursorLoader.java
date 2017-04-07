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
import fr.delicatessences.delicatessences.adapters.SearchListItem;
import fr.delicatessences.delicatessences.fragments.ViewType;
import fr.delicatessences.delicatessences.model.Bottle;
import fr.delicatessences.delicatessences.model.DatabaseHelper;
import fr.delicatessences.delicatessences.model.EOIndication;
import fr.delicatessences.delicatessences.model.EOProperty;
import fr.delicatessences.delicatessences.model.EORecipe;
import fr.delicatessences.delicatessences.model.EssentialIndication;
import fr.delicatessences.delicatessences.model.EssentialOil;
import fr.delicatessences.delicatessences.model.EssentialProperty;
import fr.delicatessences.delicatessences.model.Recipe;
import fr.delicatessences.delicatessences.model.VOIndication;
import fr.delicatessences.delicatessences.model.VOProperty;
import fr.delicatessences.delicatessences.model.VORecipe;
import fr.delicatessences.delicatessences.model.VegetalIndication;
import fr.delicatessences.delicatessences.model.VegetalOil;
import fr.delicatessences.delicatessences.model.VegetalProperty;


public class SearchCursorLoader extends AsyncTaskLoader<Cursor> {

    private final ForceLoadContentObserver mObserver;
    private final Context mActivity;
    private final String mQuery;
    private Cursor mCursor;


    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {
        Cursor cursor = null;

        if (mActivity instanceof OrmLiteBaseActionBarActivity) {
            try {
                OrmLiteBaseActionBarActivity ormActivity = (OrmLiteBaseActionBarActivity) mActivity;
                DatabaseHelper helper = (DatabaseHelper) ormActivity.getHelper();
                Dao<EssentialOil, Integer> dao = helper.getEssentialOilDao();
                String query = "SELECT " + EssentialOil.ID_FIELD_NAME + ", " + EssentialOil.NAME_FIELD_NAME
                        + ", '" + ViewType.ESSENTIAL_OILS.getInt() + "' AS viewtype"
                        + " FROM " + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME
                        + " WHERE "
                        + "(" + EssentialOil.NAME_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " OR " + EssentialOil.BOTANICAL_NAME_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " OR " + EssentialOil.DISTILLED_ORGAN_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " OR " + EssentialOil.CHEMOTYPE_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " OR " + EssentialOil.DESCRIPTION_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " OR " + EssentialOil.PRECAUTIONS_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%')"
                        + " UNION"
                        + " SELECT " + VegetalOil.ID_FIELD_NAME + ", " + VegetalOil.NAME_FIELD_NAME
                        + ", '" + ViewType.VEGETAL_OILS.getInt() + "' AS viewtype"
                        + " FROM " + DatabaseHelper.VEGETAL_OIL_TABLE_NAME
                        + " WHERE "
                        + "(" + VegetalOil.NAME_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " OR " + VegetalOil.DESCRIPTION_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%')"
                        + " UNION"
                        + " SELECT " + Recipe.ID_FIELD_NAME + ", " + Recipe.NAME_FIELD_NAME
                        + ", '" + ViewType.RECIPES.getInt() + "' AS viewtype"
                        + " FROM " + DatabaseHelper.RECIPE_TABLE_NAME
                        + " WHERE "
                        + "(" + Recipe.NAME_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " OR " + Recipe.AUTHOR_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " OR " + Recipe.PREPARATION_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%')"
                        + " UNION"
                        + " SELECT " + DatabaseHelper.BOTTLE_TABLE_NAME + "." + Bottle.ID_FIELD_NAME
                        + ", " + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.NAME_FIELD_NAME
                        + ", '" + ViewType.BOTTLES.getInt() + "' AS viewtype"
                        + " FROM " + DatabaseHelper.BOTTLE_TABLE_NAME
                        + " INNER JOIN " + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME
                        + " ON " + DatabaseHelper.BOTTLE_TABLE_NAME + "." + Bottle.OIL_FIELD_NAME
                        + "=" + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.ID_FIELD_NAME
                        + " WHERE "
                        + "(" + DatabaseHelper.BOTTLE_TABLE_NAME + "." + Bottle.BRAND_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " OR " + DatabaseHelper.BOTTLE_TABLE_NAME + "." + Bottle.ORIGIN_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " OR " + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.NAME_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%')"
                        + " UNION"
                        + " SELECT " + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.ID_FIELD_NAME
                        + ", " + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.NAME_FIELD_NAME
                        + ", '" + ViewType.ESSENTIAL_OILS.getInt() + "' AS viewtype"
                        + " FROM " + DatabaseHelper.ESSENTIAL_INDICATIONS_TABLE_NAME
                        + " INNER JOIN " + DatabaseHelper.EO_INDICATIONS_TABLE_NAME
                        + " ON " + DatabaseHelper.ESSENTIAL_INDICATIONS_TABLE_NAME + "." + EssentialIndication.ID_FIELD_NAME
                        + "=" + DatabaseHelper.EO_INDICATIONS_TABLE_NAME + "." + EOIndication.INDICATION_ID
                        + " INNER JOIN " + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME
                        + " ON " + DatabaseHelper.EO_INDICATIONS_TABLE_NAME + "." + EOIndication.OIL_ID
                        + "=" + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.ID_FIELD_NAME
                        + " WHERE "
                        + DatabaseHelper.ESSENTIAL_INDICATIONS_TABLE_NAME + "." + EssentialIndication.NAME_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " UNION"
                        + " SELECT " + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.ID_FIELD_NAME
                        + ", " + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.NAME_FIELD_NAME
                        + ", '" + ViewType.ESSENTIAL_OILS.getInt() + "' AS viewtype"
                        + " FROM " + DatabaseHelper.ESSENTIAL_PROPERTIES_TABLE_NAME
                        + " INNER JOIN " + DatabaseHelper.EO_PROPERTIES_TABLE_NAME
                        + " ON " + DatabaseHelper.ESSENTIAL_PROPERTIES_TABLE_NAME + "." + EssentialProperty.ID_FIELD_NAME
                        + "=" + DatabaseHelper.EO_PROPERTIES_TABLE_NAME + "." + EOProperty.PROPERTY_ID
                        + " INNER JOIN " + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME
                        + " ON " + DatabaseHelper.EO_PROPERTIES_TABLE_NAME + "." + EOProperty.OIL_ID
                        + "=" + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.ID_FIELD_NAME
                        + " WHERE "
                        + DatabaseHelper.ESSENTIAL_PROPERTIES_TABLE_NAME + "." + EssentialProperty.NAME_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " UNION"
                        + " SELECT " + DatabaseHelper.VEGETAL_OIL_TABLE_NAME + "." + VegetalOil.ID_FIELD_NAME
                        + ", " + DatabaseHelper.VEGETAL_OIL_TABLE_NAME + "." + VegetalOil.NAME_FIELD_NAME
                        + ", '" + ViewType.VEGETAL_OILS.getInt() + "' AS viewtype"
                        + " FROM " + DatabaseHelper.VEGETAL_INDICATIONS_TABLE_NAME
                        + " INNER JOIN " + DatabaseHelper.VO_INDICATIONS_TABLE_NAME
                        + " ON " + DatabaseHelper.VEGETAL_INDICATIONS_TABLE_NAME + "." + VegetalIndication.ID_FIELD_NAME
                        + "=" + DatabaseHelper.VO_INDICATIONS_TABLE_NAME + "." + VOIndication.INDICATION_ID
                        + " INNER JOIN " + DatabaseHelper.VEGETAL_OIL_TABLE_NAME
                        + " ON " + DatabaseHelper.VO_INDICATIONS_TABLE_NAME + "." + VOIndication.OIL_ID
                        + "=" + DatabaseHelper.VEGETAL_OIL_TABLE_NAME + "." + VegetalOil.ID_FIELD_NAME
                        + " WHERE "
                        + DatabaseHelper.VEGETAL_INDICATIONS_TABLE_NAME + "." + VegetalIndication.NAME_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " UNION"
                        + " SELECT " + DatabaseHelper.VEGETAL_OIL_TABLE_NAME + "." + VegetalOil.ID_FIELD_NAME
                        + ", " + DatabaseHelper.VEGETAL_OIL_TABLE_NAME + "." + VegetalOil.NAME_FIELD_NAME
                        + ", '" + ViewType.VEGETAL_OILS.getInt() + "' AS viewtype"
                        + " FROM " + DatabaseHelper.VEGETAL_PROPERTIES_TABLE_NAME
                        + " INNER JOIN " + DatabaseHelper.VO_PROPERTIES_TABLE_NAME
                        + " ON " + DatabaseHelper.VEGETAL_PROPERTIES_TABLE_NAME + "." + VegetalProperty.ID_FIELD_NAME
                        + "=" + DatabaseHelper.VO_PROPERTIES_TABLE_NAME + "." + VOProperty.PROPERTY_ID
                        + " INNER JOIN " + DatabaseHelper.VEGETAL_OIL_TABLE_NAME
                        + " ON " + DatabaseHelper.VO_PROPERTIES_TABLE_NAME + "." + VOProperty.OIL_ID
                        + "=" + DatabaseHelper.VEGETAL_OIL_TABLE_NAME + "." + VegetalOil.ID_FIELD_NAME
                        + " WHERE "
                        + DatabaseHelper.VEGETAL_PROPERTIES_TABLE_NAME + "." + VegetalProperty.NAME_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " UNION"
                        + " SELECT " + DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.ID_FIELD_NAME
                        + ", " + DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.NAME_FIELD_NAME
                        + ", '" + ViewType.RECIPES.getInt() + "' AS viewtype"
                        + " FROM " + DatabaseHelper.RECIPE_TABLE_NAME
                        + " INNER JOIN " + DatabaseHelper.EO_RECIPE_TABLE_NAME
                        + " ON " + DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.ID_FIELD_NAME
                        + "=" + DatabaseHelper.EO_RECIPE_TABLE_NAME + "." + EORecipe.RECIPE_ID
                        + " INNER JOIN " + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME
                        + " ON " + DatabaseHelper.EO_RECIPE_TABLE_NAME + "." + EORecipe.OIL_ID
                        + "=" + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.ID_FIELD_NAME
                        + " WHERE "
                        + DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME + "." + EssentialOil.NAME_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'"
                        + " UNION"
                        + " SELECT " + DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.ID_FIELD_NAME
                        + ", " + DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.NAME_FIELD_NAME
                        + ", '" + ViewType.RECIPES.getInt() + "' AS viewtype"
                        + " FROM " + DatabaseHelper.RECIPE_TABLE_NAME
                        + " INNER JOIN " + DatabaseHelper.VO_RECIPE_TABLE_NAME
                        + " ON " + DatabaseHelper.RECIPE_TABLE_NAME + "." + Recipe.ID_FIELD_NAME
                        + "=" + DatabaseHelper.VO_RECIPE_TABLE_NAME + "." + VORecipe.RECIPE_ID
                        + " INNER JOIN " + DatabaseHelper.VEGETAL_OIL_TABLE_NAME
                        + " ON " + DatabaseHelper.VO_RECIPE_TABLE_NAME + "." + VORecipe.OIL_ID
                        + "=" + DatabaseHelper.VEGETAL_OIL_TABLE_NAME + "." + VegetalOil.ID_FIELD_NAME
                        + " WHERE "
                        + DatabaseHelper.VEGETAL_OIL_TABLE_NAME + "." + VegetalOil.NAME_FIELD_NAME + " LIKE " + "'%" + mQuery +  "%'";

                GenericRawResults<SearchListItem> items = dao.queryRaw(query, new RawRowMapper<SearchListItem>() {
                    public SearchListItem mapRow(String[] columnNames,
                                                 String[] resultColumns) {
                        return new SearchListItem(Integer.valueOf(resultColumns[0]),
                                resultColumns[1], Integer.valueOf(resultColumns[2]));
                    }
                });


                CloseableIterator<SearchListItem> iterator = items.closeableIterator();
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




    public SearchCursorLoader(Context context, String query) {
        super(context);
        mQuery = query;
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