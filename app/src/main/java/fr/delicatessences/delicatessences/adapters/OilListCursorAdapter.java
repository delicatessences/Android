package fr.delicatessences.delicatessences.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.editor.FavoriteImageView;
import fr.delicatessences.delicatessences.fragments.ViewType;
import fr.delicatessences.delicatessences.interfaces.FavoriteSelectionListener;
import fr.delicatessences.delicatessences.listeners.ItemSelectedListener;

public class OilListCursorAdapter extends CursorRecyclerViewAdapter<OilListCursorAdapter.ViewHolder> {

    private static final String ICON_PREFIX = "ic_";
    private static final String ICON_DEFAULT = "default";
    private final ViewType mViewType;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int mId;
        final ViewType mViewType;
        final ImageView mImageView;
        final TextView mNameText;
        final FavoriteImageView mFavorite;

        ViewHolder(View v, ViewType viewType) {
            super(v);
            mId = -1;
            mViewType = viewType;
            mImageView = (ImageView) v.findViewById(R.id.item_icon);
            mFavorite = (FavoriteImageView) v.findViewById(R.id.item_star);
            mNameText = (TextView) v.findViewById(R.id.item_label);

            v.setOnClickListener(this);
            mFavorite.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mId == -1) {
                throw new IllegalArgumentException("id should be greater than -1");
            }

            Context context = v.getContext();

            if (v instanceof FavoriteImageView) {
                if (context instanceof FavoriteSelectionListener) {
                    FavoriteSelectionListener listener = (FavoriteSelectionListener) context;
                    listener.onFavoriteSet(mViewType, mId);
                } else {
                    throw new ClassCastException(context.toString()
                            + " must implement FavoriteSelectionListener");
                }
            } else {
                v.setSelected(true);
                if (context instanceof ItemSelectedListener) {
                    ItemSelectedListener listener = (ItemSelectedListener) context;
                    listener.showDetail(mViewType, mId);
                } else {
                    throw new ClassCastException(context.toString()
                            + " must implement ItemSelectedListener");
                }
            }
        }
    }


    public OilListCursorAdapter(Context context, ViewType viewType) {
        super(context);
        mViewType = viewType;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final OilListItem listItem = OilListItem.fromCursor(cursor);
        viewHolder.mId = listItem.getId();
        viewHolder.mNameText.setText(listItem.getName());
        viewHolder.mFavorite.setFavorite(listItem.isFavorite());

        Context context = getContext();
        Resources resources = context.getResources();
        String image = ICON_PREFIX + (listItem.getImage() != null ? listItem.getImage() : ICON_DEFAULT);
        int resource = resources.getIdentifier(image, "drawable", context.getPackageName());
        viewHolder.mImageView.setImageDrawable(ContextCompat.getDrawable(context, resource));
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_item, parent, false);
        return new ViewHolder(view, mViewType);
    }


}