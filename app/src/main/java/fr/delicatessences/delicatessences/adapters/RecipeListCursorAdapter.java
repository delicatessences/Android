package fr.delicatessences.delicatessences.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.editor.FavoriteImageView;
import fr.delicatessences.delicatessences.fragments.ViewType;
import fr.delicatessences.delicatessences.interfaces.FavoriteSelectionListener;
import fr.delicatessences.delicatessences.listeners.ItemSelectedListener;

public class RecipeListCursorAdapter extends CursorRecyclerViewAdapter<RecipeListCursorAdapter.ViewHolder> {


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int mId;
        final TextView mNameText;
        final TextView mAvatarView;
        final FavoriteImageView mFavorite;
        final TextView mUseText;

        ViewHolder(View v) {
            super(v);
            mId = -1;
            mAvatarView = (TextView) v.findViewById(R.id.item_icon);
            mUseText = (TextView) v.findViewById(R.id.item_use);
            mNameText = (TextView) v.findViewById(R.id.item_label);
            mFavorite = (FavoriteImageView) v.findViewById(R.id.item_star);

            v.setOnClickListener(this);
            mFavorite.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            if (mId == -1){
                throw new IllegalArgumentException("id should be greater than -1");
            }

            Context context = v.getContext();

            if (v instanceof FavoriteImageView){
                if (context instanceof FavoriteSelectionListener){
                    FavoriteSelectionListener listener = (FavoriteSelectionListener) context;
                    listener.onFavoriteSet(ViewType.RECIPES, mId);
                }else {
                    throw new ClassCastException(context.toString()
                            + " must implement FavoriteSelectionListener");
                }
            }else{
                v.setSelected(true);
                if (context instanceof ItemSelectedListener){
                    ItemSelectedListener listener = (ItemSelectedListener) context;
                    listener.showDetail(ViewType.RECIPES, mId);
                }else {
                    throw new ClassCastException(context.toString()
                            + " must implement ItemSelectedListener");
                }
            }
        }
    }





    public RecipeListCursorAdapter(Context context) {
        super(context);
    }





    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final RecipeListItem listItem = RecipeListItem.fromCursor(cursor);
        viewHolder.mId = listItem.getId();
        viewHolder.mNameText.setText(listItem.getName());
        viewHolder.mAvatarView.setText(listItem.getAvatar());
        viewHolder.mFavorite.setFavorite(listItem.isFavorite());
        viewHolder.mUseText.setText(listItem.getUse());
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list_row_item, parent, false);
        return new ViewHolder(view);
    }
}