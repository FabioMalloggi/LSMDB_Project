package it.unipi.dii.dietmanager;

import it.unipi.dii.dietmanager.entities.*;
import it.unipi.dii.dietmanager.persistence.*;

import java.util.HashMap;
import java.util.List;

public class LogicManager {
    User currentUser;

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

    //Read-Operations for Neo4J
    public Diet lookUpMostFollowedDiet() {
        Diet dietTarget = null;

        //dietTarget = Neo4J.lookUpMostFollowedDiet();

        return  dietTarget;
    }

    public Diet lookUpMostPopularDiet() {
        Diet dietTarget = null;

        //dietTarget = Neo4J.lookUpMostPopularDiet();

        return  dietTarget;
    }

    public Diet lookUpMostCompletedDiet() {
        Diet dietTarget = null;

        //dietTarget = Neo4J.lookUpMostCompletedDiet();

        return  dietTarget;
    }

    public Diet lookUpRecommendedDiet() {
        Diet dietTarget = null;

        //dietTarget = Neo4J.lookUpMostCompletedDiet();

        return  dietTarget;
    }

    public Diet lookUpMostFollowedDietByNutritionist(String username) {
        Diet dietTarget = null;

        //dietTarget = Neo4J.lookUpMostFollowedDietByNutritionist(username);

        return  dietTarget;
    }

    public Nutritionist lookUpMostPopularNutritionist(){
        Nutritionist nutritionistTarget = null;

        //nutritionistTarget = Neo4j.lookUpMostPopularNutritionist();

        return nutritionistTarget;
    }

    //Write-Operations
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

    public boolean addFoodToEatenFood(String name){
        boolean task = false;

        //task = MongoDB.addFoodToEatenFood(name, currentUser);

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

    // ****Missing checkDietProgress() ****
}
