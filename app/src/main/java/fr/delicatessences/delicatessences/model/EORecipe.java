package fr.delicatessences.delicatessences.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.delicatessences.delicatessences.model.EssentialOil;
import fr.delicatessences.delicatessences.model.Recipe;

@DatabaseTable(tableName = "eo_recipes_join")
public class EORecipe{

	public static final String RECIPE_ID = "recipe_id";

	public static final String OIL_ID = "oil_id";
	public static final String ID = "_id";

	@DatabaseField(columnName = ID, generatedId = true)
	private int id;

	@DatabaseField(foreign = true, columnName = OIL_ID)
	private	EssentialOil essentialOil;
	
	@DatabaseField(foreign = true, columnName = RECIPE_ID)
	private	Recipe recipe;

	
	public EORecipe() {
		super();
	}


	public EORecipe(EssentialOil essentialOil, Recipe recipe) {
		super();
		this.essentialOil = essentialOil;
		this.recipe = recipe;
	}


    public EssentialOil getEssentialOil(){
        return essentialOil;
    }
	
	public Recipe getRecipe(){
        return recipe;
    }
	
}
