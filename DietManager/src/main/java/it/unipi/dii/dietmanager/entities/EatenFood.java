package it.unipi.dii.dietmanager.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class EatenFood {
    public static final String ID = "id";
    public static final String FOOD_NAME = "foodName";
    public static final String QUANTITY = "quantity";
    public static final String TIMESTAMP = "timestamp";

    private String id;
    private String foodName;
    private int quantity;
    private Timestamp timestamp;

    // only for first time an eatenFood is created by standard users
    public EatenFood(String foodName, int quantity, Timestamp timestamp) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public EatenFood(String id, String foodName, int quantity, Timestamp timestamp) {
        this.id = id;
        this.foodName = foodName;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getId() { return id;}
    public String getFoodName() {return foodName;}
    public int getQuantity() { return quantity;}
    public Timestamp getTimestamp() {return timestamp;}

    public JSONObject toJSONObject(){
        JSONObject jsonEatenFood = new JSONObject();
        try {
            jsonEatenFood.put(EatenFood.ID, id);                        // inserting id
            jsonEatenFood.put(EatenFood.FOOD_NAME, foodName);           // inserting foodID
            jsonEatenFood.put(EatenFood.QUANTITY, quantity);            // inserting quantity
            jsonEatenFood.put(EatenFood.TIMESTAMP, timestamp);          // inserting timestamp
        }catch(Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return jsonEatenFood;
    }

    public static EatenFood fromJSONObject(JSONObject jsonEatenFood){
        String id, foodName;
        int quantity;
        Timestamp timestamp;
        EatenFood newEatenFood;
        try{
            id = jsonEatenFood.getString(EatenFood.ID);                                 // Retrieving ID
            foodName = jsonEatenFood.getString(EatenFood.FOOD_NAME);                    // Retrieving foodName
            quantity = jsonEatenFood.getInt(EatenFood.QUANTITY);                        // Retrieving quantity
            timestamp = Timestamp.valueOf(jsonEatenFood.getString(EatenFood.TIMESTAMP));// Retrieving timestamp

            newEatenFood = new EatenFood(id, foodName, quantity, timestamp);
        }
        catch (JSONException e){
            e.printStackTrace();
            newEatenFood = null;
        }
        return  newEatenFood;
    }
}
