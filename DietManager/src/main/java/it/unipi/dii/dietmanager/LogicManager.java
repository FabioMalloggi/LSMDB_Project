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

        //dietTarget = MongoDB.lookUpStandardUserCurrentDiet(); //****non serve passargli qualcosa alla funzione di MongoDB? come fa su MongoDB class a trovareil curretnUser ?

        return dietTarget;
    }

    public List<EatenFood> lookUpStandardUserEatenFoods() {
        List<EatenFood> eatenFoods = null;

        //eatenFoods = MongoDB.lookUpStandardUserEatenFoods();

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

    public Diet lookUpMostFollowedDietByNutritionist() {
        Diet dietTarget = null;

        //dietTarget = Neo4J.lookUpMostFollowedDietByNutritionist(); //the parameter???????

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

    public boolean followDiet(String id){
        boolean mongoDB = false, neo4J = false;

        //mongoDB = MongoDB.followDiet(id); // *** come può MongoDB (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
        if(mongoDB){
            //neo4J = Neo4J.followDiet(id); // *** come può Neo4J (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
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

    public boolean stopDiet(String id){
        boolean mongoDB = false, neo4J = false;

        //mongoDB = MongoDB.stopDiet(id); // *** come può MongoDB (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
        if(mongoDB){
            //neo4J = Neo4J.stopDiet(id); // *** come può Neo4J (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
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

    public boolean addFoodToEatenFood(String name){
        boolean task = false;

        //task = MongoDB.addFoodToEatenFood(name); // *** come può MongoDB (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser

        return  task;
    }


    public boolean removeEatenFood(String id){
        boolean task = false;

        //task = MongoDB.removeEatenFood(id); // *** come può MongoDB (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser

        return  task;
    }

    public boolean addDiet(Diet diet){
        boolean mongoDB = false, neo4J = false;

        //mongoDB = MongoDB.stopDiet(diet); // *** come può MongoDB (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
        if(mongoDB){
            //neo4J = Neo4J.stopDiet(diet); // *** come può Neo4J (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
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

    public boolean removeDiet(Diet diet){
        boolean mongoDB = false, neo4J = false;

        //mongoDB = MongoDB.stopDiet(diet); // *** come può MongoDB (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
        if(mongoDB){
            //neo4J = Neo4J.stopDiet(diet); // *** come può Neo4J (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
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

    public boolean removeUser(User user){
        boolean mongoDB = false, neo4J = false;

        //mongoDB = MongoDB.stopDiet(user); // *** come può MongoDB (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
        if(mongoDB){
            //neo4J = Neo4J.stopDiet(user); // *** come può Neo4J (Classe) avere il currentUser se è LogicalManager che crea MongoDB ? serve passare anche currentUser
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

    public boolean removeFood(Food food){
        boolean task = false;

        //task = MongoDB.removeFood(food);

        return  task;
    }

    // ****Missing checkDietProgress() ****
}
