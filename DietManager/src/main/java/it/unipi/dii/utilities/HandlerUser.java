package it.unipi.dii.utilities;
/*
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;*/

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

    public static void generatorUser() throws JSONException{
        File fileOriginalAthlete = new File("./data/derived/athleteR.csv");
        File fileAthleteJSON = new File("./data/derived/usersJ");
        File fileUser = new File("./data/derived/users1.csv");
        File fileNutritionist = new File("./data/derived/nutritionist.csv");
        JSONObject collection = new JSONObject();
        OperationsCSV opCSV = new OperationsCSV();
        BufferedWriter bufWriterJson, bufWriterNut, bufWriterUser;
        String[] tokens;
        int counter = 1, writeUser = 0, writeNut = 0;

        opCSV.initializeR(fileOriginalAthlete);
        JSONArray users = new JSONArray();

        try {
            bufWriterNut = new BufferedWriter(new FileWriter(fileNutritionist));
            bufWriterUser = new BufferedWriter(new FileWriter(fileUser));
            String line = opCSV.bufReader.readLine();
            line = opCSV.bufReader.readLine(); //first line contains the columns names
            while (line != null) {
                tokens = line.split(",");

                for(int j = 0; j < tokens.length; j++){
                    tokens[j] = tokens[j].replace("\"", "");
                }

                String[] names = tokens[1].split(" ");
                String username = "";

                int i = 0;
                while(i < names.length){
                    username = username+names[i];
                    i++;
                }

                //I generate a new user
                JSONObject user = new JSONObject();

                //user.put("_id", tokens[0]); //old decision
                //user.put("username", username);
                user.put("_id", username); //new decision
                user.put("password", username);
                user.put("name", tokens[1]);
                user.put("sex", tokens[2]);
                user.put("age", tokens[3]);
                user.put("country", tokens[6]);

                if(isNutritionist(counter)){ //nutritionist
                    user.put("userType", "nutritionist");
                    bufWriterNut.write(tokens[0]+","+username+","+username+","+tokens[1]+","+tokens[2]+","+tokens[3]+","+tokens[6]); //the password is equal to the username
                    bufWriterNut.newLine();
                    writeNut++;
                }

                else{ //standardUser
                    user.put("userType", "standardUser");
                    JSONArray eatenFoods = new JSONArray();
                    /*JSONObject eatenFood = new JSONObject();
                    eatenFood.put("eatenFoodID", "");
                    eatenFood.put("foodID", "");
                    eatenFood.put("quantity", "");
                    eatenFood.put("timestamp", "");
                    eatenFoods.put(eatenFood);*/
                    user.put("eatenFoods", eatenFoods);

                    //bufWriterUser.write(line);
                    bufWriterUser.write(tokens[0]+","+username+","+username+","+tokens[1]+","+tokens[2]+","+tokens[3]+","+tokens[6]); //the password is equal to the username
                    bufWriterUser.newLine();
                    writeUser++;
                }

                users.put(user);
                line = opCSV.bufReader.readLine();
                counter++;
                System.out.println("Counter: "+counter);
            }

            collection.put("users", users);
            bufWriterUser.close();
            bufWriterNut.close();

        }catch(IOException e) {
            e.printStackTrace();
        }
        try{
            fileAthleteJSON.delete();
            bufWriterJson = new BufferedWriter(new FileWriter(fileAthleteJSON));
            bufWriterJson.write(collection.toString());
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
        int counter = 0;
        int counterNotDuplicated = 0;
        ArrayList<String> userNamesChecked = new ArrayList<>();
        userNamesChecked.add("");
        try{
            line = opCSV.bufReader.readLine();
            while(line != null){
                tokens = line.split(",");
                if(userNamesChecked.contains(tokens[1])){ //if the analyzed username is already selected
                    counter++;
                    //System.out.println("Duplicate user: "+ tokens[1]+", id: "+tokens[0]);
                }
                else {
                    userNamesChecked.add(tokens[1]);
                    //writing the distinct usernames in a newFile
                    opCSV.bufWriter.write(line);
                    opCSV.bufWriter.newLine();

                    counterNotDuplicated++;
                }
                line  = opCSV.bufReader.readLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Counter duplicated: "+ counter+"Counter not duplicated: "+counterNotDuplicated);
        
    }

    public static void main(String[] args) throws  JSONException {
        //generatorUser();
        File fileUser = new File("./data/derived/users1.csv");
        File newFileUser = new File("./data/derived/users.csv");
        checkIfUsernameIsUnique(fileUser, newFileUser);
    }
}
