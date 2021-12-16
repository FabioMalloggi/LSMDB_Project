package it.unipi.dii.entities;

import java.util.List;

public class Food
{
    private String id;
    private List<Nutrient> nutrients;

    public Food(String id)
    {
        this.id = id;
    }
}
