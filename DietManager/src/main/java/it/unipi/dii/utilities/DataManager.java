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

    //private static OperationsCSV opCSV = new OperationsCSV();

    public static void main(String[] args) throws IOException {
            File fileInput = new File(fileNameInput);
            File fileOutput = new File(fileNameOutput);

        }

    public void handling_Per100g_CSV(){
        File file1 = new File("/Per100g_Originale");
        File file11 = new File("/Per100g_TargetNutrient");
        File file12 = new File("/Per100g_TargetNutrient_targetFood");
        File file2 = new File("/Food_Orginale");
        File file21 = new File("/Food_target");



        OperationsCSV opCSV1 = new OperationsCSV();
        OperationsCSV opCSV2 = new OperationsCSV();
        OperationsCSV opCSV3 = new OperationsCSV();

        opCSV1.initialize(file1,file11);
        opCSV1.copyFileByLineContainingTargets(nutrientIDtargets, nutrientIDFieldInPer100gFile);
        opCSV1.close();

        opCSV2.initialize(file2,file21);
        opCSV2.samplinglinesCSV(10);
        opCSV2.close();

        opCSV3.initialize(file11,file12);
        //List<String> targetFoods = TUAFUNZIONE(file21);
        //opCSV3.copyFileByLineContainingTargets(targetFoods, 1);
        opCSV3.close();

    }
}
