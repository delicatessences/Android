package fr.delicatessences.delicatessences.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

@DatabaseTable(tableName = "vegetal_oils")
public class VegetalOil{

	public static final String FAVORITE_FIELD_NAME = "favorite";
	public static final String DESCRIPTION_FIELD_NAME = "description";
	public static final String IMAGE_FIELD_NAME = "image";
	public static final String NAME_FIELD_NAME = "name";
	public static final String ID_FIELD_NAME = "_id";
	public static final String READ_ONLY_FIELD_NAME = "read_only";
	public static final String COLOR_FIELD_NAME = "color";
    private static final String DEFAULT_COLOR = "#264961";
    private static final String DEFAULT_IMAGE = "default";
	public static final String URL_FIELD_NAME = "url";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true, allowGeneratedIdInsert=true)
	private int mId;

	@DatabaseField(columnName = NAME_FIELD_NAME)
	private String mName;

	@DatabaseField(columnName = IMAGE_FIELD_NAME)
	private String mImage;

	@DatabaseField(columnName = DESCRIPTION_FIELD_NAME)
	private String mDescription;

	@DatabaseField(columnName = FAVORITE_FIELD_NAME)
	private boolean mFavorite;

	@DatabaseField(columnName = READ_ONLY_FIELD_NAME)
	private boolean mReadOnly;

    @DatabaseField(columnName = COLOR_FIELD_NAME)
    private String mColor;

	@DatabaseField(columnName = URL_FIELD_NAME)
	private String mUrl;

    private List<Integer> properties;
    private List<Integer> indications;



	public VegetalOil(){
		super();
	}

	public VegetalOil(String name, String description) {
		super();
		this.mName = name;
		this.mDescription = description;
		this.mImage = DEFAULT_IMAGE;
		this.mFavorite = false;
		this.mReadOnly = false;
        this.mColor = DEFAULT_COLOR;

	}


	public int getId(){
		return mId;
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

	public String getImage() {
		return mImage;
	}

	public boolean isReadOnly() {
		return mReadOnly;
	}

    List<Integer> getProperties() {
        return properties;
    }

    List<Integer> getIndications() {
        return indications;
    }

    public String getColor() {
        return mColor;
    }

    public String getUrl() {
        return mUrl;
    }

    @SuppressWarnings("StringBufferReplaceableByString")
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(ID_FIELD_NAME + " ").append(mId);
		builder.append(System.getProperty("line.separator"));

		builder.append(NAME_FIELD_NAME + " ").append(mName);
		builder.append(System.getProperty("line.separator"));

		builder.append(IMAGE_FIELD_NAME + " ").append(mImage);
		builder.append(System.getProperty("line.separator"));

		builder.append(DESCRIPTION_FIELD_NAME + " ").append(mDescription);
		builder.append(System.getProperty("line.separator"));

		builder.append(FAVORITE_FIELD_NAME + " ").append(mFavorite);
		builder.append(System.getProperty("line.separator"));

		builder.append(READ_ONLY_FIELD_NAME + " ").append(mReadOnly);
		builder.append(System.getProperty("line.separator"));

        builder.append(COLOR_FIELD_NAME + " ").append(mColor);
        builder.append(System.getProperty("line.separator"));

        builder.append(URL_FIELD_NAME + " ").append(mUrl);
        builder.append(System.getProperty("line.separator"));

		return builder.toString();
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VegetalOil that = (VegetalOil) o;

		return mId == that.mId;

	}

	@Override
	public int hashCode() {
		return mId;
	}
}
