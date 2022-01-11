package it.unipi.dii.dietmanager.entities;

import org.json.JSONObject;

public abstract class  User {
    public static final String USERNAME = "_id";
    public static final String PASSWORD = "password";
    public static final String FULLNAME = "fullName";
    public static final String SEX = "sex";
    public static final String AGE = "age";
    public static final String COUNTRY = "country";
    public static final String USERTYPE = "userType";

    public static final String USERTYPE_STANDARDUSER = "standardUser";
    public static final String USERTYPE_NUTRITIONIST = "nutritionist";
    public static final String USERTYPE_ADMINISTRATOR = "administrator";

    private String username;
    private String fullName;
    private String password;
    private String sex;
    private int age;
    private String country;

    public User(String username, String fullName, String password,String sex, int age, String country ){
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.sex = sex;
        this.age = age;
        this.country = country;
    }

    public User(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public String getFullName() {
        return fullName;
    }
    public String getPassword() {
        return password;
    }
    public String getSex() {
        return sex;
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


    public abstract JSONObject toJSONObject();

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", country='" + country + '\'' +
                '}';
    }
}