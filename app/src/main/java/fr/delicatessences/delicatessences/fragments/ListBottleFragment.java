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
import fr.delicatessences.delicatessences.adapters.BottleListCursorAdapter;
import fr.delicatessences.delicatessences.decorators.SimpleDividerItemDecoration;
import fr.delicatessences.delicatessences.interfaces.Reloadable;
import fr.delicatessences.delicatessences.listeners.ItemSelectedListener;
import fr.delicatessences.delicatessences.loaders.BottleCursorLoader;

public class ListBottleFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, Reloadable{

    private ItemSelectedListener listener;
    private BottleListCursorAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private Parcelable mState;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_with_fab, container, false);

        FragmentActivity activity = getActivity();
        Resources resources = getResources();

        mAdapter = new BottleListCursorAdapter(activity);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mEmptyView = (TextView) view.findViewById(R.id.empty);
        String title = resources.getString(R.string.no_bottle);
        String message = resources.getString(R.string.how_to_add_bottle);

        mEmptyView.setText(Html.fromHtml("<h1>" + title + "</h1><p>" + message + "</p>"));

        return view;
    }

    @Override
    public void onStart() {
        reload();
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mState = mRecyclerView.getLayoutManager().onSaveInstanceState();
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
            actionBar.setTitle(titles[ViewType.BOTTLES.ordinal()]);

        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        setHasOptionsMenu(true);

        if (activity instanceof ItemSelectedListener) {
            listener = (ItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MyListFragment.ItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }



    public static ListBottleFragment newInstance() {
        ListBottleFragment fragment = new ListBottleFragment();

        Bundle args = new Bundle();
        args.putInt(MainActivity.EXTRA_VIEW_TYPE, ViewType.BOTTLES.ordinal());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new BottleCursorLoader(getActivity());
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
