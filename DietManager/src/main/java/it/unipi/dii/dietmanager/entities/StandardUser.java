package it.unipi.dii.dietmanager.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class StandardUser extends User {
    public static final String EATENFOODS = "eatenFoods";
    public static final String CURRENT_DIET = "currentDiet";

    private Diet currentDiet;
    private List<EatenFood> eatenFoods;

    public StandardUser(StandardUser standardUser){
        super(standardUser.getUsername(),
                standardUser.getFullName(),
                standardUser.getPassword(),
                standardUser.getSex(),
                standardUser.getAge(),
                standardUser.getCountry());
        this.eatenFoods = new ArrayList<>(standardUser.getEatenFoods());
        if(standardUser.getCurrentDiet() != null)
            this.currentDiet = new Diet(standardUser.getCurrentDiet());
        else
            this.currentDiet = null;
    }

    public StandardUser(String UserName, String FullName, String Sex, String Password, int Age, String Country) {
        super(UserName, FullName, Password, Sex, Age, Country);
        this.eatenFoods = null;
        this.currentDiet = null;
    }

    public StandardUser(String UserName, String FullName, String Sex, String Password, int Age, String Country, List<EatenFood> eatenFoods) { // !!!
        super(UserName, FullName, Password, Sex, Age, Country);
        this.currentDiet = null;
        this.eatenFoods = eatenFoods;
    }

    public StandardUser(String UserName, String FullName, String Sex, String Password, int Age, String Country , List<EatenFood> eatenFoods, Diet currentDiet) {
        super(UserName, FullName, Password, Sex, Age, Country);
        this.eatenFoods = eatenFoods;
        this.currentDiet = currentDiet;
    }

    public StandardUser(String UserName){
        super (UserName);
    }

    public List<EatenFood> getEatenFoods() {return eatenFoods; }
    public Diet getCurrentDiet() { return currentDiet; }

    public void setEatenFoods(List<EatenFood> eatenFoods) { this.eatenFoods = eatenFoods; }
    public void setCurrentDiet(Diet currentDiet) { this.currentDiet = currentDiet; }
    public void stopCurrentDiet(){
        this.currentDiet = null;
        this.eatenFoods = null;
    }

    private String computeNewEatenFoodID(){
        long higherID = -1, currentID;
        if(this.eatenFoods == null || this.eatenFoods.isEmpty())
            higherID = 1;
        else {
            for (EatenFood eatenFood : this.eatenFoods) {
                if (eatenFood.getId() == null)
                    continue;
                else {
                    currentID = Long.parseLong(eatenFood.getId());
                    if (higherID == -1 || currentID > higherID)
                        higherID = currentID;
                }
            }
            higherID++;
        }
        return EatenFood.generateEatenFoodFormatID(higherID);
    }

    public void addEatenFood( String foodName, int quantity ){
        EatenFood eatenFood = new EatenFood(
                computeNewEatenFoodID(),
                foodName,
                quantity,
                new Timestamp(System.currentTimeMillis()) );
        if(this.eatenFoods == null)
            this.eatenFoods = new ArrayList<>();
        this.eatenFoods.add(eatenFood);
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

            if(eatenFoods != null || !(eatenFoods.isEmpty())){
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
        Diet currentDiet = null;
        try{
            username = jsonUser.getString(User.USERNAME);       // retrieving username
            password = jsonUser.getString(User.PASSWORD);       // retrieving password
            fullName = jsonUser.getString(User.FULLNAME);       // retrieving name
            sex = jsonUser.getString(User.SEX);                 // retrieving sex
            country = jsonUser.getString(User.COUNTRY);         // retrieving country
            age = jsonUser.getInt(User.AGE);                    // retrieving age


            if(!jsonUser.isNull(StandardUser.EATENFOODS)) {
                JSONArray jsonEatenFoods = jsonUser.getJSONArray(StandardUser.EATENFOODS);
                for (int i = 0; i < jsonEatenFoods.length(); i++) {
                    eatenFoods.add(EatenFood.fromJSONObject(jsonEatenFoods.getJSONObject(i)));      // retrieve eatenFoods
                }
            }
            if(!jsonUser.isNull(StandardUser.CURRENT_DIET)) {
                jsonCurrentDietStringID = jsonUser.getString(StandardUser.CURRENT_DIET);            // retrieving current Diet
                currentDiet = new Diet(jsonCurrentDietStringID);
            }
            newUser = new StandardUser(username, fullName, sex, password, age, country, eatenFoods, currentDiet);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return  newUser;
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
