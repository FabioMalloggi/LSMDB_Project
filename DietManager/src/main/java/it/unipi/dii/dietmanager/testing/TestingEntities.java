package it.unipi.dii.dietmanager.testing;

import it.unipi.dii.dietmanager.entities.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TestingEntities {
    private final int NUM_NUTRIENTS = 17;
    private final int NUM_FOODS = 10;
    private final int NUM_EATEN_FOODS = 10;
    private final int NUM_DIETS = 10;
    private final int NUM_STANDARD_USER_COMPLETE = 2;
    private final int NUM_STANDARD_USER_PARTIAL = 2; // NO current diet
    private final int NUM_ADMINISTRATOR = 10;
    private final int NUM_NUTRITIONIST = 10;

    private List<Food> foods = new ArrayList<>();
    private List<Nutrient> nutrients = new ArrayList<>();
    private List<EatenFood> eatenFoods = new ArrayList<>();
    private List<Diet> diets = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<StandardUser> standardUsers_c = new ArrayList<>();
    private List<StandardUser> standardUsers_p = new ArrayList<>();
    private List<Administrator> administrators = new ArrayList<>();
    private List<Nutritionist> nutritionists = new ArrayList<>();

    private JSONArray jsonNutrients;
    private JSONArray jsonFoods;
    private JSONArray jsonEatenFoods;
    private JSONArray jsonDiets;
    private JSONArray jsonUsers;
    private JSONArray jsonStandardUsersComplete;
    private JSONArray jsonStandardUsersPartial;
    private JSONArray jsonNutritionist;
    private JSONArray jsonAdministrator;


    public void populate(){
        for (int i = 0; i < NUM_NUTRIENTS; i++) {
            nutrients.add(new Nutrient("nutrient" + i, "mg", i+(double)i/100));
        }
        for (int i = 0; i < NUM_FOODS; i++) {
            foods.add(new Food("food" + i, "category" + i, nutrients, i));
        }
        for (int i = 0; i < NUM_EATEN_FOODS; i++) {
            eatenFoods.add(new EatenFood( String.format("%08d",i), "food" + (i%5),i*100, new Timestamp(System.currentTimeMillis())));
        }
        for (int i = 0; i < NUM_DIETS; i++) {
            diets.add(new Diet(String.format("%08d",i), "diet" + i, nutrients, "nutritionist"+(double)i%5));
        }
        for (int i = 0; i < NUM_ADMINISTRATOR; i++) {
            administrators.add(new Administrator( "administrator"+i,"Admin "+i,"m", "administrator"+i,20,"Italy" ));
        }
        for (int i = 0; i < NUM_STANDARD_USER_COMPLETE; i++) {
            standardUsers_c.add(new StandardUser( "standarduser"+i,"Standard User "+i,"m", "standarduser"+i,20,"France" ));
        }
        for (int i = NUM_STANDARD_USER_COMPLETE+1; i < NUM_STANDARD_USER_COMPLETE+NUM_STANDARD_USER_PARTIAL; i++) {
            standardUsers_p.add(new StandardUser( "standarduser"+i,"Standard User "+i + " partial Standard User","m", "standarduser"+i,20,"France" ));
        }
        for (int i = 0; i < NUM_NUTRITIONIST; i++) {
            nutritionists.add(new Nutritionist( "nutritionist"+i,"Nutritionist "+i,"m", "nutritionist"+i,20,"England" ));
        }
        users.addAll(standardUsers_c);
        users.addAll(standardUsers_p);
        users.addAll(administrators);
        users.addAll(nutritionists);
    }






    public static void main(String[] args){
        TestingEntities te = new TestingEntities();
        te.populate();

        System.out.println(te.nutrients.toString());
        for(Nutrient nutrient: te.nutrients){
            nutrient.toJSONObject();
        }

        /********************** testing Standard User **********************/

    }
}
