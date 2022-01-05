package it.unipi.dii.dietmanager.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Food
{
    public static final String NAME = "_id";
    public static final String CATEGORY = "category";
    public static final String NUTRIENTS = "nutrients";
    public static final String EATEN_TIMES_COUNT = "eatenTimesCount";

    private String name;
    private String category;
    private List<Nutrient> nutrients;
    private int eatenTimesCount;

    public Food(String name, String category, List<Nutrient> nutrients, int eatenTimesCount){
        this.name = name;
        this.category = category;
        this.nutrients = nutrients;
        this.eatenTimesCount = eatenTimesCount;
    }

    public Food(String name, List<Nutrient> nutrients, int eatenTimesCount){
        this.name = name;
        this.nutrients = nutrients;
        this.eatenTimesCount = eatenTimesCount;
    }

    public String getName() { return name; }
    public String getCategory(){ return category; }
    public List<Nutrient> getNutrients() { return nutrients; }
    public int getEatenTimesCount(){ return eatenTimesCount; }

    public void setName(String name) { this.name = name; }
    public void setCategory(String category){ this.category = category;}
    public void setNutrients(List<Nutrient> nutrients) { this.nutrients = nutrients; }
    public void incrementEatenTimesCount(){ this.eatenTimesCount++; }

    public JSONObject toJSONObject(){
        JSONObject jsonFood = new JSONObject();
        try {
            jsonFood.put(Food.NAME, name);                          // inserting name
            if(!category.isEmpty())
                jsonFood.put(Food.CATEGORY, category);              // inserting category
            jsonFood.put(Food.EATEN_TIMES_COUNT, eatenTimesCount);  // inserting eatenTimesCount

            JSONArray jsonNutrients = new JSONArray();
            JSONObject jsonNutrient;
            for(int i=0; i<nutrients.size(); i++){
                jsonNutrient = nutrients.get(i).toJSONObject();
                jsonNutrients.put(jsonNutrient);
            }
            jsonFood.put(Food.NUTRIENTS, jsonNutrients);            // inserting nutrients
        }catch(Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return jsonFood;
    }

    public static Food fromJSONObject(JSONObject jsonFood){
        Food newFood = null;
        String name, category;
        int eatenTimesCount;
        List<Nutrient> nutrients = new ArrayList<>();
        try{
            name = jsonFood.getString(Food.NAME);                           // retrieving name
            category = jsonFood.getString(Food.CATEGORY);                   // retrieving category
            eatenTimesCount = jsonFood.getInt(Food.EATEN_TIMES_COUNT);      // retrieving eatenTimesCount
            JSONArray jsonNutrients = jsonFood.getJSONArray(Food.NUTRIENTS);

            for (int i = 0; i < jsonNutrients.length(); i++){
                nutrients.add(Nutrient.fromJSONObject(jsonNutrients.getJSONObject(i))); // retrieving nutrients
            }
            newFood = new Food( name, category, nutrients, eatenTimesCount);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return newFood;
    }

    @Override
    public String toString() {
        return "Food{" +
                "_id='" + name + '\'' +
                ", category='" + category + '\'' +
                ", nutrients=" + nutrients +
                ", eatenTimesCount=" + eatenTimesCount +
                '}';
    }
}
