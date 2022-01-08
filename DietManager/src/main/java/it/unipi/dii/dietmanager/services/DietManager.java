package it.unipi.dii.dietmanager.services;

import it.unipi.dii.dietmanager.client.CLI;
import it.unipi.dii.dietmanager.entities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class DietManager {
    private static final String[] nutrients_names =
            {/*0*/"Energy",/*1*/"Protein",/*2*/"Fat",/*3*/"Carb",/*4*/"Sugar",
                    /*5*/"Fiber",/*6*/"VitA",/*7*/"VitB6",/*8*/"VitB12",/*9*/"VitC",/*10*/"VitE",
                    /*11*/"Thiamin",/*12*/"Calcium",/*13*/"Magnesium",/*14*/"Manganese",/*15*/"Phosphorus",
                    /*16*/"Zinc"};
    private static final String[] nutrients_units =
            {/*0*/"KCAL",/*1*/"G",/*2*/"G",/*3*/"G",/*4*/"G",
                    /*5*/"G",/*6*/"UG",/*7*/"MG",/*8*/"UG",/*9*/"MG",/*10*/"MG",
                    /*11*/"MG",/*12*/"MG",/*13*/"MG",/*14*/"MG",/*15*/"MG",
                    /*16*/"MG"};
    public static Food generateFood(String name, String[] nutrientsValues){
        double [] doubleValues = new double[17];
        List<Nutrient> newList = new ArrayList<>();
        Nutrient tmp;
        Food foodCreated;
        for(int i = 0; i < nutrientsValues.length; i++){
            doubleValues[i] = Double.parseDouble(nutrientsValues[i]);
        }

        //generating the list of nutrients
        for (int i = 0; i < doubleValues.length; i++){
            if(doubleValues[i] > 0) {
                tmp = new Nutrient(nutrients_names[i], nutrients_units[i], doubleValues[i]);
                newList.add(tmp);
            }
        }
        foodCreated = new Food(name, "", newList, 0);
        return foodCreated;
    }

    public static Diet generateDiet(String name, String[] nutrientsValues, Nutritionist creator){
        double [] doubleValues = new double[17];
        List<Nutrient> newList = new ArrayList<>();
        Nutrient tmp;
        Diet dietCreated;
        for(int i = 0; i < nutrientsValues.length; i++){
            doubleValues[i] = Double.parseDouble(nutrientsValues[i]);
        }

        //generating the list of nutrients
        for (int i = 0; i < doubleValues.length; i++){
            if(doubleValues[i] > 0) {
                tmp = new Nutrient(nutrients_names[i], nutrients_units[i], doubleValues[i]);
                newList.add(tmp);
            }
        }
        dietCreated = new Diet(name, newList, creator.getUsername());

        return dietCreated;
    }



    public static void main(String[] args) {
        CLI cli = new CLI();
        LogicManager logicManager = new LogicManager();
        boolean notFinish = true; //used in the most outer while
        boolean step1 = false; //used in the Registration and Sign in Step
        boolean chekUserNotExist =false; //used to check if the username digitized is already existed, if it is, the user must insert a new user
        boolean isLogged = false; //it has the same task of step1 var
        String tmp = "";
        String[] signIn;
        List<Diet> dietsTarget; List<Food> foodsTarget; List<User> usersTarget; List<EatenFood> eatenFoodsList;
        User userTarget = null;
        Food foodTarget; Nutritionist nutritionistTarget; Diet dietTarget;
        boolean checkOperation;
        HashMap<Nutritionist, Nutrient> nutrientPerNutritionist;
        Scanner scanner = new Scanner(System.in);

        while(notFinish) {

            //first setp: Sign in or Register
            while (step1 != true) {
                tmp = cli.startWelcomeMenu();
                if (!tmp.equals("R") && !tmp.equals("S") &&!tmp.equals("end")) //fare qualcosa
                    cli.generalPrint("Choice not allowed");

                else if(tmp.equals("end")) { //only during this window the user can end/close the application
                    notFinish = false;
                    break;
                }

                else step1 = true;
            }

            //second step A: sign in
            if( tmp.equals("S")) {

                signIn = cli.startSignInSubmission();
                isLogged = logicManager.signIn(signIn[0], signIn[1]);

                //to test
                System.out.println("username: "+signIn[0]+", password: "+signIn[1]);
            }


            //second step B: register, the currentUser is still null. With this check(currentUser == null) we do not need to use further variable in th previous while condition
            if( tmp.equals("R")){
                String[] newRegister = new String[7];
                chekUserNotExist = false;
                while(chekUserNotExist != true) {
                    newRegister[0] = cli.startUsernameSubmission();

                    userTarget = logicManager.lookUpUserByUsername(newRegister[0]);
                    if(userTarget == null) {chekUserNotExist = true;}
                    else
                        cli.generalPrint("Username already exists");
                }
                //check even if the input of the user is exit, over the check if the username already exist
                newRegister[1] = cli.startPasswordSubmission();
                newRegister[2] = cli.startFullNameSubmission();
                newRegister[3] = cli.startAgeSubmission();
                newRegister[4] = cli.startSexSubmission();
                newRegister[5] = cli.startCountrySubmission();
                newRegister[6] = cli.startUserTypeSubmission();

                boolean isNumber = true;

                for(int i=0; i< newRegister[3].length(); i++){
                    if(!Character.isDigit(newRegister[3].charAt(i)))
                        isNumber = false;
                }

                // I check age and sex
                if((!newRegister[4].equals("M") && !newRegister[4].equals("F")) || !isNumber || Integer.parseInt(newRegister[3]) <= 0){
                    cli.generalPrint("At least one of inserted parameters is not correct");
                    continue;
                }

                if(newRegister[6].equals("SU")){
                    userTarget = new StandardUser(newRegister[0],newRegister[2],newRegister[4],newRegister[1],Integer.parseInt(newRegister[3]),newRegister[5]);
                    if(logicManager.addUser(userTarget)){
                        logicManager.currentUser = userTarget;
                        cli.generalPrint("StandardUser correctly registrated ");
                        isLogged = true;
                    }

                }

                else if(newRegister[6].equals("N")){
                    userTarget = new Nutritionist(newRegister[0],newRegister[2],newRegister[4],newRegister[1],Integer.parseInt(newRegister[3]),newRegister[5]);
                    if(logicManager.addUser(userTarget)){
                        logicManager.currentUser = userTarget;
                        cli.generalPrint("Nutritionist correttamente generato!");
                        isLogged = true;
                    }
                }
                else if (!newRegister[6].equals("SU") && !newRegister[6].equals("N")) {
                    cli.generalPrint("The user type is not valid");
                }

                //to test
                System.out.println("username: "+newRegister[0]+", password: "+newRegister[1]+", fullName: "+newRegister[2]+", Age: "+newRegister[3]+", Sex: "+newRegister[4]+", Country: "+newRegister[5]+", UserType: "+newRegister[6]);
                //step1 = true; old
            }


            //after the registration or sign-in

            while (isLogged == true){
                String helpType;
                String choose;
                String input, input2;
                String[] tokens;
                //help menu

                //*** The real code when is all done ***
                //helpType = cli.helpMenu(logicManager.currentUser.getUserName());

                // to test
                cli.generalPrint("Write help or write a command");
                cli.generalPrintInLine("> ");
                input = scanner.nextLine();
                tokens = input.split(" ");


                // general help
                if(input.equals("help")){
                    cli.helpMenu(logicManager.currentUser.getUsername());
                }

                //possible commands for help food / help diet / help nutritionist
                else if(input.equals("help food")) {
                    cli.helpFood(logicManager.currentUser);
                }
                else if(input.equals("help diet")) {
                    cli.helpDiet(logicManager.currentUser);
                }
                else if(input.equals("help user")) {
                    cli.helpUser(logicManager.currentUser);
                }

                //helpFood
                else if(tokens[0].equals("find") && tokens[1].equals("-f") && tokens.length >= 3 && !(logicManager.currentUser instanceof Nutritionist)){ //not nutritionist
                    cli.generalPrint("-> search food by name"); //System.out.println("-> search food by name");
                    String foodName = tokens[2];
                    for(int i = 3; i < tokens.length; i++){ //enter only if the foodName has some keyboard space
                        foodName += " "+tokens[i];
                    }
                    foodsTarget = logicManager.lookUpFoodByName(foodName);

                    cli.generalPrint("List of Food with the subString: "+tokens[2]); //System.out.println("List of Food with the subString: "+tokens[2]);
                    if(foodsTarget != null) {
                        cli.printFoods(foodsTarget);
                    }
                    else{
                        System.err.println("Any food with that name");
                        System.out.flush();
                        System.err.flush();
                    }
                }
                else if(tokens[0].equals("find") && tokens[1].equals("-ef") && tokens.length>=3){
                    if(tokens.length == 3 && tokens[2].equals("-personal") && (logicManager.currentUser instanceof StandardUser)) {
                        cli.generalPrint("-> lookup your eaten foods list"); // System.out.println("-> lookup your eaten foods list");

                        cli.generalPrint("List of EatenFood : "+tokens[2]); // System.out.println("List of EatenFood : "+tokens[2]);
                        cli.printEatenFood(logicManager.currentUser);
                    }
                    else{
                        cli.generalPrint("-> lookup most eaten food by category"); // System.out.println("-> lookup most eaten food by category");

                        String categoryName = tokens[2];
                        for(int i = 3; i < tokens.length; i++){ //enter only if the foodName has some keyboard space
                            categoryName += " "+tokens[i];
                        }
                        foodTarget = logicManager.lookUpMostEatenFoodByCategory(categoryName); //*** data una categoria non ne deve restituire uno ??***

                        cli.generalPrint("List of most eaten food by category: "+categoryName); // System.out.println("List of most eaten food by category: "+tokens[2]);
                        if(foodTarget != null) {
                            cli.printFood(foodTarget);
                        }
                        else{
                            System.err.println("Error in retrieving most eaten food by category ");
                        }
                    }
                }
                else if(tokens[0].equals("add") && tokens[1].equals("-ef") && tokens.length >= 3 && (logicManager.currentUser instanceof StandardUser)){
                    if(((StandardUser) logicManager.currentUser).getCurrentDiet() == null) {
                        System.err.println("NOT allowed: you must follow a diet first");
                        continue;
                    }
                    String foodName;
                    cli.generalPrint("-> add food to your eaten foods list"); //System.out.println("-> add food to your eaten foods list");

                    /** CHIEDERE LA QUANTITA (in grammi)***/
                    input2 = cli.quantityOfEatenFood();
                    checkOperation = false;

                    foodName = tokens[2];
                    for(int i = 3; i < tokens.length; i++){ //enter only if the foodName has some keyboard space
                        foodName += " "+tokens[i];
                    }
                    checkOperation = logicManager.addFoodToEatenFoods(foodName, Integer.parseInt(input2));

                    if(checkOperation){

                        cli.generalPrint(tokens[2]+" correctly added in your EatenFoodList"); //System.out.println(tokens[2]+" correctly added in your EatenFoodList");
                    }
                    else {
                        System.err.println(tokens[2]+" not added in your EatenFoodList");
                        System.out.flush();
                        System.err.flush();
                    }

                }

                else if(tokens[0].equals("rm") && tokens[1].equals("-ef") && tokens.length == 3 && (logicManager.currentUser instanceof StandardUser)){
                    cli.generalPrint("-> remove eaten food from your eaten foods list"); //System.out.println("-> remove eaten food from your eaten foods list");

                    checkOperation = logicManager.removeEatenFood(tokens[2]);
                    checkOperation = logicManager.removeEatenFood(tokens[2]);

                    if(checkOperation){

                        cli.generalPrint(tokens[2]+" correctly removed from your EatenFoodList"); // System.out.println(tokens[2]+" correctly removed from your EatenFoodList");
                    }
                    else {
                        System.err.println(tokens[2]+" not removed from your EatenFoodList");
                    }
                }

                //commands for Administrator (Food)
                else if(tokens[0].equals("add") && tokens[1].equals("-f") && tokens.length >= 3 && (logicManager.currentUser instanceof Administrator)){
                    String[] chooseNutrients; String foodName;
                    Food newFood;
                    checkOperation = false;
                    cli.generalPrint("-> add food to catalog"); //System.out.println("-> add food to catalog");
                    chooseNutrients = cli.menuInsertNutrient(); //work


                    foodName = tokens[2];
                    for(int i = 3; i < tokens.length; i++){ //enter only if the foodName has some keyboard space
                        foodName += " "+tokens[i];
                    }

                    //to test  //work
                    String result = "";
                    for(String string: chooseNutrients){
                        result += "nutrient "+string;
                    }

                    newFood = generateFood(foodName, chooseNutrients);
                    checkOperation = logicManager.addFood(newFood);

                    if(checkOperation){

                        cli.generalPrint(foodName+" correctly inserted into catalog");
                    }
                    else {
                        System.err.println(foodName+" not inserted into catalog");
                    }

                }

                else if(tokens[0].equals("rm") && tokens[1].equals("-f") && tokens.length == 3 && (logicManager.currentUser instanceof Administrator)){
                    cli.generalPrint("-> remove food from catalog"); //System.out.println("-> remove food from catalog");

                    checkOperation = false;
                    checkOperation = logicManager.removeFood(tokens[2]);

                    if(checkOperation){
                        cli.generalPrint(tokens[2]+" correctly removed from catalog"); //System.out.println(tokens[2]+" correctly removed from catalog");
                    }
                    else {
                        System.err.println(tokens[2]+" not removed from catalog");
                    }
                }


                //helpDiet
                //check diet progress
                else if(tokens[0].equals("check") && (logicManager.currentUser instanceof StandardUser)){
                    boolean check;
                    /*if(logicManager.currentUser instanceof StandardUser){
                        //qui ci va il codice di questo else if
                    }*/
                    cli.generalPrint("checking..."); // System.out.println("checking...");
                    check = logicManager.checkDietProgress();
                    cli.printCheckDietProgress(check);
                }

                // follow a diet
                else if(tokens[0].equals("follow") && tokens.length == 2 && (logicManager.currentUser instanceof StandardUser)){

                    cli.generalPrint("start to follow, ID:" + tokens[1]); // System.out.println("start to follow, ID:" + tokens[1]);

                    checkOperation = false;
                    checkOperation = logicManager.followDiet(tokens[1]);

                    if(checkOperation){
                        cli.generalPrint("Correctly followed diet with ID: "+tokens[1]); //System.out.println("Correctly followed diet with ID: "+tokens[1]);
                    }
                    else {
                        System.err.println("NOT correctly followed diet with ID: "+tokens[1]);
                    }
                }

                //stop a diet
                else if(tokens[0].equals("stop") && (logicManager.currentUser instanceof StandardUser)){
                    cli.generalPrint("stopped the current diet"); //System.out.println("stopped the current diet");

                    checkOperation = false;
                    checkOperation = logicManager.stopDiet();

                    if(checkOperation){
                        cli.generalPrint("Correctly stopped the current diet"); // System.out.println("Correctly stopped the current diet");
                    }
                    else {
                        System.err.println("NOT correctly stopped the current diet");
                    }
                }

                //unfollow a diet
                else if(tokens[0].equals("unfollow") && (logicManager.currentUser instanceof StandardUser)){
                    cli.generalPrint("unfollowed the current diet"); //System.out.println("unfollowed the current diet");

                    checkOperation = false;
                    checkOperation = logicManager.unfollowDiet();

                    if(checkOperation){
                        cli.generalPrint("Correctly unfollowed the current diet"); // System.out.println("Correctly unfollowed the current diet");
                    }
                    else {
                        System.err.println("NOT correctly unfollowed the current diet");
                    }
                }

                // commands find -d -*
                else if(tokens[0].equals("find") && tokens[1].equals("-d") && tokens.length >= 3){

                    if(tokens[2].equals("-id") && tokens.length == 4){
                        cli.generalPrint("-> search diet by ID"); //System.out.println("-> search diet by ID");

                        dietTarget = logicManager.lookUpDietByID(tokens[3]);
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Any diet with that ID");
                        }
                    }
                    else if(tokens[2].equals("-name") && tokens.length >= 4){
                        cli.generalPrint("-> search diets by names"); //System.out.println("-> search diets by names");

                        String dietName="";
                        for(int i = 3; i < tokens.length; i++){ //enter only if the foodName has some keyboard space
                            dietName += " "+tokens[i];
                        }

                        dietsTarget = logicManager.lookUpDietByName(tokens[3]);
                        if(dietsTarget != null){
                            cli.printDiets(dietsTarget);
                        }
                        else{
                            System.err.println("Any diets with that name(subName)");
                        }
                    }
                    else if(tokens[2].equals("-nut") && tokens.length == 4){
                        cli.generalPrint("-> search diets by Nutritionist username"); //System.out.println("-> search diets by Nutritionist username");

                        dietsTarget = logicManager.lookUpDietByNutritionist(tokens[3]);
                        if(dietsTarget != null){
                            cli.printDiets(dietsTarget);
                        }
                        else{
                            System.err.println("Any diets with that nutritionist");
                        }
                    }
                    else if(tokens[2].equals("-mf")){
                        cli.generalPrint("-> search most currently followed diet"); //System.out.println("-> search most currently followed diet");

                        dietTarget = logicManager.lookUpMostFollowedDiet();
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Error in search the most followed diet");
                        }
                    }
                    else if(tokens[2].equals("-mp")){
                        cli.generalPrint("-> search most popular diet"); //System.out.println("-> search most popular diet");

                        dietTarget = logicManager.lookUpMostPopularDiet();
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Error in search the most popular diet");
                        }
                    }
                    else if(tokens[2].equals("-mc")){
                        cli.generalPrint("-> search most completed diet"); // System.out.println("-> search most completed diet");

                        dietTarget = logicManager.lookUpMostSucceededDiet();
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Error in search the most completed diet");
                        }
                    }
                    else if(tokens[2].equals("-r")){
                        cli.generalPrint("-> lookup most recommended diet"); //System.out.println("-> lookup most recommended diet");

                        dietTarget = logicManager.lookUpRecommendedDiet();
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Error in search the recommended diet");
                        }
                    }
                    else if(tokens[2].equals("-mfnut") && tokens.length ==4){
                        cli.generalPrint("-> search most followed diet by Nutritionist username"); //System.out.println("-> search most followed diet by Nutritionist username");

                        dietTarget = logicManager.lookUpMostFollowedDietByNutritionist(tokens[3]);
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Error in search the most followed diet");
                        }
                    }
                    else if(tokens[2].equals("-c")){
                        cli.generalPrint("-> lookup your current diet"); //System.out.println("-> lookup your current diet");

                        //if utente è uno standardUser
                        dietTarget = logicManager.lookUpStandardUserCurrentDiet();
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Error in search the most followed diet");
                        }
                    }

                }

                else if(tokens[0].equals("find") && tokens[1].equals("-nutrientPerNutritionist")){
                    cli.generalPrint("-> lookup most suggested nutrient for each nutritionist"); //System.out.println("-> lookup most suggested nutrient for each nutritionist");

                    nutrientPerNutritionist = logicManager.lookUpMostSuggestedNutrientForEachNutritionist();
                    if(nutrientPerNutritionist != null){
                        cli.printNutrientPerNutritinist(nutrientPerNutritionist);
                    }
                    else{
                        System.err.println("Error in search the suggested nutrient for each nutritionist");
                    }
                }

                //commands for Nutritionist (Diet)
                else if(tokens[0].equals("add") && tokens[1].equals("-d") && tokens.length >= 3 && (logicManager.currentUser instanceof Nutritionist)){
                    String[] chooseNutrients; String dietName;
                    Diet newDiet;
                    cli.generalPrint("-> add diet: "+tokens[2]); //System.out.println("-> add diet: "+tokens[2]);
                    chooseNutrients = cli.menuInsertNutrient(); //work

                    dietName = tokens[2];
                    for(int i = 3; i < tokens.length; i++){ //enter only if the foodName has some keyboard space
                        dietName += " "+tokens[i];
                    }

                    //to test menuNut //work
                    String result = "";
                    for(String string: chooseNutrients){
                        result += "nutrient "+string;
                    }
                    cli.generalPrint("result menuNutrient: "+result); //System.out.println("result menuNutrient: "+result);

                    //create a List of Nutrients with the chooseNutrients[] values
                    //create a diet object;
                    //call addDiet(Diet diet)

                    newDiet = generateDiet(tokens[2], chooseNutrients, ((Nutritionist) logicManager.currentUser));

                    checkOperation = logicManager.addDiet(newDiet);

                    if(checkOperation){
                        cli.generalPrint(dietName+" diet correctly inserted."); //System.out.println(tokens[2]+" diet correctly inserted.");
                    }
                    else {
                        System.err.println(dietName+" diet not inserted.");
                    }
                }

                else if(tokens[0].equals("rm") && tokens[1].equals("-d") && tokens.length == 3 && (logicManager.currentUser instanceof Nutritionist)){
                    cli.generalPrint("-> remove diet"); //System.out.println("-> remove diet");

                    checkOperation = false;
                    checkOperation = logicManager.removeDiet(tokens[2]);

                    if(checkOperation){
                        cli.generalPrint(tokens[2]+" correctly removed from diet"); //System.out.println(tokens[2]+" correctly removed from diet");
                    }
                    else {
                        System.err.println("Diet with ID: "+tokens[2]+" not removed");
                    }
                }


                //helpUser
                else if(tokens[0].equals("find") && tokens[1].equals("-u")){
                    if(tokens[2].equals("-u") && tokens.length == 4){
                        cli.generalPrint("-> search user by username"); // System.out.println("-> search user by username");
                        userTarget = logicManager.lookUpUserByUsername(tokens[3]);
                        cli.printUser(userTarget);
                    }
                    else if(tokens[2].equals("-c") && tokens.length == 4){ /** in LM a seconda del tipo di utente che la chiama, sarà chiamata una delle 2 dunzionipresenti in MongoDB*/
                        cli.generalPrint("-> search user by country"); //System.out.println("-> search user by country");
                        usersTarget = logicManager.lookUpUserByCountry(tokens[3]);
                        cli.printUsers(usersTarget);
                    }
                    else if(tokens[2].equals("-mpn")){
                        cli.generalPrint("-> lookup most popular nutritionist"); //System.out.println("-> lookup most popular nutritionist");
                        //((Nutritionist) userTarget) = logicManager.lookUpMostPopularNutritionist(); //*** non funge il cast***
                        nutritionistTarget = logicManager.lookUpMostPopularNutritionist();
                        cli.printUser(userTarget);
                    }
                }

                //commands for administrator(User)
                else if (tokens[0].equals("rm") && tokens[1].equals("-u") && tokens.length == 3 && (logicManager.currentUser instanceof Administrator) ){
                    cli.generalPrint("-> remove user, with username: "+tokens[2]); //System.out.println("-> remove user, with username: "+tokens[2]);

                    checkOperation = false;
                    checkOperation = logicManager.removeUser(tokens[2]);

                    if(checkOperation){
                        cli.generalPrint(tokens[2]+" correctly removed "); //System.out.println(tokens[2]+" correctly removed ");
                    }
                    else {
                        System.err.println("User with username: "+tokens[2]+" not removed");
                    }
                }


                else if(input.equals("exit")){
                    if(logicManager.currentUser instanceof StandardUser){
                        logicManager.addEatenFoodToMongo();
                    }
                    isLogged = false;
                    step1 = false;
                }

                else if(input.equals("\n")){
                    continue;
                }

                //error
                else{
                    cli.generalPrint("Wrong or incomplete command");
                }
            }
        }
    }
}
