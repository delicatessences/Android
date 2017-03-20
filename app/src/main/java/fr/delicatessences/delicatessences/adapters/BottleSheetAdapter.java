package fr.delicatessences.delicatessences.adapters;


import java.util.Date;

public class BottleSheetAdapter extends SheetAdapter{

    private final String mBrand;
    private final Double mPrice;
    private final Integer mCapacity;
    private final Date mExpiration;
    private final String mOrigin;
    private final boolean mBio;
    private final boolean mPure;
    private final boolean mHect;
    private final boolean mHebbd;
    private final String mOilName;
    private final String mOilImage;


    public BottleSheetAdapter(String brand, Double price, Integer capacity, Date expiration,
                              String origin, boolean bio, boolean pure, boolean hect, boolean hebbd,
                              String image, int color, String oilName, String oilImage) {
        super(image, color);
        this.mBrand = brand;
        this.mPrice = price;
        this.mCapacity = capacity;
        this.mExpiration = expiration;
        this.mOrigin = origin;
        this.mBio = bio;
        this.mPure = pure;
        this.mHect = hect;
        this.mHebbd = hebbd;
        this.mOilName = oilName;
        this.mOilImage = oilImage;
    }


    public String getBrand() {
        return mBrand;
    }

    public Double getPrice() {
        return mPrice;
    }

    public Integer getCapacity() {
        return mCapacity;
    }

    public Date getExpiration() {
        return mExpiration;
    }

    public String getOrigin() {
        return mOrigin;
    }

    public boolean isBio() {
        return mBio;
    }

    public boolean isPure() {
        return mPure;
    }

    public boolean isHect() {
        return mHect;
    }

    public boolean isHebbd() {
        return mHebbd;
    }

    public String getOilName() {
        return mOilName;
    }

    public String getOilImage() {
        return ICON_PREFIX + mOilImage;
    }
}
