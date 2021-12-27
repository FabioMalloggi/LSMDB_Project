package it.unipi.dii.dietmanager;

import it.unipi.dii.dietmanager.entities.*;

import java.util.*;

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
        /*String[] nameNutrients = {"energy", "protein", "fat", "carbohydrate", "sugar", "fiber", "vitaminA", "vitaminB6", "vitaminB12", "vitaminC", "vitaminE", "thiamin", "calcium", "magnesium", "manganese", "phosphoro", "zinc"};
        String[] unitForEachNutrients = {"_kcal", "_g", "_g", "_g", "_g", "_g", "_mcg", "_mg", "_mcg", "_mg", "_mg", "_mg", "_mg", "_mg", "_mg", "_mg", "_mg"};*/
        double [] doubleValues = new double[17];
        List<Nutrient> newList = new ArrayList<>();
        Nutrient tmp;
        Food foodCreated;
        for(int i = 0; i < nutrientsValues.length; i++){
            doubleValues[i] = Double.parseDouble(nutrientsValues[i]);
        }

        //generating the list of nutrients
        for (int i = 0; i < doubleValues.length; i++){
            tmp = new Nutrient(nutrients_names[i], nutrientsValues[i], doubleValues[i]);
            newList.add(tmp);
        }
        foodCreated = new Food(name, newList);
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
            tmp = new Nutrient(nutrients_names[i], nutrientsValues[i], doubleValues[i]);
            newList.add(tmp);
        }
        dietCreated = new Diet(name, newList, creator);
        return dietCreated;
    }

    public static void main(String[] args) {
        CLI cli = new CLI();
        LogicManager lM = new LogicManager();
        boolean notFinish = true; //used in the most outer while
        boolean step1 = false; //used in the Registration and Sign in Step
        boolean chekUserNotExist =false; //used to check if the username digitized is already existed, if it is, the user must insert a new user
        boolean step2 = false; //used in the rest of applicaiton
        boolean isLogged = false; //it has the same task of step1 var
        String username;
        String tmp = "";
        String[] signIn;
        List<Diet> dietsTarget;
        List<Food> foodsTarget;
        List<User> usersTarget;
        List<EatenFood> eatenFoodsList;
        User userTarget = null;
        Food foodTarget;
        Diet dietTarget;
        boolean checkOperation;
        HashMap<Nutritionist, Nutrient> npn;
        Scanner scanner = new Scanner(System.in);

        while(notFinish) {

            //first setp: Sign in or Register
            while (step1 != true) {
                tmp = cli.startWelcomeMenu();
                if (!tmp.equals("R") && !tmp.equals("S") &&!tmp.equals("end")) //fare qualcosa
                    System.out.println("fare qualcosa con eccezione");

                else if(tmp.equals("end")) { //only during this window the user can end/close the application
                    notFinish = false;
                    break;
                }

                else step1 = true;
            }

            //second step A: sign in
            while (isLogged == false && tmp.equals("S")) {

                signIn = cli.startSignInSubmission();
                //check if signIn[0] and signIn[1] is present or not in DB --> call the signIn(username, password) method of LogicalManagement
                //check if signIn[1] is the right password for the acocunt (this check is alredy done b the previouse check

                //if is all right, step = true; else remains false

                isLogged = true;


                //to test
                System.out.println("username: "+signIn[0]+", password: "+signIn[1]);
                step1 = true;
            }


            //second step B: register, the currentUser is still null. With this check(currentUser == null) we do not need to use further variable in th previous while condition
            while(isLogged== false && tmp.equals("R")){
                String[] newRegister = new String[7];
                chekUserNotExist = false;
                while(chekUserNotExist != true) {
                    newRegister[0] = cli.startUsernameSubmission();
                    //check if signIn[0] is present or not in DB --> call the lookUpUserByUsername method of LogicalManagement
                    //if is all right, chekUserNotExist = true; else remains false and launch an exception

                    //to test
                    chekUserNotExist = true;
                }
                //check even if the input of the user is exit, over the check if the username already exist
                newRegister[1] = cli.startPasswordSubmission(); //to implement this function and so on..
                newRegister[2] = cli.startFullNameSubmission();
                newRegister[3] = cli.startAgeSubmission();
                newRegister[4] = cli.startSexSubmission();
                newRegister[5] = cli.startCountrySubmission();
                newRegister[6] = cli.startUserTypeSubmission();


                if(newRegister[6].equals("SU")){
                    //the attribute user of LogicalManagement  = new StandardUser(newRegister[0],newRegister[2],newRegister[4],newRegister[1], newRegister[3], newRegister[5]); //check if the order is correct
                    System.out.println("StandardUSer correttamente generato!");
                    isLogged = true;
                }

                else if(newRegister[6].equals("N")){
                    //the attribute user of LogicalManagement  = new Nutritionist(newRegister[0],newRegister[2],newRegister[4],newRegister[1], newRegister[3], newRegister[5]); //check if the order is correct
                    System.out.println("Nutritionist correttamente generato!");
                    isLogged = true;
                }

                //else{ ...is the same
                else if (!newRegister[6].equals("SU") && !newRegister[6].equals("N")) { //fare qualcosa
                    System.out.println("fare qualcosa con eccezione");

                }

                //to test
                System.out.println("username: "+newRegister[0]+", password: "+newRegister[1]+", fullName: "+newRegister[2]+", Age: "+newRegister[3]+", Sex: "+newRegister[4]+", Country: "+newRegister[5]+", UserType: "+newRegister[6]);
                //step1 = true; old
            }


            //after the registration or sign-in

            while (isLogged == true){
                String helpType;
                String choose;
                String input;
                String[] tokens;
                //help menu

                //*** The real code when is all done ***
                //helpType = cli.helpMenu(lM.currentUser.getUserName());

                // to test
                System.out.println("Write help or write a command");
                System.out.print("> ");
                input = scanner.nextLine();
                tokens = input.split(" ");


                // general help
                if(input.equals("help")){
                    cli.helpMenu("tommasoNocchi");
                }

                //possible commands for help food / help diet / help nutritionist
                else if(input.equals("help food")) {
                    cli.helpFood("Administrator");
                }
                else if(input.equals("help diet")) {
                    cli.helpDiet("Nutritionist");
                }
                else if(input.equals("help user")) {
                    cli.helpUser("User");
                }

                //helpFood
                else if(tokens[0].equals("find") && tokens[1].equals("-f") && tokens.length == 3){
                    System.out.println("-> search food by name");
                    foodsTarget = lM.lookUpFoodByName(tokens[2]);

                    System.out.println("List of Food with the subString: "+tokens[2]);

                    if(foodsTarget != null) {
                        cli.printFoods(foodsTarget);
                    }
                    else{
                        System.err.println("Any food with that name");
                    }
                }
                else if(tokens[0].equals("find") && tokens[1].equals("-ef") && tokens.length==3){
                    if(tokens[2].equals("-personal")) {
                        System.out.println("-> lookup your eaten foods list");

                        eatenFoodsList = lM.lookUpStandardUserEatenFoods();

                        System.out.println("List of EatenFood : "+tokens[2]);
                                /*String result ="";
                                for(EatenFood ef: eatenFoodsList){
                                    result += "/"+ef.getId()+" "+ef.getFoodID()+" "+ef.getQuantity()+ " "+ef.getTimestamp()+"/";
                                }
                                System.out.println(result);*/
                    }
                    else {
                        System.out.println("-> lookup most eaten food by category");

                        foodsTarget = lM.lookUpMostEatenFoodByCategory(tokens[2]); //*** data una categoria non ne deve restituire uno ??***

                        System.out.println("List of most eaten food by category: "+tokens[2]);

                        if(foodsTarget != null) {
                            cli.printFoods(foodsTarget);
                        }
                        else{
                            System.err.println("Error in retrieving most eaten food by category ");
                        }
                    }
                }
                else if(tokens[0].equals("add") && tokens[1].equals("-ef") && tokens.length == 3){
                    System.out.println("-> add food to your eaten foods list");

                    checkOperation = false;
                    checkOperation = lM.addFoodToEatenFood(tokens[2]);

                    if(checkOperation){
                        System.out.println(tokens[2]+" correctly added in your EatenFoodList");
                    }
                    else {
                        System.err.println(tokens[2]+" not added in your EatenFoodList");
                    }

                }

                else if(tokens[0].equals("rm") && tokens[1].equals("-ef") && tokens.length == 3){
                    System.out.println("-> remove eaten food from your eaten foods list");

                    checkOperation = false;
                    checkOperation = lM.removeEatenFood(tokens[2]);

                    if(checkOperation){
                        System.out.println(tokens[2]+" correctly removed from your EatenFoodList");
                    }
                    else {
                        System.err.println(tokens[2]+" not removed from your EatenFoodList");
                    }
                }

                //commands for Administrator (Food)
                else if(tokens[0].equals("add") && tokens[1].equals("-f") && tokens.length == 3 /*&& instance of Administrator*/){
                    String[] chooseNutrients;
                    Food newFood;
                    checkOperation = false;
                    System.out.println("-> add food to catalog");
                    chooseNutrients = cli.menuInsertNutrient(); //work


                    //to test menuNut //work
                    String result = "";
                    for(String s: chooseNutrients){
                        result += "nutrient "+s;
                    }
                    System.out.println("result menuNutrient: "+result);

                    //***CREARE FUNZIONE CHE RESTITUISCE FOOD DATO token[2] e STRING[] di nutirenti.*****
                    //create a List of Nutrients with the chooseNutrients[] values
                    //create a food object;
                    newFood = generateFood(tokens[2], chooseNutrients);

                    checkOperation = lM.addFood(newFood);

                    if(checkOperation){
                        System.out.println(tokens[2]+" correctly inserted into catalog");
                    }
                    else {
                        System.err.println(tokens[2]+" not inserted into catalog");
                    }

                }

                else if(tokens[0].equals("rm") && tokens[1].equals("-f") && tokens.length == 3 /*&& instance of Administrator*/){
                    System.out.println("-> remove food from catalog");

                    checkOperation = false;
                    checkOperation = lM.removeFood(tokens[2]);

                    if(checkOperation){
                        System.out.println(tokens[2]+" correctly removed from catalog");
                    }
                    else {
                        System.err.println(tokens[2]+" not removed from catalog");
                    }
                }


                //helpDiet
                //check diet progress
                else if(tokens[0].equals("check")){
                    int i;
                    double[] total = new double[17];
                    boolean[] isBelow = new boolean[17];
                    Arrays.fill(isBelow, false); //set all the values of the bool array to 'false'. We suppose at the beginning all the values of the nutrients of the currentUSer EatenFood are higher than the corresponding nutrients values of the diet followed
                    Arrays.fill(total, 0); //set all the values of the double array to 0
                    /*if(lM.currentUser instanceof StandardUser){

                    }*/
                    System.out.println("checking...");
                    //first i retrive the eatenFood list of the current user
                    eatenFoodsList = lM.lookUpStandardUserEatenFoods();

                    /**
                     * the EatenFood has only the ID of the food, with no values of each nutrients.
                     * We are required to make an accesso to MongoDB for each nutrientFood of the list to get/obtain the values of each (its )nutrient to compute the totals
                     * Then we have to comapre the totals[] with the values of the CurrentDiet.
                    */
                    for (EatenFood ef : eatenFoodsList){
                        foodTarget = lM.lookUpFoodByID(ef.getFoodID());
                        i = 0;
                        for(Nutrient n : foodTarget.getNutrients()){
                            total[i] += n.getQuantity();
                            i++;
                        }
                    }

                    //then i retrive the diet of the current User
                    dietTarget = lM.lookUpStandardUserCurrentDiet();

                    i = 0;
                    for(Nutrient n: dietTarget.getNutrients()){
                        if(total[i] < n.getQuantity()) {
                            isBelow[i] = true;
                        }
                        System.out.println(nutrients_names[i]+": EatenFood totals : "+total[i]+" / Diet: "+n.getQuantity());
                    }

                    // ***compute the value of true and determine the decision {succeeded, failed} ***

                }

                // follow a diet
                else if(tokens[0].equals("follow") && tokens.length == 2){
                    System.out.println("start to follow, ID:" + tokens[1]);

                    checkOperation = false;
                    checkOperation = lM.followDiet(tokens[1]);

                    if(checkOperation){
                        System.out.println("Correctly followed diet with ID: "+tokens[1]);
                    }
                    else {
                        System.err.println("NOT correctly followed diet with ID: "+tokens[1]);
                    }
                }

                //stop a diet
                else if(tokens[0].equals("stop")){
                    System.out.println("stopped a diet, ID:" + tokens[1]);

                    checkOperation = false;
                    checkOperation = lM.stopDiet(tokens[1]); //***lM.stopDiet must call the check before complete the unfollowing***

                    if(checkOperation){
                        System.out.println("Correctly stopped diet with ID: "+tokens[1]);
                    }
                    else {
                        System.err.println("NOT correctly stopped diet with ID: "+tokens[1]);
                    }
                }

                // commands find -d -*
                else if(tokens[0].equals("find") && tokens[1].equals("-d") && tokens.length >= 3){

                    if(tokens[2].equals("-id") && tokens.length == 4){
                        System.out.println("-> search diet by ID");

                        dietTarget = lM.lookUpDietByID(tokens[3]);
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Any diet with that ID");
                        }
                    }
                    else if(tokens[2].equals("-name") && tokens.length == 4){
                        System.out.println("-> search diets by names");

                        dietsTarget = lM.lookUpDietByName(tokens[3]);
                        if(dietsTarget != null){
                            cli.printDiets(dietsTarget);
                        }
                        else{
                            System.err.println("Any diets with that name(subName)");
                        }
                    }
                    else if(tokens[2].equals("-nut") && tokens.length == 4){
                        System.out.println("-> search diets by Nutritionist username");

                        dietsTarget = lM.lookUpDietByNutritionist(tokens[3]);
                        if(dietsTarget != null){
                            cli.printDiets(dietsTarget);
                        }
                        else{
                            System.err.println("Any diets with that nutritionist");
                        }
                    }
                    else if(tokens[2].equals("-mf")){
                        System.out.println("-> search most currently followed diet");

                        dietTarget = lM.lookUpMostFollowedDiet();
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Error in search the most followed diet");
                        }
                    }
                    else if(tokens[2].equals("-mp")){
                        System.out.println("-> search most popular diet");

                        dietTarget = lM.lookUpMostPopularDiet();
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Error in search the most popular diet");
                        }
                    }
                    else if(tokens[2].equals("-mc")){
                        System.out.println("-> search most completed diet");

                        dietTarget = lM.lookUpMostCompletedDiet();
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Error in search the most completed diet");
                        }
                    }
                    else if(tokens[2].equals("-r")){
                        System.out.println("-> lookup recommended diet");

                        dietTarget = lM.lookUpRecommendedDiet();
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Error in search the recommended diet");
                        }
                    }
                    else if(tokens[2].equals("-mfnut") && tokens.length ==4){
                        System.out.println("-> search most followed diet by Nutritionist username");

                        dietTarget = lM.lookUpMostFollowedDietByNutritionist(tokens[3]);
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Error in search the most followed diet");
                        }
                    }
                    else if(tokens[2].equals("-c")){
                        System.out.println("-> lookup your current diet");

                        //if utente è uno standardUser
                        dietTarget = lM.lookUpStandardUserCurrentDiet();
                        if(dietTarget != null) {
                            cli.printDiet(dietTarget);
                        }
                        else{
                            System.err.println("Error in search the most followed diet");
                        }
                    }

                }

                else if(tokens[0].equals("find") && tokens[1].equals("-npn")){
                    System.out.println("-> lookup most suggested nutrient for each nutritionist");

                    npn = lM.lookUpMostSuggestedNutrientForEachNutritionist();
                    if(npn != null){
                        cli.printNutrientPerNutritinist(npn);
                    }
                    else{
                        System.err.println("Error in search the suggested nutrient for each nutritionist");
                    }
                }

                //commands for Nutritionist (Diet)
                else if(tokens[0].equals("add") && tokens[1].equals("-d") && tokens.length == 3 /*&& instance of Nutritionist*/){
                    String[] chooseNutrients;
                    Diet newDiet;
                    System.out.println("-> add diet: "+tokens[2]);
                    chooseNutrients = cli.menuInsertNutrient(); //work

                    //to test menuNut //work
                    String result = "";
                    for(String s: chooseNutrients){
                        result += "nutrient "+s;
                    }
                    System.out.println("result menuNutrient: "+result);

                    //***CREARE FUNZIONE CHE RESTITUISCE Diet DATO token[2] e STRING[] di nutirenti.*****
                    //create a List of Nutrients with the chooseNutrients[] values
                    //create a diet object;
                    //call addDiet(Diet diet)

                    newDiet = generateDiet(tokens[2], chooseNutrients, ((Nutritionist) lM.currentUser));

                    checkOperation = lM.addDiet(newDiet);

                    if(checkOperation){
                        System.out.println(tokens[2]+" diet correctly inserted.");
                    }
                    else {
                        System.err.println(tokens[2]+" diet not inserted.");
                    }
                }

                else if(tokens[0].equals("rm") && tokens[1].equals("-d") && tokens.length == 3 /*&& instance of Nutritionist*/){
                    System.out.println("-> remove diet");

                    checkOperation = false;
                    checkOperation = lM.removeDiet(tokens[2]);

                    if(checkOperation){
                        System.out.println(tokens[2]+" correctly removed from diet");
                    }
                    else {
                        System.err.println("Diet with ID: "+tokens[2]+" not removed");
                    }
                }


                //helpUser
                else if(tokens[0].equals("find") && tokens[1].equals("-u")){
                    if(tokens[2].equals("-u") && tokens.length == 4){
                        System.out.println("-> search user by username");
                        userTarget = lM.lookUpUserByUsername(tokens[3]);
                        cli.printUser(userTarget);
                    }
                    else if(tokens[2].equals("-c") && tokens.length == 4){
                        System.out.println("-> search user by country");
                        userTarget = lM.lookUpUserByCountry(tokens[3]);
                        cli.printUser(userTarget);
                    }
                    else if(tokens[2].equals("-mpn")){
                        System.out.println("-> lookup most popular nutritionist");
                        //(Nutritionist) userTarget = lM.lookUpMostPopularNutritionist(); //*** non funge il cast***
                        cli.printUser(userTarget);
                    }
                }

                //commands for administrator(User)
                else if (tokens[0].equals("rm") && tokens[1].equals("-u") && tokens.length == 3 /*&& instance of Administrator*/ ){

                    System.out.println("-> remove user, with username: "+tokens[2]);

                    checkOperation = false;
                    checkOperation = lM.removeUser(tokens[2]);

                    if(checkOperation){
                        System.out.println(tokens[2]+" correctly removed ");
                    }
                    else {
                        System.err.println("User with username: "+tokens[2]+" not removed");
                    }
                }


                else if(input.equals("exit")){
                    isLogged = false;
                    step1 = false;
                }

                //error
                else{
                    System.out.println("Eccezione o avvismo di sbagliato comando");
                }

            }// while (isLogged)
        }
    }
}
