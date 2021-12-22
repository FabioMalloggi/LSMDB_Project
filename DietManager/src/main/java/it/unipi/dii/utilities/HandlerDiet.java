package it.unipi.dii.utilities;

import it.unipi.dii.entities.*;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HandlerDiet {

    private final int DIETS_NUMBER;
    private final int[] TARGET_NUTRIENT_INDEXES_DB2 = {7, 8, 9, 10, 11, 12, 13, 14, 15,
            16, 17, 21, 22, 25, 26, 27, 29}; //count from 0
    private final int nutrientNumber = TARGET_NUTRIENT_INDEXES_DB2.length;

    private final String[] nutrients_names =
            {/*0*/"Energy",/*1*/"Protein",/*2*/"Fat",/*3*/"Carb",/*4*/"Sugar",
            /*5*/"Fiber",/*6*/"VitA",/*7*/"VitB6",/*8*/"VitB12",/*9*/"VitC",/*10*/"VitE",
            /*11*/"Thiamin",/*12*/"Calcium",/*13*/"Magnesium",/*14*/"Manganese",/*15*/"Phosphorus",
            /*16*/"Zinc"};
    private final String[] nutrients_units =
            {/*0*/"KCAL",/*1*/"G",/*2*/"G",/*3*/"G",/*4*/"G",
            /*5*/"G",/*6*/"UG",/*7*/"MG",/*8*/"UG",/*9*/"MG",/*10*/"MG",
            /*11*/"MG",/*12*/"MG",/*13*/"MG",/*14*/"MG",/*15*/"MG",
            /*16*/"MG"};

    private File fileMax = new File("data/original/max.csv");
    private File fileNutritionists = new File("data/derived/nutritionist.csv");
    private File fileDiets = new File("data/derived/diet.csv");

    private String[] max = new String[TARGET_NUTRIENT_INDEXES_DB2.length];
    private double[] maxDouble = new double[TARGET_NUTRIENT_INDEXES_DB2.length];
    private double[][] nutrientsMatrix; // new double[DIETS_NUMBER][TARGET_NUTRIENT_INDEXES_DB2.length]

    List<Nutritionist> nutritionists = new ArrayList<>();
    private int nutritionistNum = 0;
    private Random random = new Random();
    private List<Diet> diets = new ArrayList<>();



    public HandlerDiet(int dietsNumber){
        DIETS_NUMBER = dietsNumber;
        nutrientsMatrix = new double[DIETS_NUMBER][TARGET_NUTRIENT_INDEXES_DB2.length];
    }

    private String[] extractTargetNutrientfromMax(){
        String[] targetLine = new String[TARGET_NUTRIENT_INDEXES_DB2.length];

        try(BufferedReader readerMax = new BufferedReader(new FileReader(fileMax))){

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
            for (int j = 0; j < nutrientNumber; j++) {

                nutrientsMatrix[i][j] = random.nextDouble() * maxDouble[j] * 0.5 + maxDouble[j] * 0.25;
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
                nutritionists.add(new Nutritionist(/*tokens[0],*/tokens[1])); //change performed by tommaso 22-12-2021
                nutritionistNum++;
                line = readerNutritionist.readLine();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void dietCreator(){

        // generating Random Nutrients
        nutrientsGeneratorByMax();

        // reading Nutritionists from nutritionist.csv
        nutritionistReader(fileNutritionists, DIETS_NUMBER/2); // each nutritionist in average has made 2 diets.

        // generating Diets
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
            Nutritionist dietAuthor = nutritionists.get(random.nextInt(nutritionistNum));

            diets.add(new Diet(id,"name", nutrientList,dietAuthor));
            nutrientList.clear();
        }
    }

    public void printDietsToFile(){
        fileDiets.delete();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileDiets))){
            for(Diet diet: diets){
                writer.write(diet.toJSON().toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printNutritionists(){
        for(Nutritionist nutritionist: nutritionists){
            System.out.println(nutritionist.toJSON().toString());
        }
    }


    public static void main(String[] args) {
        HandlerDiet handlerDiet = new HandlerDiet(2000);
        handlerDiet.dietCreator();
        //hd.printNutritionists();
        handlerDiet.printDietsToFile();
    }

}
