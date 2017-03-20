package fr.delicatessences.delicatessences.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "vo_recipes_join")
public class VORecipe{

	public static final String RECIPE_ID = "recipe_id";

	public static final String OIL_ID = "oil_id";
	private static final String ID = "_id";

	@DatabaseField(columnName = ID, generatedId = true)
	private int id;

	@DatabaseField(foreign = true, columnName = OIL_ID)
	private	VegetalOil vegetalOil;
	
	@DatabaseField(foreign = true, columnName = RECIPE_ID)
	private	Recipe recipe;

	
	public VORecipe() {
		super();
	}


	public VORecipe(VegetalOil vegetalOil, Recipe recipe) {
		super();
		this.vegetalOil = vegetalOil;
		this.recipe = recipe;
	}



    public VegetalOil getVegetalOil(){
        return vegetalOil;
    }

    public Recipe getRecipe(){
        return recipe;
    }
	
	
}
