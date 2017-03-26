package fr.delicatessences.delicatessences.model.index;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;

import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.model.Bottle;
import fr.delicatessences.delicatessences.model.DatabaseHelper;
import fr.delicatessences.delicatessences.model.EssentialOil;
import fr.delicatessences.delicatessences.model.Recipe;
import fr.delicatessences.delicatessences.model.VegetalOil;


public class AppIndexingService extends OrmLiteBaseIntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AppIndexingService(String name) {
        super(name);
    }

    public AppIndexingService(){
        super("Delicatessences App Indexing Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ArrayList<Indexable> indexables = new ArrayList<>();

        indexBottles(indexables);
        indexEssentialOils(indexables);
        indexVegetalOils(indexables);
        indexRecipes(indexables);

        if (indexables.size() > 0) {
            Indexable[] notesArr = new Indexable[indexables.size()];
            notesArr = indexables.toArray(notesArr);

            // batch insert indexable notes into index
            FirebaseAppIndex.getInstance().update(notesArr);
        }

    }


    private void indexBottles(List<Indexable> indexables){
        DatabaseHelper helper = (DatabaseHelper) getHelper();

        try {
            Dao<Bottle, Integer> bottleDao = helper.getBottleDao();
            Dao<EssentialOil, Integer> essentialOilDao = helper.getEssentialOilDao();
            List<Bottle> bottles = bottleDao.queryForAll();
            for (Bottle bottle : bottles){
                EssentialOil essentialOil = essentialOilDao.queryForId(bottle.getEssentialOil().getId());
                if (essentialOil != null) {
                    String description = essentialOil.getDescription();
                    Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                            .setName(getBottleIndexableName(essentialOil.getName(), bottle.getBrand()))
                            .setText(description != null ? description : "")
                            .setUrl(bottle.getUrl())
                            .build();

                    indexables.add(indexable);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getBottleIndexableName(String name, String brand){
        StringBuilder sb = new StringBuilder();
        Resources resources = getResources();
        String withoutBrand = resources.getString(R.string.without_brand);
        sb.append(resources.getString(R.string.bottle_of));
        sb.append(name != null ? name : "");
        sb.append(" " + ((brand != null && brand.length() > 0) ? "(" + brand + ")" : withoutBrand));
        return sb.toString();
    }


    private String getEssentialOilIndexableName(String name){
        Resources resources = getResources();
        String namePrefix = resources.getString(R.string.eo_of);
        return namePrefix + (name != null ? name : "");
    }


    private String getVegetalOilIndexableName(String name){
        Resources resources = getResources();
        String namePrefix = resources.getString(R.string.vo_of);
        return namePrefix + (name != null ? name : "");
    }


    private String getRecipeIndexableName(String name, String author){
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


    private void indexEssentialOils(List<Indexable> indexables){
        DatabaseHelper helper = (DatabaseHelper) getHelper();

        try {
            Dao<EssentialOil, Integer> essentialOilDao = helper.getEssentialOilDao();
            List<EssentialOil> essentialOils = essentialOilDao.queryForAll();
            for (EssentialOil essentialOil : essentialOils){
                if (essentialOil.isFavorite() || !essentialOil.isReadOnly()) {
                    String description = essentialOil.getDescription();
                    Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                            .setName(getEssentialOilIndexableName(essentialOil.getName()))
                            .setText(description != null ? description : "")
                            .setUrl(essentialOil.getUrl())
                            .build();

                    indexables.add(indexable);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void indexVegetalOils(List<Indexable> indexables){
        DatabaseHelper helper = (DatabaseHelper) getHelper();

        try {
            Dao<VegetalOil, Integer> vegetalOilDao = helper.getVegetalOilDao();
            List<VegetalOil> vegetalOils = vegetalOilDao.queryForAll();
            for (VegetalOil vegetalOil : vegetalOils){
                if (vegetalOil.isFavorite() || !vegetalOil.isReadOnly()) {
                    String description = vegetalOil.getDescription();
                    Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                            .setName(getVegetalOilIndexableName(vegetalOil.getName()))
                            .setText(description != null ? description : "")
                            .setUrl(vegetalOil.getUrl())
                            .build();

                    indexables.add(indexable);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void indexRecipes(List<Indexable> indexables){
        DatabaseHelper helper = (DatabaseHelper) getHelper();

        try {
            Dao<Recipe, Integer> recipeDao = helper.getRecipeDao();
            List<Recipe> recipes = recipeDao.queryForAll();
            for (Recipe recipe : recipes){
                if (recipe.isFavorite()) {
                    String preparation = recipe.getPreparation();
                    Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                            .setName(getRecipeIndexableName(recipe.getName(), recipe.getAuthor()))
                            .setText(preparation != null ? preparation : "")
                            .setUrl(recipe.getUrl())
                            .build();

                    indexables.add(indexable);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
