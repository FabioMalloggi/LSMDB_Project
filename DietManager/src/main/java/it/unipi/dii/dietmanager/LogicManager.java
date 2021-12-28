package it.unipi.dii.dietmanager;

import it.unipi.dii.dietmanager.entities.*;
import it.unipi.dii.dietmanager.persistence.*;

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
    private final int MONGO_DB_PORT = 2222;
    private Neo4j Neo4J;
    private MongoDB MongoDB;

    public LogicManager() {
        this.currentUser = null;
        Neo4J = new Neo4j( "neo4j://localhost:7687", "neo4j", "root" ) ;
        MongoDB = new MongoDB(MONGO_DB_PORT);
    }

    public boolean signIn(String username, String password){
        boolean test = false;
        System.out.println("LogiManagment: Sign In");
        //test = MongoDB.signIn(String username, String password);
        //to test
        return test;
    }

    public Diet lookUpDietByID(String id){
        Diet dietTarget = null;

        //dietTarget = MongoDB.lookUpDietByID(id);

        return dietTarget;
    }

    public List<Diet> lookUpDietByName(String subName){
        List<Diet> dietsTarget = null;

        //dietsTarget = MongoDB.lookUpDietByName(subName);

        return  dietsTarget;
    }

    public List<Diet> lookUpDietByNutritionist (String username){
        List<Diet> dietsTarget = null;

        //dietsTarget = MongoDB.lookUpDietByNutritionist(username);

        return  dietsTarget;
    }

    public List<Food> lookUpFoodByName (String subName){
        List<Food> foodsTarget = null;

        //foodsTarget = MongoDB.lookUpFoodByName(subName);

        return  foodsTarget;
    }

    public Food lookUpFoodByID(String id){
        Food foodTarget = null;

        //foodTarget = MongoDB.lookUpFoodByID(); // *** Asking Fabio to develpo this method in MongoDB***
        return foodTarget;
    }

    public List<Food> lookUpMostEatenFoodByCategory (String category){
        List<Food> foodsTarget = null;

        //foodsTarget = MongoDB.lookUpMostEatenFoodByCategory(category);

        return  foodsTarget;
    }

    public User lookUpUserByUsername(String username){
        User userTarget = null;

        //userTarget = MongoDB.lookUpUserByUsername(username);

        return userTarget;
    }

    public User lookUpUserByCountry(String country){
        User userTarget = null;

        //userTarget = MongoDB.lookUpUserByCountry(country);

        return userTarget;
    }

    public Diet lookUpStandardUserCurrentDiet (){
        Diet dietTarget = null;

        //dietTarget = MongoDB.lookUpStandardUserCurrentDiet(currentUser);

        return dietTarget;
    }

    public List<EatenFood> lookUpStandardUserEatenFoods() {
        List<EatenFood> eatenFoods = null;

        //eatenFoods = MongoDB.lookUpStandardUserEatenFoods(currentUser);

        return eatenFoods;
    }

    public HashMap<Nutritionist, Nutrient> lookUpMostSuggestedNutrientForEachNutritionist(){
        HashMap<Nutritionist, Nutrient> npn = null;

        //npn = MongoDB.lookUpMostSuggestedNutrientForEachNutritionist();

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

    public Diet lookUpMostRecommendedDiet() {
        Diet dietTarget = null; String id;

        id = Neo4J.lookUpMostRecommendedDiet((StandardUser) currentUser);
        dietTarget = lookUpDietByID(id);

        return  dietTarget;
    }

    /**Conflitto di parametri tra questa funzione e la sua corrispettiva in Neo4j --> vedere txt apposito*/
    public Diet lookUpMostFollowedDietByNutritionist(String username) {
        Diet dietTarget = null; String id;

        /*
        id = Neo4J.lookUpMostFollowedDietByNutritionist(username);
        dietTarget = lookUpDietByID(id);
        */
        return  dietTarget;
    }

    public Nutritionist lookUpMostPopularNutritionist(){
        Nutritionist nutritionistTarget = null;

        //nutritionistTarget = Neo4j.lookUpMostPopularNutritionist();

        return nutritionistTarget;
    }

    /** Write-Operations*/
    public boolean registerUser(User user){
        boolean mongoDB = false, neo4J = false;

        //mongoDB = MongoDB.registerUser(user);
        if(mongoDB){
            //neo4J = Neo4J.registerUser(user);
            if(!neo4J){
                System.out.println("Errore cross-consistency");
                //to do something.. //REMOVE DA MONGO
                return false;
            }
            else return true;
        }
        else{
            System.out.println("Error in MongoDB");
            //to do something..
            return false;
        }

    }

    public boolean followDiet(String id){
        boolean mongoDB = false, neo4J = false;
        /* unuseful if the check is done by Mongo/Neo4J
        Diet diet;
        diet = lookUpDietByID(id);*/
        //mongoDB = MongoDB.followDiet(id, currentUser);
        if(mongoDB){
            //neo4J = Neo4J.followDiet(id, currentUser);
            if(!neo4J){
                System.out.println("Errore cross-consistency");
                //to do something.. REMOVE su MONGO
                return false;
            }
            else return true;
        }
        else{
            System.out.println("Error in MongoDB");
            //to do something..
            return false;
        }
    }

    public boolean stopDiet(String id){
        boolean mongoDB = false, neo4J = false;

        //*** PRIMA NEO E POI MONGO

        //neo4J = Neo4J.stopDiet(id, currentUser);
        if(neo4J){
            //mongoDB = MongoDB.stopDiet(id, currentUser);
            if(!mongoDB){
                System.out.println("Errore cross-consistency");
                //to do something.. //RE-INSERT IN NEO4j
                return false;
            }
            else return true;
        }
        else{
            System.out.println("Error in MongoDB");
            //to do something..
            return false;
        }
    }

    public boolean addFoodToEatenFood(String name, String quantity){
        boolean task = false; // *** se vogliamo che io qui creo un EatenFood instance da poi aggiungere alla lista del current user: devo inizialmente crearlo senza E.F ID poi in qualche modo devo recuperare l'ID e chiamare setID di E.F


        //task = MongoDB.addFoodToEatenFood(name, quantity, currentUser);

        return  task;
    }


    public boolean removeEatenFood(String id){
        boolean task = false;

        //task = MongoDB.removeEatenFood(id, currentUser);

        return  task;
    }

    public boolean addDiet(Diet diet){
        boolean mongoDB = false, neo4J = false;

        //mongoDB = MongoDB.stopDiet(diet, currentUser); // *** come può MongoDB (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
        if(mongoDB){
            //neo4J = Neo4J.stopDiet(diet, currentUSer); // *** come può Neo4J (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
            if(!neo4J){
                System.out.println("Errore cross-consistency");
                //to do something..
                return false;
            }
            else return true;
        }
        else{
            System.out.println("Error in MongoDB");
            //to do something..
            return false;
        }
    }

    public boolean removeDiet(String ID){ //OLD VERSION: Diet diet <-- con questa devo vedere se l'oggetto nutrizionista ha nella lista di diete, un istanza dieta con quell ID e poi ricavere quall'istanza e passarla qui
        boolean mongoDB = false, neo4J = false;

        //mongoDB = MongoDB.removeDiet(ID, currentUser); // *** come può MongoDB (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
        if(mongoDB){
            //neo4J = Neo4J.remove(ID, currentUer); // *** come può Neo4J (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
            if(!neo4J){
                System.out.println("Errore cross-consistency");
                //to do something..
                return false;
            }
            else return true;
        }
        else{
            System.out.println("Error in MongoDB");
            //to do something..
            return false;
        }
    }

    public boolean removeUser(String username){
        boolean mongoDB = false, neo4J = false;

        //mongoDB = MongoDB.removeUser(user);
        if(mongoDB){
            //neo4J = Neo4J.removeUser(user);
            if(!neo4J){
                System.out.println("Errore cross-consistency");
                //to do something..
                return false;
            }
            else return true;
        }
        else{
            System.out.println("Error in MongoDB");
            //to do something..
            return false;
        }
    }

    public boolean addFood(Food food){
        boolean task = false;

        //task = MongoDB.addFood(food);

        return  task;
    }

    public boolean removeFood(String food){
        boolean task = false;

        //task = MongoDB.removeFood(food);

        return  task;
    }

    private int nutrientIndex(List<Nutrient> lista, String nutrientName){
        int i = 0;
        for(i = 0; i < lista.size(); i++){
            if(lista.get(i).getName().equals(nutrientName))
                return i;
        }
        return -1;
    }


    public boolean checkDietProgress (){
        List<EatenFood> eatenFoodsList;
        Food foodTarget;
        Diet dietTarget;
        int i, index, counterFails = 0, totalQuantityEatenFoods = 0;
        double[] total = new double[17];
        boolean[] isBelow = new boolean[17];
        Arrays.fill(isBelow, false); //set all the values of the bool array to 'false'. We suppose at the beginning all the values of the nutrients of the currentUSer EatenFood are higher than the corresponding nutrients values of the diet followed
        Arrays.fill(total, 0); //set all the values of the double array to 0

        //1) i retrive the eatenFood list of the current user

        //eatenFoodsList = lookUpStandardUserEatenFoods(); //trivial i have already the eaten food list (it is modified each time the user add a food to EFL)
        /**
         * the EatenFood has only the ID of the food, with no values of each nutrients.
         * We are required to make an accesso to MongoDB for each nutrientFood of the list to get/obtain the values of each (its )nutrient to compute the totals
         * Then we have to comapre the totals[] with the values of the CurrentDiet.
         */
        for (EatenFood ef : ((StandardUser) currentUser).getEatenFoods() ){
            foodTarget = lookUpFoodByID(ef.getFoodID());
            //i = 0;
            for(int j = 0; j < nutrients_names.length; j++){
                index = nutrientIndex(foodTarget.getNutrients(), nutrients_names[j]);
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
            //System.out.println(nutrients_names[j]+": EatenFood average : "+( total[j]/ (totalQuantityEatenFoods / 100) )+" / Diet: "+dietTarget.getNutrients().get(index).getQuantity() );
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
        List<EatenFood> eatenFoodsList; Food foodTarget; Diet dietTarget; HashMap<Nutrient, double[]> hashMap = new HashMap<>();
        int i, index, totalQuantityEatenFoods = 0;
        double[] total = new double[17]; boolean[] isBelow = new boolean[17];
        Arrays.fill(isBelow, false); //set all the values of the bool array to 'false'. We suppose at the beginning all the values of the nutrients of the currentUSer EatenFood are higher than the corresponding nutrients values of the diet followed
        Arrays.fill(total, 0); //set all the values of the double array to 0

        for (EatenFood ef : ((StandardUser) currentUser).getEatenFoods() ){
            foodTarget = lookUpFoodByID(ef.getFoodID());
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
