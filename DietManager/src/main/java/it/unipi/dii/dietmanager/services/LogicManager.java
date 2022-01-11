package it.unipi.dii.dietmanager.services;

import it.unipi.dii.dietmanager.client.CLI;
import it.unipi.dii.dietmanager.entities.*;
import it.unipi.dii.dietmanager.persistence.MongoDBManager;
import it.unipi.dii.dietmanager.persistence.Neo4jManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LogicManager {
    public User currentUser;
    private final String[] nutrients_names =
            {/*0*/"Energy",/*1*/"Protein",/*2*/"Fat",/*3*/"Carb",/*4*/"Sugar",
                    /*5*/"Fiber",/*6*/"VitA",/*7*/"VitB6",/*8*/"VitB12",/*9*/"VitC",/*10*/"VitE",
                    /*11*/"Thiamin",/*12*/"Calcium",/*13*/"Magnesium",/*14*/"Manganese",/*15*/"Phosphorus",
                    /*16*/"Zinc"};

    private final int MAX_FAIL_NUTRIENT = 2;

    private final int MONGO_DB_PORT_LOCAL = 27017;
    private final int MONGO_DB_PORT_REMOTE = 27020;
    private final String MONGO_DB_ADDRESS_LOCAL = "localhost";
    private final String MONGO_DB_ADDRESS_REMOTE = "172.16.4.86";
    private final int NEO4J_PORT_LOCAL = 7687;
    private final int NEO4J_PORT_REMOTE = 7687;
    private final String NEO4J_ADDRESS_LOCAL = "localhost";
    private final String NEO4J_ADDRESS_REMOTE = "172.16.4.84";
    private final String NEO4J_CONNECTION_MODE_LOCAL = "neo4j";
    private final String NEO4J_CONNECTION_MODE_REMOTE = "bolt";
    private final String NEO4J_USER = "neo4j";
    private final String NEO4J_PASSWORD = "root";

    private Neo4jManager neo4JManager;
    private MongoDBManager mongoDBManager;
    private CLI cli;

    public LogicManager( boolean remoteConnection, CLI cli) {
        this.currentUser = null;
        this.cli = cli;
        if(remoteConnection)
            instantiateDriversToRemoteConnections();
        else
            instantiateDriversToLocalConnections();
    }

    private void instantiateDriversToLocalConnections(){
        neo4JManager = new Neo4jManager(NEO4J_CONNECTION_MODE_LOCAL,
                NEO4J_ADDRESS_LOCAL, NEO4J_PORT_LOCAL, NEO4J_USER, NEO4J_PASSWORD) ;
        mongoDBManager = new MongoDBManager(MONGO_DB_ADDRESS_LOCAL,MONGO_DB_PORT_LOCAL);
    }
    private void instantiateDriversToRemoteConnections(){
        neo4JManager = new Neo4jManager(NEO4J_CONNECTION_MODE_REMOTE,
                NEO4J_ADDRESS_REMOTE, NEO4J_PORT_REMOTE, NEO4J_USER, NEO4J_PASSWORD) ;
        mongoDBManager = new MongoDBManager(MONGO_DB_ADDRESS_REMOTE,MONGO_DB_PORT_REMOTE);
    }


    public void openOnlyOneConnection(){
        mongoDBManager.openOnlyOneConnection();
    }
    public void closeOnlyOneConnection(){
        mongoDBManager.closeOnlyOneConnection();
    }

    public void dropAllDBs(){
        neo4JManager.dropDatabase();
        mongoDBManager.dropDatabase();
    }

    public void createMongoDBindex(){
        mongoDBManager.createDietManagerIndexes();
    }

    /** Read operations in MongoDB */
    public boolean signIn(String username, String password){
        // check user type (if it's nutritionist then load him diets)
        User userTarget;
        userTarget = lookUpUserByUsername(username);

        if(userTarget != null && userTarget.getPassword().equals(password)) {
            currentUser = userTarget;
            return true;
        }
        else return false;
    }

    public Diet lookUpDietByID(String id){
        Diet dietTarget = null;

        dietTarget = mongoDBManager.lookUpDietByID(id);

        return dietTarget;
    }

    public List<Diet> lookUpDietByName(String subName){
        List<Diet> dietsTarget = null;

        dietsTarget = mongoDBManager.lookUpDietByName(subName);

        return  dietsTarget;
    }

    public List<Diet> lookUpDietByNutritionist (String username){
        List<Diet> dietsTarget = null;

        dietsTarget = mongoDBManager.lookUpDietByNutritionist(username);

        return  dietsTarget;
    }

    public List<Food> lookUpFoodByName (String subName){
        List<Food> foodsTarget = null;

        foodsTarget = mongoDBManager.lookUpFoodsByName(subName);

        return  foodsTarget;
    }


    public Food lookUpMostEatenFoodByCategory (String category){
        Food foodTarget = null;

        foodTarget = mongoDBManager.lookUpMostEatenFoodByCategory(category);

        return  foodTarget;
    }

    public HashMap<String, Integer> lookUpSumOfEatenTimesCountForEachCategory(){
        return mongoDBManager.lookUpSumOfEatenTimesCountForEachCategory();
    }

    public User lookUpUserByUsername(String username){
        User userTarget = null;

        userTarget = mongoDBManager.lookUpUserByUsername(username);

        return userTarget;
    }

    public List<User> lookUpUserByCountry(String country){
        return mongoDBManager.lookUpAllUsersByCountry(country);
    }

    public Diet lookUpStandardUserCurrentDiet (){
        Diet dietTarget = null;

        if( ((StandardUser) currentUser).getCurrentDiet() != null ) {
            if(((StandardUser) currentUser).getCurrentDiet().getName() != null) //check if the currentDiet is alredy stored locally(or only the id without the other parameters).
                return ((StandardUser) currentUser).getCurrentDiet();
            else{
                //used the first time the user look his current diet

                dietTarget = mongoDBManager.lookUpDietByID( ((StandardUser) currentUser).getCurrentDiet().getId() );
                ((StandardUser) currentUser).setCurrentDiet(dietTarget);
            }
        }
        return dietTarget;
    }

    public HashMap<String, Nutrient> lookUpMostSuggestedNutrientForEachNutritionist(){
        HashMap<String, Nutrient> npn = null;

        npn = mongoDBManager.lookUpMostSuggestedNutrientForEachNutritionist();

        return npn;
    }

    /** Read-Operations for Neo4J */
    public Diet lookUpMostFollowedDiet() {
        Diet dietTarget = null; String id;

        id = neo4JManager.lookUpMostFollowedDiet();
        dietTarget = lookUpDietByID(id);

        return  dietTarget;
    }

    public Diet lookUpMostPopularDiet() {
        Diet dietTarget = null; String id;

        id = neo4JManager.lookUpMostPopularDiet();
        dietTarget = lookUpDietByID(id);

        return  dietTarget;
    }

    public Diet lookUpMostSucceededDiet() {
        Diet dietTarget = null; String id;

        id = neo4JManager.lookUpMostSucceededDiet();
        dietTarget = lookUpDietByID(id);

        return  dietTarget;
    }

    public Diet lookUpRecommendedDiet() {
        Diet dietTarget = null; String id;

        if(((StandardUser)currentUser).getCurrentDiet() != null) {
            id = neo4JManager.lookUpRecommendedDiet((StandardUser) currentUser);
            dietTarget = lookUpDietByID(id);
        }

        return  dietTarget;
    }

    public Diet lookUpMostFollowedDietByNutritionist(String username) {
        Diet dietTarget = null; String id;

        if(lookUpUserByUsername(username) != null){
            id = neo4JManager.lookUpMostFollowedDietByNutritionist(username);
            dietTarget = lookUpDietByID(id);
        }

        return  dietTarget;
    }

    public User lookUpMostPopularNutritionist(){
        String username;


        username = neo4JManager.lookUpMostPopularNutritionist();
        User usertarget = null;
        usertarget = lookUpUserByUsername(username);
        if(usertarget instanceof Nutritionist)
            return usertarget;
        else {
            cli.generalPrint("Error, not a nutritionist - Inconsistency among the DBs");
            return  null;
        }
    }

    /** Write-Operations*/
    public boolean addUser(User user){
        boolean isMongoDBSuccessful, isNeo4jSuccessful;

        isMongoDBSuccessful = mongoDBManager.addUser(user);
        if(isMongoDBSuccessful){
            isNeo4jSuccessful = neo4JManager.addUser(user);
            if(!isNeo4jSuccessful){
                cli.generalPrint("Error cross-consistency");

                //Remove from Mongo
                mongoDBManager.removeUser(user);
                return false;
            }
            else return true;
        }
        else{
            cli.generalPrint("Error in MongoDB");
            return false;
        }

    }

    public boolean followDiet(String id){
        boolean isMongoDBSuccessful, isNeo4jSuccessful;
        Diet diet;
        diet = lookUpDietByID(id);
        if(diet != null && ((StandardUser)currentUser).getCurrentDiet() == null ){ //check
            isMongoDBSuccessful = mongoDBManager.followDiet((StandardUser)currentUser, id);
            if(isMongoDBSuccessful){
                isNeo4jSuccessful = neo4JManager.followDiet((StandardUser) currentUser, id);
                if(!isNeo4jSuccessful){
                    cli.generalPrint("Errore cross-consistency");
                    //Remove from Mongo
                    mongoDBManager.unfollowDiet((StandardUser)currentUser);
                    return false;
                }
                else {
                    ((StandardUser)currentUser).setCurrentDiet(diet);
                    return true;
                }
            }
            else{
                cli.generalPrint("Error in MongoDB");
                return false;
            }
        }
        else return false;
    }

    public boolean unfollowDiet(){
        boolean isMongoDBSuccessful, isNeo4jSuccessful;

        if(((StandardUser)currentUser).getCurrentDiet() != null){
            Diet diet;
            diet = lookUpDietByID(((StandardUser)currentUser).getCurrentDiet().getId());
            if(diet != null) { //check if diet is not null

                isNeo4jSuccessful = neo4JManager.unfollowDiet((StandardUser)currentUser);
                if(isNeo4jSuccessful){

                    isMongoDBSuccessful = mongoDBManager.unfollowDiet((StandardUser)currentUser);
                    if(!isMongoDBSuccessful){
                        cli.generalPrint("Error cross-consistency");
                        //Re-insert in Neo4J
                        neo4JManager.followDiet((StandardUser) currentUser, ((StandardUser)currentUser).getCurrentDiet().getId());
                        return false;
                    }
                    else {
                        ((StandardUser)currentUser).stopCurrentDiet();
                        return true;
                    }
                }
                else{
                    cli.generalPrint("Error in MongoDB");
                    return false;
                }
            }
            else return false;
        }
        else return false;
    }

    public boolean stopDiet(){
        boolean isMongoDBSuccessful, isNeo4jSuccessful, check;

        if(((StandardUser)currentUser).getCurrentDiet() != null){
            Diet diet = null;
            diet = lookUpDietByID(((StandardUser)currentUser).getCurrentDiet().getId());
            if(diet != null) { //check if diet is not null

                check = checkDietProgress(); //check diet progress
                isNeo4jSuccessful = neo4JManager.stopDiet((StandardUser) currentUser, check);
                if(isNeo4jSuccessful){

                    isMongoDBSuccessful = mongoDBManager.unfollowDiet((StandardUser)currentUser);
                    if(!isMongoDBSuccessful){
                        cli.generalPrint("Error cross-consistency");

                        //Re-insert in Neo4J
                        neo4JManager.followDiet((StandardUser) currentUser, ((StandardUser)currentUser).getCurrentDiet().getId());
                        return false;
                    }
                    else {
                        ((StandardUser)currentUser).stopCurrentDiet();
                        return true;
                    }
                }
                else{
                    cli.generalPrint("Error in MongoDB");
                    return false;
                }
            }
            else return false;
        }
        else return false;
    }

    //During the application, the new EatenFood will be saved only locally in Java, and persistently saved during the logging-out
    public boolean addFoodToEatenFoods(String foodName, int quantity){
        boolean task = false;

        List<Food> foodList = lookUpFoodByName(foodName);
        if(foodList != null && foodList.size() == 1){
            ((StandardUser)currentUser).addEatenFood(foodName, quantity);
            task = true;
        }
        mongoDBManager.incrementEatenTimesCount(foodName);
        return  task;
    }

    public boolean addEatenFoodToMongo(){ //it must be called only when a SU is logging-out
        return mongoDBManager.updateEatenFood((StandardUser) currentUser); //this Mongo method will be called during the exit, so that the removeEatenFood does not need to call the mongoDBManager removeEatenFood
    }


    public boolean removeEatenFood(String id){
        int indexTarget = -1;
        int i;
        for(i = 0; i < ((StandardUser)currentUser).getEatenFoods().size(); i++){
            if(((StandardUser)currentUser).getEatenFoods().get(i).getId().equals(id)){
                indexTarget = i;
            }
        }
        if(indexTarget != -1)
            ((StandardUser)currentUser).getEatenFoods().remove(indexTarget);
        return indexTarget!=-1;
    }

    public boolean addDiet(Diet diet){
        boolean isMongoDBSuccessful;
        boolean isNeo4jSuccessful;

        isMongoDBSuccessful = mongoDBManager.addDiet(diet); //it will return the ID of the new Diet added
        if(isMongoDBSuccessful){
            isNeo4jSuccessful = neo4JManager.addDiet(diet);
            if(!isNeo4jSuccessful){
                cli.generalPrint("Error cross-consistency");

                //Remove from Mongo
                mongoDBManager.removeDiet(diet.getId());
                return false;
            }
            else{
                return true;
            }
        }
        else{
            cli.generalPrint("Error in MongoDB");
            return false;
        }
    }

    public boolean removeDiet(String id){
        boolean isMongoDBSuccessful, isNeo4jSuccessful;
        Diet dietToRemove;
        dietToRemove = lookUpDietByID(id);
        if(dietToRemove != null && currentUser.getUsername().equals(dietToRemove.getNutritionist())){

            isMongoDBSuccessful = mongoDBManager.removeDiet(id);
            if(isMongoDBSuccessful){

                isNeo4jSuccessful = neo4JManager.removeDiet(id);
                if(!isNeo4jSuccessful){
                    cli.generalPrint("Errore cross-consistency");

                    //Re-insert in Mongo
                    mongoDBManager.addDiet(dietToRemove);
                    return false;
                }
                else {
                    return true;
                }
            }
            else{
                cli.generalPrint("Error in MongoDB");
                return false;
            }
        }
        else return false;
    }

    public boolean removeUser(String username){
        boolean isMongoDBSuccessful, isNeo4jSuccessful;
        User userToRemove;
        userToRemove = lookUpUserByUsername(username);
        if(userToRemove != null){

            isMongoDBSuccessful = mongoDBManager.removeUser(userToRemove);
            if(isMongoDBSuccessful){

                isNeo4jSuccessful = neo4JManager.removeUser(username);
                if(!isNeo4jSuccessful){
                    cli.generalPrint("Errore cross-consistency");

                    //Re-insert in Mongo
                    mongoDBManager.addUser(userToRemove);
                    return false;
                }
                else return true;
            }
            else{
                cli.generalPrint("Error in MongoDB");
                return false;
            }
        }
        else return false;
    }

    public boolean addFood(Food food){
        return mongoDBManager.addFood(food);
    }

    public boolean removeFood(String foodID){
        return mongoDBManager.removeFood(foodID);
    }

    private int nutrientIndex(List<Nutrient> list, String nutrientName){
        int i = 0;
        for(i = 0; i < list.size(); i++){
            if(list.get(i).getName().equals(nutrientName))
                return i;
        }
        return -1;
    }


    public boolean checkDietProgress (){
        Food foodTarget; Diet dietTarget;
        int i, index, counterFails = 0, totalQuantityEatenFoods = 0;
        double[] total = new double[17]; boolean[] isBelow = new boolean[17];
        Arrays.fill(isBelow, false); //set all the values of the bool array to 'false'. We suppose at the beginning all the values of the nutrients of the currentUSer EatenFood are higher than the corresponding nutrients values of the diet followed
        Arrays.fill(total, 0); //set all the values of the double array to 0

        //1) i retrive the eatenFood list of the current user
        if( ((StandardUser) currentUser).getEatenFoods() == null || ((StandardUser) currentUser).getEatenFoods().isEmpty() || ((StandardUser) currentUser).getCurrentDiet() == null )
            return true;

        /**
         * the EatenFood has only the ID of the food, with no values of each nutrients.
         * We are required to make an accesso to MongoDB for each nutrientFood of the list to get/obtain the values of each (its )nutrient to compute the totals
         * Then we have to comapre the totals[] with the values of the CurrentDiet.
         */
        for (EatenFood ef : ((StandardUser) currentUser).getEatenFoods() ){
            if(lookUpFoodByName(ef.getFoodName()).size() == 0) {
                foodTarget = null;
            }
            foodTarget = lookUpFoodByName(ef.getFoodName()).get(0);

            //i = 0;
            for(int j = 0; j < nutrients_names.length; j++){
                index = nutrientIndex(foodTarget.getNutrients(), nutrients_names[j]);
                if(index != -1)
                    total[j] += (foodTarget.getNutrients().get(index).getQuantity()) * ef.getQuantity() / 100;
            }
            totalQuantityEatenFoods += ef.getQuantity();
        }

        //2) i retrive the diet of the current User
        dietTarget = lookUpStandardUserCurrentDiet();

        //3) check if the currentUser is respecting the followed diet
        for(int j = 0; j < nutrients_names.length; j++){
            index = nutrientIndex(dietTarget.getNutrients(), nutrients_names[j]);
            if( ( total[j]/ (totalQuantityEatenFoods / 100) ) < dietTarget.getNutrients().get(index).getQuantity() )
                isBelow[j] = true;
        }

        for(int j = 0; j < isBelow.length; j++){
            if(!isBelow[j])
                counterFails++;

            if(counterFails > MAX_FAIL_NUTRIENT)
                return false;
        }
        return true;
    }

    // for future usage: it returns the differences between the averages quantities of each nutrients in the eaten foods
    // of the current standard user and the values of the correspondent nutrient of his current followed diet
    public HashMap<Nutrient, double[]> dietProgress (){
        Food foodTarget; Diet dietTarget; HashMap<Nutrient, double[]> hashMap = new HashMap<>();
        int i, index, totalQuantityEatenFoods = 0;
        double[] total = new double[17]; boolean[] isBelow = new boolean[17];
        Arrays.fill(isBelow, false); //set all the values of the bool array to 'false'. We suppose at the beginning all the values of the nutrients of the currentUSer EatenFood are higher than the corresponding nutrients values of the diet followed
        Arrays.fill(total, 0); //set all the values of the double array to 0

        for (EatenFood ef : ((StandardUser) currentUser).getEatenFoods() ){
            foodTarget = lookUpFoodByName(ef.getFoodName()).get(0);
            for(int j = 0; j < nutrients_names.length; j++){
                index = nutrientIndex(foodTarget.getNutrients(), nutrients_names[j]);
                total[j] += (foodTarget.getNutrients().get(index).getQuantity()) * ef.getQuantity() / 100;
            }
            totalQuantityEatenFoods += ef.getQuantity();
        }

        dietTarget = lookUpStandardUserCurrentDiet();

        for(int j = 0; j < nutrients_names.length; j++){
            index = nutrientIndex(dietTarget.getNutrients(), nutrients_names[j]);
            if( ( total[j]/ (totalQuantityEatenFoods / 100) ) < dietTarget.getNutrients().get(index).getQuantity() ) {
                double [] tmp = { (total[j]/ (totalQuantityEatenFoods / 100)), dietTarget.getNutrients().get(index).getQuantity() };
                hashMap.put(dietTarget.getNutrients().get(index), tmp);
            }
        }
        return hashMap;
    }
}
