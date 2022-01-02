package it.unipi.dii.dietmanager.testing;

import it.unipi.dii.dietmanager.entities.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EntitiesTester {
    public static EntitiesTester te = new EntitiesTester();
    public int INDEX_ANALIZED = 0;

    private final int NUM_NUTRIENTS = 17;
    private final int NUM_FOODS = 10;
    private final int NUM_EATEN_FOODS = 10;
    private final int NUM_DIETS = 10;
    private final int NUM_STANDARD_USER_COMPLETE = 10;
    private final int NUM_STANDARD_USER_PARTIAL = 10; // NO current diet
    private final int NUM_ADMINISTRATOR = 10;
    private final int NUM_NUTRITIONIST = 10;

    private List<Food> foods = new ArrayList<>();
    private List<Nutrient> nutrients = new ArrayList<>();
    private List<EatenFood> eatenFoods = new ArrayList<>();
    private List<Diet> diets = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<StandardUser> standardUsersComplete = new ArrayList<>();
    private List<StandardUser> standardUsersPartial = new ArrayList<>();
    private List<Administrator> administrators = new ArrayList<>();
    private List<Nutritionist> nutritionists = new ArrayList<>();

    private JSONArray jsonNutrients = new JSONArray();
    private JSONArray jsonFoods = new JSONArray();
    private JSONArray jsonEatenFoods = new JSONArray();
    private JSONArray jsonDiets = new JSONArray();
    private JSONArray jsonUsers = new JSONArray();
    private JSONArray jsonStandardUsersComplete = new JSONArray();
    private JSONArray jsonStandardUsersPartial = new JSONArray();
    private JSONArray jsonNutritionist = new JSONArray();
    private JSONArray jsonAdministrator = new JSONArray();

    public void setINDEX_ANALIZED(int index) {
        this.INDEX_ANALIZED = index;
    }

    public void populate(){
        for (int i = 0; i < NUM_NUTRIENTS; i++) {
            nutrients.add(new Nutrient("nutrient" + i, "mg", i+(double)i/100));
        }
        for (int i = 0; i < NUM_FOODS; i++) {
            foods.add(new Food("food" + i, "category" + i, nutrients, i));
        }
        for (int i = 0; i < NUM_EATEN_FOODS; i++) {
            eatenFoods.add(new EatenFood( String.format("%08d",i), "food" + (i%NUM_FOODS),(i%NUM_EATEN_FOODS)*100, new Timestamp(System.currentTimeMillis())));
        }
        for (int i = 0; i < NUM_DIETS; i++) {
            diets.add(new Diet(String.format("%08d",i), "diet" + i, nutrients, "nutritionist"+(double)i%NUM_NUTRITIONIST));
        }
        for (int i = 0; i < NUM_ADMINISTRATOR; i++) {
            administrators.add(new Administrator( "administrator"+i,"Admin "+i,"m", "administrator"+i,20,"Italy" ));
        }
        for (int i = 0; i < NUM_STANDARD_USER_COMPLETE; i++) {
            standardUsersComplete.add(new StandardUser( "standarduser"+i,"Standard User "+i,"m", "standarduser"+i,20,"France", eatenFoods, diets.get(i%NUM_DIETS) ));
        }
        for (int i = NUM_STANDARD_USER_COMPLETE; i < NUM_STANDARD_USER_COMPLETE+NUM_STANDARD_USER_PARTIAL; i++) {
            standardUsersPartial.add(new StandardUser( "standarduser"+i,"Standard User "+i + " partial Standard User","m", "standarduser"+i,20,"France" ));
        }
        for (int i = 0; i < NUM_NUTRITIONIST; i++) {
            nutritionists.add(new Nutritionist( "nutritionist"+i,"Nutritionist "+i,"m", "nutritionist"+i,20,"England" ));
        }
        users.addAll(standardUsersComplete);
        users.addAll(standardUsersPartial);
        users.addAll(administrators);
        users.addAll(nutritionists);
    }


    public void nutrientTester(){
        Nutrient nutrient = te.nutrients.get(INDEX_ANALIZED);
        System.out.println("JavaObject    -> toString: "+ nutrient.toString());                             // Print Java Object

        JSONObject jsonNutrient = nutrient.toJSONObject();
        System.out.println("JSONObject    -> toString: "+ jsonNutrient.toString());                         // Print JSONObject
        System.out.println("JavaObject(2) -> toString: "+ Nutrient.fromJSONObject(jsonNutrient).toString());// Print Java Object
    }

    public void foodTester(){
        Food food = te.foods.get(INDEX_ANALIZED);
        System.out.println("JavaObject    -> toString: "+ food.toString());                                 // Print Java Object

        JSONObject jsonFood = food.toJSONObject();
        System.out.println("JSONObject    -> toString: "+ jsonFood.toString());                             // Print JSONObject
        System.out.println("JavaObject(2) -> toString: "+ Food.fromJSONObject(jsonFood).toString());        // Print Java Object
    }

    public void eatenFoodTester(){
        EatenFood eatenFood = te.eatenFoods.get(INDEX_ANALIZED);
        System.out.println("JavaObject    -> toString: "+ eatenFood.toString());                              // Print Java Object

        JSONObject jsonEatenFood = eatenFood.toJSONObject();
        System.out.println("JSONObject    -> toString: "+ jsonEatenFood.toString());                          // Print JSONObject
        System.out.println("JavaObject(2) -> toString: "+ EatenFood.fromJSONObject(jsonEatenFood).toString());// Print Java Object
    }

    public void dietTester(){
        Diet diet = te.diets.get(INDEX_ANALIZED);
        System.out.println("JavaObject    -> toString: "+ diet.toString());                         // Print Java Object

        JSONObject jsonDiet = diet.toJSONObject();
        System.out.println("JSONObject    -> toString: "+ jsonDiet.toString());                     // Print JSONObject
        System.out.println("JavaObject(2) -> toString: "+ Diet.fromJSONObject(jsonDiet).toString());// Print Java Object
    }

    public void standardUserCompleteTester(){
        StandardUser standardUserComplete = te.standardUsersComplete.get(INDEX_ANALIZED);
        System.out.println("JavaObject    -> toString: "+ standardUserComplete.toString());                                 // Print Java Object

        JSONObject jsonStandardUserComplete = standardUserComplete.toJSONObject();
        System.out.println("JSONObject    -> toString: "+ jsonStandardUserComplete.toString());                             // Print JSONObject
        System.out.println("JavaObject(2) -> toString: "+ StandardUser.fromJSONObject(jsonStandardUserComplete).toString());// Print Java Object
    }

    public void standardUserPartialTester(){
        StandardUser standardUserPartial = te.standardUsersPartial.get(INDEX_ANALIZED);
        System.out.println("JavaObject    -> toString: "+ standardUserPartial.toString());                                  // Print Java Object

        JSONObject jsonStandardUserPartial = standardUserPartial.toJSONObject();
        System.out.println("JSONObject    -> toString: "+ jsonStandardUserPartial.toString());                              // Print JSONObject
        System.out.println("JavaObject(2) -> toString: "+ StandardUser.fromJSONObject(jsonStandardUserPartial).toString()); // Print Java Object
    }

    public void nutritionistTester(){
        Nutritionist nutritionist = te.nutritionists.get(INDEX_ANALIZED);
        System.out.println("JavaObject    -> toString: "+ nutritionist.toString());                                 // Print Java Object

        JSONObject jsonNutritionist = nutritionist.toJSONObject();
        System.out.println("JSONObject    -> toString: "+ jsonNutritionist.toString());                             // Print JSONObject
        System.out.println("JavaObject(2) -> toString: "+ Nutritionist.fromJSONObject(jsonNutritionist).toString());// Print Java Object
    }

    public void administratorTester(){
        Administrator administrator = te.administrators.get(INDEX_ANALIZED);
        System.out.println("JavaObject    -> toString: "+ administrator.toString());                                  // Print Java Object

        JSONObject jsonAdministrator = administrator.toJSONObject();
        System.out.println("JSONObject    -> toString: "+ jsonAdministrator.toString());                              // Print JSONObject
        System.out.println("JavaObject(2) -> toString: "+ Administrator.fromJSONObject(jsonAdministrator).toString());// Print Java Object
    }
    public void allUsersTester(){
        int counter = 1;
        for(User user: users){
            System.out.println("\n ************************************");
            System.out.println("user #"+(counter++) + " under processing:");
            System.out.println("JavaObject    -> toString: "+ user.toString());                                 // Print Java Object
            JSONObject jsonUser = user.toJSONObject();
            System.out.println("JSONObject    -> toString: "+ jsonUser.toString());                             // Print JSONObject

            if(user instanceof StandardUser)
                System.out.println("JavaObject(2) -> toString: "+ StandardUser.fromJSONObject(jsonUser).toString());// Print Java Object
            else if(user instanceof  Nutritionist)
                System.out.println("JavaObject(2) -> toString: "+ Nutritionist.fromJSONObject(jsonUser).toString());// Print Java Object
            else if(user instanceof Administrator)
                System.out.println("JavaObject(2) -> toString: "+ Administrator.fromJSONObject(jsonUser).toString());// Print Java Object
            else
                System.out.println("ERROR: user is not either StandardUser, Nutritionist or Administrator");
        }
    }



    public static void main(String[] args){
        //te.setINDEX_ANALIZED(5);
        te.populate();
        //te.nutrientTester();
        //te.foodTester();
        //te.eatenFoodTester();
        te.dietTester();
        //te.standardUserCompleteTester();
        //te.standardUserPartialTester();
        //te.nutritionistTester();
        //te.administratorTester();
        //te.allUsersTester();
    }
}
