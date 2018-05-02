package fr.delicatessences.delicatessences.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.fragments.ChooseOilDialogFragment;
import fr.delicatessences.delicatessences.fragments.DetailBottleFragment;
import fr.delicatessences.delicatessences.fragments.DetailEOFragment;
import fr.delicatessences.delicatessences.fragments.DetailRecipeFragment;
import fr.delicatessences.delicatessences.fragments.DetailVOFragment;
import fr.delicatessences.delicatessences.fragments.DiscoverFragment;
import fr.delicatessences.delicatessences.fragments.FavoritePagerFragment;
import fr.delicatessences.delicatessences.fragments.HomeFragment;
import fr.delicatessences.delicatessences.fragments.ListBottleFragment;
import fr.delicatessences.delicatessences.fragments.ListEssentialOilFragment;
import fr.delicatessences.delicatessences.fragments.ListVegetalOilFragment;
import fr.delicatessences.delicatessences.fragments.NavigationDrawerFragment;
import fr.delicatessences.delicatessences.fragments.RecipePagerFragment;
import fr.delicatessences.delicatessences.fragments.ViewType;
import fr.delicatessences.delicatessences.interfaces.FavoriteSelectionListener;
import fr.delicatessences.delicatessences.interfaces.Reloadable;
import fr.delicatessences.delicatessences.listeners.ItemSelectedListener;
import fr.delicatessences.delicatessences.model.DatabaseHelper;
import fr.delicatessences.delicatessences.model.EssentialOil;
import fr.delicatessences.delicatessences.model.Recipe;
import fr.delicatessences.delicatessences.model.VegetalOil;
import fr.delicatessences.delicatessences.model.persistence.SynchronizationHelper;

