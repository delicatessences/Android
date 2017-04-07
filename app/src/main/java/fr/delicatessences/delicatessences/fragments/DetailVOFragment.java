package fr.delicatessences.delicatessences.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.activities.EditVOActivity;
import fr.delicatessences.delicatessences.activities.MainActivity;
import fr.delicatessences.delicatessences.activities.OrmLiteBaseActionBarActivity;
import fr.delicatessences.delicatessences.adapters.SheetAdapter;
import fr.delicatessences.delicatessences.adapters.VegetalOilSheetAdapter;
import fr.delicatessences.delicatessences.loaders.CustomAsyncTaskLoader;
import fr.delicatessences.delicatessences.loaders.FavoriteWorkerTask;
import fr.delicatessences.delicatessences.model.DatabaseHelper;
import fr.delicatessences.delicatessences.model.VOIndication;
import fr.delicatessences.delicatessences.model.VOProperty;
import fr.delicatessences.delicatessences.model.VORecipe;
import fr.delicatessences.delicatessences.model.VegetalIndication;
import fr.delicatessences.delicatessences.model.VegetalOil;
import fr.delicatessences.delicatessences.model.VegetalProperty;

public class DetailVOFragment extends DetailFragment {


    private MenuItem mFavoriteMenuItem;
    private boolean mFavorite;
    private TextView mDescription;
    private TextView mProperties;
    private TextView mPropertiesLabel;
    private TextView mIndications;
    private TextView mIndicationsLabel;
    private boolean mIsReadOnly;


    @Override
    protected int getLayout() {
        return R.layout.fragment_detail_vo;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        assert view != null;
        mDescription = (TextView) view.findViewById(R.id.description);
        mProperties = (TextView) view.findViewById(R.id.properties);
        mPropertiesLabel = (TextView) view.findViewById(R.id.properties_label);
        mIndications = (TextView) view.findViewById(R.id.indications);
        mIndicationsLabel = (TextView) view.findViewById(R.id.indications_label);

        return view;
    }


    public static DetailVOFragment newInstance(int id) {
        DetailVOFragment fragment = new DetailVOFragment();

        Bundle args = new Bundle();
        args.putInt(MainActivity.EXTRA_ID, id);
        fragment.setArguments(args);

        return fragment;
    }



    //up button action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MainActivity activity = (MainActivity) getActivity();
        int mId = getmId();

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;

            case R.id.action_favorite:
                FavoriteWorkerTask task = new FavoriteWorkerTask(activity, ViewType.VEGETAL_OILS, item);
                task.execute(mId);
                return true;


            case R.id.action_edit:
                Intent intent = new Intent(activity, EditVOActivity.class);
                intent.putExtra(MainActivity.EXTRA_ID, mId);
                startActivity(intent);
                return true;

            case R.id.action_delete:
                if (mIsReadOnly){
                    showConfirmDialog(activity, activity.getString(R.string.message_read_only_vegetal_oil));
                }else {
                    showConfirmDeleteDialog(activity);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteVegetalOil() throws SQLException {
        MainActivity activity = (MainActivity) getActivity();
        final DatabaseHelper helper = activity.getHelper();

        TransactionManager.callInTransaction(helper.getConnectionSource(),
                new Callable<Void>() {
                    public Void call() throws Exception {
                        int mId = getmId();
                        Dao<VOProperty, Integer> voPropertyDao = helper.getVOPropertyDao();
                        DeleteBuilder<VOProperty, Integer> propertyDeleteBuilder = voPropertyDao.deleteBuilder();
                        propertyDeleteBuilder.where().eq(VOProperty.OIL_ID, mId);
                        propertyDeleteBuilder.delete();

                        Dao<VOIndication, Integer> voIndicationDao = helper.getVOIndicationDao();
                        DeleteBuilder<VOIndication, Integer> indicationDeleteBuilder = voIndicationDao.deleteBuilder();
                        indicationDeleteBuilder.where().eq(VOIndication.OIL_ID, mId);
                        indicationDeleteBuilder.delete();

                        Dao<VORecipe, Integer> voRecipeDao = helper.getVORecipeDao();
                        DeleteBuilder<VORecipe, Integer> voRecipeDeleteBuilder = voRecipeDao.deleteBuilder();
                        voRecipeDeleteBuilder.where().eq(VORecipe.OIL_ID, mId);

                        Dao<VegetalOil, Integer> oilDao = helper.getVegetalOilDao();
                        DeleteBuilder<VegetalOil, Integer> oilDeleteBuilder = oilDao.deleteBuilder();
                        oilDeleteBuilder.where().eq(VegetalOil.ID_FIELD_NAME, mId);
                        oilDeleteBuilder.delete();

                        return null;
                    }
                });

        FirebaseAppIndex.getInstance().remove(mIndexedURL);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_detail_menu, menu);
        mFavoriteMenuItem = menu.findItem(R.id.action_favorite);
        mFavoriteMenuItem.setIcon(mFavorite ? R.drawable.ic_star_24dp : R.drawable.ic_star_outline_24dp);
        super.onCreateOptionsMenu(menu, inflater);
    }



