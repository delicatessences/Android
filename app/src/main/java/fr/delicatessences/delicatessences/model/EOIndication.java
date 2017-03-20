package fr.delicatessences.delicatessences.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.delicatessences.delicatessences.model.EssentialIndication;
import fr.delicatessences.delicatessences.model.EssentialOil;

@DatabaseTable(tableName = "eo_indications_join")
public class EOIndication {

    public static final String INDICATION_ID = "indication_id";

    public static final String OIL_ID = "oil_id";
    private static final String ID = "_id";

    @DatabaseField(columnName = ID, generatedId = true)
    private int id;

    @DatabaseField(foreign = true, columnName = OIL_ID)
    private
    EssentialOil essentialOil;

    @DatabaseField(foreign = true, columnName = INDICATION_ID)
    private
    EssentialIndication indication;


    public EOIndication() {
        super();
    }


    public EOIndication(EssentialOil essentialOil, EssentialIndication indication) {
        super();
        this.essentialOil = essentialOil;
        this.indication = indication;
    }


    public EssentialOil getEssentialOil() {
        return essentialOil;
    }


    public EssentialIndication getIndication() {
        return indication;
    }

}
