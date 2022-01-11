package it.unipi.dii.dietmanager.datageneration.rawdatamanagement;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVFilesManager {
    BufferedReader bufReader;
    BufferedWriter bufWriter;

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
    public int copyFileByLine(File fileInput, File fileOutput) throws IOException{
        return copyFileByLine(fileInput, fileOutput,-1,-1);
    }

    public int copyFileByLine(File fileInput, File fileOutput, int firstLineIndex, int lastLineIndex) throws IOException{
        initializeRW(fileInput, fileOutput);
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
        closeRW();
        return writeCounter;
    }

    private String userFormat(String line){
        //for the athlete
        String[]tokens = line.split(",");
        for(int j = 0; j < tokens.length; j++){
            tokens[j] = tokens[j].replace("\"", "");
        }
        String[] names = tokens[1].split(" ");
        String username = "";
        int i = 0;
        while(i < names.length){
            username = username+names[i];
            i++;
        }
        return tokens[0]+","+username+","+username+","+tokens[1]+","+tokens[2]+","+tokens[3]+","+tokens[6];
    }

    //to use only if the dataset with redundant attribute value are already sorted (close together)
    public int copyfileByOrderedLineWithDistinctValue(int fieldContainingTarget){
        //in this method we copy the first occur for each target attribute values.
        int writeCounter = 0;
        String targets = "", userFormat;
        try{
            String line = bufReader.readLine();
            while (line != null) {
                System.out.println(line);
                //GENERAL
                String[] tokens = line.split(",");
                tokens[fieldContainingTarget-1] = tokens[fieldContainingTarget-1].replace("\"", "");

                userFormat = userFormat(line); //needed to have athleteR.csv in the format of user and nutritionist.csv to check if there is no duplicate username


                if(!tokens[fieldContainingTarget-1].equals(targets)) { //if the 2 strings are not equal, write the line in the new file
                    //bufWriter.write(line); //general usage
                    bufWriter.write(userFormat); //OLD bufWriter.write(tokens[0]+","+username+","+username+","+tokens[1]+","+tokens[2]+","+tokens[3]+","+tokens[6]); //for the athlete
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

    public int copyFileByLineContainingTargets(File fileInput, File fileOutput, List<String> targets, int fieldContainingTarget){
        initializeRW(fileInput,fileOutput);
        int writeCounter = 0;
        float readCounter = 0;
        try{
            String line = bufReader.readLine();
            while (line != null) {
                //System.out.println(line);
                if(containTarget(line, targets, fieldContainingTarget)) {
                    bufWriter.write(line);
                    bufWriter.newLine();
                    writeCounter++;
                }
                line = bufReader.readLine();
                readCounter++;
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        closeRW();
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

    public void insertIntoFileAttributesFrom2Files(File fileInput1, File fileInput2, int fileInput1IDIndex, int fileInput1NameIndex,
                                                   int fileInput2IDIndex, int fileInput2NameIndex, File fileOutput){
        fileOutput.delete();
        try{
            BufferedReader bufferedReader1 = new BufferedReader(new FileReader(fileInput1));
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(fileInput2));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileOutput));

            String line = bufferedReader1.readLine();
            String[] attributes;

            while(line != null){
                attributes = line.split(";");

                bufferedWriter.write(attributes[fileInput1IDIndex] + ";" + attributes[fileInput1NameIndex]);
                bufferedWriter.newLine();

                line = bufferedReader1.readLine();
            }
            bufferedReader1.close();

            line = bufferedReader2.readLine();
            while(line != null){
                attributes = line.split(";");

                bufferedWriter.write(attributes[fileInput2IDIndex] + ";" + attributes[fileInput2NameIndex]);
                bufferedWriter.newLine();

                line = bufferedReader2.readLine();
            }
            bufferedReader2.close();
            bufferedWriter.close();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public int getFileRowsNumber(File fileInput){
        int rowsCounter = 0;
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileInput));

            String line = bufferedReader.readLine();

            while(line != null){
                rowsCounter++;
                line = bufferedReader.readLine();
            }

            bufferedReader.close();

        }catch(IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return rowsCounter;
    }

    public String[] getAttributeValuesArrayFromFile(File fileInput){
        String[] attributeValues = new String[getFileRowsNumber(fileInput)];
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileInput));

            int attributeValuesIndex = 0;
            String line = bufferedReader.readLine();

            while(line != null){
               attributeValues[attributeValuesIndex++] = line;
               line = bufferedReader.readLine();
            }

            bufferedReader.close();

        }catch(IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return attributeValues;
    }

    public int writeToFileAttributeRepetitions(File fileInput, File fileOutput){
        int attributeRepetitionsCounter = 0;
        int specificAttributeRepetitionCounter;
        String[] attributeValues = getAttributeValuesArrayFromFile(fileInput);

        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileInput));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileOutput));

            String line = bufferedReader.readLine();
            String[] lineAttributeValues;
            String[] arrayAttributeValues;

            while(line != null){
                specificAttributeRepetitionCounter = 0;
                lineAttributeValues = line.split(";");
                for(String attributeValue: attributeValues){
                    arrayAttributeValues = attributeValue.split(";");
                    if(lineAttributeValues[1].equals(arrayAttributeValues[1])){
                        // the first time the attribute is matched with itself
                        if(specificAttributeRepetitionCounter >= 1){
                            bufferedWriter.write(lineAttributeValues[0]);
                            bufferedWriter.newLine();
                        }
                        specificAttributeRepetitionCounter++;
                        attributeRepetitionsCounter++;
                    }
                }
                // I decrease counter value by 1 because there will be a match between an attribute value and itself
                attributeRepetitionsCounter--;

                line = bufferedReader.readLine();
            }

            bufferedReader.close();
            bufferedWriter.close();
        }catch(IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return attributeRepetitionsCounter;
    }

    public List<String> extractDistinctAttributeList(File fileInput, int fieldContainingTarget){
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
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        closeR();
        return distinctAttributeList;
    }


    public int samplinglinesCSV(File fileInput, File fileOutput, int copyLineEveryNLines){
        initializeRW(fileInput, fileOutput);
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
        closeRW();
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
