package it.unipi.dii.dietmanager.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Nutritionist extends User {
    private List<Diet> diets;
    public Nutritionist(String Id, String FullName, String Sex, String Password, int Age, String Country, List<Diet> Diets) {
        super(Id, FullName, Password, Sex, Age, Country);
        this.diets = Diets;
    }

    public Nutritionist (String username){
        super(username);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject user = new JSONObject();
        try {
            //I generate a new user

            user.put("_id", this.getUsername());
            user.put("password", this.getPassword());
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
        String username, password, fullName, sex, country;
        int age;
        Nutritionist newNut = null;

        //first i retrive the attributes values from the JSONObject
        try{
            username = userJ.getString("_id");
            //username = userJ.getString("username");
            password = userJ.getString("password");
            fullName = userJ.getString("name");
            sex = userJ.getString("sex");
            country = userJ.getString("country");
            age = userJ.getInt("age");

            //then generate the new object Nutritionist
            newNut = new Nutritionist(username, fullName, sex, password, age, country, null);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return  newNut;
    }
}

