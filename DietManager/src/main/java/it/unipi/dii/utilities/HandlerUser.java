package it.unipi.dii.utilities;
/*
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;*/

import it.unipi.dii.dietmanager.entities.Nutritionist;
import it.unipi.dii.dietmanager.entities.StandardUser;
import it.unipi.dii.dietmanager.entities.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class HandlerUser {
    private static int K = 100;

    private static boolean isNutritionist(int counter){
        return (counter % K == 0);
    }

    public static void generatorNutritionistJSON() throws JSONException{
        File fileOriginalAthlete = new File("./data/derived/nutritionist.csv");
        File fileAthleteJSON = new File("./data/derived/nutritionistJ");
        OperationsCSV opCSV = new OperationsCSV();
        BufferedWriter bufWriterJson, bufWriterNut, bufWriterUser; String[] tokens;
        int counter = 1, writeUser = 0, writeNut = 0;
        User userTmp;
        opCSV.initializeR(fileOriginalAthlete);
        JSONArray users = new JSONArray();

        try {
            String line = opCSV.bufReader.readLine();
            line = opCSV.bufReader.readLine(); //first line contains the columns names
            while (line != null) {
                tokens = line.split(",");

                userTmp = new Nutritionist(tokens[1],tokens[3], tokens[4],tokens[1],Integer.parseInt(tokens[5]),tokens[6]);
                users.put(userTmp.toJSONObject());
                line = opCSV.bufReader.readLine();
                counter++;
                System.out.println("Counter: "+counter);
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        try{
            fileAthleteJSON.delete();
            bufWriterJson = new BufferedWriter(new FileWriter(fileAthleteJSON));
            bufWriterJson.write(users.toString());
            bufWriterJson.close();

        }catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void generatorUser() throws JSONException{
        File fileOriginalAthlete = new File("./data/derived/athleteR2.csv");
        File fileAthleteJSON = new File("./data/derived/usersJ");
        File fileUser = new File("./data/derived/users.csv");
        File fileNutritionist = new File("./data/derived/nutritionist.csv");
        //JSONObject collection = new JSONObject();
        OperationsCSV opCSV = new OperationsCSV();
        BufferedWriter bufWriterJson, bufWriterNut, bufWriterUser;
        String[] tokens;
        int counter = 1, writeUser = 0, writeNut = 0;
        User userTmp;

        opCSV.initializeR(fileOriginalAthlete);
        JSONArray users = new JSONArray();

        try {
            bufWriterNut = new BufferedWriter(new FileWriter(fileNutritionist));
            bufWriterUser = new BufferedWriter(new FileWriter(fileUser));
            String line = opCSV.bufReader.readLine();
            line = opCSV.bufReader.readLine(); //first line contains the columns names
            while (line != null) {
                tokens = line.split(",");

                JSONObject user = new JSONObject();
                if(isNutritionist(counter)){ //nutritionist
                    userTmp = new Nutritionist(tokens[1],tokens[3], tokens[4],tokens[1],Integer.parseInt(tokens[5]),tokens[6]);
                    bufWriterNut.write(tokens[0]+","+tokens[1]+","+tokens[1]+","+tokens[3]+","+tokens[4]+","+tokens[5]+","+tokens[6]); //the password is equal to the username
                    bufWriterNut.newLine();
                    writeNut++;
                }

                else{ //standardUser
                    userTmp = new StandardUser(tokens[1],tokens[3], tokens[4],tokens[1],Integer.parseInt(tokens[5]),tokens[6]);
                    bufWriterUser.write(tokens[0]+","+tokens[1]+","+tokens[1]+","+tokens[3]+","+tokens[4]+","+tokens[5]+","+tokens[6]); //the password is equal to the username
                    bufWriterUser.newLine();
                    writeUser++;
                }

                users.put(userTmp.toJSONObject());
                line = opCSV.bufReader.readLine();
                counter++;
                System.out.println("Counter: "+counter);
            }
            bufWriterUser.close();
            bufWriterNut.close();

        }catch(IOException e) {
            e.printStackTrace();
        }
        try{
            fileAthleteJSON.delete();
            bufWriterJson = new BufferedWriter(new FileWriter(fileAthleteJSON));
            bufWriterJson.write(users.toString());
            bufWriterJson.close();

        }catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void checkIfUsernameIsUnique(File fileInput, File fileOutput){
        OperationsCSV opCSV = new OperationsCSV();
        opCSV.initializeRW(fileInput, fileOutput);
        String line;
        String[] tokens;
        int counter_total = 0;
        int counter = 0;
        int counterNotDuplicated = 0;
        ArrayList<String> userNamesChecked = new ArrayList<>();
        userNamesChecked.add("");
        try{
            line = opCSV.bufReader.readLine();
            while(line != null){
                counter_total++;
                tokens = line.split(",");
                if(tokens.length == 7 && tokens[5].matches("\\d+")){
                    if(userNamesChecked.contains(tokens[1])){ //if the analyzed username is already selected
                        counter++;
                        //System.out.println("Duplicate user: "+ tokens[1]+", id: "+tokens[0]);
                    }
                    else {
                        userNamesChecked.add(tokens[1]);
                        //writing the distinct usernames in a newFile
                        opCSV.bufWriter.write(line);
                        opCSV.bufWriter.newLine();
                        opCSV.bufWriter.flush();
                        counterNotDuplicated++;
                    }

                }
                line  = opCSV.bufReader.readLine();
                //System.out.println("Coutner totalt: "+counter_total);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Counter duplicated: "+ counter+"Counter not duplicated: "+counterNotDuplicated);
        
    }

    private static void handling_athlets_CSV(){
        File fileOriginalAthlete = new File("./data/original/athlete.csv"); //with duplicate numerical ID
        File fileOTargetAthlete = new File("./data/derived/athleteR.csv"); //with distinct numerical ID

        OperationsCSV opCSV = new OperationsCSV();

        opCSV.initializeRW(fileOriginalAthlete,fileOTargetAthlete);
        opCSV.copyfileByOrderedLineWithDistinctValue(1);
        opCSV.closeRW();
    }

    public static void main(String[] args) throws  JSONException {
        //handling_athlets_CSV();

        /*
        File fileUser = new File("./data/derived/athleteR.csv");
        File newFileUser = new File("./data/derived/athleteR2.csv"); //with distinct username and without 'NA' as AGE
        checkIfUsernameIsUnique(fileUser, newFileUser);*/

        generatorUser();

        //generatorNutritionistJSON();
    }
}
