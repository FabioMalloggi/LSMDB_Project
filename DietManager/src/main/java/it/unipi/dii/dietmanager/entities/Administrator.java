package it.unipi.dii.dietmanager.entities;

import org.json.JSONException;
import org.json.JSONObject;

public class Administrator extends User{
    public Administrator (String UserName, String FullName, String Password,String Sex, int Age, String Country ){
        super(UserName, FullName, Password, Sex, Age, Country);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject user = new JSONObject();
        try {
            user.put("_id", this.getUsername()); // new change
            user.put("password", this.getPassword());
            user.put("name", this.getFullName());
            user.put("sex", this.getSex());
            user.put("age", this.getAge());
            user.put("country", this.getCountry());
            user.put("userType", "administrator");
        }
        catch(JSONException ee){
            ee.printStackTrace();
        }
        return user;
    }

    public static Administrator fromJSON(JSONObject jsonUser){
        String username, password, fullName, sex, country;
        int age;
        Administrator newAdministrator = null;

        //first i retrive the attributes values from the JSONObject
        try{
            username = jsonUser.getString("_id");
            password = jsonUser.getString("password");
            fullName = jsonUser.getString("name");
            sex = jsonUser.getString("sex");
            country = jsonUser.getString("country");
            age = jsonUser.getInt("age");

            //then generate the new object Administrator
            newAdministrator = new Administrator(username, fullName, password, sex, age, country);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return  newAdministrator;
    }

}
