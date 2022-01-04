package it.unipi.dii.utilities;

import it.unipi.dii.dietmanager.entities.Food;
import it.unipi.dii.dietmanager.entities.Nutrient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HandlerFood
{
    private final int NUTRIENT_ID_POSITION_IN_TARGET_NUTRIENTS = 0;
    private final int NUTRIENT_NAME_POSITION_IN_TARGET_NUTRIENTS = 1;
    private final int NUTRIENT_UNIT_POSITION_IN_TARGET_NUTRIENTS = 2;
    private final int NUTRIENT_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD = 1;
    private final int FOOD_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD = 0;
    private final int FOOD_CATEGORY_POSITION_IN_TARGET_FOOD = 2;
    private final int QUANTITY_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD = 2;
    private final int FOOD_ID_POSITION_IN_TARGET_FOOD = 0;
    private final int FOOD_NAME_POSITION_IN_TARGET_FOOD = 1;
    private final int FOOD_CATEGORY_POSITION_IN_FOOD_DB2 = 1;
    private final int FOOD_NAME_POSITION_IN_FOOD_DB2 = 2;
    private final int FOOD_ID_POSITION_IN_FOOD_DB2 = 0;
    private final double CONVERTION_COEFFICIENT_FROM_IU_TO_UG = 0.6;
    private final int[] TARGET_NUTRIENT_INDEXES_DB2 = {7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 21, 22, 25, 26, 27, 29};
    private final String[] TARGET_NUTRIENT_NAMES_DB2 =
                    {/*0*/"Energy",/*1*/"Protein",/*2*/"Fat",/*3*/"Carb",/*4*/"Sugar",
                    /*5*/"Fiber",/*6*/"VitA",/*7*/"VitB6",/*8*/"VitB12",/*9*/"VitC",/*10*/"VitE",
                    /*11*/"Thiamin",/*12*/"Calcium",/*13*/"Magnesium",/*14*/"Manganese",/*15*/"Phosphorus",
                    /*16*/"Zinc"};
    private final String[] TARGET_NUTRIENT_UNITS_DB2 =
                    {/*0*/"KCAL",/*1*/"G",/*2*/"G",/*3*/"G",/*4*/"G",
                    /*5*/"G",/*6*/"UG",/*7*/"MG",/*8*/"UG",/*9*/"MG",/*10*/"MG",
                    /*11*/"MG",/*12*/"MG",/*13*/"MG",/*14*/"MG",/*15*/"MG",
                    /*16*/"MG"};

    File fileTargetNutrients = new File("./data/original/nutrients_target.csv");
    File fileTargetNutrientTargetFoodPer100g = new File("./data/derived/TargetNutrientTargetFoodPer100g.csv");
    File fileTargetFood1 = new File("./data/derived/TargetFood.csv");
    File fileTargetFood2 = new File("./data/original/foodsDB2Filtered.csv");
    File fileTargetFood1WithCommaSeparators = new File("./data/derived/foodsDB1WithCommaSeparators.csv");
    File fileTargetFood1WithSemicolonSeparators = new File("./data/derived/foodsDB1WithSemicolonSeparators.csv");
    File fileTargetFood2WithCommaSeparators = new File("./data/derived/foodsDB2WithCommaSeparators.csv");
    File fileTargetFood2WithSemicolonSeparators = new File("./data/derived/foodsDB2WithSemicolonSeparators.csv");
    File fileJSONFoods = new File("./data/json/foods.json");
    File fileAttributeFoodNames = new File("./data/derived/foodNames.csv");
    File fileAttributesRepetitions = new File("./data/derived/attributesRepetitions.csv");
    OperationsCSV operationsCSV;

    public HandlerFood(){
        operationsCSV = new OperationsCSV();
    }

    public String getAttributeValueFromFile(File fileInput, String id, int attributePosition)
    {
        try{
            BufferedReader bufReader = new BufferedReader(new FileReader(fileInput));

            String line = bufReader.readLine();
            String[] attributes;

            while(line != null){
                attributes = line.split(";");

                // I remove the character ' " ' from the attributes value
                for(int i=0; i<attributes.length; i++)
                    attributes[i] = attributes[i].replace("\"", "");

                if(id.equals(attributes[FOOD_ID_POSITION_IN_TARGET_FOOD])){
                    bufReader.close();
                    return attributes[attributePosition];
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

    public JSONArray createJSONFoodsFromFile1(File fileInputTargetNutrientTargetFood, File fileInputTargetNutrients, File fileInputAttributesToDrop,
                                          File fileInputTargetFood)
    {
        String last_id;
        Nutrient row_nutrient;
        Boolean toDrop = false;
        double quantity;
        String unit;
        boolean firstInsertion = true;

        JSONArray jsonFoods = new JSONArray();
        String line;
        String[] attributes;
        Nutrient nutrient;
        List<Nutrient> nutrients = new ArrayList<>();
        Food food;
        String foodName = null, foodCategory = null;

        // I get repeated food names array
        String[] repeatedFoodNames = operationsCSV.getAttributeValuesArrayFromFile(fileInputAttributesToDrop);

        try
        {
            BufferedReader bufReader = new BufferedReader(new FileReader(fileInputTargetNutrientTargetFood));

            line = bufReader.readLine();
            last_id = "";

            while (line != null)
            {
                attributes = line.split(",");

                // I check if I have to drop the line
                for(int i=0; i<repeatedFoodNames.length; i++){
                    if(repeatedFoodNames[i].equals(attributes[FOOD_ID_POSITION_IN_TARGET_FOOD])){
                        toDrop = true;
                        break;
                    }
                }

                if(toDrop){
                    toDrop = false;
                    line = bufReader.readLine();
                    continue;
                }

                // I remove the character ' " ' from the attributes value
                for(int i=0; i<attributes.length; i++)
                    attributes[i] = attributes[i].replace("\"", "");

                // for each nutrient I get the associated nutrient object
                row_nutrient = getNutrient(fileInputTargetNutrients,
                                            attributes[NUTRIENT_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD]);

                // I check if the food ID of row that I'm scanning is already present
                if(attributes[FOOD_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD].equals(last_id))
                {
                    quantity = Double.parseDouble(attributes[QUANTITY_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD]);
                    // if "quantity" is 0 I don't add the nutrient into the JSON document
                    if(quantity != 0.0){
                        unit = row_nutrient.getUnit();
                        // if unit is "IU" I have to convert the quantity in such a way to obtain unit "UG"
                        if(unit.equals("IU")){
                            unit = "UG";
                            quantity = quantity * CONVERTION_COEFFICIENT_FROM_IU_TO_UG;
                        }
                        nutrient = new Nutrient(row_nutrient.getName(), unit, quantity);
                        nutrients.add(nutrient);
                    }
                }
                else
                {
                    if(!firstInsertion){
                        // if category is empty I don't add "category" element into JSON document
                        if(!foodCategory.isEmpty())
                            food = new Food(foodName, foodCategory, nutrients, 0);
                        else
                            food = new Food(foodName, nutrients, 0);

                        // I insert the nutrients array of the last food in the associated food object
                        jsonFoods.put(food.toJSONObject());
                    }
                    firstInsertion = false;
                    last_id = attributes[FOOD_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD];

                    // food ID is not already present, hence I create new food object
                    foodName = getAttributeValueFromFile(fileInputTargetFood,
                            attributes[FOOD_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD], FOOD_NAME_POSITION_IN_TARGET_FOOD);
                    foodCategory = getAttributeValueFromFile(fileInputTargetFood,
                            attributes[FOOD_ID_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD], FOOD_CATEGORY_POSITION_IN_TARGET_FOOD);

                    quantity = Double.parseDouble(attributes[QUANTITY_POSITION_IN_TARGET_NUTRIENTS_TARGET_FOOD]);
                    // if "quantity" is 0 I don't add the nutrient into the JSON document
                    if(quantity != 0.0){
                        unit = row_nutrient.getUnit();
                        // if unit is "IU" I have to convert the quantity in such a way to obtain unit "UG"
                        if(unit.equals("IU")){
                            unit = "UG";
                            quantity = quantity * CONVERTION_COEFFICIENT_FROM_IU_TO_UG;
                        }
                        nutrient = new Nutrient(row_nutrient.getName(), unit, quantity);
                        nutrients = new ArrayList<>();
                        nutrients.add(nutrient);
                    }
                }
                line = bufReader.readLine();
            }
            bufReader.close();
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return jsonFoods;
    }

    public JSONArray insertJSONFoodsFromFile2(File fileInput, File fileInputAttributesToDrop, JSONArray jsonFoods)
    {
        String[] repeatedFoodNames = operationsCSV.getAttributeValuesArrayFromFile(fileInputAttributesToDrop);

        try {
            BufferedReader bufReader = new BufferedReader(new FileReader(fileInput));
            String[] attributes;
            String foodName, foodCategory, nutrientName, nutrientUnit;
            double nutrientQuantity;
            List<Nutrient> nutrients = new ArrayList<>();
            Food food;
            int j=0;

            String line = bufReader.readLine();
            boolean toDrop = false;

            // I skip the header line
            line = bufReader.readLine();

            while(line != null){
                attributes = line.split(";");
                System.out.println(++j);

                // if a line contains at least 3 times the character '"', then I drop the line
                if(line.length() - line.replace("\"", "").length() >= 3)
                {
                    line = bufReader.readLine();
                    continue;
                }
                // I check if I have to drop the line
                for(int i=0; i<repeatedFoodNames.length; i++){
                    if(repeatedFoodNames[i].equals(attributes[FOOD_ID_POSITION_IN_FOOD_DB2])){
                        toDrop = true;
                        break;
                    }
                }
                if(toDrop){
                    toDrop = false;
                    line = bufReader.readLine();
                    continue;
                }

                // I remove the character ' " ' from the attributes value
                for(int i=0; i<attributes.length; i++)
                    attributes[i] = attributes[i].replace("\"", "");

                foodName = attributes[FOOD_NAME_POSITION_IN_FOOD_DB2];
                foodCategory = attributes[FOOD_CATEGORY_POSITION_IN_FOOD_DB2];

                for(int i=0; i<TARGET_NUTRIENT_INDEXES_DB2.length; i++){
                    nutrientQuantity = Double.parseDouble(attributes[TARGET_NUTRIENT_INDEXES_DB2[i]]);
                    if(nutrientQuantity != 0.0){
                        nutrientName = TARGET_NUTRIENT_NAMES_DB2[i];
                        nutrientUnit = TARGET_NUTRIENT_UNITS_DB2[i];
                        nutrients = new ArrayList<>();
                        nutrients.add(new Nutrient(nutrientName, nutrientUnit, nutrientQuantity));
                    }
                }
                // if category is empty I don't add "category" element into JSON document
                if(!foodCategory.isEmpty())
                    food = new Food(foodName, foodCategory, nutrients, 0);
                else
                    food = new Food(foodName, nutrients, 0);

                if(j==915)
                {
                    System.out.println("PRINT");
                }

                JSONObject tmp = food.toJSONObject();
                jsonFoods.put(food.toJSONObject());
                line = bufReader.readLine();
            }
            bufReader.close();
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return jsonFoods;
    }

    public void createInputFile(){
        File originalFood = new File("./data/original/Food.csv");
        operationsCSV.samplinglinesCSV(originalFood, fileTargetFood1, 1000);

        // I delete ';' in original file
        operationsCSV.replaceCharactersInFile(fileTargetFood1, fileTargetFood1WithCommaSeparators, ';', ' ');
        operationsCSV.replaceCharactersInFile(fileTargetFood2, fileTargetFood2WithCommaSeparators, ';', ' ');

        operationsCSV.changeFileSeparators(fileTargetFood1WithCommaSeparators, fileTargetFood1WithSemicolonSeparators,
                ',', ';', '"');
        operationsCSV.changeFileSeparators(fileTargetFood2WithCommaSeparators, fileTargetFood2WithSemicolonSeparators,
                ',', ';', '"');

        operationsCSV.insertIntoFileAttributesFrom2Files(fileTargetFood1WithSemicolonSeparators, fileTargetFood2WithSemicolonSeparators,
                                                0, 1, 0, 2, fileAttributeFoodNames);
        System.out.println(operationsCSV.writeToFileAttributeRepetitions(fileAttributeFoodNames, fileAttributesRepetitions));
    }

    public void createJSON()
    {
        //JSONArray jsonFoods = createJSONFoodsFromFile1(fileTargetNutrientTargetFoodPer100g,
        //                                                    fileTargetNutrients, fileAttributesRepetitions, fileTargetFood1WithSemicolonSeparators);
        System.out.println("FINITO FILE 1");
        JSONArray jsonFoods = new JSONArray();
        JSONArray jsonFile = insertJSONFoodsFromFile2(fileTargetFood2WithSemicolonSeparators, fileAttributesRepetitions, jsonFoods);

        //System.out.println(jsonFile.toString());
        fileJSONFoods.delete();

        try {
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter(fileJSONFoods));

            bufWriter.write(jsonFile.toString());
            bufWriter.close();
        }
        catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException
    {
        HandlerFood handlerFood = new HandlerFood();

        //handlerFood.createInputFile();
        handlerFood.createJSON();
    }
}
