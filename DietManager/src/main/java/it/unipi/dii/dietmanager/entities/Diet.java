package it.unipi.dii.dietmanager.entities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Diet {
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String NUTRIENTS = "nutrients";
    public static final String NUTRITIONIST = "nutritionist";

    private String id;
    private String name;
    private String nutritionist;
    private List<Nutrient> nutrients = new ArrayList<>(); // Per100g

    public Diet(String id){
        this.id = id;
    }

    public Diet(String id, String name, List<Nutrient> nutrientList, String nutritionist) {
        this.id = id;
        this.name = name;
        this.nutrients.addAll(nutrientList);
        this.nutritionist = nutritionist;
    }

    public Diet(String id, String name, String nutritionist) {
        this.id = id;
        this.name = name;
        this.nutritionist = nutritionist;
    }

    public Diet(String name, List<Nutrient> nutrients, String nutritionist) {
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
    public String getNutritionist() {
        return nutritionist;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonDiet = new JSONObject();
        try {
            jsonDiet.put(Diet.ID, id);                      // inserting ID
            jsonDiet.put(Diet.NAME, name);                  // inserting name
            jsonDiet.put(Diet.NUTRITIONIST, nutritionist);  // inserting nutritionist

            JSONArray jsonNutrients = new JSONArray();
            JSONObject jsonNutrient;
            for (Nutrient nutrient : nutrients) {
                jsonNutrient = nutrient.toJSONObject();
                jsonNutrients.put(jsonNutrient);
            }
            jsonDiet.put(Diet.NUTRIENTS, jsonNutrients);    // inserting nutrients
        }catch(JSONException e){
            e.printStackTrace();
        }
        return jsonDiet;
    }

    public static Diet fromJSONObject(JSONObject jsonDiet){
        String id, name, nutritionist;
        List<Nutrient> nutrients = new ArrayList<>();
        Diet newDiet = null;

        //first i retrive the attributes values from the JSONObject
        try{
            id = jsonDiet.getString(Diet.ID);                           // retrieving ID
            name = jsonDiet.getString(Diet.NAME);                       // retrieving name
            nutritionist = jsonDiet.getString(Diet.NUTRITIONIST);       // retrieving nutritionist

            JSONArray jsonNutrients = jsonDiet.getJSONArray(Diet.NUTRIENTS);
            for (int i = 0; i < jsonNutrients.length(); i++){
                nutrients.add(Nutrient.fromJSONObject(jsonNutrients.getJSONObject(i))); // retrieving nutrients
            }
            //then generate the new object StandardUser
            newDiet = new Diet(id, name, nutrients, nutritionist);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return newDiet;
    }

    @Override
    public String toString() {
        return "Diet{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", nutritionist='" + nutritionist + '\'' +
                ", nutrients=" + nutrients +
                '}';
    }
}
