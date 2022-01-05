package it.unipi.dii.dietmanager.startingpopulator;

import it.unipi.dii.dietmanager.entities.*;
import it.unipi.dii.dietmanager.persistence.MongoDB;
import it.unipi.dii.dietmanager.persistence.Neo4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.Random;

public class StartingPopulator {
    File fileJSONDiets = new File("./data/json/diet.json");
    File fileJSONUsers = new File("./data/json/users.json");
    File fileJSONFoods = new File("./data/json/foods.json");
    private final int MONGODB_PORT = 2222;
    private final int FOLLOW_RANDOM = 24;
    private final int MAX_TYPE_OFRELATIONSHIPS= 3;
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

    private boolean createFollow(int counter){
        return (counter % FOLLOW_RANDOM == 0);
    }

    private int randomDiet(int max){
        Random random = new Random();
        return random.nextInt(max);
    }

    private int typeOfRelationships(){
        Random rand = new Random();
        return rand.nextInt(MAX_TYPE_OFRELATIONSHIPS);
    }

    private void generationRelationshipsUsersDiet(File fileUser, File fileDiet){
        JSONParser parser = new JSONParser();
        int counterFollow = 0, indexDietTarget, typeOfRelationships;
        String idDietTarget,usernameCur;
        StandardUser standardUserTmp;
        try{
            JSONArray jsonNodesUsers = (JSONArray)parser.parse(new FileReader(fileUser));
            JSONArray jsonNodesDiets = (JSONArray)parser.parse(new FileReader(fileDiet));
            JSONObject jsonNodeUser; JSONObject jsonNodeDiet;


            for(Object node: jsonNodesUsers){
                jsonNodeUser = (JSONObject)node;
                if(jsonNodeUser.get(User.USERTYPE).equals(User.USERTYPE_STANDARDUSER)){
                    if(createFollow(counterFollow)){
                        indexDietTarget = randomDiet(jsonNodesDiets.length());
                        jsonNodeDiet = (JSONObject) jsonNodesDiets.get(indexDietTarget);
                        usernameCur = jsonNodeUser.getString(User.USERNAME);
                        idDietTarget = jsonNodeDiet.getString(Diet.ID);
                        typeOfRelationships = typeOfRelationships();

                        standardUserTmp = new StandardUser(usernameCur);
                        if(typeOfRelationships == 0){ //Follow
                            neo4j.followDiet(standardUserTmp,idDietTarget);
                            mongoDB.followDiet(standardUserTmp, idDietTarget);
                        }/*
                        else if(typeOfRelationships == 1){ //succeeded
                            //i cannot insert manually stopped diet because i do not have manner to pass the stopped diet according to the implmementation of stopped diet
                        }*/
                    }
                    counterFollow++;
                }

            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

    }

    /**
     * fare un metodo che scorre tutto l'array degli user json e che per ognuno fa laricerca sulla currentDiet:
     * se esiste decidere tra letre opzioni: tenerla follow,completed suceeded and not completed
     * se non esiste lasciarlo cosi come è
     * */

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
