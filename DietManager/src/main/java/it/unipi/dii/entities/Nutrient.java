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
}
