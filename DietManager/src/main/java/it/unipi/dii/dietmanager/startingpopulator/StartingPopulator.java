package it.unipi.dii.dietmanager.startingpopulator;

import it.unipi.dii.dietmanager.entities.*;
import it.unipi.dii.dietmanager.persistence.MongoDB;
import it.unipi.dii.dietmanager.persistence.Neo4j;
import it.unipi.dii.dietmanager.services.LogicManager;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StartingPopulator {
    File fileJSONDiets = new File("./data/json/diet.json");
    File fileJSONUsers = new File("./data/json/users.json");
    File fileJSONFoods = new File("./data/json/foods.json");
    private final int MONGODB_PORT = 27017;
    private final int FOLLOW_RANDOM = 10;
    private final int MAX_TYPE_OF_RELATIONSHIPS = 2; //current following or stopped
    private final int MAX_NUMBER_OF_EATEN_FOODS = 10;
    private final int MIN_NUMBER_OF_EATEN_FOODS = 2;
    private final int MAX_QUANTITY = 3000;

    private final int MAX_STANDARD_USER = 50000;
    private int countStandardUser = 0;
    LogicManager logicManager = new LogicManager();
    Neo4j neo4j = new Neo4j();

    public boolean insertObjects(File fileInput, String nodeType){
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileInput));

            JSONObject jsonNode;
            String line = bufferedReader.readLine();
            int count =0, counter = 0;
            while(line != null){
                jsonNode = new JSONObject(line);

                if(nodeType.equals(Food.class.getName())){
                    logicManager.addFood(Food.fromJSONObject(jsonNode));
                    System.out.println(count++);
                }

                else if(nodeType.equals(Diet.class.getName())) {
                    Diet diet = Diet.fromJSONObject(jsonNode);
                    logicManager.addDiet(diet);
                }

                else if(jsonNode.getString(User.USERTYPE).equals(User.USERTYPE_NUTRITIONIST)) {
                    Nutritionist nutritionist = Nutritionist.fromJSONObject(jsonNode);
                    logicManager.addUser(nutritionist);
                }

                else{
                    if(countStandardUser <= MAX_STANDARD_USER) {
                        StandardUser standardUser = StandardUser.fromJSONObject(jsonNode);
                        logicManager.addUser(standardUser);
                        countStandardUser++;
                    }
                }
                line = bufferedReader.readLine();
                System.out.println(counter++);
            }
            bufferedReader.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
        return false;
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
        int numberOfEatenFoods = 0, quantity;
        int [] indexesTarget;
        String foodName;
        try {
            //JSONArray jsonNodesFoods = (JSONArray) parser.parse(new FileReader(fileFood));
            List<JSONObject> jsonNodesFoods = getJSONList(fileFood);
            JSONObject jsonNodeFood;

            numberOfEatenFoods = new Random().nextInt(MAX_NUMBER_OF_EATEN_FOODS) + MIN_NUMBER_OF_EATEN_FOODS;
            indexesTarget = randomFoodsIndexes(jsonNodesFoods.size(), numberOfEatenFoods);
            for(int i=0; i < indexesTarget.length; i++){
                jsonNodeFood = jsonNodesFoods.get(indexesTarget[i]);
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

    private List<JSONObject> getJSONList(File fileInput){
        List<JSONObject> jsonList = new ArrayList<>();
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileInput));

            JSONObject element;
            String line = bufferedReader.readLine();
            while(line != null){
                element = new JSONObject(line);
                jsonList.add(element);
                line = bufferedReader.readLine();
            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
        return jsonList;
    }

    public void generationFollowRelationshipsUsersDiets(){
        int counterFollow = 0, indexDietTarget, typeOfRelationships;
        String idDietTarget;
        try{
            JSONObject jsonNodeUser; JSONObject jsonNodeDiet;
            List<JSONObject> jsonNodesUsers = getJSONList(fileJSONUsers);
            List<JSONObject> jsonNodesDiets = getJSONList(fileJSONDiets);

            for(JSONObject jsonNodesUser: jsonNodesUsers){
                if(jsonNodesUser.get(User.USERTYPE).equals(User.USERTYPE_STANDARDUSER)){

                    if(createFollow(counterFollow)){ //generation follow relationships
                        logicManager.signIn(jsonNodesUser.getString(User.USERNAME), jsonNodesUser.getString(User.PASSWORD));

                        generationEatenFoodForSU(fileJSONFoods); //generation eatenFood for the current S.U

                        indexDietTarget = random(jsonNodesDiets.size());
                        jsonNodeDiet = jsonNodesDiets.get(indexDietTarget);
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

    public void populateDBs(){
        resetDBs();
        insertObjects(fileJSONUsers, User.class.getName());
        //insertObjects(fileJSONDiets, Diet.class.getName());
        //insertObjects(fileJSONFoods, Food.class.getName());
    }

    public void resetDBs(){
        new Neo4j().dropAll();
        new MongoDB(MONGODB_PORT).dropDietManagerDatabase();
    }

    public static void main(String... args){
        StartingPopulator startingPopulator = new StartingPopulator();
        startingPopulator.populateDBs();
        //startingPopulator.generationFollowRelationshipsUsersDiets();
    }
}
