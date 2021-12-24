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
        return username; //the controller will perform the check if the username digitized  already exists
    }

    public String startPasswordSubmission(){
        String password;
        System.out.println("==> insert password:");
        System.out.print("> ");
        password = scan.next();
        return password;
    }

    public String startFullNameSubmission(){
        String fullName, first, last;
        System.out.println("==> insert fullName:");
        //System.out.print("> ");
        first = scan.next();
        last = scan.next();
        fullName = first+" "+last;
        return fullName;
    }

    public String startAgeSubmission(){
        String age;
        System.out.println("==> insert age:");
        //System.out.print("> ");
        age = scan.next();
        return age;
    }

    public String startSexSubmission(){
        String sex;
        System.out.println("==> insert sex:");
        //System.out.print("> ");
        sex = scan.next();
        return sex;
    }

    public String startCountrySubmission(){
        String country;
        System.out.println("==> insert country:");
        //System.out.print("> ");
        country = scan.next();
        return country;
    }

    public String startUserTypeSubmission(){
        String input;
        System.out.println("==> insert userType:\"\n" +
                "==> Type \"SU\" for StandardUser\n" +
                "==> Type \"N\" for Nutritionist");
        System.out.print("> ");
        input = scan.next();
        /*if(!input.equals("SU") && !input.equals("N")) //fare qualcosa
            System.out.print("fare qualcosa con eccezione");
        else return input;*/
        return input;
    }


}
