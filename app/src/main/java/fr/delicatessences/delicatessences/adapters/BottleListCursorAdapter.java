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
import fr.delicatessences.delicatessences.fragments.ViewType;
import fr.delicatessences.delicatessences.listeners.ItemSelectedListener;

public class BottleListCursorAdapter extends CursorRecyclerViewAdapter<BottleListCursorAdapter.ViewHolder> {


    private static final String ICON_PREFIX = "ic_";
    private static final String ICON_DEFAULT = "default";


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int mBottleId;
        int mOilId;
        final ImageView mImageView;
        final TextView mNameText;
        final TextView mBrandText;
        final TextView mExpirationText;

        ViewHolder(View v) {
            super(v);
            mBottleId = -1;
            mOilId = -1;
            mImageView = (ImageView) v.findViewById(R.id.item_icon);
            mNameText = (TextView) v.findViewById(R.id.item_label);
            mBrandText = (TextView) v.findViewById(R.id.item_brand);
            mExpirationText = (TextView) v.findViewById(R.id.item_expiration);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mBottleId == -1 || mOilId == -1) {
                throw new IllegalArgumentException("id should be greater than -1");
            }

            Context context = v.getContext();

            v.setSelected(true);
            if (context instanceof ItemSelectedListener) {
                ItemSelectedListener listener = (ItemSelectedListener) context;
                listener.showDetail(ViewType.BOTTLES, mBottleId);
            } else {
                throw new ClassCastException(context.toString()
                        + " must implement ItemSelectedListener");
            }
        }
    }





    public BottleListCursorAdapter(Context context) {
        super(context);
    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final BottleListItem listItem = BottleListItem.fromCursor(cursor);
        viewHolder.mBottleId = listItem.getBottleId();
        viewHolder.mOilId = listItem.getEssentialOilId();
        viewHolder.mNameText.setText(listItem.getEssentialOilName());
        viewHolder.mBrandText.setText(listItem.getBrand());

        Context context = getContext();
        Resources resources = context.getResources();
        String image = ICON_PREFIX + (listItem.getImage() != null ? listItem.getImage() : ICON_DEFAULT);
        int resource = resources.getIdentifier(image, "drawable", context.getPackageName());
        viewHolder.mImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), resource));
        String expirationDate = listItem.getExpirationDate();
        if (expirationDate != null) {
            viewHolder.mExpirationText.setText(resources.getString(R.string.expire) + " " + expirationDate);
            viewHolder.mExpirationText.setTextColor(listItem.isExpired() ?
                    resources.getColor(R.color.deli_rose_color) : resources.getColor(R.color.secondary_text_color));
        }
        else{
            viewHolder.mExpirationText.setText("");
        }
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottle_list_row_item, parent, false);
        return new ViewHolder(view);
    }


}