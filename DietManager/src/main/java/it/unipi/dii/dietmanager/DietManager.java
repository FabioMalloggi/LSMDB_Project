package it.unipi.dii.dietmanager;

import it.unipi.dii.dietmanager.entities.CLI;

import java.io.IOException;
import java.util.Scanner;

public class DietManager {

    public static void main(String[] args) {
        CLI cli = new CLI();
        //LocalManagement lM = new LocalManagement();
        boolean notFinish = true;
        boolean step = false, chekUserNotExist =false;
        String username;
        String tmp;
        String[] signIn;

        while(notFinish) {

            //first setp: Sign in or Register
            while (step != true) {
                tmp = cli.startWelcomeMenu();
                if (!tmp.equals("R") && !tmp.equals("S")) //fare qualcosa
                    System.out.print("fare qualcosa con eccezione");
                else step = true;
            }

            step = false;
            //second step A: sign in
            while (step != true ) {

                signIn = cli.startSignInSubmission();
                //check if signIn[0] and signIn[1] is present or not in DB --> call the signIn(username, password) method of LogicalManagement
                //check if signIn[1] is the right password for the acocunt (this check is alredy done b the previouse check

                //if is all right, step = true; else remains false
            }

            /** to remove this '/*'

            //second step B: register, the currentUser is still null. With this check we do not need to use further variable in th previous while condition
            step = false;
            while(step != true && lM.currentUser == null){
                String[] newRegister = new String[5];
                while(chekUserNotExist != true) {
                    newRegister[0] = cli.startUsernameSubmission();
                    //check if signIn[0] is present or not in DB --> call the lookUpUserByUsername method of LogicalManagement
                    //if is all right, chekUserNotExist = true; else remains false
                }
                //check even if the input of the user is exit, over the check if the username already exist
                newRegister[1] = cli.startPasswordSubmission(); //to implement this function and so on..

            }

            **/

        }
    }
}
