package it.unipi.dii.dietmanager.persistence;

import com.mongodb.ConnectionString;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import it.unipi.dii.dietmanager.entities.*;

import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class MongoDB{

    private MongoClient mongoClient;
    private MongoDatabase database;

    private final int localhostPort;

    private final String COLLECTION_USERS = "users";
    private final String COLLECTION_DIETS = "diets";
    private final String COLLECTION_FOODS = "foods";

    public MongoDB(int localhostPort)
    {
        this.localhostPort = localhostPort;

    }

    public boolean openConnection()
    {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:" + localhostPort);
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase("dietManagerDB");
        return true;
    }

    public void closeConnection()
    {
        try{
            mongoClient.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*************************** Users related methods **********************************/

    private User userFromJSONObject(JSONObject jsonUser){
        String userType = jsonUser.getString(User.USERTYPE);
        if(userType.equals(User.USERTYPE_STANDARDUSER))
            return StandardUser.fromJSONObject(jsonUser);

        if(userType.equals(User.USERTYPE_NUTRITIONIST))
            return Nutritionist.fromJSONObject(jsonUser);

        if(userType.equals(User.USERTYPE_ADMINISTRATOR))
            return Administrator.fromJSONObject(jsonUser);

        return null;
    }

    private User userFromDocument(Document userDocument){
        if(userDocument == null)
            return null;
        JSONObject jsonUser = new JSONObject(userDocument.toString());
        return userFromJSONObject(jsonUser);
    }

    private Document userToDocument(User user){
        return Document.parse(user.toJSONObject().toString());
    }

    // for both signIn and lookUpUserbyUsername
    public User lookUpUserByID(String username){
        openConnection();
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        Document userDocument = usersCollection.find(eq(User.USERNAME, new ObjectId(username))).first(); // there can be at least only 1 match.
        closeConnection();
        return userFromDocument(userDocument);
    }

    public boolean addUser(User user){
        openConnection();
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        InsertOneResult insertOneResult = usersCollection.insertOne(userToDocument(user));
        closeConnection();
        return insertOneResult.wasAcknowledged();
    }

    public boolean removeUser(String username){
        openConnection();
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        DeleteResult deleteResult = usersCollection.deleteOne(eq(User.USERNAME, username));
        closeConnection();
        return deleteResult.wasAcknowledged();
    }

    public List<Nutritionist> lookUpNutritionistsByCountry(String country){
        openConnection();
        List<Nutritionist> nutritionists = new ArrayList<>();
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        try(MongoCursor<Document> cursor = usersCollection.find(
                and(eq(User.COUNTRY, country),eq(User.USERNAME, User.USERTYPE_NUTRITIONIST))).iterator()){
            while(cursor.hasNext())
                nutritionists.add((Nutritionist) userFromDocument(cursor.next()));
        }
        closeConnection();
        return nutritionists;
    }

    public List<User> lookUpAllUsersByCountry(String country){
        openConnection();
        List<User> users = new ArrayList<>();
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        try(MongoCursor<Document> cursor = usersCollection.find(
                eq(User.COUNTRY, country)).iterator()){
            while(cursor.hasNext()){
                users.add(userFromDocument(cursor.next()));
            }
        }
        closeConnection();
        return users;
    }

    /************************************************************************************/
    /*************************** Diets related methods **********************************/

    private Diet dietFromDocument(Document dietDocument) {
        if(dietDocument == null)
            return null;
        JSONObject jsonDiet = new JSONObject(dietDocument.toString());
        return Diet.fromJSONObject(jsonDiet);
    }

    private Document dietToDocument(Diet diet){
        return Document.parse(diet.toJSONObject().toString());
    }

    public Diet lookUpDietByID(String id){
        openConnection();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        Document dietDocument = dietCollection.find(eq(Diet.ID, new ObjectId(id))).first(); // there can be at least only 1 match.
        closeConnection();
        return dietFromDocument(dietDocument);
    }

    public List<Diet> lookUpDietByName(String name){
        openConnection();
        List<Diet> diets = new ArrayList<>();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        try(MongoCursor<Document> cursor = dietCollection.find(
                eq(Diet.NAME, name)).iterator()){
            while(cursor.hasNext()){
                diets.add(dietFromDocument(cursor.next()));
            }
        }
        closeConnection();
        return diets;
    }

    public List<Diet> lookUpDietByNutritionist(String username){
        openConnection();
        List<Diet> diets = new ArrayList<>();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        try(MongoCursor<Document> cursor = dietCollection.find(
                eq(Diet.NUTRITIONIST, username)).iterator()){
            while(cursor.hasNext()){
                diets.add(dietFromDocument(cursor.next()));
            }
        }
        closeConnection();
        return diets;
    }

    public boolean followDiet(StandardUser standardUser, String dietID){
        openConnection();
        // verifica esistenza Dieta
        MongoCollection<Document> userCollection = database.getCollection(COLLECTION_USERS);
        Bson userFilter = Filters.eq( User.USERNAME, standardUser.getUsername() );
        Bson insertCurrentDietField = Updates.set(StandardUser.CURRENT_DIET, dietID);
        UpdateResult updateResult = userCollection.updateOne(userFilter, insertCurrentDietField);
        closeConnection();
        return updateResult.wasAcknowledged();
    }

    // it can be called either 'unfollowDiet' or 'stopDiet'
    public boolean unfollowDiet(StandardUser standardUser){
        openConnection();
        MongoCollection<Document> userCollection = database.getCollection(COLLECTION_USERS);

        Bson userFilter = Filters.eq( User.USERNAME, standardUser.getUsername() );
        Bson deleteCurrentDietField = Updates.unset(StandardUser.CURRENT_DIET);
        Bson deleteEatenFoodsField = Updates.unset(StandardUser.EATENFOODS);

        UpdateOneModel<Document> updateRemoveCurrentDietDocument = new UpdateOneModel<>(
                userFilter, deleteCurrentDietField);
        UpdateOneModel<Document> updateRemoveEatenFoodsDocument = new UpdateOneModel<>(
                userFilter, deleteEatenFoodsField);

        List<WriteModel<Document>> bulkOperations = new ArrayList<>();
        BulkWriteResult bulkWriteResult = userCollection.bulkWrite(bulkOperations);

        closeConnection();
        return bulkWriteResult.wasAcknowledged();
    }

    public boolean addDiet(Diet diet){
        openConnection();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        InsertOneResult insertOneResult = dietCollection.insertOne(dietToDocument(diet));
        closeConnection();
        return insertOneResult.wasAcknowledged(); // before: lookUpDietByID(diet.getId()) != null
    }

    public boolean removeDiet(String dietID){
        openConnection();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        DeleteResult deleteResult = dietCollection.deleteOne(eq(Diet.ID, dietID));
        closeConnection();
        return deleteResult.wasAcknowledged();
    }

    /*
    public HashMap<String, Nutrient> lookUpMostSuggestedNutrientForEachNutritionist(){
        openConnection();
        HashMap<String, Nutrient> nutritionistNutrientMap = new HashMap<>();

        Bson dietsUnwindNutrients = unwind(Diet.NUTRIENTS);
        Bson projection1 = project(fields(include(Diet.NUTRITIONIST,Nutrient.QUANTITY),computed("nutrientName",Diet.NUTRIENTS)));
        Bson groupByNutrientAndNutritionist = new Document("$group",
                new Document(Diet.NUTRITIONIST, "$"+Diet.NUTRITIONIST).append(Diet.NUTRIENTS,"$"+Diet.NUTRIENTS)
                .append("totalQuantity", new Document("$sum","$"+Nutrient.QUANTITY)));



        List<Diet> diets = new ArrayList<>();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        try(MongoCursor<Document> cursor = dietCollection.find(
                eq(Diet.NUTRITIONIST, username)).iterator()){
            while(cursor.hasNext()){
                diets.add(dietFromDocument(cursor.next()));
            }
        }
        closeConnection();
        return nutritionistNutrientMap;


    }
    */

    /************************************************************************************/
    /*************************** Foods related methods **********************************/


    private Food foodFromDocument(Document foodDocument) {
        if(foodDocument == null)
            return null;
        JSONObject jsonFood = new JSONObject(foodDocument.toString());
        return Food.fromJSONObject(jsonFood);
    }

    private Document foodToDocument(Food food){
        return Document.parse(food.toJSONObject().toString());
    }

    private EatenFood eatenFoodFromDocument(Document eatenFoodDocument) {
        if(eatenFoodDocument == null)
            return null;
        JSONObject jsonEatenFood = new JSONObject(eatenFoodDocument.toString());
        return EatenFood.fromJSONObject(jsonEatenFood);
    }

    private Document eatenFoodToDocument(EatenFood eatenFood){
        return Document.parse(eatenFood.toJSONObject().toString());
    }

    public List<Food> lookUpFoodsByName(String name){
        openConnection();
        String regex = "\\.\\*"+name+"\\.\\*";

        List<Food> foods = new ArrayList<>();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_FOODS);
        try(MongoCursor<Document> cursor = dietCollection.find(
                eq(Food.NAME, regex)).iterator()){
            while(cursor.hasNext()){
                foods.add(foodFromDocument(cursor.next()));
            }
        }
        closeConnection();
        return foods;
    }

    public Food lookUpMostEatenFoodByCategory(String category){
        openConnection();

        Food currentFood, targetFood = null;
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_FOODS);

        try(MongoCursor<Document> cursor = dietCollection.find(
                eq(Food.CATEGORY, category)).iterator()){
            while(cursor.hasNext()){
                currentFood = foodFromDocument(cursor.next());
                if(targetFood == null || currentFood.getEatenTimesCount() > targetFood.getEatenTimesCount())
                    targetFood = currentFood;
            }
        }
        closeConnection();
        return targetFood;
    }

    public boolean addFood(Food food){
        openConnection();
        MongoCollection<Document> foodCollection = database.getCollection(COLLECTION_FOODS);
        InsertOneResult insertOneResult = foodCollection.insertOne(foodToDocument(food));
        closeConnection();
        return insertOneResult.wasAcknowledged();
    }

    public boolean removeFood(String foodName){
        openConnection();
        MongoCollection<Document> foodCollection = database.getCollection(COLLECTION_FOODS);
        DeleteResult deleteResult = foodCollection.deleteOne(eq(Food.NAME, foodName));
        closeConnection();
        return deleteResult.wasAcknowledged();
    }

    public boolean addEatenFood(StandardUser standardUser, EatenFood eatenFood){
        openConnection();
        MongoCollection<Document> userCollection = database.getCollection(COLLECTION_USERS);

        Bson userFilter = Filters.eq( User.USERNAME, standardUser.getUsername() );
        Bson insertEatenFoodDocument = Updates.push(StandardUser.EATENFOODS, eatenFoodToDocument(eatenFood));
        UpdateResult updateResult = userCollection.updateOne(userFilter, insertEatenFoodDocument);

        closeConnection();
        return updateResult.wasAcknowledged();
    }

    public boolean removeEatenFood(StandardUser standardUser, String eatenFoodID){
        openConnection();
        MongoCollection<Document> userCollection = database.getCollection(COLLECTION_USERS);

        Bson userFilter = Filters.eq( User.USERNAME, standardUser.getUsername() );
        Bson deleteEatenFoodDocument = Updates.pull(StandardUser.EATENFOODS, new Document(EatenFood.ID, eatenFoodID));
        UpdateResult updateResult = userCollection.updateOne(userFilter, deleteEatenFoodDocument);

        closeConnection();
        return updateResult.wasAcknowledged();
    }


    /************************************************************************************/


    public static void main( String... args ) throws Exception {
        MongoDB mongoDB = new MongoDB( 7687);

    }
}






