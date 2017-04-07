package fr.delicatessences.delicatessences.adapters;


import android.database.Cursor;

import fr.delicatessences.delicatessences.model.DatabaseHelper;

public class SearchListItem {

    private static final String ID_FIELD_NAME = "_id";
    private static final String NAME_FIELD_NAME = "name";

    private final int id;
    private final String name;
    private final int viewType;

    public SearchListItem(int id, String name, int viewType) {
        this.id = id;
        this.name = name;
        this.viewType = viewType;
    }

    public static SearchListItem fromCursor(Cursor cursor) {

        int idColumnIndex = cursor.getColumnIndex(ID_FIELD_NAME);
        int nameColumnIndex = cursor.getColumnIndex(NAME_FIELD_NAME);
        int viewTypeColumnIndex = cursor.getColumnIndex(DatabaseHelper.VIEWTYPE_COLUMN_NAME);

        int id = cursor.getInt(idColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        int viewType = cursor.getInt(viewTypeColumnIndex);

        return new SearchListItem(id, name, viewType);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getViewType() {
        return viewType;
    }
}
