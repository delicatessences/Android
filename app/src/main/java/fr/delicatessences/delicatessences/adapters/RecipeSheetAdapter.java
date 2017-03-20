package fr.delicatessences.delicatessences.adapters;


import java.util.Date;
import java.util.List;

import fr.delicatessences.delicatessences.model.Category;
import fr.delicatessences.delicatessences.model.Use;

public class RecipeSheetAdapter extends SheetAdapter{

    private final String mName;
    private final String mAuthor;
    private final String mPreparation;
    private final boolean mFavorite;
    private final Category mCategory;
    private final Use mUse;
    private final Date mCreation;
    private final List<String> mEssentialOils;
    private final List<String> mVegetalOils;

    public RecipeSheetAdapter(String image, int color, String name, String author, String preparation,
                              boolean favorite, Category category, Use use, Date creation,
                              List<String> essentialOils, List<String> vegetalOils) {
        super(image, color);
        this.mName = name;
        this.mAuthor = author;
        this.mPreparation = preparation;
        this.mFavorite = favorite;
        this.mCategory = category;
        this.mUse = use;
        this.mCreation = creation;
        this.mEssentialOils = essentialOils;
        this.mVegetalOils = vegetalOils;

    }


    public String getName() {
        return mName;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getPreparation() {
        return mPreparation;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public Category getCategory() {
        return mCategory;
    }

    public Use getUse() {
        return mUse;
    }

    public Date getCreation() {
        return mCreation;
    }

    public List<String> getEssentialOils() {
        return mEssentialOils;
    }

    public List<String> getVegetalOils() {
        return mVegetalOils;
    }
}
