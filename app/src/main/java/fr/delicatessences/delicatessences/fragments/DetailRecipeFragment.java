package fr.delicatessences.delicatessences.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
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
import fr.delicatessences.delicatessences.activities.EditRecipeActivity;
import fr.delicatessences.delicatessences.activities.MainActivity;
import fr.delicatessences.delicatessences.activities.OrmLiteBaseActionBarActivity;
import fr.delicatessences.delicatessences.adapters.RecipeSheetAdapter;
import fr.delicatessences.delicatessences.adapters.SheetAdapter;
import fr.delicatessences.delicatessences.loaders.CustomAsyncTaskLoader;
import fr.delicatessences.delicatessences.loaders.FavoriteWorkerTask;
import fr.delicatessences.delicatessences.model.Category;
import fr.delicatessences.delicatessences.model.DatabaseHelper;
import fr.delicatessences.delicatessences.model.EORecipe;
import fr.delicatessences.delicatessences.model.EssentialOil;
import fr.delicatessences.delicatessences.model.Recipe;
import fr.delicatessences.delicatessences.model.Use;
import fr.delicatessences.delicatessences.model.VORecipe;
import fr.delicatessences.delicatessences.model.VegetalOil;

public class DetailRecipeFragment extends DetailFragment {

    private MenuItem mFavoriteMenuItem;
    private boolean mFavorite;
    private ImageView mCategoryImage;
    private TextView mCategoryName;
    private ViewGroup mCategoryLayout;
    private ImageView mUseImage;
    private TextView mUseName;
    private ViewGroup mUseLayout;
    private TextView mAuthor;
    private TextView mAuthorLabel;
    private TextView mCompositionLabel;
    private TextView mComposition;
    private TextView mPreparation;
    private TextView mPreparationLabel;


    @Override
    protected int getLayout() {
        return R.layout.fragment_detail_recipe;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        assert view != null;
        mCategoryImage = (ImageView) view.findViewById(R.id.category_image);
        mCategoryName = (TextView) view.findViewById(R.id.category_label);
        mCategoryLayout = (ViewGroup) view.findViewById(R.id.category);
        mUseImage = (ImageView) view.findViewById(R.id.use_image);
        mUseName = (TextView) view.findViewById(R.id.use_label);
        mUseLayout = (ViewGroup) view.findViewById(R.id.use);
        mAuthor = (TextView) view.findViewById(R.id.author);
        mAuthorLabel = (TextView) view.findViewById(R.id.author_label);
        mComposition = (TextView) view.findViewById(R.id.composition);
        mCompositionLabel = (TextView) view.findViewById(R.id.composition_label);
        mPreparation = (TextView) view.findViewById(R.id.preparation);
        mPreparationLabel = (TextView) view.findViewById(R.id.preparation_label);

        return view;
    }


    public static DetailRecipeFragment newInstance(int id) {
        DetailRecipeFragment fragment = new DetailRecipeFragment();

        Bundle args = new Bundle();
        args.putInt(MainActivity.EXTRA_ID, id);
        fragment.setArguments(args);

        return fragment;
    }


    //up button action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        MainActivity activity = (MainActivity) getActivity();
        int mId = getmId();

