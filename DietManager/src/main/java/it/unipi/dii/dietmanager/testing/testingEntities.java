package it.unipi.dii.dietmanager.testing;

import it.unipi.dii.dietmanager.entities.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class testingEntities {
    private final int NUM_NUTRIENTS = 17;
    private final int NUM_FOODS = 10;
    private final int NUM_EATEN_FOODS = 10;

    private List<Food> foods = new ArrayList<>();
    private List<Nutrient> nutrients = new ArrayList<>();
    private List<EatenFood> eatenFoods = new ArrayList<>();
    private List<Diet> diets = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<StandardUser> standardUsers = new ArrayList<>();
    private List<Administrator> administrators = new ArrayList<>();
    private List<Nutritionist> nutritionists = new ArrayList<>();

    public void populateNutrient(){
        for (int i = 0; i < NUM_NUTRIENTS; i++) {
            nutrients.add(new Nutrient("nutrient" + i, "mg", i+(double)i/100));
        }
    }

    public void populateFoods() {
        for (int i = 0; i < NUM_FOODS; i++) {
            foods.add(new Food("food" + i, "category" + i, nutrients, i));
        }
    }

    public void populateEatenFoods() {
        for (int i = 0; i < NUM_EATEN_FOODS; i++) {
            //eatenFoods.add(new EatenFood( String.format("%08d",i), "food" + (i%5),i*100, new Timestamp()));
        }
    }

    public void populateStandardUsers(){
        standardUsers.addAll(Arrays.asList(

        ));
    }





    public static void main(String[] args){
        /********************** testing Standard User **********************/
        List<User> standardUsers = new ArrayList<>();


        /*
        standardUsers.addAll(Arrays.asList(
                new StandardUser("stdUser1", "Std User 1", "m", "stdUser1", 20, "Italy",
                        new ArrayList<>(Arrays.asList()),"00000000"),
                new StandardUser("stdUser2", "Std User 2", "m", "stdUser2", 20, "Italy"),
                new StandardUser("stdUser3", "Std User 3", "f", "stdUser3", 20, "England"),

                new StandardUser("stdUser4", "Std User 4", "f", "stdUser4", 20, "England"),
                new StandardUser("stdUser5", "Std User 5", "f", "stdUser5", 20, "Germany"),

                new Administrator("admin1", "Admin 1", "m", "admin1", 20, "France"),
                new Administrator("admin1", "Admin 1", "m", "admin1", 20, "France")
                ));

         */
    }
}
