package fr.delicatessences.delicatessences.adapters;


import java.util.List;

public class VegetalOilSheetAdapter extends SheetAdapter{


    private final String mName;
    private final String mDescription;
    private final boolean mFavorite;
    private final boolean mIsReadOnly;
    private final List<String> mProperties;
    private final List<String> mIndications;


    public VegetalOilSheetAdapter(String image, int color, String name, String description,
                                  boolean favorite, boolean readOnly, List<String> properties,
                                  List<String> indications) {
        super(image, color);
        this.mName = name;
        this.mDescription = description;
        this.mFavorite = favorite;
        this.mIsReadOnly = readOnly;
        this.mProperties = properties;
        this.mIndications = indications;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }


    public boolean isFavorite() {
        return mFavorite;
    }


    public List<String> getProperties() {
        return mProperties;
    }

    public List<String> getIndications() {
        return mIndications;
    }

    public boolean isReadOnly() {
        return mIsReadOnly;
    }
}
