package fr.delicatessences.delicatessences.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.delicatessences.delicatessences.model.VegetalOil;
import fr.delicatessences.delicatessences.model.VegetalProperty;

@DatabaseTable(tableName = "vo_properties_join")
public class VOProperty{

	public static final String PROPERTY_ID = "property_id";

	public static final String OIL_ID = "oil_id";
	private static final String ID = "_id";

	@DatabaseField(columnName = ID, generatedId = true)
	private int id;

	@DatabaseField(foreign = true, columnName = OIL_ID)
	private	VegetalOil vegetalOil;
	
	@DatabaseField(foreign = true, columnName = PROPERTY_ID)
	private	VegetalProperty property;

	
	public VOProperty() {
		super();
	}


	public VOProperty(VegetalOil vegetalOil, VegetalProperty property) {
		super();
		this.vegetalOil = vegetalOil;
		this.property = property;
	}


    public VegetalOil getEssentialOil(){
        return vegetalOil;
    }


    public VegetalProperty getProperty(){
        return property;
    }
	
	
	
}
