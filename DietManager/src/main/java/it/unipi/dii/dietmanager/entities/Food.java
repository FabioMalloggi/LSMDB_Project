package it.unipi.dii.dietmanager.entities;

import org.json.JSONArray;
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

    public void addNutrient(Nutrient nutrient){
        nutrients.add(nutrient);
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

}
