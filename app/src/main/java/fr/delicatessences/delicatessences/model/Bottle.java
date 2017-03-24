package fr.delicatessences.delicatessences.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.Random;

import fr.delicatessences.delicatessences.fragments.ViewType;

@DatabaseTable(tableName = DatabaseHelper.BOTTLE_TABLE_NAME)
public class Bottle{

    private static final Theme[] themes = {new Theme("bottle1", "#636b90"), new Theme("bottle2", "#edb242"),
            new Theme("bottle3", "#555a53"), new Theme("bottle4", "#6fa938")};

    private static final String BOTTLE_URL = "bottle";
	public static final String HEBBD_FIELD_NAME = "hebbd";
	public static final String HECT_FIELD_NAME = "hect";
	public static final String PURE_FIELD_NAME = "pure";
	public static final String BIO_FIELD_NAME = "bio";
	public static final String ORIGIN_FIELD_NAME = "origin";
	public static final String EXPIRATION_DATE_FIELD_NAME = "expiration_date";
	public static final String CAPACITY_FIELD_NAME = "capacity";
	public static final String PRICE_FIELD_NAME = "price";
	public static final String BRAND_FIELD_NAME = "brand";
	public static final String ID_FIELD_NAME = "_id";
	public static final String OIL_FIELD_NAME = "oil";
    public static final String THEME_FIELD_NAME = "theme";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
	private int mId;

	@DatabaseField(columnName = BRAND_FIELD_NAME)
	private String mBrand;
	
	@DatabaseField(columnName = PRICE_FIELD_NAME)
	private Double mPrice;
	
	@DatabaseField(columnName = CAPACITY_FIELD_NAME)
	private Integer mCapacity;
	
	@DatabaseField(columnName = EXPIRATION_DATE_FIELD_NAME, dataType = DataType.DATE_STRING,
            format = "yyyy-MM")
	private Date mExpiration;
	
	@DatabaseField(columnName = ORIGIN_FIELD_NAME)
	private String mOrigin;
	
	@DatabaseField(columnName = BIO_FIELD_NAME)
	private boolean mBio;
	
	@DatabaseField(columnName = PURE_FIELD_NAME)
	private boolean mPure;
	
	@DatabaseField(columnName = HECT_FIELD_NAME)
	private boolean mHect;
	
	@DatabaseField(columnName = HEBBD_FIELD_NAME)
	private boolean mHebbd;

    @DatabaseField(columnName = THEME_FIELD_NAME)
    private int mTheme;

    @DatabaseField(columnName = OIL_FIELD_NAME, foreign = true, foreignAutoRefresh = true)
    private EssentialOil mEssentialOil;


	public Bottle() {}
	
	

	public Bottle(String brand, Double price, Integer capacity,
			Date expiration, String origin, boolean bio, boolean pure,
			boolean hect, boolean hebbd, EssentialOil essentialOil) {
		super();
		this.mBrand = brand;
		this.mPrice = price;
		this.mCapacity = capacity;
		this.mExpiration = expiration;
		this.mOrigin = origin;
		this.mBio = bio;
		this.mPure = pure;
		this.mHect = hect;
		this.mHebbd = hebbd;
		Random ran = new Random();
		this.mTheme = ran.nextInt(themes.length);
        this.mEssentialOil = essentialOil;
	}




	public int getId(){
		return mId;
	}


	public String getBrand() {
		return mBrand;
	}







	public Double getPrice() {
		return mPrice;
	}



	public Integer getCapacity() {
		return mCapacity;
	}




	public Date getExpiration() {
		return mExpiration;
	}




	public String getOrigin() {
		return mOrigin;
	}




	public boolean isBio() {
		return mBio;
	}




	public boolean isPure() {
		return mPure;
	}




	public boolean isHect() {
		return mHect;
	}




	public boolean isHebbd() {
		return mHebbd;
	}






    public EssentialOil getEssentialOil(){
        return this.mEssentialOil;
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


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(ID_FIELD_NAME + " ").append(mId);
		builder.append(System.getProperty("line.separator"));

		builder.append(BRAND_FIELD_NAME + " ").append(mBrand);
		builder.append(System.getProperty("line.separator"));

		builder.append(PRICE_FIELD_NAME + " ").append(mPrice);
		builder.append(System.getProperty("line.separator"));

		builder.append(CAPACITY_FIELD_NAME + " ").append(mCapacity);
		builder.append(System.getProperty("line.separator"));

		if (mExpiration != null){
			builder.append(EXPIRATION_DATE_FIELD_NAME + " ").append(mExpiration.toString());
			builder.append(System.getProperty("line.separator"));			
		}

		builder.append(ORIGIN_FIELD_NAME + " ").append(mOrigin);
		builder.append(System.getProperty("line.separator"));

		builder.append(BIO_FIELD_NAME + " ").append(mBio);
		builder.append(System.getProperty("line.separator"));
		
		builder.append(PURE_FIELD_NAME + " ").append(mPure);
		builder.append(System.getProperty("line.separator"));
		
		builder.append(HECT_FIELD_NAME + " ").append(mHect);
		builder.append(System.getProperty("line.separator"));
		
		builder.append(HEBBD_FIELD_NAME + " ").append(mHebbd);
		builder.append(System.getProperty("line.separator"));


		
		return builder.toString();
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Bottle bottle = (Bottle) o;

		return mId == bottle.mId;

	}

	@Override
	public int hashCode() {
		return mId;
	}

    public String getUrl() {
        int urlId = (mId << 2) | ViewType.BOTTLES.getInt();
        return DatabaseHelper.URL_PATTERN + BOTTLE_URL + "-" + urlId + DatabaseHelper.URL_EXTENSION;

    }
}
