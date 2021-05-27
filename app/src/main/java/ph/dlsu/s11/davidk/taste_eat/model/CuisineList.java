package ph.dlsu.s11.davidk.taste_eat.model;

import java.io.Serializable;

public class CuisineList implements Serializable {
    private String Image ="";
    private String Name = "";

    public CuisineList(){

    }

    public CuisineList(String Image, String Name){
        this.Image = Image;
        this.Name = Name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
