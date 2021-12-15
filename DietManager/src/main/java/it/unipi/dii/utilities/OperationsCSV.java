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
        return copyFileByLine(-1,-1);
    }

    public int copyFileByLine(int firstLineIndex, int lastLineIndex) throws IOException{
        int writeCounter = 0;
        boolean limitMaxLine = true;
        if(lastLineIndex == -1)
            limitMaxLine = false;

        String line = bufReader.readLine();
        int readCounter = 1;
        while (line != null && readCounter >= firstLineIndex && (readCounter <= lastLineIndex || limitMaxLine == false)) {
            bufWriter.write(line);
            bufWriter.newLine();
            writeCounter++;
            line = bufReader.readLine();
            readCounter++;
        }
        return writeCounter;
    }

    public int copyFileByLineContainingTargets(List<String> targets, int fieldContainingTarget){
        int writeCounter = 0;
        try{
            String line = bufReader.readLine();
            while (line != null) {
                if(containTarget(line, targets, fieldContainingTarget)) {
                    bufWriter.write(line);
                    bufWriter.newLine();
                    writeCounter++;
                }
                line = bufReader.readLine();
            }
        }catch(IOException e) {
            e.printStackTrace();
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


    public int samplinglinesCSV(int copyLineEveryNLines){
        int readCounter = 0, writeCounter = 0;
        try {
            String line = bufReader.readLine();
            while (line != null) {
                readCounter++;
                if (readCounter % copyLineEveryNLines == 0) {
                    bufWriter.write(line);
                    bufWriter.newLine();
                    writeCounter++;
                }
                line = bufReader.readLine();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        return writeCounter;
    }
}
