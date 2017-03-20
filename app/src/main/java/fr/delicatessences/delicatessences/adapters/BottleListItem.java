package fr.delicatessences.delicatessences.adapters;


import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BottleListItem {


    private final int bottleId;
    private final int essentialOilId;
    private final String essentialOilName;
    private final String brand;
    private final String image;
    private String expirationDate;
    private boolean isExpired;



    public static BottleListItem fromCursor(Cursor cursor) {

        int bottleId = cursor.getInt(0);
        String brand = cursor.getString(1);
        String expirationDate = cursor.getString(2);
        int essentialOilId = cursor.getInt(3);
        String essentialOilName = cursor.getString(4);
        String imageResource = cursor.getString(5);

        return new BottleListItem(bottleId, brand, expirationDate, essentialOilId, essentialOilName, imageResource);
    }




    public BottleListItem(int bottleId, String brand, String expiration, int essentialOilId, String essentialOilName, String image) {
        this.bottleId = bottleId;
        this.essentialOilId = essentialOilId;
        this.essentialOilName = essentialOilName;
        this.brand = brand;
        this.image = image;

        if (expiration != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.US);
            try {
                Date date = dateFormat.parse(expiration);
                dateFormat.applyPattern("MM/yyyy");
                expirationDate = dateFormat.format(date);
                Calendar calendar = Calendar.getInstance();
                Date todayDate = calendar.getTime();
                isExpired = date.before(todayDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }


    public int getBottleId() {
        return bottleId;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public int getEssentialOilId() {
        return essentialOilId;
    }

    public String getEssentialOilName() {
        return essentialOilName;
    }

    public String getBrand() {
        return brand;
    }

    public String getImage() {
        return image;
    }

    public String getExpirationDate() { return expirationDate; }
}
