package it.unipi.dii.dietmanager.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Nutritionist extends User {

    public Nutritionist(String Id, String FullName, String Sex, String Password, int Age, String Country) {
        super(Id, FullName, Password, Sex, Age, Country);
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject user = new JSONObject();
        try {
            user.put(User.USERNAME, this.getUsername());        // inserting username
            user.put(User.PASSWORD, this.getPassword());        // inserting password
            user.put(User.FULLNAME, this.getFullName());        // inserting fullName
            user.put(User.SEX, this.getSex());                  // inserting sex
            user.put(User.AGE, this.getAge());                  // inserting age
            user.put(User.COUNTRY, this.getCountry());          // inserting country
            user.put(User.USERTYPE, User.USERTYPE_NUTRITIONIST);// inserting usertype
        }
        catch(JSONException ee){
            ee.printStackTrace();
        }
        return user;
    }

    public static Nutritionist fromJSONObject(JSONObject jsonUser){
        String username, password, fullName, sex, country;
        int age;
        Nutritionist newNutritionist = null;
        try{
            username = jsonUser.getString(User.USERNAME);       // retrieving username
            password = jsonUser.getString(User.PASSWORD);       // retrieving password
            fullName = jsonUser.getString(User.FULLNAME);       // retrieving name
            sex = jsonUser.getString(User.SEX);                 // retrieving sex
            country = jsonUser.getString(User.COUNTRY);         // retrieving country
            age = jsonUser.getInt(User.AGE);                    // retrieving age

            newNutritionist = new Nutritionist(username, fullName, sex, password, age, country);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return  newNutritionist;
    }

    @Override
    public String toString() {
        return "Nutritionist{" +
                super.toString() +
                '}';
    }
}

