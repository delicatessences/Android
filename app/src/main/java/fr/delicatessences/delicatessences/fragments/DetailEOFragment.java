package fr.delicatessences.delicatessences.fragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import fr.delicatessences.delicatessences.activities.EditEOActivity;
import fr.delicatessences.delicatessences.activities.MainActivity;
import fr.delicatessences.delicatessences.activities.OrmLiteBaseActionBarActivity;
import fr.delicatessences.delicatessences.adapters.EssentialOilSheetAdapter;
import fr.delicatessences.delicatessences.adapters.LastRecipesCursorAdapter;
import fr.delicatessences.delicatessences.adapters.SheetAdapter;
import fr.delicatessences.delicatessences.decorators.SimpleDividerItemDecoration;
import fr.delicatessences.delicatessences.loaders.CustomAsyncTaskLoader;
import fr.delicatessences.delicatessences.loaders.EORecipeCursorLoader;
import fr.delicatessences.delicatessences.loaders.FavoriteWorkerTask;
import fr.delicatessences.delicatessences.model.Administration;
import fr.delicatessences.delicatessences.model.Bottle;
import fr.delicatessences.delicatessences.model.DatabaseHelper;
import fr.delicatessences.delicatessences.model.EOAdministration;
import fr.delicatessences.delicatessences.model.EOIndication;
import fr.delicatessences.delicatessences.model.EOProperty;
import fr.delicatessences.delicatessences.model.EORecipe;
import fr.delicatessences.delicatessences.model.EssentialIndication;
import fr.delicatessences.delicatessences.model.EssentialOil;
import fr.delicatessences.delicatessences.model.EssentialProperty;

public class DetailEOFragment extends DetailFragment  {

