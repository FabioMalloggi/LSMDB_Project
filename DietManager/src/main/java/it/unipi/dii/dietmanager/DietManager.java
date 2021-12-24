package it.unipi.dii.dietmanager;

import it.unipi.dii.dietmanager.entities.CLI;

public class DietManager {

    public static void main(String[] args) {
        CLI cli = new CLI();
        //LocalManagement lM = new LocalManagement();
        boolean notFinish = true; //used in the most outer while
        boolean step1 = false; //used in the Registration and Sign in Step
        boolean chekUserNotExist =false; //used to check if the username digitized is already existed, if it is, the user must insert a new user
        boolean step2 = false; //used in the rest of applicaiton
        String username;
        String tmp = "";
        String[] signIn;

        while(notFinish) {

            //first setp: Sign in or Register
            while (step1 != true) {
                tmp = cli.startWelcomeMenu();
                if (!tmp.equals("R") && !tmp.equals("S")) //fare qualcosa
                    System.out.println("fare qualcosa con eccezione");
                else step1 = true;
            }

            step1 = false;
            //second step A: sign in
            while (step1 != true && tmp.equals("S")) {

                signIn = cli.startSignInSubmission();
                //check if signIn[0] and signIn[1] is present or not in DB --> call the signIn(username, password) method of LogicalManagement
                //check if signIn[1] is the right password for the acocunt (this check is alredy done b the previouse check

                //if is all right, step = true; else remains false



                //to test
                System.out.println("username: "+signIn[0]+", password: "+signIn[1]);
                step1 = true;
            }


            //second step B: register, the currentUser is still null. With this check(currentUser == null) we do not need to use further variable in th previous while condition
            step1 = false;
            while(step1 != true && tmp.equals("R")){
                String[] newRegister = new String[7];
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
                }

                else if(newRegister[6].equals("N")){
                    //the attribute user of LogicalManagement  = new Nutritionist(newRegister[0],newRegister[2],newRegister[4],newRegister[1], newRegister[3], newRegister[5]); //check if the order is correct
                    System.out.println("StandardUSer correttamente generato!");
                }

                //else{ ...is the same
                else if (!newRegister[6].equals("SU") && !newRegister[6].equals("N")) { //fare qualcosa
                    System.out.print("fare qualcosa con eccezione");
                }

                //to test
                System.out.println("username: "+newRegister[0]+", password: "+newRegister[1]+", fullName: "+newRegister[2]+", Age: "+newRegister[3]+", Sex: "+newRegister[4]+", Country: "+newRegister[5]+", UserType: "+newRegister[6]);
                step1 = true;
            }

            step2 = false;
            while (step2 !=true){
                //help menu
            }

            break;
        }
    }
}
