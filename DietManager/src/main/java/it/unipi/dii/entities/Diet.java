package it.unipi.dii.entities;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Diet {
    private String id;
    private String name;
    //private List<String> tags = new ArrayList<>();
    private List<Nutrient> nutrients = new ArrayList<>(); // Per100g
    private User nutritionist;


    public Diet(String id, String name, List<Nutrient> nutrientList, User nutritionist) {
        this.id = id;
        this.name = name;
        this.nutrients.addAll(nutrientList);
        this.nutritionist = nutritionist;
    }

    //private void computeTags() {
    //}

    public static void printDietToFile(File fileOutput, Diet diet) {
/*        JSONObject jsonDiet = new JSONObject();

        jsonDiet.put("_id",diet.id);
        jsonDiet.put("name",diet.name);

        JSONObject jsonNutrient;
        JSONArray jsonNutrientList = new JSONArray();

        for(Nutrient nutrient: diet.nutrients){
            jsonNutrient = new JSONObject();
            jsonNutrient.put("name",nutrient.getName());
            jsonNutrient.put("unit", nutrient.getUnit());
            jsonNutrient.put("quantity", nutrient.getQuantity());
            jsonNutrientList.put(jsonNutrient);
        }

        JSONObject jsonNutritionist = new JSONObject();
        // DA FINIRE.


*/

        JSONObject jsonFile = null;
        try (BufferedWriter bufWriter = new BufferedWriter(new FileWriter(fileOutput, true))) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
