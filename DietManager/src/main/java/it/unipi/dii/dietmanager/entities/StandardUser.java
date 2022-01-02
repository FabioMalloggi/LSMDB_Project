package it.unipi.dii.dietmanager.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StandardUser extends User {
    public static final String EATENFOODS = "eatenFoods";
    public static final String CURRENT_DIET = "currentDiet";

    private Diet currentDiet;
    private List<EatenFood> eatenFoods;

    public StandardUser(String UserName, String FullName, String Sex, String Password, int Age, String Country) {
        super(UserName, FullName, Password, Sex, Age, Country);
        this.eatenFoods = null;
        this.currentDiet = null;
    }

    public StandardUser(String UserName, String FullName, String Sex, String Password, int Age, String Country , List<EatenFood> eatenFoods, Diet currentDiet) {
        super(UserName, FullName, Password, Sex, Age, Country);
        this.eatenFoods = eatenFoods;
        this.currentDiet = currentDiet;
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
            user.put(User.USERTYPE, User.USERTYPE_STANDARDUSER);// inserting usertype

            if(eatenFoods != null){
                JSONArray jsonEatenFoods = new JSONArray();
                for(EatenFood eatenFood: this.getEatenFoods())
                    jsonEatenFoods.put(eatenFood.toJSONObject());
                user.put(StandardUser.EATENFOODS, eatenFoods);                 // inserting eatenFoods
            }
            if(currentDiet != null){
                user.put(StandardUser.CURRENT_DIET, currentDiet.getId());      // inserting currentDiet
            }
        }
        catch(JSONException ee){
            ee.printStackTrace();
        }
        return user;
    }

    public static StandardUser fromJSONObject(JSONObject jsonUser){
        String username, password, fullName, sex, country, jsonCurrentDietStringID;
        int age;
        StandardUser newUser = null;
        List<EatenFood> eatenFoods = new ArrayList<>();
        try{
            username = jsonUser.getString(User.USERNAME);       // retrieving username
            password = jsonUser.getString(User.PASSWORD);       // retrieving password
            fullName = jsonUser.getString(User.FULLNAME);       // retrieving name
            sex = jsonUser.getString(User.SEX);                 // retrieving sex
            country = jsonUser.getString(User.COUNTRY);         // retrieving country
            age = jsonUser.getInt(User.AGE);                    // retrieving age

            JSONArray jsonEatenFoods = jsonUser.getJSONArray(StandardUser.EATENFOODS);
            if(jsonEatenFoods != null)
                for (int i = 0; i < jsonEatenFoods.length(); i++){
                    eatenFoods.add(EatenFood.fromJSONObject(new JSONObject(jsonEatenFoods.get(i))));    // retrieve eatenFoods
                }
            jsonCurrentDietStringID = jsonUser.getString("StandardUser.CURRENT_DIET");    // retrieving current Diet
            Diet currentDiet = new Diet(jsonCurrentDietStringID);
            newUser = new StandardUser(username, fullName, sex, password, age, country, eatenFoods, currentDiet);
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

    public void stopCurrentDiet(){
        this.currentDiet = null;
    }

    @Override
    public String toString() {
        return "StandardUser{" +
                super.toString() +
                ", currentDiet=" + currentDiet +
                ", eatenFoods=" + eatenFoods +
                '}';
    }
}
