package ph.dlsu.s11.davidk.taste_eat.model;

import java.io.Serializable;

public class Recipes implements Serializable {
    private String name;
    private String Image;
    private String ingredients;
    private String cuisine;
    private String instructions;
    private String meal;
    private int likes;

    public Recipes(){

    }

    public Recipes(String name, String Image, String ingredients, String cuisine, String instructions, String meal, int likes){
        this.name = name;
        this.Image = Image;
        this.ingredients = ingredients;
        this.cuisine = cuisine;
        this.instructions = instructions;
        this.meal = meal;
        this.likes = likes;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
