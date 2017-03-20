package fr.delicatessences.delicatessences.adapters;


import android.database.Cursor;

import fr.delicatessences.delicatessences.model.Recipe;

public class LastRecipeListItem {


    private final int id;
    private final String name;

    private LastRecipeListItem(int id, String name) {
        this.id = id;
        this.name = name;
    }



    public static LastRecipeListItem fromCursor(Cursor cursor) {

        int id = cursor.getInt(cursor.getColumnIndex(Recipe.ID_FIELD_NAME));
        String name = cursor.getString(cursor.getColumnIndex(Recipe.NAME_FIELD_NAME));


        return new LastRecipeListItem(id, name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


}

