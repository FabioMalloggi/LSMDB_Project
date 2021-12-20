package it.unipi.dii.entities;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Diet {
    private String id;
    private String name;
    //private List<String> tags = new ArrayList<>();
    private List<Nutrient> nutrients = new ArrayList<>(); // Per100g
    private Nutritionist nutritionist;


    public Diet(String id, String name, List<Nutrient> nutrientList, Nutritionist nutritionist) {
        this.id = id;
        this.name = name;
        this.nutrients.addAll(nutrientList);
        this.nutritionist = nutritionist;
    }

    //private void computeTags() {
    //}

    public JSONObject toJSON() {
        JSONObject jsonDiet = new JSONObject();
        jsonDiet.put("_id",id);
        jsonDiet.put("name",name);

        JSONArray jsonNutrients = new JSONArray();
        JSONObject jsonNutrient;
        for(Nutrient nutrient: nutrients){
            jsonNutrient = nutrient.toJSON();
            jsonNutrients.put(jsonNutrient);
        }
        jsonDiet.put("nutrients", jsonNutrients);

        JSONObject jsonNutritionistRed = new JSONObject(); // Reduced Nutritionist information: only _id and username
        jsonNutritionistRed.put("_id", nutritionist.getId());
        jsonNutritionistRed.put("username",nutritionist.getUserName());
        jsonDiet.put("nutritionist",jsonNutritionistRed);
        return jsonDiet;
    }

    /*
    public static Diet fromJson(JSONObject jsonDiet){
        return new Diet();
    }

     */
}
