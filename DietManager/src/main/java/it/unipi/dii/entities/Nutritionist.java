package it.unipi.dii.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Nutritionist extends User {
    public Nutritionist(String Id, String UserName, String FullName, String Sex, String Password, int Age, String Country) {
        super(Id, UserName, FullName, Password, Sex, Age, Country);
    }

    @Override
    public JSONObject toJson() {
        JSONObject user = new JSONObject();
        try {
            //I generate a new user

            user.put("_id", this.getId());
            user.put("username", this.getUserName());
            user.put("password", this.getPassoword());
            user.put("name", this.getFullName());
            user.put("sex", this.getSex());
            user.put("age", this.getAge());
            user.put("country", this.getCountry());
            user.put("userType", "nutritionist");
        }
        catch(JSONException ee){
            ee.printStackTrace();
        }
        return user;
    }

    public static Nutritionist fromJSON(JSONObject userJ){
        String _id, username, password, fullName, sex, country;
        int age;
        Nutritionist newNut = null;

        //first i retrive the attributes values from the JSONObject
        try{
            _id = userJ.getString("_id");
            username = userJ.getString("username");
            password = userJ.getString("password");
            fullName = userJ.getString("name");
            sex = userJ.getString("sex");
            country = userJ.getString("country");
            age = userJ.getInt("age");

            //then generate the new object Nutritionist
            newNut = new Nutritionist(_id,username, fullName, sex, password, age, country);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return  newNut;
    }
}

