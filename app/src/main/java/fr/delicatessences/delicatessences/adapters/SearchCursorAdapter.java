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

import static fr.delicatessences.delicatessences.adapters.SheetAdapter.ICON_PREFIX;

public class SearchCursorAdapter extends CursorRecyclerViewAdapter<SearchCursorAdapter.ViewHolder> {


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int mId;
        ViewType mViewType;
        final TextView mNameText;

        ViewHolder(View v) {
            super(v);
            mId = -1;
            mViewType = null;
            mNameText = (TextView) v.findViewById(R.id.item_label);

            v.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mId == -1) {
                throw new IllegalArgumentException("id should be greater than -1");
            }
            if (mViewType == null){
                throw new IllegalArgumentException("viewtype should not be null");
            }

            Context context = v.getContext();

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


    public SearchCursorAdapter(Context context) {
        super(context);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final SearchListItem listItem = SearchListItem.fromCursor(cursor);
        viewHolder.mId = listItem.getId();
        viewHolder.mViewType = ViewType.fromInt(listItem.getViewType());
        StringBuilder sb = new StringBuilder();
        sb.append(listItem.getName()).append(" (").append(valueOf(viewHolder.mViewType)).append(")");
        viewHolder.mNameText.setText(sb.toString());
    }

    private String valueOf(ViewType viewType){
        Resources resources = getContext().getResources();
        switch (viewType){
            case BOTTLES:
                return resources.getString(R.string.bottle);
            case ESSENTIAL_OILS:
                return resources.getString(R.string.essential_oil);
            case VEGETAL_OILS:
                return resources.getString(R.string.vegetal_oil);
            case RECIPES:
                return resources.getString(R.string.recipe);
            default:
                throw new IllegalStateException("valueOf - invalid view type : " + viewType);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.searchable_row_item, parent, false);
        return new ViewHolder(view);
    }


}