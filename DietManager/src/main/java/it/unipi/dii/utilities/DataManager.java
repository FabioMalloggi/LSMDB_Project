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
        File fileOriginalFoodPer100g = new File("C:/Users/Simone/OneDrive/Desktop/data/fileOriginalFoodPer100g.csv");
        File fileTargetNutrientPer100g = new File("C:/Users/Simone/OneDrive/Desktop/data/fileTargetNutrientPer100g.csv");
        File fileTargetNutrientTargetFoodPer100g = new File("C:/Users/Simone/OneDrive/Desktop/data/fileTargetNutrientTargetFoodPer100g.csv");
        File fileOriginalFood = new File("C:/Users/Simone/OneDrive/Desktop/data/fileOriginalFood.csv");
        File fileTargetFood = new File("C:/Users/Simone/OneDrive/Desktop/data/fileTargetFood.csv");

        OperationsCSV opCSV = new OperationsCSV();

        opCSV.initializeRW(fileOriginalFoodPer100g,fileTargetNutrientPer100g);
        opCSV.copyFileByLineContainingTargets(nutrientIDtargets, nutrientIDFieldInPer100gFile);
        opCSV.closeRW();

        opCSV.initializeRW(fileOriginalFood,fileTargetFood);
        opCSV.samplinglinesCSV(10);
        opCSV.closeRW();

        opCSV.initializeR(fileTargetFood);
        List<String> targetFoods = opCSV.extractDistinctAttributeList(foodIDFieldInPer100gFile);
        opCSV.closeR();

        opCSV.initializeRW(fileTargetNutrientPer100g,fileTargetNutrientTargetFoodPer100g);
        opCSV.copyFileByLineContainingTargets(targetFoods, foodIDFieldInPer100gFile);
        opCSV.closeRW();

    }
    public static void main(String[] args) throws IOException {
            File fileInput = new File(fileNameInput);
            File fileOutput = new File(fileNameOutput);

            handling_Per100g_CSV();
        }

}
