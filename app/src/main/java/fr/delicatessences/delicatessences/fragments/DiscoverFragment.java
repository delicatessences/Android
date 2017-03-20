package fr.delicatessences.delicatessences.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.activities.MainActivity;
import fr.delicatessences.delicatessences.utils.ImageUtils;

public class DiscoverFragment extends Fragment{

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_discover,
                container, false);

        {
            ImageView imageView = (ImageView) view.findViewById(R.id.tuto1_card_image);
            ImageUtils.loadDrawable(getActivity(), R.drawable.pic_add_recipe, imageView);
        }
        {
            ImageView imageView = (ImageView) view.findViewById(R.id.tuto2_card_image);
            ImageUtils.loadDrawable(getActivity(), R.drawable.pic_add_bottle, imageView);
        }
        {
            ImageView imageView = (ImageView) view.findViewById(R.id.tuto3_card_image);
            ImageUtils.loadDrawable(getActivity(), R.drawable.pic_oils, imageView);
        }
        {
            ImageView imageView = (ImageView) view.findViewById(R.id.tuto4_card_image);
            ImageUtils.loadDrawable(getActivity(), R.drawable.pic_way, imageView);
        }

        return view;
	}

	
	public static DiscoverFragment newInstance() {
		DiscoverFragment fragment = new DiscoverFragment();

	   Bundle args = new Bundle();
	    args.putInt(MainActivity.EXTRA_VIEW_TYPE, ViewType.DISCOVER.ordinal());
	   fragment.setArguments(args);

	    return fragment;
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
            actionBar.setTitle(titles[ViewType.DISCOVER.ordinal()]);
        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        setHasOptionsMenu(true);
    }

}
