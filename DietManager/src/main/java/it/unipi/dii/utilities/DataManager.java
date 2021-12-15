package it.unipi.dii.utilities;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DataManager {
    /***************/
    private static String fileNameInput = "C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/FoodId-NutrientId-Per100g_2.csv";
    private static String fileNameOutput = "C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/FoodId-NutrientId-Per100g_target.csv";
    /***************/
    private static final List<String> nutrientIDtargets = Arrays.asList( "1008","1003","1258","1005","2000","1079",
            "1104","1175","1178","1241","1158","1165",
            "1087","1090","1101","1091","1095");
    private static final int nutrientIDFieldInPer100gFile = 2;

    private static OperationsCSV opCSV = new OperationsCSV();

    public static void main(String[] args) throws IOException {
            File fileInput = new File(fileNameInput);
            File fileOutput = new File(fileNameOutput);

            opCSV.initialize(fileInput, fileOutput);
            opCSV.copyFileByLineContainingTargets(nutrientIDtargets, nutrientIDFieldInPer100gFile);
            opCSV.close();

        }
}
