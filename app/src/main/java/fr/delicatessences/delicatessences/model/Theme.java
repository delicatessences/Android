package fr.delicatessences.delicatessences.model;


class Theme {

    private final String image;
    private final String color;

    public Theme(final String image, final String color){
        this.image = image;
        this.color = color;
    }


    public String getImage() {
        return image;
    }

    public String getColor() {
        return color;
    }
}
