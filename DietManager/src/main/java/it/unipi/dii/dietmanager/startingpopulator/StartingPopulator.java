package it.unipi.dii.dietmanager.startingpopulator;

import it.unipi.dii.dietmanager.entities.*;
import it.unipi.dii.dietmanager.persistence.MongoDB;
import it.unipi.dii.dietmanager.persistence.Neo4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;

public class StartingPopulator {
    File fileJSONDiets = new File("./data/json/diet.json");
    File fileJSONUsers = new File("./data/json/users.json");
    File fileJSONFoods = new File("./data/json/foods.json");
    private final int MONGODB_PORT = 2222;
    MongoDB mongoDB = new MongoDB(MONGODB_PORT);
    Neo4j neo4j = new Neo4j();

    public boolean insertObjects(File fileInput, String nodeType){
        JSONParser parser = new JSONParser();
        try{
            JSONArray jsonNodes = (JSONArray)parser.parse(new FileReader(fileInput));
            JSONObject jsonNode;

            for(Object node: jsonNodes){
                jsonNode = (JSONObject)node;

                if(nodeType.equals(Food.class.getName())){
                    return mongoDB.addFood(Food.fromJSONObject(jsonNode));
                }

                if(nodeType.equals(Diet.class.getName())) {
                    Diet diet = Diet.fromJSONObject(jsonNode);
                    return neo4j.addDiet(diet) && mongoDB.addDiet(diet);
                }

                if(jsonNode.getString(User.USERTYPE).equals(User.USERTYPE_NUTRITIONIST)) {
                    Nutritionist nutritionist = Nutritionist.fromJSONObject(jsonNode);
                    return neo4j.addUser(nutritionist) && mongoDB.addUser(nutritionist);
                }

                StandardUser standardUser = StandardUser.fromJSONObject(jsonNode);
                return neo4j.addUser(standardUser) && mongoDB.addUser(standardUser);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
        return true;
    }

    public void populateDBs(){
        insertObjects(fileJSONDiets, Diet.class.getName());
        insertObjects(fileJSONFoods, Food.class.getName());
        insertObjects(fileJSONUsers, User.class.getName());
    }

    public static void main(String... args){
        StartingPopulator startingPopulator = new StartingPopulator();
        startingPopulator.populateDBs();
    }
}