    private void showConfirmDeleteDialog(final MainActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(activity.getString(R.string.delete_vegetal_oil_warning));
        final Resources resources = getResources();
        builder.setPositiveButton(resources.getString(R.string.action_ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            deleteVegetalOil();
                            activity.showFeedbackMessage(resources.getString(R.string.delete_vegetal_oil));
                            activity.showList(ViewType.VEGETAL_OILS);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            activity.showFeedbackMessage(resources.getString(R.string.delete_failed));
                        }
                    }
                }
        );
        builder.setNegativeButton(resources.getString(R.string.action_cancel), null);
        builder.show();
    }


    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        final OrmLiteBaseActionBarActivity activity = (OrmLiteBaseActionBarActivity) getActivity();
        return new CustomAsyncTaskLoader<Object>(activity){

            @Override
            public SheetAdapter loadInBackground() {
                DatabaseHelper helper = (DatabaseHelper) activity.getHelper();
                SheetAdapter adapter = null;
                try {
                    Dao<VegetalOil, Integer> oilDao = helper.getVegetalOilDao();
                    int mId = getmId();
                    VegetalOil vegetalOil = oilDao.queryForId(mId);
                    if (vegetalOil != null) {

                        prepareIndex(vegetalOil);

                        List<VegetalProperty> properties = helper.getVegetalProperties(mId);
                        List<String> propertiesName = new ArrayList<>(properties.size());
                        for (VegetalProperty property : properties){
                            propertiesName.add(property.getName());
                        }
                        List<VegetalIndication> indications = helper.getVegetalIndications(mId);
                        List<String> indicationsName = new ArrayList<>(indications.size());
                        for (VegetalIndication indication : indications){
                            indicationsName.add(indication.getName());
                        }

                        adapter = new VegetalOilSheetAdapter(vegetalOil.getImage(), Color.parseColor(vegetalOil.getColor()),
                                vegetalOil.getName(), vegetalOil.getDescription(), vegetalOil.isFavorite(),
                                vegetalOil.isReadOnly(), propertiesName, indicationsName);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return adapter;
            }
        };
    }


    private void prepareIndex(VegetalOil vegetalOil) {
        mIndexedURL = vegetalOil.getUrl();
        Resources resources = getResources();
        String namePrefix = resources.getString(R.string.vo_of);
        String name = vegetalOil.getName();
        mIndexedName = namePrefix + name != null ? name : "";
        String description = vegetalOil.getDescription();
        mIndexedText = description != null ? description : "";
    }


    @Override
    protected Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mIndexedName != null ? mIndexedName : "")
                .setUrl(mIndexedURL != null ? Uri.parse(mIndexedURL) : Uri.EMPTY)
                .build();


        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .build();

    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object o) {
        super.onLoadFinished(loader, o);
        if (o instanceof VegetalOilSheetAdapter){
            VegetalOilSheetAdapter adapter = (VegetalOilSheetAdapter) o;
            Resources resources = getResources();
            String name = adapter.getName();
            mIsReadOnly = adapter.isReadOnly();
            TextView mTitle = getTitle();
            mTitle.setText(name != null && name.length() > 0 ? name : resources.getString(R.string.without_name));
            mTitle.setVisibility(View.VISIBLE);
            mFavorite = adapter.isFavorite();
            if (mFavoriteMenuItem != null){
                mFavoriteMenuItem.setIcon(mFavorite ? R.drawable.ic_star_24dp : R.drawable.ic_star_outline_24dp);
            }
            String description = adapter.getDescription();
            if (description != null && description.length() > 0){
                mDescription.setText(description);
                mDescription.setVisibility(View.VISIBLE);
            }
            else {
                mDescription.setVisibility(View.GONE);
            }

            List<String> properties = adapter.getProperties();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < properties.size(); i++){
                if (i > 0){
                    sb.append(" \u2022 ");
                }
                sb.append(properties.get(i));
            }
            if (sb.length() > 0){
                mProperties.setText(sb.toString());
                mProperties.setVisibility(View.VISIBLE);
                mPropertiesLabel.setTextColor(adapter.getColor());
                mPropertiesLabel.setVisibility(View.VISIBLE);
            }else{
                mProperties.setVisibility(View.GONE);
                mPropertiesLabel.setVisibility(View.GONE);
            }

            List<String> indications = adapter.getIndications();
            sb = new StringBuilder();
            for (int i = 0; i < indications.size(); i++){
                if (i > 0){
                    sb.append(" \u2022 ");
                }
                sb.append(indications.get(i));
            }
            if (sb.length() > 0){
                mIndications.setText(sb.toString());
                mIndications.setVisibility(View.VISIBLE);
                mIndicationsLabel.setTextColor(adapter.getColor());
                mIndicationsLabel.setVisibility(View.VISIBLE);
            }else{
                mIndications.setVisibility(View.GONE);
                mIndicationsLabel.setVisibility(View.GONE);
            }


        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
