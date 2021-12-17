package it.unipi.dii.utilities;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OperationsCSV {
    BufferedReader bufReader;
    BufferedWriter bufWriter;
    private final int per100gRows = 3614000;

    public void initializeRW(File fileInput, File fileOutput){
        fileOutput.delete();
        try{
            bufReader = new BufferedReader(new FileReader(fileInput));
            bufWriter = new BufferedWriter(new FileWriter(fileOutput));
        }catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void initializeR(File fileInput){
        try{
            bufReader = new BufferedReader(new FileReader(fileInput));
        }catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void closeRW(){
        try{
            bufReader.close();
            bufWriter.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void closeR(){
        try{
            bufReader.close();
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
            System.out.println("Progress: " + (readCounter/per100gRows)*100 + "%");
        }
        return writeCounter;
    }


    public int copyfileByLineWithDistinctValue(int fieldContainingTarget){ //(1)
        //in this method we copy the first occur for each target attribute values.
        int writeCounter = 0;
        List<String> targets = new ArrayList<>();
        targets.add(""); //for using the already existed functions
        try{
            String line = bufReader.readLine();
            while (line != null) {
                System.out.println(line);
                if(!containTarget(line, targets, fieldContainingTarget)) { //if it is not present in the array, is the first occur of that athletes
                    bufWriter.write(line);
                    bufWriter.newLine();
                    writeCounter++;
                    String[] tokens = line.split(",");
                    targets.add(tokens[fieldContainingTarget-1]);
                }
                line = bufReader.readLine();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        return writeCounter;
    }

    public int copyFileByLineContainingTargets(List<String> targets, int fieldContainingTarget){
        int writeCounter = 0;
        float readCounter = 0;
        try{
            String line = bufReader.readLine();
            while (line != null) {
                System.out.println(line);
                if(containTarget(line, targets, fieldContainingTarget)) {
                    bufWriter.write(line);
                    bufWriter.newLine();
                    writeCounter++;
                }
                line = bufReader.readLine();
                readCounter++;
                System.out.println("Progress: " + (readCounter/per100gRows)*100 + "%");
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        return writeCounter;
    }

    public boolean containTarget(String line, List<String> targets, int fieldContainingTarget){
        String[] tokens = line.split(",");
        tokens[fieldContainingTarget-1] = tokens[fieldContainingTarget-1].replace("\"", "");
        return targets.contains(tokens[fieldContainingTarget-1]);
    }

    public String getAttributeValue(int attributeIndex, String record)
    {
        String[] attributes = record.split(",");
        return attributes[attributeIndex-1];
    }

    public List<String> extractDistinctAttributeList(int fieldContainingTarget){
        List<String> distinctAttributeList = new ArrayList<>();
        float readCounter = 0;
        String attributeValue;

        try
        {
            String line = bufReader.readLine();
            while (line != null)
            {
                attributeValue = getAttributeValue(fieldContainingTarget, line);
                if(!distinctAttributeList.contains(attributeValue))
                {
                    // the list doesn't contain item, hence I add it in the list
                    distinctAttributeList.add(attributeValue);
                }
                line = bufReader.readLine();
                readCounter++;
                System.out.println("Progress: " + (readCounter/per100gRows)*100 + "%");
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return distinctAttributeList;
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
