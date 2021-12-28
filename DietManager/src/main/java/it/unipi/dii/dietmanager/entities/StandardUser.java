package it.unipi.dii.dietmanager.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StandardUser extends User {

    private List<EatenFood> eatenFoods;
    private Diet currentDiet;
    // private Date currentDietStartDate; //we do not need the dateofStartingDiet to consider only the eatenfood since the diet is started if we mantain only the eatenFoods for the currentDiet

    //this first constructor should be used during the registration when the SU does not have the list of EatenFood yet
    public StandardUser(String UserName, String FullName, String Sex, String Password, int Age, String Country) {
        super(UserName, FullName, Password, Sex, Age, Country);
        this.eatenFoods = null;
        //this.currentDietStartDate = null;
        this.currentDiet = null;
    }

    public StandardUser(String UserName, String FullName, String Sex, String Password, int Age, String Country , List<EatenFood> eatenFoods) {
        super(UserName, FullName, Password, Sex, Age, Country);
        this.eatenFoods = eatenFoods;
        //this.currentDietStartDate = null;
        this.currentDiet = null;
    }

    public StandardUser(String UserName, String FullName, String Sex, String Password, int Age, String Country , List<EatenFood> eatenFoods, Diet currentDiet /*, Date currentDietStartDate*/) {
        super(UserName, FullName, Password, Sex, Age, Country);
        this.eatenFoods = eatenFoods;
        this.currentDiet = currentDiet;
        //this.currentDietStartDate = currentDietStartDate;
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

    public static StandardUser fromJSON(JSONObject jsonUser){
        String username, password, fullName, sex, country;
        int age;
        StandardUser newUser = null;
        List<EatenFood> eatenFoods = new ArrayList<>();

        //first i retrive the attributes values from the JSONObject
        try{
            username = jsonUser.getString("_id");
            password = jsonUser.getString("password");
            fullName = jsonUser.getString("name");
            sex = jsonUser.getString("sex");
            country = jsonUser.getString("country");
            age = jsonUser.getInt("age");

            JSONArray jsonEatenFoods = jsonUser.getJSONArray("eatenFoods");
            for (int i = 0; i < jsonEatenFoods.length(); i++){
                eatenFoods.add((EatenFood) jsonEatenFoods.get(i));
            }
            //then generate the new object StandardUser
            newUser = new StandardUser(username, fullName, sex, password, age, country, eatenFoods);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return  newUser;
    }

    public List<EatenFood> getEatenFoods() {
        return eatenFoods;
    }
    public void setEatenFoods(List<EatenFood> eatenFoods) {
        this.eatenFoods = eatenFoods;
    }

    public Diet getCurrentDiet() {
        return currentDiet;
    }
    public void setCurrentDiet(Diet currentDiet) {
        this.currentDiet = currentDiet;
    }

    /* we do not need the dateofStartingDiet to consider only the eatenfood since the diet is started if we mantain only the eatenFoods for the currentDiet
    public Date getCurrentDietStartDate() {
        return currentDietStartDate;
    }

    public void setCurrentDietStartDate(Date currentDietStartDate) {
        this.currentDietStartDate = currentDietStartDate;
    }*/
}
