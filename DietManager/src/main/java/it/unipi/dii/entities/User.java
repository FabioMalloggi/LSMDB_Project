package it.unipi.dii.entities;

import org.json.JSONObject;

public abstract class  User {
    //private String id;
    private String username;
    private String fullName;
    private String password;
    private String sex;
    private int age;
    private String country;

    //private List<Food> eatenFoods; //quantity? timestamp ?


    //Abstract ?
    public User(String UserName, String FullName, String Password,String Sex, int Age, String Country ){
        //this.id = Id;
        this.username = UserName;
        this.fullName = FullName;
        this.password = Password;
        this.sex = Sex;
        this.age = Age;
        this.country = Country;
    }

    public User(/*String id,*/ String username){
        //this.id = id;
        this.username = username;
    }

    /* new change 22-12-2021
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }*/

    public String getUsername() {
        return username;
    }

    public void setUsername (String userName) {
        this.username = userName;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public abstract JSONObject toJSON ();
}