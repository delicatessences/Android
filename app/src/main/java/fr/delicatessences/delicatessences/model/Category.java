package fr.delicatessences.delicatessences.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

@DatabaseTable(tableName = "categories")
public class Category{

	private static final String IMAGE_FIELD_NAME = "image";
	private static final String RECIPES_FIELD_NAME = "recipes";
	private static final String NAME_FIELD_NAME = "name";

    public static final String ID_FIELD_NAME = "_id";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private int mId;

	@DatabaseField(columnName = NAME_FIELD_NAME, unique = true)
	private String mName;
	
	@DatabaseField(columnName = IMAGE_FIELD_NAME)
	private String mImage;
	
	@ForeignCollectionField(columnName = RECIPES_FIELD_NAME)
	private ForeignCollection<Recipe> mRecipes;
	
	
	public Category() {
		super();
	}

	public Category(String name, String image) {
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
	
	public Collection<Recipe> getRecipes() {
		return mRecipes;
	}
	
	@SuppressWarnings("StringBufferReplaceableByString")
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(NAME_FIELD_NAME + " ").append(mName);
		builder.append(System.getProperty("line.separator"));

		builder.append(IMAGE_FIELD_NAME + " ").append(mImage);
		builder.append(System.getProperty("line.separator"));

		return builder.toString();
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Category category = (Category) o;

		return mId == category.mId;

	}

	@Override
	public int hashCode() {
		return mId;
	}
}
