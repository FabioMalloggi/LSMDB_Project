package it.unipi.dii.dietmanager.services;

import it.unipi.dii.dietmanager.entities.*;
import it.unipi.dii.dietmanager.persistence.MongoDB;
import it.unipi.dii.dietmanager.persistence.MongoDB2;
import it.unipi.dii.dietmanager.persistence.Neo4j;

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

    private final String[] nutrients_units =
            {/*0*/"KCAL",/*1*/"G",/*2*/"G",/*3*/"G",/*4*/"G",
                    /*5*/"G",/*6*/"UG",/*7*/"MG",/*8*/"UG",/*9*/"MG",/*10*/"MG",
                    /*11*/"MG",/*12*/"MG",/*13*/"MG",/*14*/"MG",/*15*/"MG",
                    /*16*/"MG"};
    private final int MAX_FAIL_NUTRIENT = 2;
    private final int MONGO_DB_PORT = 27020;
    private final String MONGO_DB_ADDRESS = "172.16.4.84";
    private Neo4j Neo4J;
    private MongoDB2 MongoDB;

    public LogicManager() {
        this.currentUser = null;
        Neo4J = new Neo4j() ;
        MongoDB = new MongoDB2(MONGO_DB_ADDRESS,MONGO_DB_PORT);
        MongoDB.openConnection();
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

        dietTarget = MongoDB.lookUpDietByID(id);

        return dietTarget;
    }

    public List<Diet> lookUpDietByName(String subName){
        List<Diet> dietsTarget = null;

        dietsTarget = MongoDB.lookUpDietByName(subName);

        return  dietsTarget;
    }

    public List<Diet> lookUpDietByNutritionist (String username){
        List<Diet> dietsTarget = null;

        dietsTarget = MongoDB.lookUpDietByNutritionist(username);

        return  dietsTarget;
    }

    public List<Food> lookUpFoodByName (String subName){
        List<Food> foodsTarget = null;

        foodsTarget = MongoDB.lookUpFoodsByName(subName);

        return  foodsTarget;
    }


    public Food lookUpMostEatenFoodByCategory (String category){
        Food foodTarget = null;

        foodTarget = MongoDB.lookUpMostEatenFoodByCategory(category);

        return  foodTarget;
    }

    public User lookUpUserByUsername(String username){
        User userTarget = null;

        userTarget = MongoDB.lookUpUserByUsername(username);

        return userTarget;
    }

    public List<User> lookUpUserByCountry(String country){
        return MongoDB.lookUpAllUsersByCountry(country);
    }

    public Diet lookUpStandardUserCurrentDiet (){
        Diet dietTarget = null;

        if( ((StandardUser) currentUser).getCurrentDiet() != null ) {
            if(((StandardUser) currentUser).getCurrentDiet().getName() != null) //check if the currentDiet is alredy stored locally(or only the id without the other parameters).
                return ((StandardUser) currentUser).getCurrentDiet();
            else{
                //used the first time the user look his current diet

                dietTarget = MongoDB.lookUpDietByID( ((StandardUser) currentUser).getCurrentDiet().getId() );
                ((StandardUser) currentUser).setCurrentDiet(dietTarget);
            }
        }
        return dietTarget;
    }

    public HashMap<String, Nutrient> lookUpMostSuggestedNutrientForEachNutritionist(){
        HashMap<String, Nutrient> npn = null;

        npn = MongoDB.lookUpMostSuggestedNutrientForEachNutritionist();

        return npn;
    }

    /** Read-Operations for Neo4J */
    public Diet lookUpMostFollowedDiet() {
        Diet dietTarget = null; String id;

        id = Neo4J.lookUpMostFollowedDiet();
        dietTarget = lookUpDietByID(id);

        return  dietTarget;
    }

    public Diet lookUpMostPopularDiet() {
        Diet dietTarget = null; String id;

        id = Neo4J.lookUpMostPopularDiet();
        dietTarget = lookUpDietByID(id);

        return  dietTarget;
    }

    public Diet lookUpMostSucceededDiet() {
        Diet dietTarget = null; String id;

        id = Neo4J.lookUpMostSucceededDiet();
        dietTarget = lookUpDietByID(id);

        return  dietTarget;
    }

    public Diet lookUpRecommendedDiet() {
        Diet dietTarget = null; String id;

        if(((StandardUser)currentUser).getCurrentDiet() != null) {
            id = Neo4J.lookUpRecommendedDiet((StandardUser) currentUser);
            dietTarget = lookUpDietByID(id);
        }

        return  dietTarget;
    }

    public Diet lookUpMostFollowedDietByNutritionist(String username) {
        Diet dietTarget = null; String id;

        if(lookUpUserByUsername(username) != null){
            id = Neo4J.lookUpMostFollowedDietByNutritionist(username);
            dietTarget = lookUpDietByID(id);
        }

        return  dietTarget;
    }

    public User lookUpMostPopularNutritionist(){
        String username;


        username = Neo4J.lookUpMostPopularNutritionist();
        User usertarget = null;
        usertarget = lookUpUserByUsername(username);
        if(usertarget instanceof Nutritionist)
            return usertarget;
        else {
            System.out.println("Error, not a nutritionist - Inconsistency among the DBs");
            return  null;
        }
    }

    /** Write-Operations*/
    public boolean addUser(User user){
        boolean mongoDB, neo4J;

        mongoDB = MongoDB.addUser(user);
        if(mongoDB){
            neo4J = Neo4J.addUser(user);
            if(!neo4J){
                System.out.println("Error cross-consistency");

                //Remove from Mongo
                MongoDB.removeUser(user);
                return false;
            }
            else return true;
        }
        else{
            System.out.println("Error in MongoDB");
            return false;
        }

    }

    public boolean followDiet(String id){
        boolean mongoDB, neo4J;
        Diet diet;
        diet = lookUpDietByID(id);
        if(diet != null && ((StandardUser)currentUser).getCurrentDiet() == null ){ //check
            mongoDB = MongoDB.followDiet((StandardUser)currentUser, id);
            if(mongoDB){
                neo4J = Neo4J.followDiet((StandardUser) currentUser, id);
                if(!neo4J){
                    System.out.println("Errore cross-consistency");
                    //Remove from Mongo
                    MongoDB.unfollowDiet((StandardUser)currentUser);
                    return false;
                }
                else {
                    ((StandardUser)currentUser).setCurrentDiet(diet);
                    return true;
                }
            }
            else{
                System.out.println("Error in MongoDB");
                return false;
            }
        }
        else return false;
    }

    public boolean unfollowDiet(){
        boolean mongoDB, neo4J;

        if(((StandardUser)currentUser).getCurrentDiet() != null){
            Diet diet;
            diet = lookUpDietByID(((StandardUser)currentUser).getCurrentDiet().getId());
            if(diet != null) { //check if diet is not null

                neo4J = Neo4J.unfollowDiet((StandardUser)currentUser);
                if(neo4J){

                    mongoDB = MongoDB.unfollowDiet((StandardUser)currentUser);
                    if(!mongoDB){
                        System.out.println("Errore cross-consistency");
                        //Re-insert in Neo4J
                        Neo4J.followDiet((StandardUser) currentUser, ((StandardUser)currentUser).getCurrentDiet().getId());
                        return false;
                    }
                    else {
                        ((StandardUser)currentUser).stopCurrentDiet();
                        return true;
                    }
                }
                else{
                    System.out.println("Error in MongoDB");
                    return false;
                }
            }
            else return false;
        }
        else return false;
    }

    public boolean stopDiet(){
        boolean mongoDB, neo4J, check;

        if(((StandardUser)currentUser).getCurrentDiet() != null){
            Diet diet = null;
            diet = lookUpDietByID(((StandardUser)currentUser).getCurrentDiet().getId());
            if(diet != null) { //check if diet is not null

                check = checkDietProgress(); //check diet progress
                neo4J = Neo4J.stopDiet((StandardUser) currentUser, check);
                if(neo4J){

                    mongoDB = MongoDB.unfollowDiet((StandardUser)currentUser);
                    if(!mongoDB){
                        System.out.println("Error cross-consistency");

                        //Re-insert in Neo4J
                        Neo4J.followDiet((StandardUser) currentUser, ((StandardUser)currentUser).getCurrentDiet().getId());
                        return false;
                    }
                    else {
                        ((StandardUser)currentUser).stopCurrentDiet();
                        return true;
                    }
                }
                else{
                    System.out.println("Error in MongoDB");
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
        MongoDB.incrementEatenTimesCount(foodName);
        return  task;
    }

    public boolean addEatenFoodToMongo(){ //it must be called only when a SU is logging-out
        return MongoDB.updateEatenFood((StandardUser) currentUser); //this Mongo method will be called during the exit, so that the removeEatenFood does not need to call the mongoDB removeEatenFood
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
        boolean mongoDB;
        boolean neo4J;

        mongoDB = MongoDB.addDiet(diet); //it will return the ID of the new Diet added
        if(mongoDB){
            neo4J = Neo4J.addDiet(diet);
            if(!neo4J){
                System.out.println("Errore cross-consistency");

                //Remove from Mongo
                MongoDB.removeDiet(diet.getId());
                return false;
            }
            else{
                return true;
            }
        }
        else{
            System.out.println("Error in MongoDB");
            return false;
        }
    }

    public boolean removeDiet(String id){
        boolean mongoDB, neo4J;
        Diet dietToRemove;
        dietToRemove = lookUpDietByID(id);
        if(currentUser.getUsername().equals(dietToRemove.getNutritionist())){

            mongoDB = MongoDB.removeDiet(id);
            if(mongoDB){

                neo4J = Neo4J.removeDiet(id);
                if(!neo4J){
                    System.out.println("Errore cross-consistency");

                    //Re-insert in Mongo
                    MongoDB.addDiet(dietToRemove);
                    return false;
                }
                else {
                    return true;
                }
            }
            else{
                System.out.println("Error in MongoDB");
                return false;
            }
        }
        else return false;
    }

    public boolean removeUser(String username){
        boolean mongoDB, neo4J;
        User userToRemove;
        userToRemove = lookUpUserByUsername(username);
        if(userToRemove != null){

            mongoDB = MongoDB.removeUser(userToRemove);
            if(mongoDB){

                neo4J = Neo4J.removeUser(username);
                if(!neo4J){
                    System.out.println("Errore cross-consistency");

                    //Re-insert in Mongo
                    MongoDB.addUser(userToRemove);
                    return false;
                }
                else return true;
            }
            else{
                System.out.println("Error in MongoDB");
                return false;
            }
        }
        else return false;
    }

    public boolean addFood(Food food){
        return MongoDB.addFood(food);
    }

    public boolean removeFood(String foodID){
        return MongoDB.removeFood(foodID);
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
