package it.unipi.dii.utilities;

import java.io.*;
import java.util.List;

public class OperationsCSV {
    BufferedReader bufReader;
    BufferedWriter bufWriter;

    public void initialize(File fileInput, File fileOutput){
        try{
            bufReader = new BufferedReader(new FileReader(fileInput));
            bufWriter = new BufferedWriter(new FileWriter(fileOutput, true));
        }catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void close(){
        try{
            bufReader.close();
            bufWriter.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    public int copyFileByLine() throws IOException{
        int writeCounter = 0;

        String line = bufReader.readLine();
        while (line != null) {

            bufWriter.write(line);
            bufWriter.newLine();
            writeCounter++;
            line = bufReader.readLine();
        }
        return writeCounter;
    }

    public int copyFileByLineContainingTargets(List<String> targets, int fieldContainingTarget) throws IOException{
        int writeCounter = 0;

        String line = bufReader.readLine();
        while (line != null) {
            if(containTarget(line, targets, fieldContainingTarget)) {
                bufWriter.write(line);
                bufWriter.newLine();
                writeCounter++;
            }
            line = bufReader.readLine();
        }


        return writeCounter;
    }

    public boolean containTarget(String line, List<String> targets, int fieldContainingTarget){
        String[] tokens = line.split(",");
        boolean targetFound = false;
        for(String target: targets){
            if(tokens[fieldContainingTarget-1].contains(target)) {
                targetFound = true;
                break;
            }
        }
        return targetFound;
    }





    public int samplinglinesCSV(File fileInput, File fileOutput, int copyLineEveryNLines){

        fileOutput.delete();

        int readCounter = 0, writeCounter = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(fileInput));
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileOutput, true)) ){

            String line = reader.readLine();
            while (line != null) {
                readCounter++;

                if(readCounter % copyLineEveryNLines == 0){
                    writer.write(line);
                    writer.newLine();
                    writeCounter++;
                }
                line = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return writeCounter;
    }
}
