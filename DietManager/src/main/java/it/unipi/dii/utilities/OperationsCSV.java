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

    //to use only if the dataset with redundant attribute value are already sorted (close together)
    public int copyfileByOrderedLineWithDistinctValue(int fieldContainingTarget){
        //in this method we copy the first occur for each target attribute values.
        int writeCounter = 0;
        String targets = "";
        try{
            String line = bufReader.readLine();
            while (line != null) {
                System.out.println(line);
                String[] tokens = line.split(",");
                tokens[fieldContainingTarget-1] = tokens[fieldContainingTarget-1].replace("\"", "");
                if(!tokens[fieldContainingTarget-1].equals(targets)) { //if the 2 strings are not equal, write the line in the new file
                    bufWriter.write(line);
                    bufWriter.newLine();
                    writeCounter++;
                    targets = tokens[fieldContainingTarget-1];
                }
                line = bufReader.readLine();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        return writeCounter;
    }

    //in this method we copy the first occur for each target attribute values which are redundant. (Suggestion: using only if the redundant attribute value are not sorted and put together)
    public int copyfileByLineWithDistinctValue(int fieldContainingTarget){
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

    public void changeFileSeparators(File original_file, File modified_file, char old_separator,
                                     char new_separator, char string_delimiter)
    {
        modified_file.delete();
        try{
            BufferedReader bufReader = new BufferedReader(new FileReader(original_file));
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter(modified_file));

            boolean inString = false;
            char[] lineCharacters;
            String line = bufReader.readLine();
            while(line != null){
                lineCharacters = line.toCharArray();

                // I scan all the line, and I substitute "," with ";" only if I'm not in a string
                for(int i=0; i<line.length(); i++){
                    // I check if a new string is started
                    if(!inString && lineCharacters[i] == string_delimiter){
                        inString = true;
                        continue;
                    }

                    // I check if a string is finished
                    if(inString && lineCharacters[i] == string_delimiter){
                        inString = false;
                        continue;
                    }

                    if(!inString && lineCharacters[i] == old_separator)
                        lineCharacters[i] = new_separator;
                }
                bufWriter.write(lineCharacters);
                bufWriter.newLine();

                line = bufReader.readLine();
            }

            bufReader.close();
            bufWriter.close();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void replaceCharactersInFile(File fileInput, File fileOutput, char old_character, char new_character)
    {
        fileOutput.delete();
        try{
            BufferedReader bufReader = new BufferedReader(new FileReader(fileInput));
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter(fileOutput));

            String line = bufReader.readLine();

            while(line != null){
                bufWriter.write(line.replace(old_character, new_character));
                bufWriter.newLine();
                line = bufReader.readLine();
            }

            bufReader.close();
            bufWriter.close();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }

    }

}
