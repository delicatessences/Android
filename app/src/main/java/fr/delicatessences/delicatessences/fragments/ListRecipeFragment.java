package fr.delicatessences.delicatessences.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.melnykov.fab.FloatingActionButton;

import java.sql.SQLException;
import java.util.List;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.activities.MainActivity;
import fr.delicatessences.delicatessences.activities.OrmLiteBaseActionBarActivity;
import fr.delicatessences.delicatessences.adapters.RecipeListCursorAdapter;
import fr.delicatessences.delicatessences.decorators.SimpleDividerItemDecoration;
import fr.delicatessences.delicatessences.interfaces.Reloadable;
import fr.delicatessences.delicatessences.listeners.ItemSelectedListener;
import fr.delicatessences.delicatessences.loaders.RecipeCursorLoader;
import fr.delicatessences.delicatessences.model.DatabaseHelper;

public class ListRecipeFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, Reloadable {

    private static final String POSITION = "position";

    private ItemSelectedListener listener;
    private boolean mOnlyFavorites;
    private int mCategory;
    private RecipeListCursorAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private OrmLiteBaseActionBarActivity mActivity;
    private Parcelable mState;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        Resources resources = getResources();

        mAdapter = new RecipeListCursorAdapter(mActivity);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mEmptyView = (TextView) view.findViewById(R.id.empty);
        String title;
        String message;
        if (mOnlyFavorites){
            title = resources.getString(R.string.no_favorite);
            message = resources.getString(R.string.how_to_add_recipe_favorite);
        }else{
            title = resources.getString(R.string.no_recipe);
            DatabaseHelper helper = (DatabaseHelper) mActivity.getHelper();
            try {
                List<String> categories = helper.getCategories();
                title += mCategory > 0 ? " " + categories.get(mCategory - 1) : "";
            } catch (SQLException e) {
                e.printStackTrace();
            }
            message = resources.getString(mCategory > 0 ? R.string.how_to_add_categorized_recipe : R.string.how_to_add_recipe);
        }

        mEmptyView.setText(Html.fromHtml("<h1>" + title + "</h1><p>" + message + "</p>"));




        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reload();
    }

    @Override
    public void reload(){
        getLoaderManager().restartLoader(0, null, this);
    }


    @Override
    public void onPause() {
        super.onPause();
        mState = mRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Bundle args = getArguments();
        mOnlyFavorites = args.getBoolean(MainActivity.EXTRA_ONLY_FAVORITES, false);
        mCategory = args.getInt(POSITION, 0);

        if (activity instanceof OrmLiteBaseActionBarActivity) {
            mActivity = (OrmLiteBaseActionBarActivity) getActivity();
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement OrmLiteBaseActionBarActivity");
        }

        if (activity instanceof ItemSelectedListener) {
            listener = (ItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MyListFragment.ItemSelectedListener");
        }
        setHasOptionsMenu(!mOnlyFavorites);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    public static ListRecipeFragment newInstance(int position, boolean onlyFavorites) {
        ListRecipeFragment fragment = new ListRecipeFragment();

        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putBoolean(MainActivity.EXTRA_ONLY_FAVORITES, onlyFavorites);
        fragment.setArguments(args);

        return fragment;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new RecipeCursorLoader(getActivity(), mCategory, mOnlyFavorites);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            mAdapter.changeCursor(data);
            if (mState != null){
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mState);
                mState = null;
            }
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }else{
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}
