package it.unipi.dii.dietmanager.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class EatenFood {
    public static final String ID = "id";
    public static final String FOOD_ID = "foodID";
    public static final String QUANTITY = "quantity";
    public static final String TIMESTAMP = "timestamp";

    private String id;
    private String foodID;
    private int quantity;
    private Timestamp timestamp;

    public EatenFood(String id, String foodID, int quantity, Timestamp timestamp) {
        this.id = id;
        this.foodID = foodID;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getId() { return id;}
    public String getFoodID() {return foodID;}
    public int getQuantity() { return quantity;}
    public Timestamp getTimestamp() {return timestamp;}

    /*
    public void setId(String id) { this.id = id; }
    public void setFoodID(String foodID) { this.foodID = foodID;}
    public void setQuantity(int quantity) {this.quantity = quantity;}
    public void setTimestamp(Timestamp timestamp) {this.timestamp = timestamp;}

     */

    public JSONObject toJSONObject(){
        JSONObject jsonEatenFood = new JSONObject();
        try {
            jsonEatenFood.put(EatenFood.ID, id);                    // inserting id
            jsonEatenFood.put(EatenFood.FOOD_ID, foodID);           // inserting foodID
            jsonEatenFood.put(EatenFood.QUANTITY, quantity);        // inserting quantity
            jsonEatenFood.put(EatenFood.TIMESTAMP, timestamp);       // inserting timestamp
        }catch(Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return jsonEatenFood;
    }

    public static EatenFood fromJSONObject(JSONObject jsonEatenFood){
        String id, foodID;
        int quantity;
        Timestamp timestamp;
        EatenFood newEatenFood;
        try{
            id = jsonEatenFood.getString("eatenFoodID");
            foodID = jsonEatenFood.getString("foodID");
            quantity = jsonEatenFood.getInt("quantity");
            timestamp = Timestamp.valueOf(jsonEatenFood.getString("timestamp"));

            newEatenFood = new EatenFood(id, foodID, quantity, timestamp);
        }
        catch (JSONException e){
            e.printStackTrace();
            newEatenFood = null;
        }
        return  newEatenFood;
    }
}
