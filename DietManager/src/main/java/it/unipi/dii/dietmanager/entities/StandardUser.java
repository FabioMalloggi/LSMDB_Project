package it.unipi.dii.dietmanager.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StandardUser extends User {

    private List<EatenFood> eatenFoods;
    private Diet currentDiet;
    private Date currentDietStartDate;

    //this first constructor should be used during the registration when the SU does not have the list of EatenFood yet
    public StandardUser(String UserName, String FullName, String Sex, String Password, int Age, String Country) {
        super(UserName, FullName, Password, Sex, Age, Country);
        this.eatenFoods = null;
        this.currentDietStartDate = null;
        this.currentDiet = null;
    }

    public StandardUser(String UserName, String FullName, String Sex, String Password, int Age, String Country , List<EatenFood> eatenFoods) {
        super(UserName, FullName, Password, Sex, Age, Country);
        this.eatenFoods = eatenFoods;
        this.currentDietStartDate = null;
        this.currentDiet = null;
    }

    public StandardUser(String UserName, String FullName, String Sex, String Password, int Age, String Country , List<EatenFood> eatenFoods, Diet currentDiet, Date currentDietStartDate) {
        super(UserName, FullName, Password, Sex, Age, Country);
        this.eatenFoods = eatenFoods;
        this.currentDiet = currentDiet;
        this.currentDietStartDate = currentDietStartDate;
    }

    public StandardUser(String username){
        super(username);
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
        String username, password, fullName, sex, country;
        int age;
        StandardUser newUser = null;
        List<EatenFood> list = new ArrayList<>();

        //first i retrive the attributes values from the JSONObject
        try{
            username = userJ.getString("_id");
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
            newUser = new StandardUser(username, fullName, sex, password, age, country, list);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return  newUser;
    }
}
