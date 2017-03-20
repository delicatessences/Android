package fr.delicatessences.delicatessences.adapters;


import android.database.Cursor;

public class OilListItem {

    private static final String ID_FIELD_NAME = "_id";
    private static final String IMAGE_FIELD_NAME = "image";
    private static final String NAME_FIELD_NAME = "name";
    private static final String FAVORITE_FIELD_NAME = "favorite";

    private final int id;
    private final String name;
    private final String image;
    private final boolean favorite;

    private OilListItem(int id, String name, String image, boolean favorite) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.favorite = favorite;
    }

    public static OilListItem fromCursor(Cursor cursor) {

        int imageColumnIndex = cursor.getColumnIndex(IMAGE_FIELD_NAME);
        int idColumnIndex = cursor.getColumnIndex(ID_FIELD_NAME);
        int nameColumnIndex = cursor.getColumnIndex(NAME_FIELD_NAME);
        int favoriteColumnIndex = cursor.getColumnIndex(FAVORITE_FIELD_NAME);

        int id = cursor.getInt(idColumnIndex);
        String imageResource = cursor.getString(imageColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        boolean favorite = cursor.getInt(favoriteColumnIndex) != 0;

        return new OilListItem(id, name,imageResource, favorite);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public boolean isFavorite() {
        return favorite;
    }
}
