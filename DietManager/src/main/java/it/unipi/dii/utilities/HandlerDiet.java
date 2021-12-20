package it.unipi.dii.utilities;

import it.unipi.dii.entities.Diet;
import it.unipi.dii.entities.User;
import org.json.JSONArray;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HandlerDiet {

    private int DIETS_NUMBER = 2000;
    private int[] TARGET_NUTRIENT_INDEXES_DB2 = {7, 8, 9, 10, 11, 12, 13, 14, 15,
            16, 17, 21, 22, 25, 26, 27, 29}; //count from 0
    private int nutrientNum = TARGET_NUTRIENT_INDEXES_DB2.length;

    private String[] nutrients_names =
            {/*0*/"Energy",/*1*/"Protein",/*2*/"Fat",/*3*/"Carb",/*4*/"Sugar",
            /*5*/"Fiber",/*6*/"VitA",/*7*/"VitB6",/*8*/"VitB12",/*9*/"VitC",/*10*/"VitE",
            /*11*/"Thiamin",/*12*/"Calcium",/*13*/"Magnesium",/*14*/"Manganese",/*15*/"Phosphorus",
            /*16*/"Zinc"};
    private String[] nutrients_units =
            {/*0*/"KCAL",/*1*/"G",/*2*/"G",/*3*/"G",/*4*/"G",
            /*5*/"G",/*6*/"UG",/*7*/"MG",/*8*/"UG",/*9*/"MG",/*10*/"MG",
            /*11*/"MG",/*12*/"MG",/*13*/"MG",/*14*/"MG",/*15*/"MG",
            /*16*/"MG"};

    private File file_max = new File("data/original/max.csv");
    private File file_nutritionists = new File("data/derived/nutritionist.csv");
    private File file_diets = new File("data/derived/diet.csv");

    private String[] max = new String[TARGET_NUTRIENT_INDEXES_DB2.length];
    private double[] maxDouble = new double[TARGET_NUTRIENT_INDEXES_DB2.length];
    private double[][] nutrientsMatrix = new double[DIETS_NUMBER][TARGET_NUTRIENT_INDEXES_DB2.length];

    List<User> nutritionistList = new ArrayList<>();
    private int nutritionistNum = 0;
    private Random r = new Random();
    private List<Diet> dietList = new ArrayList<>();


    private String[] extractTargetNutrientfromMax(){
        String[] targetLine = new String[TARGET_NUTRIENT_INDEXES_DB2.length];

        try(BufferedReader readerMax = new BufferedReader(new FileReader(file_max))){

            // EXTRACTING TARGET NUTRIENT FROM max.csv
            String line = readerMax.readLine();
            String[] lineArray = line.split(";");
            int currentIndex = 0;
            for(int i: TARGET_NUTRIENT_INDEXES_DB2) {
                targetLine[currentIndex] = lineArray[i];
                currentIndex++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return targetLine;
    }

    private void nutrientsGeneratorByMax(){
        max = extractTargetNutrientfromMax();
        for(int i=0; i<max.length; i++)
            maxDouble[i] = Double.parseDouble(max[i]);

        for(int i=0; i<DIETS_NUMBER; i++) {
            for (int j = 0; j < nutrientNum; j++) {

                nutrientsMatrix[i][j] = r.nextDouble() * maxDouble[j] * 0.5 + maxDouble[j] * 0.25;
            }
        }
    }

    private void nutritionistReader( File inputFile, int numMax ){
        nutritionistNum = 0;
        try(BufferedReader readerNutritionist = new BufferedReader(new FileReader(inputFile))){
            String line = readerNutritionist.readLine();
            String[] tokens;

            while(line != null){
                if(0 < nutritionistNum && nutritionistNum >= numMax)
                    break;
                tokens = line.split(",");
                //nutritionistList.add(new User(tokens[0],tokens[1]));
                nutritionistNum++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void dietCreator(int numDiets){
        DIETS_NUMBER = numDiets;

        // generating Random Nutrients
        nutrientsGeneratorByMax();

        // reading Nutritionists from nutritionist.csv
        nutritionistReader(file_nutritionists, DIETS_NUMBER/2); // each nutritionist in average has made 2 diets.

        // generating Diet
        List<Nutrient> nutrientList = new ArrayList<>();
        for(int i=0; i<DIETS_NUMBER; i++) {

            // creating ID
            String id = String.format("%08d",i);

            // creating name
            String name = "";

            // obtaining Nutrients List for a single Diet from Matrix
            for (int j = 0; j < TARGET_NUTRIENT_INDEXES_DB2.length; j++) {
                Nutrient newNutrient = new Nutrient(nutrients_names[j],nutrients_units[j],nutrientsMatrix[i][j]);
                nutrientList.add(newNutrient);
            }

            // extracting one Random Nutritionist from List:
            User dietAuthor = nutritionistList.get(r.nextInt(nutritionistNum));

            dietList.add(new Diet(id,"name", nutrientList,dietAuthor));
            nutrientList.clear();
        }
    }



    public static void main(String[] args) {

        HandlerDiet hd = new HandlerDiet();
        hd.dietCreator(2000);
    }

}
