package it.unipi.dii.dietmanager.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Food
{
    private String id;
    private List<Nutrient> nutrients;

    public Food(String id) {
        this.id = id;
        nutrients = new ArrayList<>();
    }

    public Food(String id, List<Nutrient> nutrients) {
        this.id = id;
        this.nutrients = nutrients;
    }

    public void addNutrient(Nutrient nutrient){
        nutrients.add(nutrient);
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public List<Nutrient> getNutrients() {
        return nutrients;
    }
    public void setNutrients(List<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

    public JSONObject toJSON(){
        JSONObject jsonFood = new JSONObject();
        try {
            JSONArray jsonNutrients = new JSONArray();
            JSONObject jsonNutrient;

            jsonFood.put("id", id);
            for(int i=0; i<nutrients.size(); i++){
                jsonNutrient = nutrients.get(i).toJSON();
                jsonNutrients.put(jsonNutrient);
                jsonFood.put("nutrients", jsonNutrients);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return jsonFood;
    }

    public static Food fromJSON(JSONObject jsonFood){
        Food newFood = null;
        String id;
        List<Nutrient> nutrients = new ArrayList<>();

        //first i retrive the attributes values from the JSONObject
        try{
            id = jsonFood.getString("_id");
            JSONArray jsonNutrients = jsonFood.getJSONArray("nutrients");

            for (int i = 0; i < jsonNutrients.length(); i++){
                nutrients.add((Nutrient) jsonNutrients.get(i));
            }
            //then generate the new object Food
            newFood = new Food( id, nutrients);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return newFood;
    }
}
