package fr.delicatessences.delicatessences.adapters;


import android.database.Cursor;

public class RecipeListItem {


    private final int id;
    private final String name;
    private final boolean favorite;
    private final String use;
    private final int category;

    public RecipeListItem(int id, String name, int category, boolean favorite, String use) {
        this.id = id;
        this.name = name;
        this.favorite = favorite;
        this.use = use;
        this.category = category;
    }



    public static RecipeListItem fromCursor(Cursor cursor) {

        int id = cursor.getInt(0);
        String name = cursor.getString(1);
        int category = cursor.getInt(2);
        boolean favorite = cursor.getInt(3) != 0;
        String use = cursor.getString(4);

        return new RecipeListItem(id, name, category, favorite, use);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar(){
        return name != null && name.length() > 0 ? String.valueOf(name.charAt(0)).toUpperCase() : "#";
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getUse() {
        return use;
    }

    public int getCategory() { return category; }
}

