package it.unipi.dii.dietmanager.entities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Diet {
    private String id;
    private String name;
    private List<Nutrient> nutrients = new ArrayList<>(); // Per100g
    private Nutritionist nutritionist;


    public Diet(String id, String name, List<Nutrient> nutrientList, Nutritionist nutritionist) {
        this.id = id;
        this.name = name;
        this.nutrients.addAll(nutrientList);
        this.nutritionist = nutritionist;
    }

    public Diet(String id, String name, Nutritionist nutritionist) {
        this.id = id;
        this.name = name;
        this.nutritionist = nutritionist;
    }

    public Diet(String name, List<Nutrient> nutrients, Nutritionist nutritionist) {
        this.name = name;
        this.nutrients = nutrients;
        this.nutritionist = nutritionist;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public List<Nutrient> getNutrients() {
        return nutrients;
    }
    public Nutritionist getNutritionist() {
        return nutritionist;
    }

    public JSONObject toJSON() {
        JSONObject jsonDiet = new JSONObject();
        try {
            jsonDiet.put("_id", id);
            jsonDiet.put("name", name);

            JSONArray jsonNutrients = new JSONArray();
            JSONObject jsonNutrient;
            for (Nutrient nutrient : nutrients) {
                jsonNutrient = nutrient.toJSON();
                jsonNutrients.put(jsonNutrient);
            }
            jsonDiet.put("nutrients", jsonNutrients);

            jsonDiet.put("nutritionist", nutritionist.getUsername());
        }catch(JSONException e){
            e.printStackTrace();
        }
        return jsonDiet;
    }

    public static Diet fromJSON(JSONObject jsonDiet){
        String id, name, nutritionistUsername;
        List<Nutrient> nutrients = new ArrayList<>();
        Nutritionist nutritionist = null;
        Diet newDiet = null;

        //first i retrive the attributes values from the JSONObject
        try{
            id = jsonDiet.getString("_id");
            name = jsonDiet.getString("name");
            nutritionistUsername = jsonDiet.getString("nutritionist");
            nutritionist = new Nutritionist(nutritionistUsername);

            JSONArray jsonNutrients = jsonDiet.getJSONArray("nutrients");
            for (int i = 0; i < jsonNutrients.length(); i++){
                nutrients.add((Nutrient) jsonNutrients.get(i));
            }
            //then generate the new object StandardUser
            newDiet = new Diet(id, name, nutrients, nutritionist);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return newDiet;
    }


}
