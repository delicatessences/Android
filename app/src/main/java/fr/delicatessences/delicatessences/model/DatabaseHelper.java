package fr.delicatessences.delicatessences.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "delicatessences.db";
    private static final int DATABASE_VERSION = 3;
    private static final String VERSION = "version";
    public static final String BOTTLE_TABLE_NAME = "bottles";
    public static final String ESSENTIAL_OIL_TABLE_NAME = "essential_oils";
    public static final String RECIPE_TABLE_NAME = "recipes";
    public static final String USE_TABLE_NAME = "uses";
    private static final String PATH_TO_JSON = "json";
    private static final String PATH_TO_ESSENTIAL_OILS = "essential_oils";
    private static final String PATH_TO_VEGETAL_OILS = "vegetal_oils";
    private static final String PATH_TO_ESSENTIAL_PROPERTIES = PATH_TO_JSON + "/essential_properties.json";
    private static final String PATH_TO_ESSENTIAL_INDICATIONS = PATH_TO_JSON + "/essential_indications.json";
    private static final String PATH_TO_VEGETAL_PROPERTIES = PATH_TO_JSON + "/vegetal_properties.json";
    private static final String PATH_TO_VEGETAL_INDICATIONS = PATH_TO_JSON + "/vegetal_indications.json";
    private static final String PATH_TO_ADMINISTRATIONS = PATH_TO_JSON + "/administrations.json";
    private static final String PATH_TO_USES = PATH_TO_JSON + "/uses.json";
    private static final String PATH_TO_CATEGORIES = PATH_TO_JSON + "/categories.json";
    private static final String PATH_TO_RECIPES = PATH_TO_JSON + "/recipes.json";



    // the DAO objects
    private Dao<EssentialOil, Integer> essentialDao = null;
    private Dao<VegetalOil, Integer> vegetalDao = null;
    private Dao<Bottle, Integer> bottleDao = null;
    private Dao<Recipe, Integer> recipeDao = null;
    private Dao<Category, Integer> categoryDao = null;
    private Dao<Use, Integer> useDao = null;
    private Dao<Administration, Integer> administrationDao = null;
    private Dao<EssentialProperty, Integer> essentialPropertyDao = null;
    private Dao<VegetalProperty, Integer> vegetalPropertyDao = null;
    private Dao<EssentialIndication, Integer> essentialIndicationDao = null;
    private Dao<VegetalIndication, Integer> vegetalIndicationDao = null;
    private Dao<EOAdministration, Integer> eOAdministrationDao = null;
    private Dao<EOIndication, Integer> eOIndicationDao = null;
    private Dao<EOProperty, Integer> eOPropertyDao = null;
    private Dao<EORecipe, Integer> eORecipeDao = null;
    private Dao<VOIndication, Integer> vOIndicationDao = null;
    private Dao<VOProperty, Integer> vOPropertyDao = null;
    private Dao<VORecipe, Integer> vORecipeDao = null;
    private Dao<Configuration, Integer> configurationDao = null;

    //the queries objects
    private PreparedQuery<EssentialProperty> essentialPropertiesQuery = null;
    private PreparedQuery<EssentialIndication> essentialIndicationsQuery = null;
    private PreparedQuery<VegetalProperty> vegetalPropertiesQuery = null;
    private PreparedQuery<VegetalIndication> vegetalIndicationsQuery = null;
    private PreparedQuery<Administration> administrationsQuery = null;
    private PreparedQuery<EssentialOil> essentialOilsQuery = null;
    private PreparedQuery<VegetalOil> vegetalOilsQuery = null;
    private PreparedQuery<Recipe> recipesQuery = null;

    //fixed objects
    private List<String> categories;
    private List<String> uses;

    private final Context context;
    private static final AtomicInteger usageCounter = new AtomicInteger(0);

    private static DatabaseHelper helper = null;





    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Get the helper, possibly constructing it if necessary. For each call to this method, there should be 1 and only 1
     * call to {@link #close()}.
     */
    public static synchronized DatabaseHelper getHelper(Context context) {
        if (helper == null) {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }

        usageCounter.incrementAndGet();
        return helper;
    }




    private void initializeDatabase() {

        try {
            initializeConfiguration();
            initializeEssentialProperties();
            initializeEssentialIndications();
            initializeVegetalProperties();
            initializeVegetalIndications();
            initializeAdministrations();
            initializeCategories();
            initializeUses();
            initializeEssentialOils();
            initializeVegetalOils();
            initializeRecipes();
        }catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }




    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");

            //creating tables
            TableUtils.createTable(connectionSource, Configuration.class);
            TableUtils.createTable(connectionSource, Administration.class);
            TableUtils.createTable(connectionSource, Category.class);
            TableUtils.createTable(connectionSource, Use.class);
            TableUtils.createTable(connectionSource, EssentialProperty.class);
            TableUtils.createTable(connectionSource, VegetalProperty.class);
            TableUtils.createTable(connectionSource, EssentialIndication.class);
            TableUtils.createTable(connectionSource, VegetalIndication.class);
            TableUtils.createTable(connectionSource, Bottle.class);
            TableUtils.createTable(connectionSource, EssentialOil.class);
            TableUtils.createTable(connectionSource, VegetalOil.class);
            TableUtils.createTable(connectionSource, Recipe.class);
            TableUtils.createTable(connectionSource, EOAdministration.class);
            TableUtils.createTable(connectionSource, EOIndication.class);
            TableUtils.createTable(connectionSource, EOProperty.class);
            TableUtils.createTable(connectionSource, EORecipe.class);
            TableUtils.createTable(connectionSource, VOIndication.class);
            TableUtils.createTable(connectionSource, VOProperty.class);
            TableUtils.createTable(connectionSource, VORecipe.class);

            //populating tables
            initializeDatabase();

            Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate");
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.i(DatabaseHelper.class.getName(), "onUpgrade");
        if (oldVersion == 1 && newVersion > 1){
                permuteLavandes();
        }

        updateEssentialOils(oldVersion, newVersion);
        updateVegetalOils(oldVersion, newVersion);
    }




    private void permuteLavandes(){
        try {
                Dao<EORecipe, Integer> dao = getEORecipeDao();

                //permute lavande aspic - lavande vraie (init error)
                UpdateBuilder<EORecipe, Integer> updateBuilder = dao.updateBuilder();
                updateBuilder.where().eq(EORecipe.OIL_ID, 14);
                updateBuilder.updateColumnValue(EORecipe.OIL_ID, 999);
                updateBuilder.update();

                updateBuilder = dao.updateBuilder();
                updateBuilder.where().eq(EORecipe.OIL_ID, 15);
                updateBuilder.updateColumnValue(EORecipe.OIL_ID, 14);
                updateBuilder.update();

                updateBuilder = dao.updateBuilder();
                updateBuilder.where().eq(EORecipe.OIL_ID, 999);
                updateBuilder.updateColumnValue(EORecipe.OIL_ID, 15);
                updateBuilder.update();

        } catch (SQLException e) {
            Log.i(DatabaseHelper.class.getName(), "permuteLavandes failed");
        }
    }



    //DAOs
    public Dao<EssentialOil, Integer> getEssentialOilDao() throws SQLException {
        if (essentialDao == null) {
            essentialDao = getDao(EssentialOil.class);
        }
        return essentialDao;
    }

    public Dao<VegetalOil, Integer> getVegetalOilDao() throws SQLException {
        if (vegetalDao == null) {
            vegetalDao = getDao(VegetalOil.class);
        }
        return vegetalDao;
    }

    public Dao<Bottle, Integer> getBottleDao() throws SQLException {
        if (bottleDao == null) {
            bottleDao = getDao(Bottle.class);
        }
        return bottleDao;
    }

    public Dao<Recipe, Integer> getRecipeDao() throws SQLException {
        if (recipeDao == null) {
            recipeDao = getDao(Recipe.class);
        }
        return recipeDao;
    }

    public Dao<Category, Integer> getCategoryDao() throws SQLException {
        if (categoryDao == null) {
            categoryDao = getDao(Category.class);
        }
        return categoryDao;
    }

    public Dao<Use, Integer> getUseDao() throws SQLException {
        if (useDao == null) {
            useDao = getDao(Use.class);
        }
        return useDao;
    }

    public Dao<Configuration, Integer> getConfigurationDao() throws SQLException {
        if (configurationDao == null) {
            configurationDao = getDao(Configuration.class);
        }
        return configurationDao;
    }


    public Dao<Administration, Integer> getAdministrationDao() throws SQLException  {
        if (administrationDao == null) {
            administrationDao = getDao(Administration.class);
        }
        return administrationDao;
    }


    public Dao<EssentialProperty, Integer> getEssentialPropertyDao() throws SQLException {
        if (essentialPropertyDao == null) {
            essentialPropertyDao = getDao(EssentialProperty.class);
        }
        return essentialPropertyDao;
    }


    public Dao<VegetalProperty, Integer> getVegetalPropertyDao() throws SQLException {
        if (vegetalPropertyDao == null) {
            vegetalPropertyDao = getDao(VegetalProperty.class);
        }
        return vegetalPropertyDao;
    }


    public Dao<EssentialIndication, Integer> getEssentialIndicationDao() throws SQLException {
        if (essentialIndicationDao == null) {
            essentialIndicationDao = getDao(EssentialIndication.class);
        }
        return essentialIndicationDao;
    }


    public Dao<VegetalIndication, Integer> getVegetalIndicationDao() throws SQLException {
        if (vegetalIndicationDao == null) {
            vegetalIndicationDao = getDao(VegetalIndication.class);
        }
        return vegetalIndicationDao;
    }


    public Dao<EOAdministration, Integer> getEOAdministrationDao() throws SQLException {
        if (eOAdministrationDao == null) {
            eOAdministrationDao = getDao(EOAdministration.class);
        }
        return eOAdministrationDao;
    }


    public Dao<EOIndication, Integer> getEOIndicationDao() throws SQLException {
        if (eOIndicationDao == null) {
            eOIndicationDao = getDao(EOIndication.class);
        }
        return eOIndicationDao;
    }


    public Dao<EOProperty, Integer> getEOPropertyDao() throws SQLException {
        if (eOPropertyDao == null) {
            eOPropertyDao = getDao(EOProperty.class);
        }
        return eOPropertyDao;
    }


    public Dao<EORecipe, Integer> getEORecipeDao() throws SQLException {
        if (eORecipeDao == null) {
            eORecipeDao = getDao(EORecipe.class);
        }
        return eORecipeDao;
    }


    public Dao<VOIndication, Integer> getVOIndicationDao() throws SQLException {
        if (vOIndicationDao == null) {
            vOIndicationDao = getDao(VOIndication.class);
        }
        return vOIndicationDao;
    }


    public Dao<VOProperty, Integer> getVOPropertyDao() throws SQLException {
        if (vOPropertyDao == null) {
            vOPropertyDao = getDao(VOProperty.class);
        }
        return vOPropertyDao;
    }


    public Dao<VORecipe, Integer> getVORecipeDao() throws SQLException {
        if (vORecipeDao == null) {
            vORecipeDao = getDao(VORecipe.class);
        }
        return vORecipeDao;
    }







    /**
     *
     *
     *
     *
     * Queries
     *
     *
     *
     *
     *
     */



    //Query for the properties of an essential oil
    public List<EssentialProperty> getEssentialProperties(int id) throws SQLException {
        if (essentialPropertiesQuery == null) {
            essentialPropertiesQuery = makeEssentialPropertiesQuery();
        }

        essentialPropertiesQuery.setArgumentHolderValue(0, id);
        Dao<EssentialProperty,Integer> essentialPropertyDao = getEssentialPropertyDao();
        return essentialPropertyDao.query(essentialPropertiesQuery);
    }

    //Prepare query
    private PreparedQuery<EssentialProperty> makeEssentialPropertiesQuery() throws SQLException {
        Dao<EOProperty,Integer> eoPropertyDao = getEOPropertyDao();
        QueryBuilder<EOProperty, Integer> eoPropertyQb = eoPropertyDao.queryBuilder();
        eoPropertyQb.selectColumns(EOProperty.PROPERTY_ID);
        SelectArg selectArg = new SelectArg();
        eoPropertyQb.where().eq(EOProperty.OIL_ID, selectArg);

        Dao<EssentialProperty,Integer> essentialPropertyDao = getEssentialPropertyDao();
        QueryBuilder<EssentialProperty, Integer> propertyQb = essentialPropertyDao.queryBuilder();
        propertyQb.where().in(EssentialProperty.ID_FIELD_NAME, eoPropertyQb);
        return propertyQb.prepare();
    }







    //Query for the indications of an essential oil
    public List<EssentialIndication> getEssentialIndications(int id) throws SQLException {
        if (essentialIndicationsQuery == null) {
            essentialIndicationsQuery = makeEssentialIndicationsQuery();
        }

        essentialIndicationsQuery.setArgumentHolderValue(0, id);
        Dao<EssentialIndication,Integer> essentialIndicationDao = getEssentialIndicationDao();
        return essentialIndicationDao.query(essentialIndicationsQuery);
    }

    //Prepare query
    private PreparedQuery<EssentialIndication> makeEssentialIndicationsQuery() throws SQLException {
        Dao<EOIndication, Integer> eoIndicationDao = getEOIndicationDao();
        QueryBuilder<EOIndication, Integer> eoIndicationQb = eoIndicationDao.queryBuilder();
        eoIndicationQb.selectColumns(EOIndication.INDICATION_ID);
        SelectArg selectArg = new SelectArg();
        eoIndicationQb.where().eq(EOIndication.OIL_ID, selectArg);

        Dao<EssentialIndication,Integer> essentialIndicationDao = getEssentialIndicationDao();
        QueryBuilder<EssentialIndication, Integer> indicationQb = essentialIndicationDao.queryBuilder();
        indicationQb.where().in(EssentialIndication.ID_FIELD_NAME, eoIndicationQb);
        return indicationQb.prepare();
    }








    //Query for the properties of an vegetal oil
    public List<VegetalProperty> getVegetalProperties(int id) throws SQLException {
        if (vegetalPropertiesQuery == null) {
            vegetalPropertiesQuery = makeVegetalPropertiesQuery();
        }

        vegetalPropertiesQuery.setArgumentHolderValue(0, id);
        Dao<VegetalProperty,Integer> vegetalPropertyDao = getVegetalPropertyDao();
        return vegetalPropertyDao.query(vegetalPropertiesQuery);
    }

    //Prepare query
    private PreparedQuery<VegetalProperty> makeVegetalPropertiesQuery() throws SQLException {
        Dao<VOProperty,Integer> voPropertyDao = getVOPropertyDao();
        QueryBuilder<VOProperty, Integer> voPropertyQb = voPropertyDao.queryBuilder();
        voPropertyQb.selectColumns(VOProperty.PROPERTY_ID);
        SelectArg selectArg = new SelectArg();
        voPropertyQb.where().eq(VOProperty.OIL_ID, selectArg);

        Dao<VegetalProperty,Integer> vegetalPropertyDao = getVegetalPropertyDao();
        QueryBuilder<VegetalProperty, Integer> propertyQb = vegetalPropertyDao.queryBuilder();
        propertyQb.where().in(VegetalProperty.ID_FIELD_NAME, voPropertyQb);
        return propertyQb.prepare();
    }







    //Query for the indications of an vegetal oil
    public List<VegetalIndication> getVegetalIndications(int id) throws SQLException {
        if (vegetalIndicationsQuery == null) {
            vegetalIndicationsQuery = makeVegetalIndicationsQuery();
        }

        vegetalIndicationsQuery.setArgumentHolderValue(0, id);
        Dao<VegetalIndication,Integer> vegetalIndicationDao = getVegetalIndicationDao();
        return vegetalIndicationDao.query(vegetalIndicationsQuery);
    }

    //Prepare query
    private PreparedQuery<VegetalIndication> makeVegetalIndicationsQuery() throws SQLException {
        Dao<VOIndication, Integer> voIndicationDao = getVOIndicationDao();
        QueryBuilder<VOIndication, Integer> voIndicationQb = voIndicationDao.queryBuilder();
        voIndicationQb.selectColumns(VOIndication.INDICATION_ID);
        SelectArg selectArg = new SelectArg();
        voIndicationQb.where().eq(VOIndication.OIL_ID, selectArg);

        Dao<VegetalIndication,Integer> vegetalIndicationDao = getVegetalIndicationDao();
        QueryBuilder<VegetalIndication, Integer> indicationQb = vegetalIndicationDao.queryBuilder();
        indicationQb.where().in(VegetalIndication.ID_FIELD_NAME, voIndicationQb);
        return indicationQb.prepare();
    }






    //Query for the administrations of an essential oil
    public List<Administration> getAdministrations(int id) throws SQLException {
        if (administrationsQuery == null) {
            administrationsQuery = makeAdministrationsQuery();
        }

        administrationsQuery.setArgumentHolderValue(0, id);
        Dao<Administration, Integer> administrationDao = getAdministrationDao();
        return administrationDao.query(administrationsQuery);
    }

    //Prepare query
    private PreparedQuery<Administration> makeAdministrationsQuery() throws SQLException {
        Dao<EOAdministration,Integer> eOAdministrationDao = getEOAdministrationDao();
        QueryBuilder<EOAdministration, Integer> eoAdministrationQb = eOAdministrationDao.queryBuilder();
        eoAdministrationQb.selectColumns(EOAdministration.ADMINISTRATION_ID);
        SelectArg selectArg = new SelectArg();
        eoAdministrationQb.where().eq(EOAdministration.OIL_ID, selectArg);

        Dao<Administration, Integer> administrationDao = getAdministrationDao();
        QueryBuilder<Administration, Integer> administrationQb = administrationDao.queryBuilder();
        administrationQb.where().in(Administration.ID_FIELD_NAME, eoAdministrationQb);
        return administrationQb.prepare();
    }






    //Query for the essential oils of a recipe
    public List<EssentialOil> getEssentialOils(int id) throws SQLException {
        if (essentialOilsQuery == null) {
            essentialOilsQuery = makeEssentialOilForRecipeQuery();
        }

        essentialOilsQuery.setArgumentHolderValue(0, id);
        Dao<EssentialOil,Integer> essentialOilDao = getEssentialOilDao();
        return essentialOilDao.query(essentialOilsQuery);
    }

    //Prepare query
    private PreparedQuery<EssentialOil> makeEssentialOilForRecipeQuery() throws SQLException {
        Dao<EORecipe,Integer> eoRecipeDao = getEORecipeDao();
        QueryBuilder<EORecipe, Integer> eoRecipeQb = eoRecipeDao.queryBuilder();
        eoRecipeQb.selectColumns(EORecipe.OIL_ID);
        SelectArg selectArg = new SelectArg();
        eoRecipeQb.where().eq(EORecipe.RECIPE_ID, selectArg);

        Dao<EssentialOil,Integer> essentialOilDao = getEssentialOilDao();
        QueryBuilder<EssentialOil, Integer> essentialOilsQb = essentialOilDao.queryBuilder();
        essentialOilsQb.where().in(EssentialOil.ID_FIELD_NAME, eoRecipeQb);
        return essentialOilsQb.prepare();
    }






    //Query for the vegetal oils of a recipe
    public List<VegetalOil> getVegetalOils(int id) throws SQLException {
        if (vegetalOilsQuery == null) {
            vegetalOilsQuery = makeVegetalOilsQuery();
        }

        vegetalOilsQuery.setArgumentHolderValue(0, id);
        Dao<VegetalOil,Integer> vegetalOilDao = getVegetalOilDao();
        return vegetalOilDao.query(vegetalOilsQuery);
    }

    //Prepare query
    private PreparedQuery<VegetalOil> makeVegetalOilsQuery() throws SQLException {
        Dao<VORecipe,Integer> voRecipeDao = getVORecipeDao();
        QueryBuilder<VORecipe, Integer> voRecipeQb = voRecipeDao.queryBuilder();
        voRecipeQb.selectColumns(VORecipe.OIL_ID);
        SelectArg selectArg = new SelectArg();
        voRecipeQb.where().eq(VORecipe.RECIPE_ID, selectArg);

        Dao<VegetalOil,Integer> vegetalOilDao = getVegetalOilDao();
        QueryBuilder<VegetalOil, Integer> vegetalOilsQb = vegetalOilDao.queryBuilder();
        vegetalOilsQb.where().in(VegetalOil.ID_FIELD_NAME, voRecipeQb);
        return vegetalOilsQb.prepare();
    }





    //Query for vegetal properties by id
    public List<VegetalProperty> getVegetalProperties(List<Integer> properties) throws SQLException {

        Dao<VegetalProperty,Integer> vegetalPropertiesDao = getVegetalPropertyDao();
        QueryBuilder<VegetalProperty,Integer> queryBuilder = vegetalPropertiesDao.queryBuilder();
        queryBuilder.where().in(VegetalProperty.ID_FIELD_NAME, properties);
        return vegetalPropertiesDao.query(queryBuilder.prepare());
    }




    //Query for vegetal indications by id
    public List<EssentialIndication> getEssentialIndications(List<Integer> indications) throws SQLException {

        Dao<EssentialIndication, Integer> essentialIndicationsDao = getEssentialIndicationDao();
        QueryBuilder<EssentialIndication, Integer> queryBuilder = essentialIndicationsDao.queryBuilder();
        queryBuilder.where().in(EssentialIndication.ID_FIELD_NAME, indications);

        return essentialIndicationsDao.query(queryBuilder.prepare());
    }


    //Query for essential properties by id
    public List<EssentialProperty> getEssentialProperties(List<Integer> properties) throws SQLException {

        Dao<EssentialProperty,Integer> essentialPropertiesDao = getEssentialPropertyDao();
        QueryBuilder<EssentialProperty,Integer> queryBuilder = essentialPropertiesDao.queryBuilder();
        queryBuilder.where().in(EssentialProperty.ID_FIELD_NAME, properties);
        return essentialPropertiesDao.query(queryBuilder.prepare());
    }


    //Query for essential oils by id
    public List<EssentialOil> getEssentialOils(List<Integer> essentialOils) throws SQLException {

        Dao<EssentialOil,Integer> essentialOilDao = getEssentialOilDao();
        QueryBuilder<EssentialOil,Integer> queryBuilder = essentialOilDao.queryBuilder();
        queryBuilder.where().in(EssentialOil.ID_FIELD_NAME, essentialOils);
        return essentialOilDao.query(queryBuilder.prepare());
    }


    //Query for vegetal oils by id
    public List<VegetalOil> getVegetalOils(List<Integer> vegetalOils) throws SQLException {

        Dao<VegetalOil,Integer> vegetalOilDao = getVegetalOilDao();
        QueryBuilder<VegetalOil,Integer> queryBuilder = vegetalOilDao.queryBuilder();
        queryBuilder.where().in(VegetalOil.ID_FIELD_NAME, vegetalOils);
        return vegetalOilDao.query(queryBuilder.prepare());
    }



    //Query for vegetal indications by id
    public List<VegetalIndication> getVegetalIndications(List<Integer> indications) throws SQLException {

        Dao<VegetalIndication, Integer> vegetalIndicationsDao = getVegetalIndicationDao();
        QueryBuilder<VegetalIndication, Integer> queryBuilder = vegetalIndicationsDao.queryBuilder();
        queryBuilder.where().in(VegetalIndication.ID_FIELD_NAME, indications);

        return vegetalIndicationsDao.query(queryBuilder.prepare());
    }



    //Query for administrations by id
    public List<Administration> getAdministrations(List<Integer> administrations) throws SQLException {

        Dao<Administration,Integer> administrationsDao = getAdministrationDao();
        QueryBuilder<Administration,Integer> queryBuilder = administrationsDao.queryBuilder();
        queryBuilder.where().in(Administration.ID_FIELD_NAME, administrations);
        return administrationsDao.query(queryBuilder.prepare());
    }



    public List<String> getCategories()throws SQLException{
        if (categories == null){
            loadCategories();
        }
        return  categories;
    }


    public void loadCategories()throws SQLException{
        if (categories == null){

            categories = new ArrayList<>();
            List<Category> all = getCategoryDao().queryForAll();

            for (Category category : all){
                categories.add(category.getName());
            }

        }
    }



    public List<String> getUses()throws SQLException{
        if (uses == null){
            uses = new ArrayList<>();
            List<Use> all = getUseDao().queryForAll();
            for (Use use : all){
                uses.add(use.getName());
            }
        }

        return  uses;
    }


    private void updateEssentialOils(int oldVersion, int newVersion){
        try{
            Resources resources = context.getResources();
            AssetManager assets = resources.getAssets();

            String[] dbVersions = assets.list(PATH_TO_JSON);
            for(String version : dbVersions){
                String[] splitted = version.split("_");
                if (splitted.length == 2 && VERSION.equals(splitted[0])){
                    int versionNumber = Integer.valueOf(splitted[1]);
                    if (versionNumber > oldVersion && versionNumber <= newVersion){
                        String folder = PATH_TO_JSON + "/" + version + "/" + PATH_TO_ESSENTIAL_OILS;
                        String[] files = assets.list(folder);
                        for (String file : files){
                            loadEssentialOil(folder + "/" + file);
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateVegetalOils(int oldVersion, int newVersion){
        try {
            Resources resources = context.getResources();
            AssetManager assets = resources.getAssets();

            String[] dbVersions = new String[0];

            dbVersions = assets.list(PATH_TO_JSON);

            for(String version : dbVersions){
                String[] splitted = version.split("_");
                if (splitted.length == 2 && VERSION.equals(splitted[0])){
                    int versionNumber = Integer.valueOf(splitted[1]);
                    if (versionNumber > oldVersion && versionNumber <= newVersion){
                        String folder = PATH_TO_JSON + "/" + version + "/" + PATH_TO_VEGETAL_OILS;
                        String[] files = assets.list(folder);
                        for (String file : files){
                            loadVegetalOil(folder + "/" + file);
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void initializeEssentialOils() throws SQLException, IOException{
        Resources resources = context.getResources();
        AssetManager assets = resources.getAssets();

        String[] dbVersions = assets.list(PATH_TO_JSON);
        for(String version : dbVersions){
            String[] splitted = version.split("_");
            if (splitted.length == 2 && VERSION.equals(splitted[0])){
                int versionNumber = Integer.valueOf(splitted[1]);
                if (versionNumber <= DATABASE_VERSION){
                    String folder = PATH_TO_JSON + "/" + version + "/" + PATH_TO_ESSENTIAL_OILS;
                    String[] files = assets.list(folder);
                    for (String file : files){
                        loadEssentialOil(folder + "/" + file);
                    }
                }
            }

        }
    }



    private void loadEssentialOil(String file) throws SQLException, IOException{
        Dao<EssentialOil, Integer> dao = getEssentialOilDao();
        Dao<EssentialProperty, Integer> propertyDao = getEssentialPropertyDao();
        Dao<EssentialIndication, Integer> indicationDao = getEssentialIndicationDao();
        Dao<EOProperty, Integer> eoPropertyDao = getEOPropertyDao();
        Dao<EOIndication, Integer> eoIndicationDao = getEOIndicationDao();
        Dao<Administration, Integer> administrationDao = getAdministrationDao();
        Dao<EOAdministration, Integer> eoAdministrationDao = getEOAdministrationDao();

        StringBuilder sb = parseJson(file);
        Gson gson = new GsonBuilder().create();
        EssentialOil essentialOil = gson.fromJson(sb.toString(), EssentialOil.class);
        dao.create(essentialOil);

        List<Integer> properties = essentialOil.getProperties();
        for (Integer property : properties){
            List<EssentialProperty> matchingProperties = propertyDao.queryForEq(EssentialProperty.ID_FIELD_NAME, property);
            if (matchingProperties == null) continue;
            EssentialProperty essentialProperty = matchingProperties.get(0);
            EOProperty eoProperty = new EOProperty(essentialOil, essentialProperty);
            eoPropertyDao.create(eoProperty);
        }

        List<Integer> indications = essentialOil.getIndications();
        for (Integer indication : indications){
            List<EssentialIndication> matchingIndications = indicationDao.queryForEq(EssentialIndication.ID_FIELD_NAME, indication);
            if (matchingIndications == null) continue;
            EssentialIndication essentialIndication = matchingIndications.get(0);
            EOIndication eoIndication = new EOIndication(essentialOil, essentialIndication);
            eoIndicationDao.create(eoIndication);
        }

        List<Integer> administrations = essentialOil.getAdministrations();
        for (Integer a : administrations){
            List<Administration> matchingAdministrations = administrationDao.queryForEq(Administration.ID_FIELD_NAME, a);
            if (matchingAdministrations == null) continue;
            Administration administration = matchingAdministrations.get(0);
            EOAdministration eoAdministration = new EOAdministration(essentialOil, administration);
            eoAdministrationDao.create(eoAdministration);
        }
    }



    private void initializeVegetalOils() throws SQLException, IOException{
        Resources resources = context.getResources();
        AssetManager assets = resources.getAssets();

        String[] dbVersions = assets.list(PATH_TO_JSON);
        for(String version : dbVersions){
            String[] splitted = version.split("_");
            if (splitted.length == 2 && VERSION.equals(splitted[0])){
                int versionNumber = Integer.valueOf(splitted[1]);
                if (versionNumber <= DATABASE_VERSION){
                    String folder = PATH_TO_JSON + "/" + version + "/" + PATH_TO_VEGETAL_OILS;
                    String[] files = assets.list(folder);
                    for (String file : files){
                        loadVegetalOil(folder + "/" + file);
                    }
                }
            }

        }


    }


    private void loadVegetalOil(String file) throws SQLException, IOException {
        {
            Dao<VegetalOil, Integer> dao = getVegetalOilDao();
            Dao<VegetalProperty, Integer> propertyDao = getVegetalPropertyDao();
            Dao<VegetalIndication, Integer> indicationDao = getVegetalIndicationDao();
            Dao<VOProperty, Integer> voPropertyDao = getVOPropertyDao();
            Dao<VOIndication, Integer> voIndicationDao = getVOIndicationDao();

            StringBuilder sb = parseJson(file);
            Gson gson = new GsonBuilder().create();
            VegetalOil vegetalOil = gson.fromJson(sb.toString(), VegetalOil.class);
            dao.create(vegetalOil);

            List<Integer> properties = vegetalOil.getProperties();
            for (Integer property : properties) {
                List<VegetalProperty> matchingProperties = propertyDao.queryForEq(VegetalProperty.ID_FIELD_NAME, property);
                if (matchingProperties == null) continue;
                VegetalProperty vegetalProperty = matchingProperties.get(0);
                VOProperty voProperty = new VOProperty(vegetalOil, vegetalProperty);
                voPropertyDao.create(voProperty);
            }

            List<Integer> indications = vegetalOil.getIndications();
            for (Integer indication : indications) {
                List<VegetalIndication> matchingIndications = indicationDao.queryForEq(VegetalIndication.ID_FIELD_NAME, indication);
                if (matchingIndications == null) continue;
                VegetalIndication vegetalIndication = matchingIndications.get(0);
                VOIndication voIndication = new VOIndication(vegetalOil, vegetalIndication);
                voIndicationDao.create(voIndication);
            }
        }
    }



    private void initializeRecipes() throws SQLException, IOException{
        Dao<Recipe, Integer> dao = getRecipeDao();
        Dao<EssentialOil, Integer> essentialOilDao = getEssentialOilDao();
        Dao<EORecipe, Integer> eoRecipeDao = getEORecipeDao();
        Dao<VegetalOil, Integer> vegetalOilDao = getVegetalOilDao();
        Dao<VORecipe, Integer> voRecipeDao = getVORecipeDao();
        Dao<Category, Integer> categoryDao = getCategoryDao();
        Dao<Use, Integer> useDao = getUseDao();

        StringBuilder sb = parseJson(PATH_TO_RECIPES);
        Gson gson = new GsonBuilder().create();
        Recipe[] recipes = gson.fromJson(sb.toString(), Recipe[].class);
        for (Recipe recipe : recipes){

            int categoryId = recipe.getInternalCategory();
            List<Category> categories = categoryDao.queryForEq(Category.ID_FIELD_NAME, categoryId);
            if (categories != null){
                Category category = categories.get(0);
                recipe.setCategory(category);
            }

            int useId = recipe.getInternalUse();
            List<Use> uses = useDao.queryForEq(Use.ID_FIELD_NAME, useId);
            if (uses != null){
                Use use = uses.get(0);
                recipe.setUse(use);
            }

            dao.create(recipe);

            List<Integer> essentialOils = recipe.getEssentialOils();
            for (Integer essentialOil : essentialOils){
                List<EssentialOil> matchingOils = essentialOilDao.queryForEq(EssentialOil.ID_FIELD_NAME, essentialOil);
                if (matchingOils == null) continue;
                EssentialOil oil = matchingOils.get(0);
                EORecipe eoRecipe = new EORecipe(oil, recipe);
                eoRecipeDao.create(eoRecipe);
            }

            List<Integer> vegetalOils = recipe.getVegetalOils();
            for (Integer vegetalOil : vegetalOils) {
                List<VegetalOil> matchingOils = vegetalOilDao.queryForEq(VegetalOil.ID_FIELD_NAME, vegetalOil);
                if (matchingOils == null) continue;
                VegetalOil oil = matchingOils.get(0);
                VORecipe voRecipe = new VORecipe(oil, recipe);
                voRecipeDao.create(voRecipe);
            }

        }

    }


    private void initializeEssentialProperties() throws SQLException,IOException{
        StringBuilder sb = parseJson(PATH_TO_ESSENTIAL_PROPERTIES);
        Gson gson = new GsonBuilder().create();
        EssentialProperty[] essentialProperties = gson.fromJson(sb.toString(), EssentialProperty[].class);
        Dao<EssentialProperty, Integer> dao = getEssentialPropertyDao();
        for (EssentialProperty p : essentialProperties){
            dao.create(p);
        }
    }


    private void initializeEssentialIndications() throws SQLException,IOException{
        StringBuilder sb = parseJson(PATH_TO_ESSENTIAL_INDICATIONS);
        Gson gson = new GsonBuilder().create();
        EssentialIndication[] essentialIndications = gson.fromJson(sb.toString(), EssentialIndication[].class);
        Dao<EssentialIndication, Integer> dao = getEssentialIndicationDao();
        for (EssentialIndication i : essentialIndications){
            dao.create(i);
        }
    }


    private void initializeVegetalProperties() throws SQLException,IOException{
        StringBuilder sb = parseJson(PATH_TO_VEGETAL_PROPERTIES);
        Gson gson = new GsonBuilder().create();
        VegetalProperty[] vegetalProperties = gson.fromJson(sb.toString(), VegetalProperty[].class);
        Dao<VegetalProperty, Integer> dao = getVegetalPropertyDao();
        for (VegetalProperty p : vegetalProperties){
            dao.create(p);
        }
    }


    private void initializeVegetalIndications() throws SQLException,IOException{
        StringBuilder sb = parseJson(PATH_TO_VEGETAL_INDICATIONS);
        Gson gson = new GsonBuilder().create();
        VegetalIndication[] vegetalIndications = gson.fromJson(sb.toString(), VegetalIndication[].class);
        Dao<VegetalIndication, Integer> dao = getVegetalIndicationDao();
        for (VegetalIndication i : vegetalIndications){
            dao.create(i);
        }
    }


    private void initializeAdministrations() throws SQLException,IOException{
        StringBuilder sb = parseJson(PATH_TO_ADMINISTRATIONS);
        Gson gson = new GsonBuilder().create();
        Administration[] administrations = gson.fromJson(sb.toString(), Administration[].class);
        Dao<Administration, Integer> dao = getAdministrationDao();
        for (Administration a : administrations){
            dao.create(a);
        }
    }

    private void initializeCategories() throws SQLException,IOException{
        StringBuilder sb = parseJson(PATH_TO_CATEGORIES);
        Gson gson = new GsonBuilder().create();
        Category[] categories = gson.fromJson(sb.toString(), Category[].class);
        Dao<Category, Integer> dao = getCategoryDao();
        for (Category c : categories){
            dao.create(c);
        }
    }


    private void initializeUses() throws SQLException,IOException{
        StringBuilder sb = parseJson(PATH_TO_USES);
        Gson gson = new GsonBuilder().create();
        Use[] uses = gson.fromJson(sb.toString(), Use[].class);
        Dao<Use, Integer> dao = getUseDao();
        for (Use u : uses){
            dao.create(u);
        }
    }


    private StringBuilder parseJson(String file) throws IOException{
        Resources resources = context.getResources();
        AssetManager assets = resources.getAssets();

        InputStreamReader isr = new InputStreamReader(assets.open(file));
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        isr.close();
        reader.close();

        return sb;
    }


    private void initializeConfiguration() throws SQLException {
        Dao<Configuration, Integer> dao = getConfigurationDao();
        Configuration initialConfiguration = Configuration.newInstance();
        dao.create(initialConfiguration);
    }


    //Close
    @Override
    public void close() {
        if (usageCounter.decrementAndGet() == 0) {
            super.close();
            essentialDao = null;
            vegetalDao = null;
            bottleDao = null;
            recipeDao = null;
            categoryDao = null;
            useDao = null;
            administrationDao = null;
            essentialIndicationDao = null;
            essentialPropertyDao = null;
            vegetalIndicationDao = null;
            vegetalPropertyDao = null;
            eOAdministrationDao = null;
            eOIndicationDao = null;
            eOPropertyDao = null;
            eORecipeDao = null;
            vOIndicationDao = null;
            vOPropertyDao = null;
            vORecipeDao = null;
            helper = null;
        }
    }



    //Prepare query
    public PreparedQuery<Recipe> prepareRecipeQuery(int id) throws SQLException {
        Dao<EORecipe, Integer> eoRecipeDao = getEORecipeDao();
        QueryBuilder<EORecipe, Integer> eoRecipeQb = eoRecipeDao.queryBuilder();
        eoRecipeQb.selectColumns(EORecipe.RECIPE_ID);
        eoRecipeQb.where().eq(EORecipe.OIL_ID, id);

        Dao<Recipe, Integer> recipeDao = getRecipeDao();
        QueryBuilder<Recipe, Integer> recipeQb = recipeDao.queryBuilder();
        recipeQb.where().in(Recipe.ID_FIELD_NAME, eoRecipeQb);
        return recipeQb.prepare();
    }
}