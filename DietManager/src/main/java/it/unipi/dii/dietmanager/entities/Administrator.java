package it.unipi.dii.dietmanager.entities;

import org.json.JSONException;
import org.json.JSONObject;

public class Administrator extends User{
    public Administrator (String UserName, String FullName, String Sex, String Password, int Age, String Country ){
        super(UserName, FullName, Password, Sex, Age, Country);
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
            user.put(User.USERTYPE, User.USERTYPE_ADMINISTRATOR);// inserting usertype
        }
        catch(JSONException ee){
            ee.printStackTrace();
        }
        return user;
    }

    public static Administrator fromJSONObject(JSONObject jsonUser){
        String username, password, fullName, sex, country;
        int age;
        Administrator newAdministrator;
        try{
            username = jsonUser.getString(User.USERNAME);       // retrieving username
            password = jsonUser.getString(User.PASSWORD);       // retrieving password
            fullName = jsonUser.getString(User.FULLNAME);       // retrieving name
            sex = jsonUser.getString(User.SEX);                 // retrieving sex
            country = jsonUser.getString(User.COUNTRY);         // retrieving country
            age = jsonUser.getInt(User.AGE);                    // retrieving age

            newAdministrator = new Administrator(username, fullName, password, sex, age, country);
        }
        catch (JSONException e){
            e.printStackTrace();
            newAdministrator = null;
        }
        return  newAdministrator;
    }

}
