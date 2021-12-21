package it.unipi.dii.entities;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class StandardUser extends User {
    /*private List<Food> eatenFoods;
    private List<Integer> quantity;
    private List<Timestamp> timestamp;*/

    private List<EatenFood> eatenFoods;

    public StandardUser(String Id/*, String UserName*/, String FullName, String Sex, String Password, int Age, String Country , List<EatenFood> eatenFoods) {
        super(Id/*, UserName*/, FullName, Password, Sex, Age, Country);
        this.eatenFoods = eatenFoods;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject user = new JSONObject();
        try {
            user.put("_id", this.getId());
            //user.put("username", this.getUserName());
            user.put("password", this.getPassoword());
            user.put("name", this.getFullName());
            user.put("sex", this.getSex());
            user.put("age", this.getAge());
            user.put("country", this.getCountry());
            user.put("userType", "standardUser");
            JSONArray eatenFoods = new JSONArray();

            /*JSONObject eatenFood = new JSONObject();
            eatenFood.put("eatenFoodID", "");
            eatenFood.put("foodID", "");
            eatenFood.put("quantity", "");
            eatenFood.put("timestamp", "");
            eatenFoods.put(eatenFood);*/
            user.put("eatenFoods", eatenFoods);


        }
        catch(JSONException ee){
            ee.printStackTrace();
        }
        return user;
    }

    public static StandardUser fromJSON(JSONObject userJ){
        String _id, username, password, fullName, sex, country;
        int age;
        StandardUser newUser = null;
        List<EatenFood> list = new ArrayList<>();

        //first i retrive the attributes values from the JSONObject
        try{
            _id = userJ.getString("_id"); //it will have the username value, not more numerical value
            //username = userJ.getString("username");
            password = userJ.getString("password");
            fullName = userJ.getString("name");
            sex = userJ.getString("sex");
            country = userJ.getString("country");
            age = userJ.getInt("age");

            JSONArray eatenfoods = userJ.getJSONArray("eatenFoods");
            for (int i = 0; i < eatenfoods.length(); i++){
                list.add((EatenFood) eatenfoods.get(i));
            }
            //then generate the new object StandardUser
            newUser = new StandardUser(_id/*,username*/, fullName, sex, password, age, country, list);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return  newUser;
    }
}
