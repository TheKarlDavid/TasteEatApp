package ph.dlsu.s11.davidk.taste_eat.model;

import java.util.ArrayList;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private ArrayList<String> saved_recipes = new ArrayList<>();

    public User(){

    }

    public User(String email, String password){

    }

    public User(String firstName, String lastName, String email, String password, String role){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public ArrayList<String> getSaved_recipes() {
        return saved_recipes;
    }

    public void setSaved_recipes(ArrayList<String> saved_recipes) {
        this.saved_recipes = saved_recipes;
    }
}
