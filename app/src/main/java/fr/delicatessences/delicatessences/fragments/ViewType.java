package fr.delicatessences.delicatessences.fragments;

public enum ViewType {

	HOME,
    RECIPES,
	BOTTLES,
	ESSENTIAL_OILS,
	VEGETAL_OILS,
	FAVORITES,
	DISCOVER,
	WEBSITE;


	public static ViewType fromInt(int id){
        switch(id){
            case 1 :
                return ESSENTIAL_OILS;

            case 2 :
                return VEGETAL_OILS;

            case 3 :
                return RECIPES;

            default:
                return HOME;
        }
    }

}
