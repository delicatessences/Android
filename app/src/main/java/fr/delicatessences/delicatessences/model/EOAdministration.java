package fr.delicatessences.delicatessences.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.delicatessences.delicatessences.model.Administration;
import fr.delicatessences.delicatessences.model.EssentialOil;

@DatabaseTable(tableName = "eo_administrations_join")
public class EOAdministration{

	public static final String ADMINISTRATION_ID = "administration_id";
	public static final String OIL_ID = "oil_id";
	private static final String ID = "_id";

	@DatabaseField(columnName = ID, generatedId = true)
	private int id;

	@DatabaseField(foreign = true, columnName = OIL_ID)
	private	EssentialOil essentialOil;
	
	@DatabaseField(foreign = true, columnName = ADMINISTRATION_ID)
	private	Administration administration;

	
	public EOAdministration() {

	}


	public EOAdministration(EssentialOil essentialOil, Administration administration) {
		super();
		this.essentialOil = essentialOil;
		this.administration = administration;
	}

    public EssentialOil getEssentialOil(){
        return essentialOil;
    }


    public Administration getAdministration(){
        return administration;
    }

	
	
	
}
