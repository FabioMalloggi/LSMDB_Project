package it.unipi.dii.utilities;

import java.io.File;

public class DataManager {
    /***************/
    private static String fileNameInput = "C:/ProgramData/nutrient.csv";
    private static String fileNameOutput = "C:/ProgramData/nutrient2.csv";
    /***************/

    private static OperationsCSV fop = new OperationsCSV();


    public static void main(String[] args) {
        File file1 = new File(fileNameInput);
        File file2 = new File(fileNameOutput);

        //file2.delete();
        fop.samplinglinesCSV(file1, file2, 10);
    }
}
