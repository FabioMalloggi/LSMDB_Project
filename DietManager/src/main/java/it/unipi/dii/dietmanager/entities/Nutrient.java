package it.unipi.dii.dietmanager.entities;

import org.json.JSONObject;

public class Nutrient
{
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

    public JSONObject toJSON()
    {
        JSONObject jsonNutrient = null;
        try {
            jsonNutrient = new JSONObject();
            jsonNutrient.put("name", name);
            jsonNutrient.put("unit", unit);
            jsonNutrient.put("quantity", quantity);
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return jsonNutrient;
    }
}
