package it.unipi.dii.utilities;

import java.io.*;

public class Converter
{
    public void convertCSVintoJSON(File fileInputTargetNutrientTargetFood, File fileInputTargetNutrients, File fileOutput)
    {
        final int nutrientIdIndex = 1;
        fileOutput.delete();
        try {
            BufferedReader bufReader1 = new BufferedReader(new FileReader(fileInputTargetNutrientTargetFood));
            BufferedReader bufReader2 = new BufferedReader(new FileReader(fileInputTargetNutrients));
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter(fileOutput));
            String line = bufReader1.readLine();
            String[] tokens;

            while (line != null)
            {
                tokens = line.split(",");



                line = bufReader1.readLine();

                bufReader1.close();
                bufReader2.close();
                bufWriter.close();
            }

        }catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


    }
}
