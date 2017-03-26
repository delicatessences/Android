package fr.delicatessences.delicatessences.activities;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.editor.CustomEditText;
import fr.delicatessences.delicatessences.editor.MembershipView;
import fr.delicatessences.delicatessences.editor.SpinnerWithHint;
import fr.delicatessences.delicatessences.model.Category;
import fr.delicatessences.delicatessences.model.DatabaseHelper;
import fr.delicatessences.delicatessences.model.EORecipe;
import fr.delicatessences.delicatessences.model.EssentialOil;
import fr.delicatessences.delicatessences.model.Recipe;
import fr.delicatessences.delicatessences.model.Use;
import fr.delicatessences.delicatessences.model.VORecipe;
import fr.delicatessences.delicatessences.model.VegetalOil;

public class EditRecipeActivity extends EditActivity {

    private static final String STATE_NAME = "name";
    private static final String STATE_AUTHOR = "author";
    private static final String STATE_PREPARATION = "preparation";
    private static final String STATE_ESSENTIAL_OILS = "essential_oils";
    private static final String STATE_VEGETAL_OILS = "vegetal_oils";
    private static final String STATE_CATEGORY = "category";
    private static final String STATE_USE = "use";

    private CustomEditText mNameText;
    private CustomEditText mAuthorText;
    private CustomEditText mPreparationText;
    private MembershipView mEssentialOilsView;
    private MembershipView mVegetalOilsView;
    private SpinnerWithHint mCategorySpinner;
    private SpinnerWithHint mUseSpinner;
    private Recipe mRecipe;

    @Override
    protected int getLayout() {
        return R.layout.activity_edit_recipe;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mId = getId();
        if (mId > 0) {
            setTitle(getResources().getString(R.string.title_edit_recipe));
            try {
                DatabaseHelper helper = getHelper();
                Dao<Recipe, Integer> dao = helper.getRecipeDao();
                mRecipe = dao.queryForId(mId);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        mNameText = (CustomEditText) findViewById(R.id.name);
        mAuthorText = (CustomEditText) findViewById(R.id.author);
        mPreparationText = (CustomEditText) findViewById(R.id.preparation);
        mEssentialOilsView = (MembershipView) findViewById(R.id.essential_oils_view);
        mVegetalOilsView = (MembershipView) findViewById(R.id.vegetal_oils_view);
        mCategorySpinner = (SpinnerWithHint) findViewById(R.id.category);
        mUseSpinner = (SpinnerWithHint) findViewById(R.id.use);


        initializeView();

    }



    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_NAME, mNameText.getText().toString());
        savedInstanceState.putString(STATE_AUTHOR, mAuthorText.getText().toString());
        savedInstanceState.putString(STATE_PREPARATION, mPreparationText.getText().toString());
        savedInstanceState.putIntegerArrayList(STATE_ESSENTIAL_OILS, mEssentialOilsView.getMembers());
        savedInstanceState.putIntegerArrayList(STATE_VEGETAL_OILS, mVegetalOilsView.getMembers());
        savedInstanceState.putInt(STATE_CATEGORY, mCategorySpinner.getSelection());
        savedInstanceState.putInt(STATE_USE, mUseSpinner.getSelection());

        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mNameText.setText(savedInstanceState.getString(STATE_NAME), false);
        mAuthorText.setText(savedInstanceState.getString(STATE_AUTHOR), false);
        mPreparationText.setText(savedInstanceState.getString(STATE_PREPARATION), false);
        mEssentialOilsView.setMembers(savedInstanceState.getIntegerArrayList(STATE_ESSENTIAL_OILS), false);
        mVegetalOilsView.setMembers(savedInstanceState.getIntegerArrayList(STATE_VEGETAL_OILS), false);
        mCategorySpinner.setSelection(savedInstanceState.getInt(STATE_CATEGORY), false);
        mUseSpinner.setSelection(savedInstanceState.getInt(STATE_USE), false);
    }





