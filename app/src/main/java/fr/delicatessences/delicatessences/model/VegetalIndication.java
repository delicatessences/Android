package fr.delicatessences.delicatessences.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "vegetal_indications")
public class VegetalIndication{

	public static final String NAME_FIELD_NAME = "name";

    public static final String ID_FIELD_NAME = "_id";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private int mId;

	@DatabaseField(columnName = NAME_FIELD_NAME, unique = true)
	private String mName;
		
	
	public VegetalIndication() {
		super();
	}

	public VegetalIndication(String name) {
		super();
		this.mName = name;
	}

	public String getName() {
		return mName;
	}
    public int getId(){
        return mId;
    }
	
	
}
