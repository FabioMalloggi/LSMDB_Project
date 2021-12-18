package it.unipi.dii.entities;

import java.util.List;

public class User {
    private String id;
    private String userName;
    private String fullName;
    private String sex;
    private int age;
    private String country;

    //
    private List<Food> eatenFoods; //quantity? timestamp ?

    //Abstract ?
    public User(String Id, String UserName, String FullName, String Sex, int Age, String Country ){
        this.id = Id;
        this.userName = UserName;
        this.fullName = FullName;
        this.sex = Sex;
        this.age = Age;
        this.country = Country;
    }
}