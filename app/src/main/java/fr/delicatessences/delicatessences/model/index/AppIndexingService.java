package fr.delicatessences.delicatessences.model.index;

import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.delicatessences.delicatessences.adapters.BottleSheetAdapter;
import fr.delicatessences.delicatessences.adapters.SheetAdapter;
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
                    Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                            .setName(essentialOil.getName() + " - " + bottle.getBrand())
                            .setText(essentialOil.getDescription())
                            .setUrl(bottle.getUrl())
                            .build();

                    indexables.add(indexable);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void indexEssentialOils(List<Indexable> indexables){
        DatabaseHelper helper = (DatabaseHelper) getHelper();

        try {
            Dao<EssentialOil, Integer> essentialOilDao = helper.getEssentialOilDao();
            List<EssentialOil> essentialOils = essentialOilDao.queryForAll();
            for (EssentialOil essentialOil : essentialOils){
                if (essentialOil.isFavorite() || !essentialOil.isReadOnly()) {
                    Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                            .setName(essentialOil.getName())
                            .setText(essentialOil.getDescription())
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
                    Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                            .setName(vegetalOil.getName())
                            .setText(vegetalOil.getDescription())
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
                    Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                            .setName(recipe.getName() + " - " + recipe.getAuthor())
                            .setText(recipe.getPreparation())
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