    private TextView mDescription;
    private TextView mBotanicalName;
    private TextView mBotanicalNameLabel;
    private TextView mDistilled;
    private TextView mDistilledLabel;
    private TextView mChemotype;
    private TextView mChemotypeLabel;
    private TextView mProperties;
    private TextView mPropertiesLabel;
    private TextView mIndications;
    private TextView mIndicationsLabel;
    private TextView mRecipes;
    private TextView mRecipesLabel;
    private TextView mPrecautions;
    private TextView mPrecautionsLabel;
    private TextView mAdministrationsLabel;
    private List<ImageView> mAdministrations;
    private MenuItem mFavoriteMenuItem;
    private boolean mFavorite;
    private boolean mIsReadOnly;
    private LastRecipesCursorAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected int getLayout() {
        return R.layout.fragment_detail_eo;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        assert view != null;

        Activity activity = getActivity();
        mAdapter = new LastRecipesCursorAdapter(activity);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recipes);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mDescription = (TextView) view.findViewById(R.id.description);
        mBotanicalName = (TextView) view.findViewById(R.id.botanical_name);
        mBotanicalNameLabel = (TextView) view.findViewById(R.id.botanical_name_label);
        mDistilled = (TextView) view.findViewById(R.id.distilled_organ);
        mDistilledLabel = (TextView) view.findViewById(R.id.distilled_organ_label);
        mChemotype = (TextView) view.findViewById(R.id.chemotype);
        mChemotypeLabel = (TextView) view.findViewById(R.id.chemotype_label);
        mProperties = (TextView) view.findViewById(R.id.properties);
        mPropertiesLabel = (TextView) view.findViewById(R.id.properties_label);
        mIndications = (TextView) view.findViewById(R.id.indications);
        mIndicationsLabel = (TextView) view.findViewById(R.id.indications_label);
        mPrecautions = (TextView) view.findViewById(R.id.precautions);
        mPrecautionsLabel = (TextView) view.findViewById(R.id.precautions_label);
        mAdministrationsLabel = (TextView) view.findViewById(R.id.administration_way);
        mAdministrations = new ArrayList<>(3);
        mAdministrations.add((ImageView) view.findViewById(R.id.administration_1));
        mAdministrations.add((ImageView) view.findViewById(R.id.administration_2));
        mAdministrations.add((ImageView) view.findViewById(R.id.administration_3));
        mRecipesLabel = (TextView) view.findViewById(R.id.recipes_label);
        return view;
    }

    @Override
    protected Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mIndexedName != null ? mIndexedName : "")
                .setUrl(Uri.parse(mIndexedURL))
                .build();


        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .build();
    }


    //up button action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MainActivity activity = (MainActivity) getActivity();
        int mId = getmId();

        switch (item.getItemId()) {
            case android.R.id.home:
                activity.onBackPressed();
                return true;

            case R.id.action_favorite:
                FavoriteWorkerTask task = new FavoriteWorkerTask(activity, ViewType.ESSENTIAL_OILS, item);
                task.execute(mId);
                return true;

            case R.id.action_edit:
                Intent intent = new Intent(activity, EditEOActivity.class);
                intent.putExtra(MainActivity.EXTRA_ID, mId);
                startActivity(intent);
                return true;

            case R.id.action_delete:
                if (mIsReadOnly){
                    showConfirmDialog(activity, activity.getString(R.string.message_read_only_essential_oil));
                }else {
                    showConfirmDeleteDialog(activity);
                }
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public static DetailEOFragment newInstance(int id) {
        DetailEOFragment fragment = new DetailEOFragment();

        Bundle args = new Bundle();
        args.putInt(MainActivity.EXTRA_ID, id);
        fragment.setArguments(args);

        return fragment;
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

        builder.setMessage(getResources().getString(R.string.delete_essential_oil_warning));
        final Resources resources = getResources();
        builder.setPositiveButton(resources.getString(R.string.action_ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            deleteEssentialOil();
                            activity.showFeedbackMessage(resources.getString(R.string.delete_essential_oil));
                            activity.showList(ViewType.ESSENTIAL_OILS);
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







    private void deleteEssentialOil() throws SQLException {
        MainActivity activity = (MainActivity) getActivity();
        final DatabaseHelper helper = activity.getHelper();

        TransactionManager.callInTransaction(helper.getConnectionSource(),
                new Callable<Void>() {
                    public Void call() throws Exception {
                        int mId = getmId();
                        Dao<EOProperty, Integer> eoPropertyDao = helper.getEOPropertyDao();
                        DeleteBuilder<EOProperty, Integer> propertyDeleteBuilder = eoPropertyDao.deleteBuilder();
                        propertyDeleteBuilder.where().eq(EOProperty.OIL_ID, mId);
                        propertyDeleteBuilder.delete();

                        Dao<EOIndication, Integer> eoIndicationDao = helper.getEOIndicationDao();
                        DeleteBuilder<EOIndication, Integer> indicationDeleteBuilder = eoIndicationDao.deleteBuilder();
                        indicationDeleteBuilder.where().eq(EOIndication.OIL_ID, mId);
                        indicationDeleteBuilder.delete();

                        Dao<EOAdministration, Integer> eoAdministrationDao = helper.getEOAdministrationDao();
                        DeleteBuilder<EOAdministration, Integer> administrationDeleteBuilder = eoAdministrationDao.deleteBuilder();
                        administrationDeleteBuilder.where().eq(EOAdministration.OIL_ID, mId);
                        administrationDeleteBuilder.delete();

                        Dao<Bottle, Integer> bottleDao = helper.getBottleDao();
                        DeleteBuilder<Bottle, Integer> bottleDeleteBuilder = bottleDao.deleteBuilder();
                        bottleDeleteBuilder.where().eq(Bottle.OIL_FIELD_NAME, mId);
                        bottleDeleteBuilder.delete();

                        Dao<EORecipe, Integer> eoRecipeDao = helper.getEORecipeDao();
                        DeleteBuilder<EORecipe, Integer> eoRecipeDeleteBuilder = eoRecipeDao.deleteBuilder();
                        eoRecipeDeleteBuilder.where().eq(EORecipe.OIL_ID, mId);

                        Dao<EssentialOil, Integer> oilDao = helper.getEssentialOilDao();
                        DeleteBuilder<EssentialOil, Integer> oilDeleteBuilder = oilDao.deleteBuilder();
                        oilDeleteBuilder.where().eq(EssentialOil.ID_FIELD_NAME, mId);
                        oilDeleteBuilder.delete();

                        return null;
                    }
                });

        FirebaseAppIndex.getInstance().remove(mIndexedURL);
    }


    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        final OrmLiteBaseActionBarActivity activity = (OrmLiteBaseActionBarActivity) getActivity();
        switch(id){
            case 0:
                return new CustomAsyncTaskLoader<Object>(activity){

                    @Override
                    public SheetAdapter loadInBackground() {
                        DatabaseHelper helper = (DatabaseHelper) activity.getHelper();
                        SheetAdapter adapter = null;
                        try {
                            Dao<EssentialOil, Integer> oilDao = helper.getEssentialOilDao();
                            int mId = getmId();
                            EssentialOil essentialOil = oilDao.queryForId(mId);
                            if (essentialOil != null) {

                                prepareIndex(essentialOil);


                                List<Administration> administrations = helper.getAdministrations(mId);
                                List<String> administrationsImages = new ArrayList<>(administrations.size());
                                for (Administration administration : administrations){
                                    administrationsImages.add(administration.getImage());
                                }
                                List<EssentialProperty> properties = helper.getEssentialProperties(mId);
                                List<String> propertiesName = new ArrayList<>(properties.size());
                                for (EssentialProperty property : properties){
                                    propertiesName.add(property.getName());
                                }
                                List<EssentialIndication> indications = helper.getEssentialIndications(mId);
                                List<String> indicationsName = new ArrayList<>(indications.size());
                                for (EssentialIndication indication : indications){
                                    indicationsName.add(indication.getName());
                                }

                                adapter = new EssentialOilSheetAdapter(essentialOil.getImage(), Color.parseColor(essentialOil.getColor()),
                                        essentialOil.getName(), essentialOil.getBotanicalName(), essentialOil.getDescription(),
                                        essentialOil.getDistilledOrgan(), essentialOil.getChemotype(),essentialOil.getPrecautions(),
                                        essentialOil.isFavorite(), essentialOil.isReadOnly(), administrationsImages, propertiesName,
                                        indicationsName);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        return adapter;
                    }
                };

            case 1:
                return new EORecipeCursorLoader(activity, getmId());

            default:
                return null;
        }


    }

    private void prepareIndex(EssentialOil essentialOil) {
        mIndexedURL = essentialOil.getUrl();
        Resources resources = getResources();
        String namePrefix = resources.getString(R.string.eo_of);
        String name = essentialOil.getName();
        mIndexedName = namePrefix + name != null ? name : "";
        String description = essentialOil.getDescription();
        mIndexedText = description != null ? description : "";
    }


    @Override
    public void onLoadFinished(Loader<Object> loader, Object o) {
        super.onLoadFinished(loader, o);
        final OrmLiteBaseActionBarActivity activity = (OrmLiteBaseActionBarActivity) getActivity();

        switch (loader.getId()){
            case 0:
                if (o instanceof EssentialOilSheetAdapter){

                    EssentialOilSheetAdapter adapter = (EssentialOilSheetAdapter) o;
                    Resources resources = getResources();
                    String name = adapter.getName();
                    mIsReadOnly = adapter.isReadOnly();
                    TextView mTitle = getTitle();
                    String title = name != null && name.length() > 0 ? name : resources.getString(R.string.without_name);
                    mTitle.setText(title);
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
                    else{
                        mDescription.setVisibility(View.GONE);
                    }

                    List<String> administrations = adapter.getAdministrations();
                    mAdministrationsLabel.setVisibility(administrations.size() > 0 ? View.VISIBLE : View.GONE);
                    for (int i = 0; i < administrations.size(); i++){
                        ImageView imageView = mAdministrations.get(i);
                        String image = administrations.get(i);
                        int resource = resources.getIdentifier(image, "drawable", activity.getPackageName());
                        imageView.setImageDrawable(ContextCompat.getDrawable(activity, resource));
                        imageView.setVisibility(View.VISIBLE);
                    }
                    for (int i = administrations.size(); i < mAdministrations.size(); i++){
                        ImageView imageView = mAdministrations.get(i);
                        imageView.setVisibility(View.GONE);
                    }

                    String botanicalName = adapter.getBotanicalName();
                    if (botanicalName != null && botanicalName.length() > 0){
                        mBotanicalName.setText(botanicalName);
                        mBotanicalName.setVisibility(View.VISIBLE);
                        mBotanicalNameLabel.setTextColor(adapter.getColor());
                        mBotanicalNameLabel.setVisibility(View.VISIBLE);
                    }else{
                        mBotanicalName.setVisibility(View.GONE);
                        mBotanicalNameLabel.setVisibility(View.GONE);
                    }

                    String distilledOrgan = adapter.getDistilledOrgan();
                    if (distilledOrgan != null && distilledOrgan.length() > 0){
                        mDistilled.setText(distilledOrgan);
                        mDistilled.setVisibility(View.VISIBLE);
                        mDistilledLabel.setTextColor(adapter.getColor());
                        mDistilledLabel.setVisibility(View.VISIBLE);
                    }else{
                        mDistilled.setVisibility(View.GONE);
                        mDistilledLabel.setVisibility(View.GONE);
                    }

                    String chemotype = adapter.getChemotype();
                    if (chemotype != null && chemotype.length() > 0){
                        mChemotype.setText(chemotype);
                        mChemotype.setVisibility(View.VISIBLE);
                        mChemotypeLabel.setTextColor(adapter.getColor());
                        mChemotypeLabel.setVisibility(View.VISIBLE);
                    }else{
                        mChemotype.setVisibility(View.GONE);
                        mChemotypeLabel.setVisibility(View.GONE);
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

                    String precautions = adapter.getPrecautions();
                    if (precautions != null && precautions.length() > 0){
                        mPrecautions.setText(precautions);
                        mPrecautions.setVisibility(View.VISIBLE);
                        mPrecautionsLabel.setTextColor(adapter.getColor());
                        mPrecautionsLabel.setVisibility(View.VISIBLE);
                    }else{
                        mPrecautions.setVisibility(View.GONE);
                        mPrecautionsLabel.setVisibility(View.GONE);
                    }
                    mRecipesLabel.setTextColor(adapter.getColor());

                }
                break;

            case 1:
                if (o instanceof Cursor){
                    Cursor data = (Cursor) o;
                    int nbRecipes = data.getCount();
                    if (nbRecipes > 0) {
                        mAdapter.changeCursor(data);
                        ViewGroup.LayoutParams layoutParams = mRecyclerView.getLayoutParams();
                        layoutParams.height = nbRecipes * getResources().getDimensionPixelSize(R.dimen.card_view_list_item_height);
                        mRecyclerView.setLayoutParams(layoutParams);
                        mRecipesLabel.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
                break;

            default:break;
        }



    }



    @Override
    public void onLoaderReset(Loader<Object> loader) {
        switch (loader.getId()){
            case 1:
                mAdapter.changeCursor(null);
                mRecipesLabel.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void reload() {
        super.reload();
        getLoaderManager().restartLoader(1, null, this);
    }
}
