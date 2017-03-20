package fr.delicatessences.delicatessences.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

@DatabaseTable(tableName = DatabaseHelper.RECIPE_TABLE_NAME)
public class Recipe {

    private static final Theme[] themes = {new Theme("recipe1", "#4a6147"), new Theme("recipe2", "#858b2e"),
            new Theme("recipe3", "#90a23f"), new Theme("recipe4", "#23325b")};

	public static final String USE_FIELD_NAME = "use";
	public static final String CATEGORY_FIELD_NAME = "category";
	public static final String FAVORITE_FIELD_NAME = "favorite";
	public static final String PREPARATION_FIELD_NAME = "preparation";
	public static final String AUTHOR_FIELD_NAME = "author";
	public static final String NAME_FIELD_NAME = "name";
	public static final String ID_FIELD_NAME = "_id";
    public static final String CREATION_DATE_FIELD_NAME = "creation";
    public static final String THEME_FIELD_NAME = "theme";
    public static final String URL_FIELD_NAME = "url";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
	private int mId;

	@DatabaseField(columnName = NAME_FIELD_NAME)
	private String mName;

	@DatabaseField(columnName = AUTHOR_FIELD_NAME)
	private String mAuthor;

	@DatabaseField(columnName = PREPARATION_FIELD_NAME)
	private String mPreparation;

	@DatabaseField(columnName = FAVORITE_FIELD_NAME)
	private boolean mFavorite;

	@DatabaseField(columnName = CATEGORY_FIELD_NAME, foreign = true, foreignAutoRefresh = true)
	private Category mCategory;
	
	@DatabaseField(columnName = USE_FIELD_NAME, foreign = true, foreignAutoRefresh = true)
	private Use mUse;

    @DatabaseField(columnName = CREATION_DATE_FIELD_NAME)
    private Date mCreation;

    @DatabaseField(columnName = THEME_FIELD_NAME)
    private int mTheme;

	@DatabaseField(columnName = URL_FIELD_NAME)
	private String mUrl;

    private List<Integer> essential_oils;
    private List<Integer> vegetal_oils;
    private int category;
    private int use;

	
	public Recipe() {
		super();
        Random ran = new Random();
        this.mTheme = ran.nextInt(themes.length);
		Calendar cal = Calendar.getInstance();
		this.mCreation = cal.getTime();
	}


	public Recipe(String name, String author, String preparation,
			Category category, Use use) {
		super();
		this.mName = name;
		this.mAuthor = author;
		this.mPreparation = preparation;
		this.mFavorite = false;
		this.mCategory = category;
		this.mUse = use;
		Random ran = new Random();
		this.mTheme = ran.nextInt(themes.length);
        Calendar cal = Calendar.getInstance();
        this.mCreation = cal.getTime();
	}

	
	public int getId(){
		return mId;
	}

	public String getName() {
		return mName;
	}


	public String getAuthor() {
		return mAuthor;
	}



	public String getPreparation() {
		return mPreparation;
	}



	public boolean isFavorite() {
		return mFavorite;
	}



	public Category getCategory() {
		return mCategory;
	}

    public void setCategory(Category category) {
        this.mCategory = category;
    }

    public Use getUse() {
		return mUse;
	}

    public void setUse(Use use) {
        this.mUse = use;
    }

    public int getTheme() {
        return mTheme;
    }

    public String getColor(){
        return themes[mTheme].getColor();
    }


    public String getImage(){
        return themes[mTheme].getImage();
    }

	public Date getCreation() {
		return mCreation;
	}


    List<Integer> getEssentialOils() {
        return essential_oils;
    }

    List<Integer> getVegetalOils() {
        return vegetal_oils;
    }

	public String getUrl() {
		return mUrl;
	}


    int getInternalUse(){
        return use;
    }

    int getInternalCategory(){
        return category;
    }

    @SuppressWarnings("StringBufferReplaceableByString")
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(ID_FIELD_NAME + " ").append(mId);
		builder.append(System.getProperty("line.separator"));

		builder.append(NAME_FIELD_NAME + " ").append(mName);
		builder.append(System.getProperty("line.separator"));

		builder.append(AUTHOR_FIELD_NAME + " ").append(mAuthor);
		builder.append(System.getProperty("line.separator"));

		builder.append(PREPARATION_FIELD_NAME + " ").append(mPreparation);
		builder.append(System.getProperty("line.separator"));

		builder.append(FAVORITE_FIELD_NAME + " ").append(mFavorite);
		builder.append(System.getProperty("line.separator"));

		builder.append(URL_FIELD_NAME + " ").append(mUrl);
		builder.append(System.getProperty("line.separator"));

		return builder.toString();
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Recipe recipe = (Recipe) o;

		return mId == recipe.mId;

	}

	@Override
	public int hashCode() {
		return mId;
	}
}
