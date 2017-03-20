package fr.delicatessences.delicatessences.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.delicatessences.delicatessences.model.VegetalIndication;
import fr.delicatessences.delicatessences.model.VegetalOil;

@DatabaseTable(tableName = "vo_indications_join")
public class VOIndication{

	public static final String INDICATION_ID = "indication_id";

	public static final String OIL_ID = "oil_id";
	private static final String ID = "_id";

	@DatabaseField(columnName = ID, generatedId = true)
	private int id;

	@DatabaseField(foreign = true, columnName = OIL_ID)
	private	VegetalOil vegetalOil;
	
	@DatabaseField(foreign = true, columnName = INDICATION_ID)
	private	VegetalIndication indication;

	
	public VOIndication() {
		super();
	}


	public VOIndication(VegetalOil vegetalOil, VegetalIndication indication) {
		super();
		this.vegetalOil = vegetalOil;
		this.indication = indication;
	}


    public VegetalOil getVegetalOil(){
        return vegetalOil;
    }


    public VegetalIndication getIndication(){
        return indication;
    }
	
	
	
}
