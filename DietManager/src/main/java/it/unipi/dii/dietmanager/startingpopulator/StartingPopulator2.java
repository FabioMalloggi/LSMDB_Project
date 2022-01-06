package it.unipi.dii.dietmanager.startingpopulator;

import it.unipi.dii.dietmanager.entities.*;
import it.unipi.dii.dietmanager.persistence.MongoDB;
import it.unipi.dii.dietmanager.persistence.Neo4j;
import it.unipi.dii.dietmanager.services.LogicManager;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class StartingPopulator2 {

    File fileJSONDiets = new File("./data/json/diet.json");
    File fileJSONUsers = new File("./data/json/users.json");
    File fileJSONFoods = new File("./data/json/foods.json");
    private final int MONGODB_PORT = 27017;
    private final int FOLLOW_RANDOM = 10;
    private final int MAX_TYPE_OF_RELATIONSHIPS = 2; // 0 = current; 1 = following; 2 = stopped
    private final int MAX_NUMBER_OF_EATEN_FOODS = 10;
    private final int MIN_NUMBER_OF_EATEN_FOODS = 2;
    private final int MAX_QUANTITY = 3000;

    LogicManager logicManager = new LogicManager();

    private List<User> users = new ArrayList<>();
    private List<Food> foods = new ArrayList<>();
    private List<Diet> diets = new ArrayList<>();

    private List<JSONObject> jsonUsers = new ArrayList<>();
    private List<JSONObject> jsonFoods = new ArrayList<>();
    private List<JSONObject> jsonDiets = new ArrayList<>();

    private Random random = new Random();

    public void readJSONUsersFile() {
        try (BufferedReader readerUsers = new BufferedReader(new FileReader(fileJSONUsers))) {

            String line = readerUsers.readLine();
            while(line != null){
                JSONObject jsonUser = new JSONObject(line);
                jsonUsers.add(jsonUser);

                if(jsonUser.getString(User.USERTYPE).equals(User.USERTYPE_NUTRITIONIST))
                    users.add(Nutritionist.fromJSONObject(jsonUser));
                else if(jsonUser.getString(User.USERTYPE).equals(User.USERTYPE_ADMINISTRATOR))
                    users.add(Administrator.fromJSONObject(jsonUser));
                else if(jsonUser.getString(User.USERTYPE).equals(User.USERTYPE_STANDARDUSER))
                    users.add(StandardUser.fromJSONObject(jsonUser));
                else
                    System.out.println("found users with no correct userType");
                line = readerUsers.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void readJSONFoodsFile() {
        try (BufferedReader readerFoods = new BufferedReader(new FileReader(fileJSONFoods))) {

            String line = readerFoods.readLine();
            while(line != null){
                JSONObject jsonFood = new JSONObject(line);
                jsonFoods.add(jsonFood);
                foods.add(Food.fromJSONObject(jsonFood));
                line = readerFoods.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void readJSONDietsFile() {
        try (BufferedReader readerDiets = new BufferedReader(new FileReader(fileJSONDiets))) {

            String line = readerDiets.readLine();
            while(line != null){
                JSONObject jsonDiet = new JSONObject(line);
                jsonDiets.add(jsonDiet);
                diets.add(Diet.fromJSONObject(jsonDiet));
                line = readerDiets.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void readJSONFiles(){
        System.out.println(">>> Starting reading json files");
        readJSONFoodsFile();
        System.out.println("foods read: " + jsonFoods.size());
        readJSONDietsFile();
        System.out.println("diets read: " + jsonDiets.size());
        readJSONUsersFile();
        System.out.println("users read: " + jsonUsers.size());
        System.out.println(">>> Finished reading json files");
    }

    public int insertFoods(){
        int count = 0;
        for(Food food: foods){
            logicManager.addFood(food);
            count++;
        }
        return count;
    }

    public int insertDiets(){
        int count = 0;
        for(Diet diet: diets){
            logicManager.addDiet(diet);
            count++;
        }
        return count;
    }

    public int insertUsers(){
        int count = 0;
        for(User user: users){
            logicManager.addUser(user);
            count++;
        }
        return count;
    }

    public void insertSimpleObjects(){
        int countFoods = 0, countDiets = 0, countUsers = 0;
        System.out.println(">>> Starting inserting Foods");
        countFoods = insertFoods();
        System.out.println(">>> Finished inserting foods");
        System.out.println("foods inserted: " + countFoods);

        System.out.println(">>> Starting inserting Diets");
        countDiets = insertDiets();
        System.out.println(">>> Finished inserting Diets");
        System.out.println("diets inserted: " + countDiets);

        System.out.println(">>> Starting inserting Users");
        countUsers = insertUsers();
        System.out.println(">>> Finished inserting Users");
        System.out.println("diets inserted: " + countUsers);
    }



    private boolean createFollow(int counter){
        return (counter % FOLLOW_RANDOM == 0);
    }

    private int typeOfRelationships(){
        return random.nextInt(MAX_TYPE_OF_RELATIONSHIPS);
    }

    private void signIn(User user){



    }

    private void generationEatenFoodForSingleSU(){
        int quantity;
        String foodName;
        int numberOfEatenFoods = new Random().nextInt(MAX_NUMBER_OF_EATEN_FOODS);// + MIN_NUMBER_OF_EATEN_FOODS

        for (int i = 0; i < numberOfEatenFoods; i++) {
            foodName = foods.get(random.nextInt(foods.size())).getName();
            quantity = random.nextInt(MAX_QUANTITY);
            logicManager.addFoodToEatenFoods(foodName, quantity); //we are adding food to eatenFoodList local in Java
        }
        logicManager.addEatenFoodToMongo(); //we must insert them in MongoDB
    }

    public void processStandardUsers(){
        int count=0;
        for(User user: users) {
            // only StandardUser can add eatenFoods
            if(!(user instanceof StandardUser))
                continue;

            logicManager.signIn(user.getUsername(), user.getPassword());

            //generation eatenFood for the current S.U
            generationEatenFoodForSingleSU();

            generationFollowRelationshipsSingleUsertoDiet();

            count++;
        }
        System.out.println("users processed: "+count);

    }

    public void generationFollowRelationshipsSingleUsertoDiet(){
        int counterFollow = 0, indexDietTarget, typeOfRelationships;
        String idDietTarget;

        Diet dietTarget = diets.get(random.nextInt(diets.size()));
        idDietTarget = dietTarget.getId();
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

    public void resetDBs(){
        new Neo4j().dropAll();
        new MongoDB(MONGODB_PORT).dropDietManagerDatabase();
    }

    public static void main(String... args){
        StartingPopulator2 startingPopulator2 = new StartingPopulator2();
        startingPopulator2.resetDBs();
        startingPopulator2.insertSimpleObjects();
        startingPopulator2.processStandardUsers();
    }

}



