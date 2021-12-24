package it.unipi.dii.dietmanager.entities;

import java.util.Scanner;

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
                "==> Type \"R\" for Register");
        System.out.print("> ");
        input = scan.next();
        if(!input.equals("R") && !input.equals("S")) //fare qualcosa
            System.out.print("fare qualcosa con eccezione");
        else return input;
        return input;
    }

    public String[] startSignInSubmission(){
        String[] input = new String[2];
        System.out.println("=======> Sign In:\n" +
                "==> insert username:");
        System.out.print("> ");
        input[0] = scan.next();
        System.out.println("");
        System.out.println("==> insert password:");
        System.out.print("> ");
        input[1] = scan.next();

        /*il check lo faccio qui in CLI o ritorno intanto le due stringhe al Controller e poi
        //controller.signIn(username, password);*/
        return input; //il controllo sarà fatto dal controller, nel caso verrà ri eseguito questo
    }

    public String startUsernameSubmission(){
        String username;
        System.out.println("=======> Registration:\n" +
                "==> insert username:");
        System.out.print("> ");
        username = scan.next();
        return username; //the controller will perform the check if the username digited  alredy exists
    }
}
