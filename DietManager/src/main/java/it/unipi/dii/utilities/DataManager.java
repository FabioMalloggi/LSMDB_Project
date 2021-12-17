package it.unipi.dii.utilities;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DataManager {
    /***************/
    private static String fileNameInput = "C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/FoodId-NutrientId-Per100g_target.csv";
    private static String fileNameOutput = "C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/FoodId-NutrientId-Per100g_target_part1.csv";
    /***************/
    private static final List<String> nutrientIDtargets = Arrays.asList( "1008","1003","1258","1005","2000","1079",
                                                                        "1104","1175","1178","1241","1158","1165",
                                                                        "1087","1090","1101","1091","1095");
    private static final int nutrientIDFieldInPer100gFile = 2;
    private static final int foodIDFieldInPer100gFile = 1;

    //private static OperationsCSV opCSV = new OperationsCSV();

    private static void handling_Per100g_CSV(){
        File fileOriginalFoodPer100g = new File("./data/original/Per100g.csv");
        File fileTargetNutrientPer100g = new File("./data/derived/TargetNutrientPer100g.csv");
        File fileTargetNutrientTargetFoodPer100g = new File("./data/derived/TargetNutrientTargetFoodPer100g.csv");
        File fileOriginalFood = new File("./data/original/Food.csv");
        File fileTargetFood = new File("./data/derived/TargetFood.csv");

        OperationsCSV opCSV = new OperationsCSV();

        // copy only target nutrients
        opCSV.initializeRW(fileOriginalFoodPer100g,fileTargetNutrientPer100g);
        opCSV.copyFileByLineContainingTargets(nutrientIDtargets, nutrientIDFieldInPer100gFile);
        opCSV.closeRW();

        // copy 1 row every k
        opCSV.initializeRW(fileOriginalFood,fileTargetFood);
        opCSV.samplinglinesCSV(35);
        opCSV.closeRW();



        opCSV.initializeR(fileTargetFood);
        List<String> targetFoods = opCSV.extractDistinctAttributeList(foodIDFieldInPer100gFile);
        opCSV.closeR();

        opCSV.initializeRW(fileTargetNutrientPer100g,fileTargetNutrientTargetFoodPer100g);
        opCSV.copyFileByLineContainingTargets(targetFoods, foodIDFieldInPer100gFile);
        opCSV.closeRW();


    }

    private static void handling_athlets_CSV(){
        /*File fileOriginalFoodPer100g = new File("./data/original/Per100g.csv");
        File fileTargetNutrientPer100g = new File("./data/derived/TargetNutrientPer100g.csv");
        File fileTargetNutrientTargetFoodPer100g = new File("./data/derived/TargetNutrientTargetFoodPer100g.csv");*/
        File fileOriginalAthlete = new File("./data/original/athlete.csv");
        File fileOTargetAthlete = new File("./data/derived/athleteR.csv");

        OperationsCSV opCSV = new OperationsCSV();

        // copy 1 row every k
        opCSV.initializeRW(fileOriginalAthlete,fileOTargetAthlete);
        opCSV.samplinglinesCSV(10);
        opCSV.closeRW();




        /*
        opCSV.initializeR(fileOriginalAthlete);
        List<String> targetAthletes = opCSV.extractDistinctAttributeList(1);
        opCSV.closeR();*/
        /*
        opCSV.initializeRW(fileTargetNutrientPer100g,fileTargetNutrientTargetFoodPer100g);
        opCSV.copyFileByLineContainingTargets(targetFoods, foodIDFieldInPer100gFile);
        opCSV.closeRW();*/


    }
    public static void main(String[] args) throws IOException {
            File fileInput = new File(fileNameInput);
            File fileOutput = new File(fileNameOutput);

            //handling_Per100g_CSV();
            handling_athlets_CSV();
    }

}
