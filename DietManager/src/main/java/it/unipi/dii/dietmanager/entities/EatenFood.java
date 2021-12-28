package it.unipi.dii.dietmanager.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class EatenFood {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public static EatenFood fromJSON(JSONObject jsonEatenFood){
        String id, foodID;
        int quantity;
        Timestamp timestamp;
        EatenFood eatenFood = null;

        try{
            id = jsonEatenFood.getString("eatenFoodID");
            foodID = jsonEatenFood.getString("foodID");
            quantity = jsonEatenFood.getInt("quantity");
            timestamp = Timestamp.valueOf(jsonEatenFood.getString("timestamp"));

            eatenFood = new EatenFood(id, foodID, quantity, timestamp);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return  eatenFood;
    }

}
