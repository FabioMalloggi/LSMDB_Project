package it.unipi.dii.entities;

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
}
