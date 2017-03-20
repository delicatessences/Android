package fr.delicatessences.delicatessences.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.fragments.ViewType;
import fr.delicatessences.delicatessences.listeners.ItemSelectedListener;

public class LastRecipesCursorAdapter extends CursorRecyclerViewAdapter<LastRecipesCursorAdapter.ViewHolder> {


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int mId;
        final TextView mNameText;

        ViewHolder(View v) {
            super(v);
            mId = -1;
            mNameText = (TextView) v.findViewById(R.id.item_label);
            v.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mId == -1) {
                throw new IllegalArgumentException("id should be greater than -1");
            }

            Context context = v.getContext();

            v.setSelected(true);
            if (context instanceof ItemSelectedListener) {
                ItemSelectedListener listener = (ItemSelectedListener) context;
                listener.showDetail(ViewType.RECIPES, mId);
            } else {
                throw new ClassCastException(context.toString()
                        + " must implement ItemSelectedListener");
            }

        }
    }


    public LastRecipesCursorAdapter(Context context) {
        super(context);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final LastRecipeListItem listItem = LastRecipeListItem.fromCursor(cursor);
        viewHolder.mId = listItem.getId();
        viewHolder.mNameText.setText(listItem.getName());
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_item, parent, false);
        return new ViewHolder(view);
    }







}