package it.unipi.dii.entities;

import org.json.JSONObject;

import java.util.List;

public abstract class  User {
    private String id;
    private String userName;
    private String fullName;
    private String passoword;
    private String sex;
    private int age;
    private String country;

    //private List<Food> eatenFoods; //quantity? timestamp ?


    //Abstract ?
    public User(String Id, String UserName, String FullName, String Password,String Sex, int Age, String Country ){
        this.id = Id;
        this.userName = UserName;
        this.fullName = FullName;
        this.passoword = Password;
        this.sex = Sex;
        this.age = Age;
        this.country = Country;
    }

    public User(String id, String username){
        this.id = id;
        this.userName = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPassoword() {
        return passoword;
    }

    public void setPassoword(String passoword) {
        this.passoword = passoword;
    }

    public abstract JSONObject toJSON ();
}