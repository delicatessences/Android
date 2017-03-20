package fr.delicatessences.delicatessences.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "config")
public class Configuration {

	public static final String WELCOME_MESSAGE_FIELD_NAME = "show_welcome_message";
	private static final String VERSION_FIELD_NAME = "version";
    public static final String ID_FIELD_NAME = "_id";
	private static final String DELICATESSENCES_VERSION = "1.0.0";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private int mId;

	@DatabaseField(columnName = WELCOME_MESSAGE_FIELD_NAME)
	private boolean mShowWelcomeMessage;

	@DatabaseField(columnName = VERSION_FIELD_NAME)
	private String mVersion;



	public Configuration() {}


	public static Configuration newInstance(){
		Configuration configuration = new Configuration();
		configuration.mVersion = DELICATESSENCES_VERSION;
		configuration.mShowWelcomeMessage = true;
		return configuration;
	}

	public int getId(){
        return mId;
    }

	public boolean isShowWelcomeMessage() {
		return mShowWelcomeMessage;
	}


	public void setShowWelcomeMessage(boolean showWelcomeMessage) {
		this.mShowWelcomeMessage = showWelcomeMessage;
	}

	public String getVersion() {
		return mVersion;
	}

	@SuppressWarnings("StringBufferReplaceableByString")
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(WELCOME_MESSAGE_FIELD_NAME + " ").append(mShowWelcomeMessage);
		builder.append(System.getProperty("line.separator"));

		builder.append(VERSION_FIELD_NAME + " ").append(mVersion);
		builder.append(System.getProperty("line.separator"));

		return builder.toString();
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Configuration that = (Configuration) o;

		return mId == that.mId;

	}

	@Override
	public int hashCode() {
		return mId;
	}
}
