package it.unipi.dii.entities;

import org.json.JSONArray;
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
}
