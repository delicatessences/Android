package fr.delicatessences.delicatessences.adapters;


import java.util.List;

public class EssentialOilSheetAdapter extends SheetAdapter{


    private final String mName;
    private final String mBotanicalName;
    private final String mDescription;
    private final String mDistilledOrgan;
    private final String mChemotype;
    private final String mPrecautions;
    private final boolean mFavorite;
    private final boolean mReadOnly;
    private final List<String> mAdministrations;
    private final List<String> mProperties;
    private final List<String> mIndications;
    //private final List<String> mRecipes;


    public EssentialOilSheetAdapter(String image, int color, String name, String botanicalName,
                                    String description, String distilledOrgan, String chemotype,
                                    String precautions, boolean favorite, boolean readOnly,
                                    List<String> administrations, List<String> properties,
                                    List<String> indications){//}, List<String> recipes) {
        super(image, color);
        this.mName = name;
        this.mBotanicalName = botanicalName;
        this.mDescription = description;
        this.mDistilledOrgan = distilledOrgan;
        this.mChemotype = chemotype;
        this.mPrecautions = precautions;
        this.mFavorite = favorite;
        this.mReadOnly = readOnly;
        this.mAdministrations = administrations;
        this.mProperties = properties;
        this.mIndications = indications;
        //this.mRecipes = recipes;
    }

    public String getName() {
        return mName;
    }

    public String getBotanicalName() {
        return mBotanicalName;
    }


    public String getDescription() {
        return mDescription;
    }


    public String getDistilledOrgan() {
        return mDistilledOrgan;
    }


    public String getChemotype() {
        return mChemotype;
    }


    public String getPrecautions() {
        return mPrecautions;
    }

    public boolean isFavorite() {
        return mFavorite;
    }


    public List<String> getAdministrations() {
        return mAdministrations;
    }

    public List<String> getProperties() {
        return mProperties;
    }

    public List<String> getIndications() {
        return mIndications;
    }

    public boolean isReadOnly() {
        return mReadOnly;
    }

    /*public List<String> getRecipes() {
        return mRecipes;
    }*/
}
