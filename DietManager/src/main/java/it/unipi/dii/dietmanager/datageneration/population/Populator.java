package it.unipi.dii.dietmanager.datageneration.population;

import it.unipi.dii.dietmanager.entities.*;
import it.unipi.dii.dietmanager.persistence.MongoDBManager;
import it.unipi.dii.dietmanager.persistence.Neo4jManager;
import it.unipi.dii.dietmanager.services.LogicManager;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Populator {

    File fileJSONDiets = new File("./data/json/diet.json");
    File fileJSONUsers = new File("./data/json/users.json");
    File fileJSONFoods = new File("./data/json/foods.json");
    private final int MONGODB_PORT = 27017;

    private final int MAX_NUMBER_OF_EATEN_FOODS = 10;
    private final int MAX_QUANTITY = 3000;
    //private final int USER_SAMPLING_PERCENTAGE = 10;
    private int MAX_STANDARD_USERS= 1000000;
    private int MAX_STANDARD_USERS_RELATIONSHIPS = 1000000;

    private final String[] followRelationships = {"current", "stopped"};

    LogicManager logicManager = new LogicManager( false );

    private int numStandardUser=0, numNutritionist=0, numAdministrator=0;
    private int currentPercentage=0, oldPercentage=0;

    private List<User> users = new ArrayList<>();
    private List<Food> foods = new ArrayList<>();
    private List<Diet> diets = new ArrayList<>();

    private List<JSONObject> jsonUsers = new ArrayList<>();
    private List<JSONObject> jsonFoods = new ArrayList<>();
    private List<JSONObject> jsonDiets = new ArrayList<>();

    private Random random = new Random();

    private void setMAX_STANDARD_USERS(int max){ MAX_STANDARD_USERS = max;}
    private void setMAX_STANDARD_USERS_RELATIONSHIPS(int max){ MAX_STANDARD_USERS_RELATIONSHIPS = max;}

    public void readJSONUsersFile() {
        try (BufferedReader readerUsers = new BufferedReader(new FileReader(fileJSONUsers))) {

            String line = readerUsers.readLine();
            while(line != null){
                JSONObject jsonUser = new JSONObject(line);
                jsonUsers.add(jsonUser);

                if(jsonUser.getString(User.USERTYPE).equals(User.USERTYPE_NUTRITIONIST)) {
                    users.add(Nutritionist.fromJSONObject(jsonUser));
                    numNutritionist++;
                }
                else if(jsonUser.getString(User.USERTYPE).equals(User.USERTYPE_ADMINISTRATOR)) {
                    users.add(Administrator.fromJSONObject(jsonUser));
                    numAdministrator++;
                }
                else if(jsonUser.getString(User.USERTYPE).equals(User.USERTYPE_STANDARDUSER)) {
                    users.add(StandardUser.fromJSONObject(jsonUser));
                    numStandardUser++;
                }
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

    public void readPopulationFromJSONFiles(){
        System.out.println(">>> Starting reading json files");
        readJSONFoodsFile();
        System.out.println("foods read: " + jsonFoods.size());
        readJSONDietsFile();
        System.out.println("diets read: " + jsonDiets.size());
        readJSONUsersFile();
        System.out.println("users read: " + jsonUsers.size());
        System.out.println(">>> Finished reading json files");
    }

    public void printPercentageProgress(int currentElementCount, int totalElementsCount, String nameOfProgress){
        currentPercentage = (int) Math.floor(currentElementCount*100/totalElementsCount);
        if(currentPercentage != oldPercentage){
            System.out.println(nameOfProgress +" progress: "+currentPercentage+" %");
            oldPercentage = currentPercentage;
        }
    }

    public int insertFoods(){
        currentPercentage=0; oldPercentage=-1;
        int count = 0;
        for(Food food: foods){
            logicManager.addFood(food);
            count++;
            printPercentageProgress(count, foods.size(), "foods");
        }
        return count;
    }

    public int insertDiets(){
        currentPercentage=0; oldPercentage=-1;
        int count = 0;
        for(Diet diet: diets){
            logicManager.addDiet(diet);
            count++;
            printPercentageProgress(count, diets.size(), "diets");
        }
        return count;
    }

    public int insertUsers(){
        currentPercentage=0; oldPercentage=-1;
        int count = 0, standardUserCount = 0;
        for(User user: users){
            if(user instanceof StandardUser)
                standardUserCount++;
            if(!logicManager.addUser(user))
                System.exit(1);
            count++;
            //System.out.println(count);
            printPercentageProgress(count, users.size(), "users");
        }
        System.out.println("standardUsers inserted: "+ standardUserCount);
        System.out.println("other users inserted: "+ (count-standardUserCount));
        return count;
    }


    public int insertUsersWithLimitedStandardUsers(){
        currentPercentage=0; oldPercentage=-1;
        int count = 0, standardUserCount = 0;
        int numUserToInsert = (numAdministrator + numNutritionist
                                +(numStandardUser < MAX_STANDARD_USERS ? numStandardUser : MAX_STANDARD_USERS));
        for(User user: users){
            if( !(user instanceof StandardUser) || standardUserCount <= MAX_STANDARD_USERS ){
                if(user instanceof StandardUser)
                    standardUserCount++;
                if(!logicManager.addUser(user))
                    System.exit(1);
                count++;
                printPercentageProgress(count, numUserToInsert, "users");
            }
        }
        System.out.println("standardUsers inserted: "+ standardUserCount);
        System.out.println("other users inserted: "+ (count-standardUserCount));
        return count;
    }


    public void populateDBs(boolean populateFoods, boolean populateDiets, boolean populateUsers){
        int countFoods = 0, countDiets = 0, countUsers = 0;
        if(populateFoods){
            System.out.println(">>> Starting inserting Foods");
            countFoods = insertFoods();
            System.out.println(">>> Finished inserting foods");
            System.out.println("foods inserted: " + countFoods);
        }
        if(populateDiets){
            System.out.println(">>> Starting inserting Diets");
            countDiets = insertDiets();
            System.out.println(">>> Finished inserting Diets");
            System.out.println("diets inserted: " + countDiets);
        }
        if(populateUsers){
            System.out.println(">>> Starting inserting Users");
            countUsers = insertUsersWithLimitedStandardUsers();
            System.out.println(">>> Finished inserting Users");
            System.out.println("users inserted: " + countUsers);
        }
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
        currentPercentage=0; oldPercentage=-1;
        if(MAX_STANDARD_USERS_RELATIONSHIPS > MAX_STANDARD_USERS)
            MAX_STANDARD_USERS_RELATIONSHIPS = MAX_STANDARD_USERS;
        int standardUserToProcessForRelationships =
                numStandardUser < MAX_STANDARD_USERS_RELATIONSHIPS ? numStandardUser : MAX_STANDARD_USERS_RELATIONSHIPS;
        int standardUsersCount = 0;
        int standardUsersWithoutRelationship = 0;
        int standardUsersWithRelationship = 0;
        int currentDietUsersCount = 0;
        int stoppedDietUsersCount = 0;
        diets.clear();
        diets = logicManager.lookUpDietByName("");

        for(User user: users) {
            // only StandardUser can add eatenFoods
            if(user instanceof StandardUser){

                if(standardUsersCount < MAX_STANDARD_USERS_RELATIONSHIPS){

                    logicManager.signIn(user.getUsername(), user.getPassword());

                    Diet dietTarget = diets.get(random.nextInt(diets.size()));

                    logicManager.followDiet(dietTarget.getId());
                    //generation eatenFood for the current S.U
                    generationEatenFoodForSingleSU();
                    //eatenFoods are removed whenever the stopDiet is call (in the next method)

                    String randomRelationship = followRelationships[random.nextInt(2)];
                    if(randomRelationship.equals("current")){
                        currentDietUsersCount++;
                    }
                    else if(randomRelationship.equals("stopped")) {
                        logicManager.stopDiet();
                        stoppedDietUsersCount++;
                    }
                    else
                        System.exit(1);

                    standardUsersWithRelationship++;
                }
                standardUsersCount++;
                printPercentageProgress(standardUsersWithRelationship, standardUserToProcessForRelationships, "stdUser-R");
            }
        }
        standardUsersWithoutRelationship = standardUserToProcessForRelationships - standardUsersWithRelationship;
        System.out.println("users processed: "+standardUsersCount);
        System.out.println("users with relationship: "+standardUsersWithRelationship);
        System.out.println("users without relationship: "+standardUsersWithoutRelationship);
        System.out.println("users without relationship: "+standardUsersWithoutRelationship);
        System.out.println("users with current diet: "+currentDietUsersCount);
        System.out.println("users with stopped diet: "+stoppedDietUsersCount);
    }

    public void resetDBs(){
        System.out.println(">>> databases reset is starting");
        logicManager.dropAllDBs();
        System.out.println(">>> databases reset finished");
    }

    public static void main(String... args){
        Populator populator = new Populator();

        populator.setMAX_STANDARD_USERS( 50000 );
        populator.setMAX_STANDARD_USERS_RELATIONSHIPS( 10000 );

        populator.readPopulationFromJSONFiles();

        //startingPopulator2.resetDBs();
        populator.logicManager.openOnlyOneConnection();

        //populator.populateDBs(false, true, false);
        //startingPopulator2.processStandardUsers();

        populator.logicManager.closeOnlyOneConnection();
        System.out.println(">>> Code finished: stop the execution");
    }

}



