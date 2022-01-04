package it.unipi.dii.dietmanager.startingpopulator;

import it.unipi.dii.dietmanager.entities.Diet;
import it.unipi.dii.dietmanager.entities.Nutritionist;
import it.unipi.dii.dietmanager.entities.StandardUser;
import it.unipi.dii.dietmanager.persistence.MongoDB;
import it.unipi.dii.dietmanager.persistence.Neo4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;

public class StartingPopulator {
    File fileJSONDiets = new File("./data/json/diet.json");
    File fileJSONStandardUsers = new File("./data/json/users.json");
    File fileJSONNutritionist = new File("");
    private final int MONGODB_PORT = 2222;
    MongoDB mongoDB = new MongoDB(MONGODB_PORT);
    Neo4j neo4j = new Neo4j();

    public boolean insertNodes(File fileInput, String nodeType){
        JSONParser parser = new JSONParser();
        try{
            JSONArray jsonNodes = (JSONArray)parser.parse(new FileReader(fileInput));
            JSONObject jsonNode;

            for(Object node: jsonNodes){
                jsonNode = (JSONObject)node;

                if(nodeType.equals(Diet.class.getName()))
                    return neo4j.addDiet(Diet.fromJSONObject(jsonNode));

                if(nodeType.equals(Nutritionist.class.getName()))
                    return neo4j.addUser(Nutritionist.fromJSONObject(jsonNode));

                return neo4j.addUser(StandardUser.fromJSONObject(jsonNode));
            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
        return true;
    }

    public void populateDBs(){
        insertNodes(fileJSONDiets, Diet.class.getName());
        insertNodes(fileJSONStandardUsers, StandardUser.class.getName());
        insertNodes(fileJSONNutritionist, Nutritionist.class.getName());
    }

    public static void main(String... args){
        StartingPopulator startingPopulator = new StartingPopulator();
        startingPopulator.populateDBs();
    }
}
