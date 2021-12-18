package it.unipi.dii.utilities;
import it.unipi.dii.entities.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class FoodHandler
{
    private final int NUTRIENT_ID_POSITION_IN_TARGET_NUTRIENTS = 0;
    private final int NUTRIENT_NAME_POSITION_IN_TARGET_NUTRIENTS = 1;
    private final int NUTRIENT_UNIT_POSITION_IN_TARGET_NUTRIENTS = 2;
    private final int NUTRIENT_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD = 1;
    private final int FOOD_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD = 0;
    private final int QUANTITY_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD = 2;

    public Nutrient getNutrient(File fileInput, String targetID)
    {
        String[] attributes;
        String line;

        try
        {
            BufferedReader bufReader = new BufferedReader(new FileReader(fileInput));

            line = bufReader.readLine();

            while (line != null)
            {
                attributes = line.split(",");

                // I remove the character ' " ' from the attributes value
                for(int i=0; i<attributes.length; i++)
                {
                    attributes[i] = attributes[i].replace("\"", "");
                }

                if(attributes[NUTRIENT_ID_POSITION_IN_TARGET_NUTRIENTS].equals(targetID))
                {
                    Nutrient target_nutrient = new Nutrient(attributes[NUTRIENT_NAME_POSITION_IN_TARGET_NUTRIENTS],
                                                attributes[NUTRIENT_UNIT_POSITION_IN_TARGET_NUTRIENTS]);
                    return target_nutrient;
                }
                line = bufReader.readLine();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        // error case
        return null;
    }

    public void createJSONFoodsFile(File fileInputTargetNutrientTargetFood, File fileInputTargetNutrients, File fileOutput)
    {
        fileOutput.delete();

        String last_id;
        Nutrient row_nutrient;

        JSONArray jsonFoods = new JSONArray();
        String line;
        String[] attributes;
        JSONObject jsonNutrient;
        JSONObject jsonFood;
        JSONArray jsonNutrients;
        JSONObject jsonFile = null;

        try
        {
            BufferedReader bufReader = new BufferedReader(new FileReader(fileInputTargetNutrientTargetFood));
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter(fileOutput));

            //NOTE: I skip first line for the sake of semplicity
            line = bufReader.readLine();
            attributes = line.split(",");
            last_id = attributes[FOOD_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD];

            // I remove the character ' " ' from the attributes value
            for(int i=0; i<attributes.length; i++)
            {
                attributes[i] = attributes[i].replace("\"", "");
            }

            // I initialize the first jsonFood object
            jsonFood = new JSONObject();
            jsonFood.put("id", attributes[FOOD_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD]);
            jsonNutrients = new JSONArray();

            while (line != null)
            {
                attributes = line.split(",");

                // I remove the character ' " ' from the attributes value
                for(int i=0; i<attributes.length; i++)
                {
                    attributes[i] = attributes[i].replace("\"", "");
                }

                // for each nutrient I get the associated nutrient object
                row_nutrient = getNutrient(fileInputTargetNutrients,
                                            attributes[NUTRIENT_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD]);


                // I check if the food ID of row that I'm scanning is already present
                if(attributes[FOOD_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD].equals(last_id))
                {
                    jsonNutrient = new JSONObject();
                    jsonNutrient.put("name", row_nutrient.getName());
                    jsonNutrient.put("unit", row_nutrient.getUnit());
                    jsonNutrient.put("quantity", attributes[QUANTITY_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD]);
                    jsonNutrients.put(jsonNutrient);
                }
                else
                {
                    // I insert the nutrients array of the last food in the associated food object
                    jsonFood.put("nutrients", jsonNutrients);
                    jsonFoods.put(jsonFood);

                    last_id = attributes[FOOD_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD];

                    // food ID is not already present, hence I create new food object
                    jsonFood = new JSONObject();
                    jsonFood.put("id", attributes[FOOD_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD]);

                    jsonNutrients = new JSONArray();

                    // I add first element to nutrients array
                    jsonNutrient = new JSONObject();
                    jsonNutrient.put("name", row_nutrient.getName());
                    jsonNutrient.put("unit", row_nutrient.getUnit());
                    jsonNutrient.put("quantity", attributes[QUANTITY_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD]);
                    jsonNutrients.put(jsonNutrient);
                }

                line = bufReader.readLine();
            }

            jsonFile = new JSONObject();
            jsonFile.put("foods", jsonFoods);

            System.out.println(jsonFile.toString());
            bufWriter.write(jsonFile.toString());

            bufReader.close();
            bufWriter.close();

        }catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
