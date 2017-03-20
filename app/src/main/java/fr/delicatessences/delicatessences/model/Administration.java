package fr.delicatessences.delicatessences.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "administrations")
public class Administration{

	public static final String NAME_FIELD_NAME = "name";
	private static final String IMAGE_FIELD_NAME = "image";
    public static final String ID_FIELD_NAME = "_id";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private int mId;

	@DatabaseField(columnName = NAME_FIELD_NAME, unique = true)
	private String mName;
	
	@DatabaseField(columnName = IMAGE_FIELD_NAME)
	private String mImage;
	
	
	public Administration() {
	}

	public Administration(String name, String image) {
		super();
		this.mName = name;
		this.mImage = image;
	}

    public int getId(){
        return mId;
    }

	public String getName() {
		return mName;
	}

	public String getImage() {
		return mImage;
	}
	
	@Override
	public String toString() {
		return NAME_FIELD_NAME + " " + mName;	
	}

}
