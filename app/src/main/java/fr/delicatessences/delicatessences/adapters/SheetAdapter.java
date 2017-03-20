package fr.delicatessences.delicatessences.adapters;

public class SheetAdapter {

    private static final String IMAGE_PREFIX = "pic_";
    static final String ICON_PREFIX = "ic_";
    private static final String IMAGE_LOW_RES_SUFFIX = "_low";
    private final String image;
    private final int color;

    SheetAdapter(String image, int color) {
        this.image = image;
        this.color = color;
    }

    public String getImage() {
        return IMAGE_PREFIX + image;
    }

    public String getImageLowRes() {
        return  getImage() + IMAGE_LOW_RES_SUFFIX;
    }

    public int getColor() {
        return color;
    }
}
