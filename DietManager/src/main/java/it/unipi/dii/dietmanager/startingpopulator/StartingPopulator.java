package it.unipi.dii.dietmanager.startingpopulator;

import it.unipi.dii.dietmanager.entities.*;
import it.unipi.dii.dietmanager.persistence.MongoDB;
import it.unipi.dii.dietmanager.persistence.Neo4j;
import it.unipi.dii.dietmanager.services.LogicManager;
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
    private final int FOLLOW_RANDOM = 10;
    private final int MAX_TYPE_OF_RELATIONSHIPS = 2; //current following or stopped
    private final int MAX_NUMBER_OF_EATEN_FOODS = 10;
    private final int MIN_NUMBER_OF_EATEN_FOODS = 2;
    private final int MAX_QUANTITY = 3000;
    MongoDB mongoDB = new MongoDB(MONGODB_PORT);
    Neo4j neo4j = new Neo4j();
    LogicManager logicManager = new LogicManager();

    public boolean insertObjects(File fileInput, String nodeType){
        JSONParser parser = new JSONParser();
        try{
            JSONArray jsonNodes = (JSONArray)parser.parse(new FileReader(fileInput));
            JSONObject jsonNode;

            for(Object node: jsonNodes){
                jsonNode = (JSONObject)node;

                if(nodeType.equals(Food.class.getName())){
                    return logicManager.addFood(Food.fromJSONObject(jsonNode));
                }

                if(nodeType.equals(Diet.class.getName())) {
                    Diet diet = Diet.fromJSONObject(jsonNode);
                    return logicManager.addDiet(diet);
                }

                if(jsonNode.getString(User.USERTYPE).equals(User.USERTYPE_NUTRITIONIST)) {
                    Nutritionist nutritionist = Nutritionist.fromJSONObject(jsonNode);
                    return logicManager.addUser(nutritionist);
                }

                StandardUser standardUser = StandardUser.fromJSONObject(jsonNode);
                return logicManager.addUser(standardUser);
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

    private int random(int max){
        Random random = new Random();
        return random.nextInt(max);
    }

    private int typeOfRelationships(){
        Random rand = new Random();
        return rand.nextInt(MAX_TYPE_OF_RELATIONSHIPS);
    }

    private int[] randomFoodsIndexes(int max, int numberOfEatenFood){
        int[] indexesTarget = new int[numberOfEatenFood];
        Random rand = new Random();

        for(int i = 0; i < numberOfEatenFood; i++){
            indexesTarget[i] = rand.nextInt(max);
        }
        return indexesTarget;
    }

    private void generationEatenFoodForSU(File fileFood){
        JSONParser parser = new JSONParser();
        Random rand = new Random();
        int numberOfEatenFoods = 0, quantity;
        int [] indexesTarget;
        String foodName;
        try {
            JSONArray jsonNodesFoods = (JSONArray) parser.parse(new FileReader(fileFood));
            JSONObject jsonNodeFood;

            //chiamata funzione che ritorna il numero di eatenfood da generare
            numberOfEatenFoods = new Random().nextInt(MAX_NUMBER_OF_EATEN_FOODS) + MIN_NUMBER_OF_EATEN_FOODS;
            indexesTarget = randomFoodsIndexes(jsonNodesFoods.length(), numberOfEatenFoods);
            for(int i=0; i < indexesTarget.length; i++){
                jsonNodeFood = (JSONObject) jsonNodesFoods.get(indexesTarget[i]);
                foodName = jsonNodeFood.getString(Food.NAME);
                quantity = random(MAX_QUANTITY);
                logicManager.addFoodToEatenFoods(foodName, quantity); //we are adding food to eatenFoodList local in Java
            }
            logicManager.addEatenFoodToMongo(); //we must insert them in MongoDB
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void generationFollowRelationshipsUsersDiets(File fileUser, File fileDiet, File fileFood){
        JSONParser parser = new JSONParser();
        int counterFollow = 0, indexDietTarget, typeOfRelationships;
        String idDietTarget;
        try{
            JSONArray jsonNodesUsers = (JSONArray)parser.parse(new FileReader(fileUser));
            JSONArray jsonNodesDiets = (JSONArray)parser.parse(new FileReader(fileDiet));
            JSONObject jsonNodeUser; JSONObject jsonNodeDiet;


            for(Object node: jsonNodesUsers){
                jsonNodeUser = (JSONObject)node;
                if(jsonNodeUser.get(User.USERTYPE).equals(User.USERTYPE_STANDARDUSER)){

                    if(createFollow(counterFollow)){ //generation follow relationships
                        logicManager.signIn(jsonNodeUser.getString(User.USERNAME), jsonNodeUser.getString(User.PASSWORD));

                        generationEatenFoodForSU(fileFood); //generation eatenFood for the current S.U

                        indexDietTarget = random(jsonNodesDiets.length());
                        jsonNodeDiet = (JSONObject) jsonNodesDiets.get(indexDietTarget);
                        idDietTarget = jsonNodeDiet.getString(Diet.ID);
                        typeOfRelationships = typeOfRelationships();

                        if(typeOfRelationships == 0){ //Follow
                            logicManager.followDiet(idDietTarget);
                        }
                        else if(typeOfRelationships == 1){
                            //i cannot insert manually stopped diet because i do not have manner to pass the stopped diet according to the implmementation of stopped diet
                            logicManager.followDiet(idDietTarget);
                            logicManager.stopDiet();
                        }
                        counterFollow++;
                    }
                }

            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

    }

    /*
    private void changeFollowRelationships(File fileUser){
        JSONParser parser = new JSONParser();
        boolean isFollowing;
        User userTMP
        try{
            JSONArray jsonNodesUsers = (JSONArray)parser.parse(new FileReader(fileUser));
            JSONObject jsonNodeUser; JSONObject jsonNodeDiet;
            for(Object node: jsonNodesUsers) {
                jsonNodeUser = (JSONObject) node;
                if (jsonNodeUser.get(User.USERTYPE).equals(User.USERTYPE_STANDARDUSER)) { //i consider only the SU
                    userTMP = mongoDB.lookUpUserByUsername(jsonNodeUser.getString(User.USERNAME)); //i retrive the instance of the corrisponding user
                    if( ((StandardUser)userTMP).getCurrentDiet() != null ){

                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }*/

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
