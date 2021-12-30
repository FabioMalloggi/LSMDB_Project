package it.unipi.dii.dietmanager.entities;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CLI {
    public Scanner scan;

    public CLI() {
        this.scan = new Scanner(System.in);
    }

    //public LogicManager controller; //<-- se lo definisco qui sarà u oggetto diverso rispetto a quello che definirò nel controller
    public String startWelcomeMenu(){
        String input;
        System.out.println("=======> Welcome to DietManager Application\n" +
                "==> Type \"S\" for Sign-in\n" +
                "==> Type \"R\" for Register\n" +
                "==> Type \"end\" for StandardUser");
        System.out.print("> ");
        input = scan.nextLine();
        /* alreay done in controller
        if(!input.equals("R") && !input.equals("S")) //fare qualcosa
            System.out.print("fare qualcosa con eccezione");
        else return input;
        */
        return input;
    }

    public String[] startSignInSubmission(){
        String[] input = new String[2];
        System.out.println("=======> Sign In:\n" +
                "==> insert username:");
        System.out.print("> ");
        input[0] = scan.nextLine();
        System.out.println("");
        System.out.println("==> insert password:");
        System.out.print("> ");
        input[1] = scan.nextLine();

        /*il check lo faccio qui in CLI o ritorno intanto le due stringhe al Controller e poi
        //controller.signIn(username, password);*/
        return input; //il controllo sarà fatto dal controller, nel caso verrà ri eseguito questo
    }

    public String startUsernameSubmission(){
        String username;
        System.out.println("=======> Registration:\n" +
                "==> insert username:");
        System.out.print("> ");
        username = scan.nextLine();
        return username; //the controller will perform the check if the username digitized  already exists
    }

    public String startPasswordSubmission(){
        String password;
        System.out.println("==> insert password:");
        System.out.print("> ");
        password = scan.nextLine();
        return password;
    }

    public String startFullNameSubmission(){
        String fullName;
        System.out.println("==> insert fullName:");
        //System.out.print("> ");
        fullName = scan.nextLine();
        return fullName;
    }

    public String startAgeSubmission(){
        String age;
        System.out.println("==> insert age:");
        //System.out.print("> ");
        age = scan.nextLine();
        return age;
    }

    public String startSexSubmission(){
        String sex;
        System.out.println("==> insert sex:");
        //System.out.print("> ");
        sex = scan.nextLine();
        return sex;
    }

    public String startCountrySubmission(){
        String country;
        System.out.println("==> insert country:");
        //System.out.print("> ");
        country = scan.nextLine();
        return country;
    }

    public String startUserTypeSubmission(){
        String input;
        System.out.println("==> insert userType:\"\n" +
                "==> Type \"SU\" for StandardUser\n" +
                "==> Type \"N\" for Nutritionist\n");
        System.out.print("> ");
        input = scan.nextLine();
        /*if(!input.equals("SU") && !input.equals("N")) //fare qualcosa
            System.out.print("fare qualcosa con eccezione");
        else return input;*/
        return input;
    }

    public void helpMenu(String username){
        String helpType;
        //String ret = "";
        //List<String> helpType;
        System.out.println("=======> Welcome "+username+"\n" +
                "=======> help\n" +
                "help food\t\t-> retrieve help commands on foods\n" +
                "help diet\t\t-> retrieve help commands on diets\n" +
                "help user\t\t-> retrieve help commands on users\n"+
                "exit \t\t\t-> log-out from application\n");
        /*System.out.print("> ");
        helpType = scan.nextLine();*/
        /*
        helpFood("Administrator");
        helpDiet("Nutritionist");
        helpUser("Administrator");
        return helpType;*/
    }

    public void helpFood (String typeOfuser /*better if passed an object User*/ ){
        String choose;
        System.out.println("====> help food\n" +
                "find -f \"foodName\"\t\t-> search food by name\n" +
                "find -ef \"category\"\t\t-> lookup most eaten food by category\n" +
                "find -ef -personal\t\t-> lookup your eaten foods list\n" +
                "add -ef \"foodname\"\t\t-> add food to your eaten foods list\n" +
                "rm -ef \t\"eatenFoodID\"\t\t-> remove eaten food from your eaten foods list\n");

        if(typeOfuser.equals("Administrator")){ //instanceof su user
            System.out.println("===> commands for administrators \n" +
                    "add -f \"newFoodName\"\t-> add food to catalog " +
                    "rmF -f \"foodName\"\t-> remove food from catalog");
        }

        /*
        System.out.print("> ");
        choose = scan.nextLine();
        return choose;*/

    }

    public void helpDiet(String typeOfuser /*better if passed an object User*/ ) {
        String choose;
        System.out.println("====> help diet\n" +
                "find -d -id \"dietID\"\t-> search diet by ID\n" +
                "find -d -name \"dietName\"\t-> search diets by names\n" +
                "find -d -nut\"username\" \t-> search diets by Nutritionist username\n" +
                "find -d -mf\t\t\t\t-> search most currently followed diet\n" +
                "find -d -mp\t\t\t\t-> search most popular diet\n" +
                "find -d -mc\t\t\t\t-> search most completed diet\n" +
                "find -d -r\t\t\t\t-> lookup recommended diet\n" +
                "find -d -mfnut \"username\" \t-> search most followed diet by Nutritionist username\n" +
                "follow \"dietID\"\t-> follow a diet\n" +
                "stop \"dietID\"\t-> stop a diet\n" +
                "check\t\t\t\t\t-> check your current diet against your eaten foods\n" +
                "find -d -c\t\t\t\t-> lookup your current diet\n" +
                "find -npn\t\t\t\t-> lookup most suggested nutrient for each nutritionist");

        if(typeOfuser.equals("Nutritionist")){
            System.out.println("==> commands for nutritionist\n" +
                    "add -d \"dietID\" \"dietName\"\t\t-> add diet\n"+
                    "rm -d \"dietID\"\t\t\t-> remove your diet");
        }

        /*
        System.out.print("> ");
        choose = scan.nextLine();
        return choose;*/
    }

    public void helpUser(String typeOfuser /*better if passed an object User*/ ) {
        String choose;
        System.out.println("==> help user\n" +
                "find -u -u \"username\"\t\t\t-> search user by username\n" +
                "find -u -c \"country\"\t\t\t-> search user by country\n" +
                "find -u -mpn\t\t\t\t-> lookup most popular nutritionist");

        if(typeOfuser.equals("Administrator")){
            System.out.println("===> commands for administrators \n" +
                    "rm -u \"username\"\t\t\t-> remove user by username");
        }

        /*
        System.out.print("> ");
        choose = scan.nextLine();
        return choose;*/
    }

    public String[] menuInsertNutrient(){
        String[] nutrientValues = new String[17];
        //0
        System.out.println("==> 0) insert Energy (mg in 100g of food):");
        System.out.print("> ");
        nutrientValues[0] = scan.nextLine();
        //1
        System.out.println("==> 1) insert Protein (mg in 100g of food):");
        System.out.print("> ");
        nutrientValues[1] = scan.nextLine();
        //2
        System.out.println("==> 2) insert Fat (g in 100g of food):");
        System.out.print("> ");
        nutrientValues[2] = scan.nextLine();
        //3
        System.out.println("==> 3) insert Carbohydrate (g in 100g of food):");
        System.out.print("> ");
        nutrientValues[3] = scan.nextLine();

        //4
        System.out.println("==> 4) insert Sugar (g in 100g of food):");
        System.out.print("> ");
        nutrientValues[4] = scan.nextLine();
        //5
        System.out.println("==> 5) insert Fiber (g in 100g of food):");
        System.out.print("> ");
        nutrientValues[5] = scan.nextLine();
        //6
        System.out.println("==> 6) insert Vitamin A (mcg in 100g of food):");
        System.out.print("> ");
        nutrientValues[6] = scan.nextLine();
        //7
        System.out.println("==> 7) insert Vitamin B6 (mg in 100g of food):");
        System.out.print("> ");
        nutrientValues[7] = scan.nextLine();

        //8
        System.out.println("==> 8) insert Vitamin B12 (mg in 100g of food):");
        System.out.print("> ");
        nutrientValues[8] = scan.nextLine();
        //9
        System.out.println("==> 9) insert Vitamin C (mg in 100g of food):");
        System.out.print("> ");
        nutrientValues[9] = scan.nextLine();
        //10
        System.out.println("==> 10) insert Vitamin E (mg in 100g of food):");
        System.out.print("> ");
        nutrientValues[10] = scan.nextLine();
        //11
        System.out.println("==> 11) insert Thiamine (mg in 100g of food):");
        System.out.print("> ");
        nutrientValues[11] = scan.nextLine();

        //12
        System.out.println("==> 12) insert Calcium (mg in 100g of food):");
        System.out.print("> ");
        nutrientValues[12] = scan.nextLine();
        //13
        System.out.println("==> 13) insert Magnesium (mg in 100g of food):");
        System.out.print("> ");
        nutrientValues[13] = scan.nextLine();
        //14
        System.out.println("==> 14) insert Manganese (mg in 100g of food):");
        System.out.print("> ");
        nutrientValues[14] = scan.nextLine();
        //15
        System.out.println("==> 15) insert Phosphor (mg in 100g of food):");
        System.out.print("> ");
        nutrientValues[15] = scan.nextLine();
        //16
        System.out.println("==> 16) insert Zinc (mg in 100g of food):");
        System.out.print("> ");
        nutrientValues[16] = scan.nextLine();

        return nutrientValues;
    }

    public void printDiet(Diet dietTarget){
        System.out.println("Diet, ID: "+dietTarget.getId()+", Name: "+dietTarget.getName()+", Nutritionist: "+dietTarget.getNutritionist());
        for (Nutrient n : dietTarget.getNutrients()) {
            System.out.println("Nutrient: " + n.getName() + ", quantity: " + n.getQuantity());
        }
    }

    public void printDiets(List<Diet> dietsTarget){
        for(Diet d: dietsTarget){
            /*System.out.println("Diet, ID: "+d.getId()+", Name: "+d.getName()+", Nutritionist: "+d.getNutritionist().getUsername());
            for (Nutrient n : d.getNutrients()){
                System.out.println("Nutrient: "+n.getName()+", quantity: "+n.getQuantity());
            }*/
            printDiet(d);
        }
    }

    public void printNutrientPerNutritinist(HashMap<Nutritionist, Nutrient> npn){
        Iterator hmIterator = npn.entrySet().iterator();
        while(hmIterator.hasNext()){
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            Nutrient n = ((Nutrient) mapElement.getValue());
            System.out.println(((Nutritionist)(mapElement.getKey())).getUsername()+": "+n.getName() );
        }
    }

    public void printFood(Food foodTarget){
        System.out.println("Food, name: "+foodTarget.getName());
        for (Nutrient n : foodTarget.getNutrients()) {
            System.out.println("Nutrient: " + n.getName() + ", quantity: " + n.getQuantity());
        }
    }

    public void printFoods(List<Food> foodsTarget){
        for(Food f: foodsTarget){
            /*System.out.println("Food, ID: "+foodTarget.getId());
            for (Nutrient n : foodTarget.getNutrients()) {
                System.out.println("Nutrient: " + n.getName() + ", quantity: " + n.getQuantity());
            }*/
            printFood(f);
        }
    }

    public void printUser(User user){
        System.out.println("Username: "+user.getUsername()+", Full Name: "+user.getUsername()+", Sex: "+user.getSex()+", Age:"+user.getAge()+", Country: "+user.getCountry());
        if(user instanceof StandardUser){

            //StandardUser su = (StandardUser) user;
            System.out.println("Current diet: "+((StandardUser) user).getCurrentDiet().getId());

            /*System.out.println("List of Eaten foods: ");
            for(EatenFood ef: ((StandardUser)user).getEatenFoods()){
                System.out.println("ID: "+ef.getId()+", Food ID: "+ef.getFoodID()+", Quantity"+ef.getQuantity()+",Timestamp: "+ef.getTimestamp().toString());
            }*/
        }

        else if(user instanceof Nutritionist){
            System.out.println("List of diets generated:");
            for(Diet d: ((Nutritionist)user).getDiets()){
                printDiet(d);
            }
        }
    }

    public void printEatenFood(User user){
        System.out.println("List of Eaten foods: ");
        for(EatenFood ef: ((StandardUser)user).getEatenFoods()){
            System.out.println("ID: "+ef.getId()+", Food ID: "+ef.getFoodID()+", Quantity"+ef.getQuantity()+",Timestamp: "+ef.getTimestamp().toString());
        }
    }

    public void printCheckDietProgress(boolean check){
        if(check)
            System.out.println("You have successfully completed the followed diet");
        else
            System.out.println("You don't have successfully completed the followed diet");
    }

    public void printDietProgress(HashMap<Nutrient, double[]> hashMap){
        Iterator hmIterator = hashMap.entrySet().iterator();
        while(hmIterator.hasNext()){
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            double[] value = ((double[]) mapElement.getValue());
            System.out.println(((Nutrient)(mapElement.getKey())).getName()+": EatenFood average : "+value[0]+" / Diet: "+value[1]); //getKey returns a generic Object. I can't call the methods of Nutritionist
        }
    }

    public String quantityOfEatenFood(){
        String in;
        System.out.println("insert the quantity (in g)");
        System.out.print("> ");
        in = scan.nextLine();
        return in;
    }


}