        switch (item.getItemId()) {
            case android.R.id.home:
                activity.onBackPressed();
                return true;

            case R.id.action_favorite:
                FavoriteWorkerTask task = new FavoriteWorkerTask(activity, ViewType.RECIPES, item);
                task.execute(mId);
                return true;


            case R.id.action_edit:
                Intent intent = new Intent(activity, EditRecipeActivity.class);
                intent.putExtra(MainActivity.EXTRA_ID, mId);
                startActivity(intent);
                return true;

            case R.id.action_delete:
                showDialog(activity, getResources().getString(R.string.delete_recipe_warning));
                return true;

            default:
                return false;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_detail_menu, menu);
        mFavoriteMenuItem = menu.findItem(R.id.action_favorite);
        mFavoriteMenuItem.setIcon(mFavorite ? R.drawable.ic_star_24dp : R.drawable.ic_star_outline_24dp);
        super.onCreateOptionsMenu(menu, inflater);
    }



    private void showDialog(final MainActivity activity, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(message);
        final Resources resources = getResources();
        builder.setPositiveButton(resources.getString(R.string.action_ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            deleteRecipe();
                            activity.showFeedbackMessage(resources.getString(R.string.delete_recipe));
                            activity.showList(ViewType.RECIPES);
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


    private void deleteRecipe() throws SQLException {
        MainActivity activity = (MainActivity) getActivity();
        final DatabaseHelper helper = activity.getHelper();

        TransactionManager.callInTransaction(helper.getConnectionSource(),
                new Callable<Void>() {
                    public Void call() throws Exception {
                        int mId = getmId();
                        Dao<EORecipe, Integer> eoRecipeDao = helper.getEORecipeDao();
                        DeleteBuilder<EORecipe, Integer> eoDeleteBuilder = eoRecipeDao.deleteBuilder();
                        eoDeleteBuilder.where().eq(EORecipe.RECIPE_ID, mId);
                        eoDeleteBuilder.delete();

                        Dao<VORecipe, Integer> voRecipeDao = helper.getVORecipeDao();
                        DeleteBuilder<VORecipe, Integer> voDeleteBuilder = voRecipeDao.deleteBuilder();
                        voDeleteBuilder.where().eq(VORecipe.RECIPE_ID, mId);
                        voDeleteBuilder.delete();

                        Dao<Recipe, Integer> recipeDao = helper.getRecipeDao();
                        DeleteBuilder<Recipe, Integer> recipeDeleteBuilder = recipeDao.deleteBuilder();
                        recipeDeleteBuilder.where().eq(Recipe.ID_FIELD_NAME, mId);
                        recipeDeleteBuilder.delete();

                        return null;
                    }

                });

        FirebaseAppIndex.getInstance().remove(mIndexedURL);
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
                    Dao<Recipe, Integer> oilDao = helper.getRecipeDao();
                    int mId = getmId();
                    Recipe recipe = oilDao.queryForId(mId);
                    if (recipe != null) {

                        prepareIndex(recipe);

                        Dao<Category, Integer> categoryDao = helper.getCategoryDao();
                        Category category = null;
                        if (recipe.getCategory() != null){
                            category = categoryDao.queryForId(recipe.getCategory().getId());
                        }

                        Dao<Use, Integer> useDao = helper.getUseDao();
                        Use use = null;
                        if (recipe.getUse() != null){
                            use = useDao.queryForId(recipe.getUse().getId());
                        }
                        List<EssentialOil> essentialOils = helper.getEssentialOils(mId);
                        List<String> essentialOilNames = new ArrayList<>(essentialOils.size());
                        for (EssentialOil essentialOil : essentialOils){
                            essentialOilNames.add(essentialOil.getName());
                        }
                        List<VegetalOil> vegetalOils = helper.getVegetalOils(mId);
                        List<String> vegetalOilNames = new ArrayList<>(vegetalOils.size());
                        for (VegetalOil vegetalOil : vegetalOils){
                            vegetalOilNames.add(vegetalOil.getName());
                        }

                        adapter = new RecipeSheetAdapter(recipe.getImage(), Color.parseColor(recipe.getColor()),
                                recipe.getName(), recipe.getAuthor(), recipe.getPreparation(), recipe.isFavorite(),
                                category, use, recipe.getCreation(), essentialOilNames, vegetalOilNames);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return adapter;
            }
        };
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

    private void prepareIndex(Recipe recipe) {
        mIndexedURL = recipe.getUrl();
        String name = recipe.getName();
        StringBuilder sb = new StringBuilder();
        Resources resources = getResources();
        sb.append(resources.getString(R.string.recipe_of));
        sb.append(name != null ? name : "");
        String author = recipe.getAuthor();
        if (author != null && author.length() > 0){
            sb.append(" (");
            sb.append(author);
            sb.append(")");
        }
        mIndexedName = sb.toString();
        String preparation = recipe.getPreparation();
        mIndexedText = preparation != null ? preparation : "";
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object o) {
        super.onLoadFinished(loader, o);
        final OrmLiteBaseActionBarActivity activity = (OrmLiteBaseActionBarActivity) getActivity();
        if (o instanceof RecipeSheetAdapter) {
            RecipeSheetAdapter adapter = (RecipeSheetAdapter) o;
            Resources resources = getResources();
            String name = adapter.getName();
            TextView mTitle = getTitle();
            mTitle.setText(name != null && name.length() > 0 ? name : resources.getString(R.string.without_name));
            mTitle.setVisibility(View.VISIBLE);
            mFavorite = adapter.isFavorite();
            if (mFavoriteMenuItem != null){
                mFavoriteMenuItem.setIcon(mFavorite ? R.drawable.ic_star_24dp : R.drawable.ic_star_outline_24dp);
            }
            Category category = adapter.getCategory();
            if (category != null){
                int resource = resources.getIdentifier(category.getImage(), "drawable", activity.getPackageName());
                mCategoryImage.setImageDrawable(ContextCompat.getDrawable(activity, resource));
                mCategoryName.setText(category.getName());
                mCategoryLayout.setVisibility(View.VISIBLE);
            }else{
                mCategoryLayout.setVisibility(View.GONE);
            }

            Use use = adapter.getUse();
            if (use != null){
                int resource = resources.getIdentifier(use.getImage(), "drawable", activity.getPackageName());
                mUseImage.setImageDrawable(ContextCompat.getDrawable(activity, resource));
                mUseName.setText(use.getName());
                mUseLayout.setVisibility(View.VISIBLE);
            }else{
                mUseLayout.setVisibility(View.GONE);
            }

            String author = adapter.getAuthor();
            if (author != null && author.length() > 0){
                mAuthor.setText(author);
                mAuthor.setVisibility(View.VISIBLE);
                mAuthorLabel.setTextColor(adapter.getColor());
                mAuthorLabel.setVisibility(View.VISIBLE);
            }else{
                mAuthor.setVisibility(View.GONE);
                mAuthorLabel.setVisibility(View.GONE);
            }

            StringBuilder sb = new StringBuilder();
            for (String essentialOil : adapter.getEssentialOils()){
                if (sb.length() > 0){
                    sb.append("\n");
                }
                sb.append("\u2022 " +
                        "").append(essentialOil);
            }

            for (String vegetalOil : adapter.getVegetalOils()){
                if (sb.length() > 0){
                    sb.append("\n");
                }
                sb.append("\u2022 ").append(vegetalOil);
            }
            if (sb.length() > 0){
                mComposition.setText(sb.toString());
                mComposition.setVisibility(View.VISIBLE);
                mCompositionLabel.setTextColor(adapter.getColor());
                mCompositionLabel.setVisibility(View.VISIBLE);
            }
            else{
                mComposition.setVisibility(View.GONE);
                mCompositionLabel.setVisibility(View.GONE);
            }

            String preparation = adapter.getPreparation();
            if (preparation != null && preparation.length() > 0){
                mPreparation.setText(preparation);
                mPreparation.setVisibility(View.VISIBLE);
                mPreparationLabel.setTextColor(adapter.getColor());
                mPreparationLabel.setVisibility(View.VISIBLE);
            }else{
                mPreparation.setVisibility(View.GONE);
                mPreparationLabel.setVisibility(View.GONE);
            }


        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
