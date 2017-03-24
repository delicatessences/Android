package fr.delicatessences.delicatessences.fragments;

import static android.R.attr.id;

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
            case 0:
                return BOTTLES;

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


    public int getInt(){
        switch(this){
            case BOTTLES:
                return 0;

            case ESSENTIAL_OILS:
                return 1;

            case VEGETAL_OILS:
                return 2;

            case RECIPES:
                return 3;

            default:
                throw new IllegalStateException("getInt not supported for this enum type");
        }
    }

}