@SuppressWarnings("UnusedParameters")
public class MainActivity extends OrmLiteBaseActionBarActivity<DatabaseHelper> implements
        NavigationDrawerFragment.NavigationDrawerCallbacks, ItemSelectedListener,
        ChooseOilDialogFragment.NoticeDialogListener, FavoriteSelectionListener,
        ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String WEBSITE_ADRESS = "http://www.delicatessences.fr";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_CLASS = "class";
    public static final String EXTRA_ESSENTIAL_OIL_ID = "essential_oil_id";
    public static final String EXTRA_VIEW_TYPE = "essential_oil_id";
    public static final String EXTRA_ONLY_FAVORITES = "favorites";
    private static final String MAIL_ADDRESS = "contact@delicatessences.fr";
    private static final String MAIL_MESSAGE_TYPE = "message/rfc822";
    public static final String TAG_DEEP_LINK = "deeplink";
    public static final String TAG_SEARCH = "search";
    public static final String TAG_DEFAULT = "default";
    public static final String EXTRA_UPLOAD = "upload";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private boolean deepLink;
    private boolean searchResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Resources resources = getResources();
        SystemBarTintManager statusBarManager = new SystemBarTintManager(this);
        statusBarManager.setStatusBarTintEnabled(true);
        statusBarManager.setTintColor(resources.getColor(R.color.primary_color));

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        String[] titles = getResources().getStringArray(R.array.drawer_items);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=  null){
            actionBar.setTitle(titles[ViewType.HOME.ordinal()]);
        }

        Intent intent = getIntent();
        if (intent != null){

            boolean upload = intent.getBooleanExtra(EXTRA_UPLOAD, false);


            String tag = intent.getStringExtra(EXTRA_CLASS);
            if (TAG_DEEP_LINK.equals(tag)){
               onDeepLink(intent);
            } else if (TAG_SEARCH.equals(tag)){
                onSearchResult(intent);
            }
        }

        /*SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        boolean isFirstStart = true;// preferences.getBoolean("firstLaunch", true);

        if (isFirstStart) {
            Intent i = new Intent(this, IntroActivity.class);
            startActivity(i);
            SharedPreferences.Editor e = preferences.edit();
            e.putBoolean("firstLaunch", false);
            e.apply();
        }*/

    }

    private void onSearchResult(Intent intent) {
        this.searchResult = true;
        int id = intent.getIntExtra(EXTRA_ID, 1);
        int viewType = intent.getIntExtra(EXTRA_VIEW_TYPE, 1);
        showDetail(ViewType.fromInt(viewType), id);
    }


    private void onDeepLink(Intent intent) {
        this.deepLink = true;
        int id = intent.getIntExtra(EXTRA_ID, 1);
        int viewType = intent.getIntExtra(EXTRA_VIEW_TYPE, 1);
        showDetail(ViewType.fromInt(viewType), id);
    }



    @Override
    public void onNavigationDrawerItemSelected(int position) {
        ViewType[] viewTypes = ViewType.values();
        ViewType viewType = viewTypes[position];
        showList(viewType);
    }



    public void showList(ViewType viewType) {
        if (this.deepLink || this.searchResult){
            return;
        }

        Fragment fragment = null;

        switch (viewType) {
            case HOME:
                fragment = HomeFragment.newInstance();
                break;

            case RECIPES:
                fragment = RecipePagerFragment.newInstance();
                break;

            case BOTTLES:
                fragment = ListBottleFragment.newInstance();
                break;

            case ESSENTIAL_OILS: {
                fragment = ListEssentialOilFragment.newInstance(false);
                break;
            }

            case VEGETAL_OILS: {
                fragment = ListVegetalOilFragment.newInstance(false);
                break;
            }

            case FAVORITES: {
                fragment = FavoritePagerFragment.newInstance();
                break;
            }

            case DISCOVER: {
                fragment = DiscoverFragment.newInstance();
                break;
            }

            case WEBSITE:
                visitWebsite(null);
                return;


            default:
                break;
        }

        if (fragment == null) {
            throw new IllegalStateException("Could not create fragment for viewtype " + viewType);
        }

        mNavigationDrawerFragment.setDrawerIndicatorEnabled(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();


    }



    private void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/fr.delicatessences.delicatessences/databases/delicatessences.db";
        String backupDBPath = "delicatessences.db";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void showDetail(ViewType type, int primaryId) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;

        switch (type) {

            case BOTTLES:
                fragment = DetailBottleFragment.newInstance(primaryId);
                break;

            case ESSENTIAL_OILS:
                fragment = DetailEOFragment.newInstance(primaryId);
                break;

            case VEGETAL_OILS:
                fragment = DetailVOFragment.newInstance(primaryId);
                break;

            case FAVORITES:
                fragment = DetailEOFragment.newInstance(primaryId);
                break;

            case RECIPES:
                fragment = DetailRecipeFragment.newInstance(primaryId);
                break;

            default:
                break;

        }

        if (fragment == null) {
            throw new IllegalStateException("Could not create fragment for viewtype " + type + " and id " + primaryId);
        }

        if (deepLink | searchResult){
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.container, fragment)
                    .commit();
        } else{
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }




    }


    @Override
    public void onBackPressed() {
        if (deepLink){
            deepLink = false;
            showList(ViewType.HOME);
        } else {
            super.onBackPressed();
        }
    }

    public void setDrawerIndicatorEnabled(boolean b) {
        mNavigationDrawerFragment.setDrawerIndicatorEnabled(b);
    }


    public void newBottle(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);
        if (currentFragment instanceof DetailEOFragment) {
            DetailEOFragment fragment = (DetailEOFragment) currentFragment;
            Intent intent = new Intent(this, EditBottleActivity.class);
            Bundle args = fragment.getArguments();
            intent.putExtra(MainActivity.EXTRA_ESSENTIAL_OIL_ID, args.getInt(MainActivity.EXTRA_ID));
            startActivity(intent);
        }
    }


    public void hideWelcomeCard(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);
        if (currentFragment instanceof HomeFragment){
            HomeFragment homeFragment = (HomeFragment) currentFragment;
            //HideWelcomeWorkerTask task = new HideWelcomeWorkerTask(this, homeFragment);
            //task.execute();
            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor e = preferences.edit();
            e.putBoolean("showWelcome", false);
            e.apply();
            homeFragment.hideWelcomeCard();
        }

    }


    public void showTips(View view){
        DiscoverFragment fragment = DiscoverFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }



    public void showSheet(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);
        if (currentFragment instanceof HomeFragment) {
            HomeFragment homeFragment = (HomeFragment) currentFragment;
            int essentialOil = homeFragment.getEssentialOil();
            if (essentialOil != -1){
                showDetail(ViewType.ESSENTIAL_OILS, essentialOil);
            }
        }
    }


    public void sendMail(View view){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType(MAIL_MESSAGE_TYPE);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{MAIL_ADDRESS});
        try {
            startActivity(Intent.createChooser(i, getString(R.string.mail_action)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.email_no_client), Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public void visitWebsite(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEBSITE_ADRESS));
        try {
            startActivity(browserIntent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.browser_no_client), Toast.LENGTH_SHORT).show();
        }
    }


    public void newItem(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);

        Bundle args = currentFragment.getArguments();
        ViewType viewType = ViewType.values()[args.getInt(EXTRA_VIEW_TYPE)];

        switch (viewType) {
            case BOTTLES:
                ChooseOilDialogFragment chooseOilFragment = new ChooseOilDialogFragment();
                chooseOilFragment.show(fragmentManager, "");
                break;

            case RECIPES: {
                Intent intent = new Intent(this, EditRecipeActivity.class);
                startActivity(intent);
                break;
            }

            case ESSENTIAL_OILS: {
                Intent intent = new Intent(this, EditEOActivity.class);
                startActivity(intent);
                break;
            }

            case VEGETAL_OILS: {
                Intent intent = new Intent(this, EditVOActivity.class);
                startActivity(intent);
                break;
            }
        }
    }


    public void showFeedbackMessage(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();

    }





    @Override
    public void onFavoriteSet(ViewType viewType, int id) {
        DatabaseHelper helper = getHelper();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);

        switch (viewType) {
            case ESSENTIAL_OILS:
                try {
                    Dao<EssentialOil, Integer> dao = helper.getEssentialOilDao();
                    EssentialOil essentialOil = dao.queryForId(id);
                    UpdateBuilder<EssentialOil, Integer> updateBuilder = dao.updateBuilder();
                    updateBuilder.where().eq(EssentialOil.ID_FIELD_NAME, id);
                    updateBuilder.updateColumnValue(EssentialOil.FAVORITE_FIELD_NAME, !essentialOil.isFavorite());
                    updateBuilder.update();
                    if (fragment instanceof Reloadable) {
                        ((Reloadable)fragment).reload();
                    }
                    uploadDatabase();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case VEGETAL_OILS:
                try {
                    Dao<VegetalOil, Integer> dao = helper.getVegetalOilDao();
                    VegetalOil vegetalOil = dao.queryForId(id);
                    UpdateBuilder<VegetalOil, Integer> updateBuilder = dao.updateBuilder();
                    updateBuilder.where().eq(VegetalOil.ID_FIELD_NAME, id);
                    updateBuilder.updateColumnValue(VegetalOil.FAVORITE_FIELD_NAME, !vegetalOil.isFavorite());
                    updateBuilder.update();
                    if (fragment instanceof Reloadable) {
                        ((Reloadable)fragment).reload();
                    }
                    uploadDatabase();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;


            case RECIPES:
                try {
                    Dao<Recipe, Integer> dao = helper.getRecipeDao();
                    Recipe recipe = dao.queryForId(id);
                    UpdateBuilder<Recipe, Integer> updateBuilder = dao.updateBuilder();
                    updateBuilder.where().eq(Recipe.ID_FIELD_NAME, id);
                    updateBuilder.updateColumnValue(Recipe.FAVORITE_FIELD_NAME, !recipe.isFavorite());
                    updateBuilder.update();
                    if (fragment instanceof Reloadable) {
                        ((Reloadable)fragment).reload();
                    }
                    uploadDatabase();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }

    }


    @Override
    public void onDialogPositiveClick(int id) {
        Intent intent = new Intent(this, EditBottleActivity.class);
        intent.putExtra(MainActivity.EXTRA_ESSENTIAL_OIL_ID, id);
        startActivity(intent);
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        return Actions.newView("Main", "http://[ENTER-YOUR-URL-HERE]");
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().start(getIndexApiAction());
    }

    @Override
    public void onStop() {

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().end(getIndexApiAction());
        super.onStop();
    }


    private void uploadDatabase(){
        // upload database
        SynchronizationHelper.saveLastUpdateTime(this, System.currentTimeMillis());
        SynchronizationHelper.uploadDatabase(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(MainActivity.class.getName(), "onConnectionFailed:" + connectionResult);
    }
}


