package fr.delicatessences.delicatessences.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

@DatabaseTable(tableName = DatabaseHelper.ESSENTIAL_OIL_TABLE_NAME)
public class EssentialOil{

    public static final String BOTTLE_FIELD_NAME = "bottle";
	public static final String FAVORITE_FIELD_NAME = "favorite";
	public static final String PRECAUTIONS_FIELD_NAME = "precautions";
	public static final String CHEMOTYPE_FIELD_NAME = "chemotype";
	public static final String DISTILLED_ORGAN_FIELD_NAME = "distilled_organ";
	public static final String DESCRIPTION_FIELD_NAME = "description";
	public static final String IMAGE_FIELD_NAME = "image";
	public static final String BOTANICAL_NAME_FIELD_NAME = "botanical_name";
	public static final String NAME_FIELD_NAME = "name";
	public static final String ID_FIELD_NAME = "_id";
    public static final String READ_ONLY_FIELD_NAME = "read_only";
    public static final String URL_FIELD_NAME = "url";
    private static final String COLOR_FIELD_NAME = "color";
    private static final String DEFAULT_COLOR = "#264961";
    private static final String DEFAULT_IMAGE = "default";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true, allowGeneratedIdInsert=true)
	private int mId;
	
	@DatabaseField(columnName = NAME_FIELD_NAME)
	private String mName;
	
	@DatabaseField(columnName = BOTANICAL_NAME_FIELD_NAME)
	private String mBotanicalName;

	@DatabaseField(columnName = IMAGE_FIELD_NAME)
	private String mImage;
	
	@DatabaseField(columnName = DESCRIPTION_FIELD_NAME)
	private String mDescription;
	
	@DatabaseField(columnName = DISTILLED_ORGAN_FIELD_NAME)
	private String mDistilledOrgan;
	
	@DatabaseField(columnName = CHEMOTYPE_FIELD_NAME)
	private String mChemotype;
	
	@DatabaseField(columnName = PRECAUTIONS_FIELD_NAME)
	private String mPrecautions;
	
	@DatabaseField(columnName = FAVORITE_FIELD_NAME)
	private boolean mFavorite;

    @ForeignCollectionField(columnName = BOTTLE_FIELD_NAME)
	private ForeignCollection<Bottle> mBottles;
	
	@DatabaseField(columnName = READ_ONLY_FIELD_NAME)
	private boolean mReadOnly;

    @DatabaseField(columnName = COLOR_FIELD_NAME)
    private String mColor;

    @DatabaseField(columnName = URL_FIELD_NAME)
    private String mUrl;

    private List<Integer> properties;
    private List<Integer> indications;
    private List<Integer> administrations;
	
	public EssentialOil(){
		super();
	}
	
	public EssentialOil(String name, String botanicalName,
			String description, String distilledOrgan, String chemotype,
			String precautions) {
		super();
		this.mName = name;
		this.mBotanicalName = botanicalName;
		this.mDescription = description;
		this.mImage = DEFAULT_IMAGE;
		this.mDistilledOrgan = distilledOrgan;
		this.mChemotype = chemotype;
		this.mPrecautions = precautions;
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

	public ForeignCollection<Bottle> getBottles() {
		return mBottles;
	}

	public String getImage() {
		return mImage;
	}

	public boolean isReadOnly() {
		return mReadOnly;
	}

    List<Integer> getProperties() { return properties; }

    List<Integer> getIndications() {
        return indications;
    }

    List<Integer> getAdministrations() {
        return administrations;
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
		
		builder.append(BOTANICAL_NAME_FIELD_NAME + " ").append(mBotanicalName);
		builder.append(System.getProperty("line.separator"));
		
		builder.append(IMAGE_FIELD_NAME + " ").append(mImage);
		builder.append(System.getProperty("line.separator"));
		
		builder.append(DESCRIPTION_FIELD_NAME + " ").append(mDescription);
		builder.append(System.getProperty("line.separator"));
		
		builder.append(DISTILLED_ORGAN_FIELD_NAME + " ").append(mDistilledOrgan);
		builder.append(System.getProperty("line.separator"));
		
		builder.append(CHEMOTYPE_FIELD_NAME + " ").append(mChemotype);
		builder.append(System.getProperty("line.separator"));
		
		builder.append(PRECAUTIONS_FIELD_NAME + " ").append(mPrecautions);
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

		EssentialOil that = (EssentialOil) o;

		return mId == that.mId;

	}

	@Override
	public int hashCode() {
		return mId;
	}
}