    private void initializeView() {

        DatabaseHelper helper = getHelper();

        try {
            Dao<Category, Integer> dao = helper.getCategoryDao();
            QueryBuilder<Category, Integer> queryBuilder = dao.queryBuilder();
            CloseableIterator<Category> categoryIterator = dao.iterator(queryBuilder.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) categoryIterator.getRawResults();
            Cursor cursor = results.getRawCursor();
            mCategorySpinner.setMetaData(cursor);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Dao<Use, Integer> dao = helper.getUseDao();
            QueryBuilder<Use, Integer> queryBuilder = dao.queryBuilder();
            CloseableIterator<Use> useIterator = dao.iterator(queryBuilder.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) useIterator.getRawResults();
            Cursor cursor = results.getRawCursor();
            mUseSpinner.setMetaData(cursor);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Dao<EssentialOil, Integer> dao = helper.getEssentialOilDao();
            QueryBuilder<EssentialOil, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(EssentialOil.NAME_FIELD_NAME, true);
            CloseableIterator<EssentialOil> essentialIterator = dao.iterator(queryBuilder.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) essentialIterator.getRawResults();
            Cursor cursor = results.getRawCursor();
            mEssentialOilsView.setMetaData(cursor);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            Dao<VegetalOil, Integer> dao = helper.getVegetalOilDao();
            QueryBuilder<VegetalOil, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(VegetalOil.NAME_FIELD_NAME, true);
            CloseableIterator<VegetalOil> vegetalIterator = dao.iterator(queryBuilder.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) vegetalIterator.getRawResults();
            Cursor cursor = results.getRawCursor();
            mVegetalOilsView.setMetaData(cursor);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //edit mode
        if (mRecipe != null) {
            mNameText.setText(mRecipe.getName(), true);
            mAuthorText.setText(mRecipe.getAuthor(), true);
            mPreparationText.setText(mRecipe.getPreparation(), true);

            Category category = mRecipe.getCategory();
            if (category != null) {
                mCategorySpinner.setSelection(category.getId(), true);
            }

            Use use = mRecipe.getUse();
            if (use != null) {
                mUseSpinner.setSelection(use.getId(), true);
            }
            int mId = getId();
            try {
                Dao<EORecipe, Integer> eoRecipeDao = helper.getEORecipeDao();
                List<EORecipe> eoRecipes = eoRecipeDao.queryForEq(EORecipe.RECIPE_ID, mId);
                ArrayList<Integer> essentialOilIds = new ArrayList<>();
                for (EORecipe eoRecipe : eoRecipes) {
                    essentialOilIds.add(eoRecipe.getEssentialOil().getId());
                }
                mEssentialOilsView.setMembers(essentialOilIds, true);

                Dao<VORecipe, Integer> voRecipeDao = helper.getVORecipeDao();
                List<VORecipe> voRecipes = voRecipeDao.queryForEq(VORecipe.RECIPE_ID, mId);
                ArrayList<Integer> vegetalOilIds = new ArrayList<>();
                for (VORecipe voRecipe : voRecipes) {
                    vegetalOilIds.add(voRecipe.getVegetalOil().getId());
                }
                mVegetalOilsView.setMembers(vegetalOilIds, true);
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }

    }



    @Override
    protected void saveNew() throws SQLException {

        //user entered nothing - do nothing
        if (isEmpty()) {
            return;
        }

        //get the name
        String name = mNameText.getText().toString();

        //get the botanical name
        String author = mAuthorText.getText().toString();

        //get the distilled organ
        String preparation = mPreparationText.getText().toString();

        //get the properties
        List<Integer> essentialOilIds = mEssentialOilsView.getMembers();

        //get the indications
        List<Integer> vegetalOilIds = mVegetalOilsView.getMembers();


        //save to database
        final DatabaseHelper helper = getHelper();
        Dao<Category, Integer> categoryDao = helper.getCategoryDao();
        Category category = categoryDao.queryForId(mCategorySpinner.getSelection());
        Dao<Use, Integer> useDao = helper.getUseDao();
        Use use = useDao.queryForId(mUseSpinner.getSelection());
        final Recipe recipe = new Recipe(name, author, preparation, category, use);
        final List<EssentialOil> essentialOils = helper.getEssentialOils(essentialOilIds);
        final List<VegetalOil> vegetalOils = helper.getVegetalOils(vegetalOilIds);

        setFeedbackMessage(R.string.save_recipe);

        TransactionManager.callInTransaction(helper.getConnectionSource(),
                new Callable<Void>() {
                    public Void call() throws Exception {

                        Dao<Recipe, Integer> recipeDao = helper.getRecipeDao();
                        recipeDao.create(recipe);

                        Dao<EORecipe, Integer> eoRecipeDap = helper.getEORecipeDao();
                        for (EssentialOil oil : essentialOils) {
                            EORecipe eoRecipe = new EORecipe(oil, recipe);
                            eoRecipeDap.create(eoRecipe);
                        }

                        Dao<VORecipe, Integer> voRecipeDao = helper.getVORecipeDao();
                        for (VegetalOil oil : vegetalOils) {
                            VORecipe voRecipe = new VORecipe(oil, recipe);
                            voRecipeDao.create(voRecipe);
                        }


                        return null;
                    }
                });

        addToIndex(recipe);
    }


    private void addToIndex(Recipe recipe) {

        String preparation = recipe.getPreparation();

        Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                .setName(getIndexableName(recipe.getName(), recipe.getAuthor()))
                .setText(preparation != null ? preparation : "")
                .setUrl(recipe.getUrl())
                .build();

        FirebaseAppIndex.getInstance().update(indexable);
    }


    private String getIndexableName(String name, String author){
        StringBuilder sb = new StringBuilder();
        Resources resources = getResources();
        sb.append(resources.getString(R.string.recipe_of));
        sb.append(name != null ? name : "");
        if (author != null && author.length() > 0){
            sb.append(" (");
            sb.append(author);
            sb.append(")");
        }
        return sb.toString();
    }


    private void updateIndex(String name, String author, String preparation) {
        Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                .setName(getIndexableName(name, author))
                .setText(preparation != null ? preparation : "")
                .setUrl(mRecipe.getUrl())
                .build();
    }



    @Override
    protected boolean hasChanged() {

        return mNameText.hasChanged() ||
                mAuthorText.hasChanged() ||
                mPreparationText.hasChanged() ||
                mCategorySpinner.hasChanged() ||
                mUseSpinner.hasChanged() ||
                mEssentialOilsView.hasChanged() ||
                mVegetalOilsView.hasChanged();
    }

    @Override
    protected boolean isEmpty() {
        return mNameText.isEmpty() &&
                mAuthorText.isEmpty() &&
                mPreparationText.isEmpty() &&
                mCategorySpinner.isEmpty() &&
                mUseSpinner.isEmpty() &&
                mEssentialOilsView.isEmpty() &&
                mVegetalOilsView.isEmpty();
    }





    @Override
    protected void saveModifications() throws SQLException {

        if (!hasChanged()) {
            return;
        }

        //save to database
        final DatabaseHelper helper = getHelper();
        TransactionManager.callInTransaction(helper.getConnectionSource(),
                new Callable<Void>() {
                    public Void call() throws Exception {
                        int mId = getId();
                        Dao<Recipe, Integer> recipeDao = helper.getRecipeDao();
                        UpdateBuilder<Recipe, Integer> updateBuilder = recipeDao.updateBuilder();
                        updateBuilder.where().eq(Recipe.ID_FIELD_NAME, mId);
                        boolean canUpdate = false;
                        if (mNameText.hasChanged()) {
                            SelectArg selectArg = new SelectArg(mNameText.getText().toString());
                            updateBuilder.updateColumnValue(Recipe.NAME_FIELD_NAME, selectArg);
                            canUpdate = true;
                        }
                        if (mAuthorText.hasChanged()) {
                            SelectArg selectArg = new SelectArg(mAuthorText.getText().toString());
                            updateBuilder.updateColumnValue(Recipe.AUTHOR_FIELD_NAME, selectArg);
                            canUpdate = true;
                        }
                        if (mPreparationText.hasChanged()) {
                            SelectArg selectArg = new SelectArg(mPreparationText.getText().toString());
                            updateBuilder.updateColumnValue(Recipe.PREPARATION_FIELD_NAME, selectArg);
                            canUpdate = true;
                        }
                        if (mCategorySpinner.hasChanged()) {
                            Dao<Category, Integer> categoryDao = helper.getCategoryDao();
                            Category category = categoryDao.queryForId(mCategorySpinner.getSelection());
                            updateBuilder.updateColumnValue(Recipe.CATEGORY_FIELD_NAME, category);
                            canUpdate = true;
                        }
                        if (mUseSpinner.hasChanged()) {
                            Dao<Use, Integer> useDao = helper.getUseDao();
                            Use use = useDao.queryForId(mUseSpinner.getSelection());
                            updateBuilder.updateColumnValue(Recipe.USE_FIELD_NAME, use);
                            canUpdate = true;
                        }
                        if (canUpdate) {
                            updateBuilder.update();
                        }


                        Recipe recipe = recipeDao.queryForId(mId);
                        if (recipe == null) {
                            throw new NullPointerException("Cannot find recipe with id " + mId + " in database, aborting modifications");
                        }


                        if (mEssentialOilsView.hasChanged()) {
                            List<Integer> added = mEssentialOilsView.getAdded();
                            List<EssentialOil> addedEssentialOils = helper.getEssentialOils(added);
                            Dao<EORecipe, Integer> eoRecipeDao = helper.getEORecipeDao();
                            for (EssentialOil essentialOil : addedEssentialOils) {
                                EORecipe eoRecipe = new EORecipe(essentialOil, recipe);
                                eoRecipeDao.create(eoRecipe);
                            }

                            List<Integer> removed = mEssentialOilsView.getRemoved();
                            DeleteBuilder<EORecipe, Integer> deleteBuilder = eoRecipeDao.deleteBuilder();
                            deleteBuilder.where().in(EORecipe.OIL_ID, removed).and().eq(EORecipe.RECIPE_ID, mId);
                            deleteBuilder.delete();

                        }

                        if (mVegetalOilsView.hasChanged()) {
                            List<Integer> added = mVegetalOilsView.getAdded();
                            List<VegetalOil> addedVegetalOils = helper.getVegetalOils(added);
                            Dao<VORecipe, Integer> voRecipeDao = helper.getVORecipeDao();
                            for (VegetalOil vegetalOil : addedVegetalOils) {
                                VORecipe voRecipe = new VORecipe(vegetalOil, recipe);
                                voRecipeDao.create(voRecipe);
                            }

                            List<Integer> removed = mVegetalOilsView.getRemoved();
                            DeleteBuilder<VORecipe, Integer> deleteBuilder = voRecipeDao.deleteBuilder();
                            deleteBuilder.where().in(VORecipe.OIL_ID, removed).and().eq(VORecipe.RECIPE_ID, mId);
                            deleteBuilder.delete();

                        }


                        return null;
                    }
                });


        setFeedbackMessage(R.string.edit_recipe);
        updateIndex(mNameText.getText().toString(), mAuthorText.getText().toString(), mPreparationText.getText().toString());

    }

}
