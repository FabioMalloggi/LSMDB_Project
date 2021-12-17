package it.unipi.dii.utilities;
/*
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;*/

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class HandlingUser {

    public static void generatorUser() throws JSONException{
        File fileOriginalAthlete = new File("./data/derived/athleteR.csv");
        File fileAthleteJOSN = new File("./data/derived/athleteRR.json");

        JSONObject collection = new JSONObject();
        OperationsCSV opCSV = new OperationsCSV();
        BufferedWriter bufWriter;
        String[] tokens;

        opCSV.initializeR(fileOriginalAthlete);
        JSONArray users = new JSONArray();

        try {
            String line = opCSV.bufReader.readLine();

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

                user.put("_id", tokens[0]);
                user.put("username", username);
                user.put("password", "");
                user.put("name", tokens[1]);
                user.put("sex", tokens[2]);
                user.put("country", tokens[6]);
                user.put("userType", "standardUser");

                JSONArray eatenFoods = new JSONArray();
                /*JSONObject eatenFood = new JSONObject();
                eatenFood.put("eatenFoodID", "");
                eatenFood.put("foodID", "");
                eatenFood.put("quantity", "");
                eatenFood.put("timestamp", "");
                eatenFoods.put(eatenFood);*/

                user.put("eatenFoods", eatenFoods);


                users.put(user);
                line = opCSV.bufReader.readLine();
            }
            collection.put("users", users);
        }catch(IOException e) {
            e.printStackTrace();
        }
        try{
            bufWriter = new BufferedWriter(new FileWriter(fileAthleteJOSN));
            bufWriter.write("prova");
            bufWriter.newLine();
            //bufWriter.write(collection.toString());
        }catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws  JSONException {
        generatorUser();
    }
}
