package fr.delicatessences.delicatessences.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.delicatessences.delicatessences.model.EssentialOil;
import fr.delicatessences.delicatessences.model.EssentialProperty;

@DatabaseTable(tableName = "eo_properties_join")
public class EOProperty{

	public static final String PROPERTY_ID = "property_id";

	public static final String OIL_ID = "oil_id";
	private static final String ID = "_id";

	@DatabaseField(columnName = ID, generatedId = true)
	private int id;

	@DatabaseField(foreign = true, columnName = OIL_ID)
	private	EssentialOil essentialOil;
	
	@DatabaseField(foreign = true, columnName = PROPERTY_ID)
	private	EssentialProperty property;

	
	public EOProperty() {
		super();
	}


	public EOProperty(EssentialOil essentialOil, EssentialProperty property) {
		super();
		this.essentialOil = essentialOil;
		this.property = property;
	}


    public EssentialOil getEssentialOil(){
        return essentialOil;
    }


    public EssentialProperty getProperty(){
        return property;
    }
	
}
