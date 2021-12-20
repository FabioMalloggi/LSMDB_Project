package it.unipi.dii.utilities;

import it.unipi.dii.entities.Nutrient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class HandlerFood
{
    private final int NUTRIENT_ID_POSITION_IN_TARGET_NUTRIENTS = 0;
    private final int NUTRIENT_NAME_POSITION_IN_TARGET_NUTRIENTS = 1;
    private final int NUTRIENT_UNIT_POSITION_IN_TARGET_NUTRIENTS = 2;
    private final int NUTRIENT_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD = 1;
    private final int FOOD_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD = 0;
    private final int QUANTITY_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD = 2;
    private final int FOOD_ID_POSITION_IN_TARGET_FOOD = 0;
    private final int FOOD_NAME_POSITION_IN_TARGET_FOOD = 1;

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

    public String getFoodNameFromFile(File fileInput, String id)
    {
        try{
            BufferedReader bufReader = new BufferedReader(new FileReader(fileInput));

            String line = bufReader.readLine();
            String[] attributes;

            while(line != null){
                attributes = line.split(",");

                // I remove the character ' " ' from the attributes value
                for(int i=0; i<attributes.length; i++)
                    attributes[i] = attributes[i].replace("\"", "");

                if(id.equals(attributes[FOOD_ID_POSITION_IN_TARGET_FOOD])){
                    bufReader.close();
                    return attributes[FOOD_NAME_POSITION_IN_TARGET_FOOD];
                }

                line = bufReader.readLine();
            }

            bufReader.close();
        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        // error case
        return null;
    }

    public JSONObject createJSONFoodsFromFile1(File fileInputTargetNutrientTargetFood, File fileInputTargetNutrients,
                                          File fileInputTargetFood)
    {
        String last_id;
        Nutrient row_nutrient;

        JSONArray jsonFoods = new JSONArray();
        String line;
        String[] attributes;
        JSONObject jsonNutrient;
        JSONObject jsonFood;
        JSONArray jsonNutrients;
        JSONObject jsonObject = null;

        try
        {
            BufferedReader bufReader = new BufferedReader(new FileReader(fileInputTargetNutrientTargetFood));

            jsonFood = new JSONObject();
            jsonNutrients = new JSONArray();

            line = bufReader.readLine();
            last_id = "";

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
                    jsonFood.put("name", getFoodNameFromFile(fileInputTargetFood,
                                attributes[FOOD_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD]));

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

            jsonObject = new JSONObject();
            jsonObject.put("foods", jsonFoods);

            bufReader.close();
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return jsonObject;
    }

    public JSONObject createJSONFoodsFromFile2(File fileInput)
    {
        try {
            BufferedReader bufReader = new BufferedReader(new FileReader(fileInput));

            String line = bufReader.readLine();
            String[] attributes;

            while(line != null){
                    attributes = line.split(",");
            }
        }catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static void createJSON()
    {
        File fileTargetNutrients = new File("./data/original/nutrients_target.csv");
        File fileTargetNutrientTargetFoodPer100g = new File("./data/derived/TargetNutrientTargetFoodPer100g.csv");
        File fileTargetFood1 = new File("./data/derived/TargetFood.csv");
        File fileTargetFood2WithCommaSeparators = new File("./data/original/foodsDB2.csv");
        File fileTargetFood2WithSemicolonSeparators = new File("./data/original/foodsDB2newVersion.csv");
        File fileJSONFoods = new File("./data/derived/JSONFoods");
        HandlerFood foodHandler = new HandlerFood();
        OperationsCSV operationsCSV = new OperationsCSV();

        //JSONObject fileJSON = foodHandler.createJSONFoodsFromFile1(fileTargetNutrientTargetFoodPer100g,
        //                                                        fileTargetNutrients, fileTargetFood1);
        operationsCSV.changeFileSeparators(fileTargetFood2WithCommaSeparators, fileTargetFood2WithSemicolonSeparators,
                                            ',', ';', '"');

        /*fileJSONFoods.delete();

        try {
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter(fileJSONFoods));
        }
        catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
*/

    }

    public static void main(String[] args) throws IOException
    {
        createJSON();

/*        Food food1 = new Food("123");
        Nutrient nutrient1 = new Nutrient("Energy", "MG", 19.2);
        Nutrient nutrient2 = new Nutrient("Protein", "UG", 0.00231);
        food1.addNutrient(nutrient1);
        food1.addNutrient(nutrient2);
        System.out.println(food1.toJSON().toString());
 */
    }
}
