package it.unipi.dii.dietmanager.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class EatenFood {
    public static final String ID = "id";
    public static final String FOOD_NAME = "foodName";
    public static final String QUANTITY = "quantity";
    public static final String TIMESTAMP = "timestamp";


    private String id;
    private String foodName;
    private int quantity;
    private Timestamp timestamp;

    // needed for inserting empty eatenFoods into mongoDB in order to reduce re-allocations of documents.
    public EatenFood(){
        this(   generateEatenFoodFormatID(-1),
                String.format(Food.foodNameFieldFormat,""), -1, new Timestamp(0));
    }

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

    public static String generateEatenFoodFormatID(long newID){
        return Long.toString(newID);
    }

    public String getId() { return id;}
    public String getFoodName() {return foodName;}
    public int getQuantity() { return quantity;}
    public Timestamp getTimestamp() {return timestamp;}


    public JSONObject toJSONObject(){
        JSONObject jsonEatenFood = new JSONObject();
        try {
            jsonEatenFood.put(EatenFood.ID, id);                                    // inserting id
            jsonEatenFood.put(EatenFood.FOOD_NAME, foodName);                       // inserting foodID
            jsonEatenFood.put(EatenFood.QUANTITY, quantity);                        // inserting quantity
            jsonEatenFood.put(EatenFood.TIMESTAMP, timestamp.toString());           // inserting timestamp
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
        EatenFood newEatenFood = null;
        try{
            id = jsonEatenFood.getString(EatenFood.ID);                                 // Retrieving ID
            foodName = jsonEatenFood.getString(EatenFood.FOOD_NAME);                    // Retrieving foodName
            quantity = jsonEatenFood.getInt(EatenFood.QUANTITY);                        // Retrieving quantity
            timestamp = Timestamp.valueOf(jsonEatenFood.getString(EatenFood.TIMESTAMP));      // Retrieving timestamp

            newEatenFood = new EatenFood(id, foodName, quantity, timestamp);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return  newEatenFood;
    }

    @Override
    public String toString() {
        return "EatenFood{" +
                "id='" + id + '\'' +
                ", foodName='" + foodName + '\'' +
                ", quantity=" + quantity +
                ", timestamp=" + timestamp +
                '}';
    }

    // needed for indexOf() operation in MongoDB
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EatenFood eatenFood = (EatenFood) o;
        return quantity == eatenFood.quantity &&
                Objects.equals(id, eatenFood.id) &&
                Objects.equals(foodName, eatenFood.foodName) &&
                Objects.equals(timestamp, eatenFood.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, foodName, quantity, timestamp);
    }
}
