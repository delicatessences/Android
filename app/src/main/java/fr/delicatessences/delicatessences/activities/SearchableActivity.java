package fr.delicatessences.delicatessences.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.adapters.SearchCursorAdapter;
import fr.delicatessences.delicatessences.decorators.SimpleDividerItemDecoration;
import fr.delicatessences.delicatessences.fragments.ViewType;
import fr.delicatessences.delicatessences.interfaces.Reloadable;
import fr.delicatessences.delicatessences.listeners.ItemSelectedListener;
import fr.delicatessences.delicatessences.loaders.SearchCursorLoader;
import fr.delicatessences.delicatessences.model.DatabaseHelper;

import static java.security.AccessController.getContext;

public class SearchableActivity extends OrmLiteBaseActionBarActivity<DatabaseHelper>
        implements LoaderManager.LoaderCallbacks<Cursor>, Reloadable, ItemSelectedListener {


    private SearchCursorAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Parcelable mState;
    private String query;
    private TextView mEmptyView;

    @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        SystemBarTintManager statusBarManager = new SystemBarTintManager(this);
        statusBarManager.setStatusBarTintEnabled(true);
        statusBarManager.setTintColor(getResources().getColor(R.color.primary_color));

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        }


            mAdapter = new SearchCursorAdapter(this);
            mRecyclerView = (RecyclerView) findViewById(R.id.list);
            mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(mAdapter);

        Resources resources = getResources();
        mEmptyView = (TextView) findViewById(R.id.empty);
        String title = resources.getString(R.string.no_results);
        String message = resources.getString(R.string.how_to_search);

        mEmptyView.setText(Html.fromHtml("<h1>" + title + "</h1><p>" + message + "</p>"));

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SearchCursorLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            setTitle(buildPositiveTitle(data));
            mAdapter.changeCursor(data);
            if (mState != null){
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mState);
                mState = null;
            }
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }else{
            setTitle(buildNegativeTitle(data));
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }


    private String buildPositiveTitle(Cursor data){
        StringBuilder sb = new StringBuilder();
        sb.append(data.getCount());
        sb.append(" ");
        sb.append(getResources().getString(R.string.results_for));
        sb.append(" ");
        sb.append(query);
        return sb.toString();
    }

    private String buildNegativeTitle(Cursor data){
        StringBuilder sb = new StringBuilder();
        sb.append(0);
        sb.append(" ");
        sb.append(getResources().getString(R.string.results_for));
        sb.append(" ");
        sb.append(query);
        return sb.toString();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public void reload() {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mState = mRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void showDetail(ViewType type, int primaryId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_ID, primaryId);
        intent.putExtra(MainActivity.EXTRA_VIEW_TYPE, type.getInt());
        intent.putExtra(MainActivity.EXTRA_CLASS, MainActivity.TAG_SEARCH);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        overridePendingTransition(0,0);
        reload();
        super.onResume();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        }
        super.onNewIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(this, SearchableActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showProgress(int message) {

    }

    @Override
    public void hideProgress() {

    }
}

