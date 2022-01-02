package it.unipi.dii.dietmanager.entities;

import org.json.JSONException;
import org.json.JSONObject;

public class Nutrient
{
    public static final String NAME = "name";
    public static final String UNIT = "unit";
    public static final String QUANTITY = "quantity";

    private String name;
    private String unit;
    private double quantity;

    public Nutrient(String name, String unit, double quantity)
    {
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
    }

    public Nutrient(String name, String unit)
    {
        this.name = name;
        this.unit = unit;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    public void setUnit(String unit)
    {
        this.unit = unit;
    }
    public void setQuantity(double quantity)
    {
        this.quantity = quantity;
    }

    public String getName()
    {
        return name;
    }
    public String getUnit()
    {
        return unit;
    }
    public double getQuantity()
    {
        return quantity;
    }

    public JSONObject toJSONObject()
    {
        JSONObject jsonNutrient = null;
        try {
            jsonNutrient = new JSONObject();
            jsonNutrient.put(Nutrient.NAME, name);              // inserting name
            jsonNutrient.put(Nutrient.UNIT, unit);              // inserting unit
            jsonNutrient.put(Nutrient.QUANTITY, quantity);      // inserting quantity
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return jsonNutrient;
    }

    public static Nutrient fromJSONObject(JSONObject jsonNutrient){
        String name, unit;
        double quantity;
        Nutrient newNutrient = null;
        try{
            name = jsonNutrient.getString(Nutrient.NAME);           // retrieving name
            unit = jsonNutrient.getString(Nutrient.UNIT);           // retrieving unit
            quantity = jsonNutrient.getDouble(Nutrient.QUANTITY);   // retrieving quantity

            newNutrient = new Nutrient(name, unit, quantity);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return  newNutrient;
    }
}
