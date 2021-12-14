package it.unipi.dii.utilities;

import java.io.*;

public class OperationsCSV {

    public int samplinglinesCSV(File fileInput, File fileOutput, int copyLineEveryNLines){

        fileOutput.delete();

        int readCounter = 0, writeCounter = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(fileInput));
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileOutput, true)) ){

            String line = reader.readLine();
            while (line != null) {
                readCounter++;

                /************* APPLICATION SPECIFIC ***************/
                /**************************************************/
                if(readCounter % copyLineEveryNLines == 0){
                    writer.write(line);
                    writer.newLine();
                    writeCounter++;
                }
                /**************************************************/
                line = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return writeCounter;
    }
}
