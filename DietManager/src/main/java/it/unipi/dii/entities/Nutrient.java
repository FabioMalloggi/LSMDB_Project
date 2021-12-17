package it.unipi.dii.entities;

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
}