/*
    public MongoDB(List<String> hosts, List<Integer> ports, String username, String authenticationDB, String password ){
        String uriComposition = "mongodb://";
        for(int i=0; i<hosts.size(); i++){
            uriComposition += username + ":" + password + "@" + hosts.get(i) + ":" + ports.get(i);
            if(i < hosts.size()-1)
                uriComposition += ",";
            else
                uriComposition += "/?authSource=" + authenticationDB;
        }
        ConnectionString connectionString = new ConnectionString(uriComposition);
        mongoClient = MongoClients.create(connectionString);
    }

     */

    /*
    // REMIND: poolsize, writing mode (majority ecc) and other options are available at instantiation phase of MongoClient!
    public MongoDB(List<String> hosts, List<Integer> ports, String username, String authenticationDB, String password ){
        char[] passwordCharArray = password.toCharArray();
        if(hosts.size() == ports.size()){
            System.err.println("MongoDB connection error: replicas addresses and replicas corresponding ports must match in size");
            System.exit(1);
        }
        List<ServerAddress> replicas = new ArrayList<>();
        for(int i=0; i< hosts.size(); i++){
            replicas.add(new ServerAddress(hosts.get(i), ports.get(i)));
        }
        MongoCredential credential = MongoCredential.createCredential(username, authenticationDB, passwordCharArray);
        mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder.hosts(replicas))
                        .credential(credential)
                        .build());
    }

     */
