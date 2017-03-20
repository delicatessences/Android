package fr.delicatessences.delicatessences.editor;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import fr.delicatessences.delicatessences.R;

public class FavoriteImageView extends ImageView {

    private boolean mFavorite;

    public FavoriteImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFavorite = false;
    }


    public boolean isFavorite(){
        return mFavorite;
    }


    public void setFavorite(boolean isFavorite){
        mFavorite = isFavorite;
        Context context = getContext();
        setImageDrawable(mFavorite ? ContextCompat.getDrawable(context, R.drawable.ic_star) :
                ContextCompat.getDrawable(context, R.drawable.ic_star_outline));
    }


}
