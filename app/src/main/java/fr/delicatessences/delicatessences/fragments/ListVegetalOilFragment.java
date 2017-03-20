package fr.delicatessences.delicatessences.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.activities.MainActivity;
import fr.delicatessences.delicatessences.adapters.OilListCursorAdapter;
import fr.delicatessences.delicatessences.decorators.SimpleDividerItemDecoration;
import fr.delicatessences.delicatessences.interfaces.Reloadable;
import fr.delicatessences.delicatessences.loaders.VegetalOilCursorLoader;

public class ListVegetalOilFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, Reloadable {

    private boolean mOnlyFavorites;
    private OilListCursorAdapter mAdapter;
    private TextView mEmptyView;
    private RecyclerView mRecyclerView;
    private Parcelable mState;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int resLayout = mOnlyFavorites ? R.layout.fragment_list : R.layout.fragment_list_with_fab;
        View view = inflater.inflate(resLayout, container, false);

        FragmentActivity activity = getActivity();
        Resources resources = getResources();

        mAdapter = new OilListCursorAdapter(activity, ViewType.VEGETAL_OILS);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mEmptyView = (TextView) view.findViewById(R.id.empty);
        String title;
        String message;
        if (mOnlyFavorites){
            title = resources.getString(R.string.no_favorite);
            message = resources.getString(R.string.how_to_add_vegetal_oil_favorite);
        }else{
            title = resources.getString(R.string.no_vegetal_oil);
            message = resources.getString(R.string.how_to_add_vegetal_oil);
        }

        mEmptyView.setText(Html.fromHtml("<h1>" + title + "</h1><p>" + message + "</p>"));

        return view;
    }



    @Override
    public void onPause() {
        super.onPause();
        mState = mRecyclerView.getLayoutManager().onSaveInstanceState();
    }


    @Override
    public void onStart() {
        reload();
        super.onStart();
    }

    @Override
    public void reload() {
        getLoaderManager().restartLoader(0, null, this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MainActivity activity = (MainActivity) getActivity();
        activity.setDrawerIndicatorEnabled(true);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayShowTitleEnabled(true);
            String[] titles = getResources().getStringArray(R.array.drawer_items);
            actionBar.setTitle(titles[ViewType.VEGETAL_OILS.ordinal()]);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Bundle args = getArguments();
        mOnlyFavorites = args.getBoolean(MainActivity.EXTRA_ONLY_FAVORITES, false);

        setHasOptionsMenu(!mOnlyFavorites);

    }


    public static ListVegetalOilFragment newInstance(boolean onlyFavorites) {
        ListVegetalOilFragment fragment = new ListVegetalOilFragment();

        Bundle args = new Bundle();
        args.putBoolean(MainActivity.EXTRA_ONLY_FAVORITES, onlyFavorites);
        args.putInt(MainActivity.EXTRA_VIEW_TYPE, ViewType.VEGETAL_OILS.ordinal());
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new VegetalOilCursorLoader(getActivity(), mOnlyFavorites);
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
