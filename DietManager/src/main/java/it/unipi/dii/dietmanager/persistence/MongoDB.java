package it.unipi.dii.dietmanager.persistence;

import com.mongodb.ConnectionString;
import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;
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

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;

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

    public void createSimpleIndex(String collectionName, String attributeName, boolean isAttributeAscending){
        openConnection();
        int sortingOrder = isAttributeAscending ? 1:-1;
        //Retrieving the collection on which you want to create the index
        MongoCollection<Document> collection = database.getCollection(collectionName);
        //Creating an index
        collection.createIndex(new Document(attributeName, sortingOrder));
/*        System.out.println("Index created successfully");
        //Printing the list of indices in the collection
        for (Document index : collection.listIndexes()) {
            System.out.println(index.toJson());
        }
 */
        closeConnection();
    }

    public void createCompoundIndex(String collectionName, String firstAttributeName, boolean isFirstAttributeAscending,
                                    String secondAttributeName, boolean isSecondAttributeAscending){
        openConnection();
        int firstAttributeSortingOrder = isFirstAttributeAscending ? 1:-1;
        int secondAttributeSortingOrder = isSecondAttributeAscending ? 1:-1;
        //Retrieving the collection on which you want to create the index
        MongoCollection<Document> collection = database.getCollection(collectionName);
        //Creating an index
        collection.createIndex(new Document(firstAttributeName, firstAttributeSortingOrder).append(secondAttributeName, secondAttributeSortingOrder));
/*        System.out.println("Index created successfully");
        //Printing the list of indices in the collection
        for (Document index : collection.listIndexes()) {
            System.out.println(index.toJson());
        }
*/
        closeConnection();
    }

    public void createCompoundPartialIndex(String collectionName, String firstAttributeName, String firstAttributeValue,
                                           String secondAttributeName, boolean isSecondAttributeAscending){
        openConnection();
        int secondAttributeSortingOrder = isSecondAttributeAscending ? 1:-1;
        MongoCollection<Document> collection = database.getCollection(collectionName);
        IndexOptions partialFilterIndexOptions = new IndexOptions()
                .partialFilterExpression(Filters.eq(firstAttributeName, firstAttributeValue));
        collection.createIndex(new Document(secondAttributeName, secondAttributeSortingOrder), partialFilterIndexOptions);
        closeConnection();
    }

    public boolean openConnection()
    {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:" + localhostPort);
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase("dietManagerDB");
        database.withWriteConcern(WriteConcern.ACKNOWLEDGED);   // Write Concern to wait for acknowledgement according to Server configuration.
                                                                // Needed for wasAcknowledged() methods.
                                                                // other options are possible.
        database.withReadConcern(ReadConcern.DEFAULT);          // Use the servers default read concern.
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
    public User lookUpUserByUsername(String username){
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
        User userToRemove = lookUpUserByUsername(username);
        if(userToRemove == null)
            return false;
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        DeleteResult deleteResult = usersCollection.deleteOne(eq(User.USERNAME, username));
        boolean isSuccessful = deleteResult.wasAcknowledged();

        // if user removed was a Nutritionist, all his diets are deleted altogether.
        if(isSuccessful && userToRemove instanceof Nutritionist){
            isSuccessful = removeDietsByNutritionist(username);
        }
        closeConnection();
        return isSuccessful;
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

    // for both lookUpDietByID and lookUpStandardUserCurrentDiet
    public Diet lookUpDietByID(String id){
        openConnection();
        Document dietDocument = null;
        try {
            MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
            dietDocument = dietCollection.find(eq(Diet.ID, new ObjectId(id))).first(); // there can be at least only 1 match.
        }catch(Exception e){}
        closeConnection();
        return dietFromDocument(dietDocument);
    }

    public List<Diet> lookUpDietByName(String subname){    // RETURN ALL DIETS whose name include substring seached.
        openConnection();
        String regex = "\\.\\*"+subname+"\\.\\*";
        List<Diet> diets = new ArrayList<>();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        try(MongoCursor<Document> cursor = dietCollection.find(
                eq(Diet.NAME, regex)).iterator()){
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
        bulkOperations.addAll(Arrays.asList(updateRemoveCurrentDietDocument,updateRemoveEatenFoodsDocument));
        BulkWriteResult bulkWriteResult = userCollection.bulkWrite(bulkOperations);
        closeConnection();
        return bulkWriteResult.wasAcknowledged();
    }

    public boolean addDiet(Diet diet){
        openConnection();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        Document dietDocument = dietToDocument(diet);
        InsertOneResult insertOneResult = dietCollection.insertOne(dietDocument);
        // adding '_id' to diet object passed as argument
        diet.setId(dietDocument.getObjectId("_id").toString());
        closeConnection();
        return insertOneResult.wasAcknowledged();
    }

    public boolean removeDiet(String dietID){
        openConnection();
        // deleting 'diet' document from 'diets' collection
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        DeleteResult deleteDietResult = dietCollection.deleteOne(eq(Diet.ID, dietID));
        boolean isSuccessful = deleteDietResult.wasAcknowledged();

        // deleting 'currentDiet' field from each 'user' document which has as 'currentDiet' value the target diet to remove
        if(isSuccessful){
            MongoCollection<Document> userCollection = database.getCollection(COLLECTION_USERS);

            Bson userFilter = Filters.eq( StandardUser.CURRENT_DIET, dietID );
            Bson deleteCurrentDietField = Updates.unset(StandardUser.CURRENT_DIET);
            Bson deleteEatenFoodsField = Updates.unset(StandardUser.EATENFOODS);

            UpdateManyModel<Document> updateRemoveCurrentDietDocument = new UpdateManyModel<>(
                    userFilter, deleteCurrentDietField);
            UpdateManyModel<Document> updateRemoveEatenFoodsDocument = new UpdateManyModel<>(
                    userFilter, deleteEatenFoodsField);

            List<WriteModel<Document>> bulkOperations = new ArrayList<>();
            bulkOperations.addAll(Arrays.asList(updateRemoveCurrentDietDocument,updateRemoveEatenFoodsDocument));
            BulkWriteResult bulkWriteResult = userCollection.bulkWrite(bulkOperations);
            isSuccessful = bulkWriteResult.wasAcknowledged();
        }
        closeConnection();
        return isSuccessful;
    }

    private boolean removeDietsByNutritionist(String username){
        List<Diet> dietsToRemove = lookUpDietByNutritionist(username);
        boolean isSuccessful = true, isCurrentSuccessful;
        for(Diet diet: dietsToRemove){
            isCurrentSuccessful = removeDiet(diet.getId());
            if(isCurrentSuccessful == false)
                isSuccessful = false;
        }
        return isSuccessful;
    }


    public HashMap<String, Nutrient> lookUpMostSuggestedNutrientForEachNutritionist(){
        openConnection();

        // retrieve documents representing for each combination of Nutritionist-Nutrient, the average quantity of the nutrient
        // that the nutritionist has recommended in its diets.
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        HashMap<String, Nutrient> nutritionistNutrientMap = new HashMap<>();

        Bson dietsUnwindNutrients = unwind(Diet.NUTRIENTS);
        Bson projectionFields = project(
                fields(include(Diet.NUTRITIONIST,Nutrient.QUANTITY, Nutrient.UNIT),computed("nutrientName",Diet.NUTRIENTS)));
        Bson dietsMatchOutEnergyNutrient = match(
                Filters.eq("nutrientName", "Energy"));
        Bson groupByNutrientAndNutritionist = group(
                new Document(Diet.NUTRITIONIST, "$"+Diet.NUTRITIONIST).append(Diet.NUTRIENTS,"$"+Diet.NUTRIENTS).append(Nutrient.UNIT,"$"+Nutrient.UNIT),
                Accumulators.avg("averageQuantity", "$"+Nutrient.QUANTITY));
        closeConnection();

        // analyze for each nutritionist which nutrient he has recommended the most.
        Document currentDocument;
        List<String> nutritionists = new ArrayList<>();
        List<String> nutrients = new ArrayList<>();
        List<String> units = new ArrayList<>();
        List<Double> quantities = new ArrayList<>();
        try(MongoCursor<Document> cursor = dietCollection.aggregate(Arrays.asList(dietsUnwindNutrients,
                                                projectionFields,
                                                dietsMatchOutEnergyNutrient,
                                                groupByNutrientAndNutritionist)).iterator()){
            while(cursor.hasNext()){
                currentDocument = cursor.next();
                nutritionists.add(currentDocument.getString(Diet.NUTRITIONIST));
                nutrients.add(currentDocument.getString("nutrientName"));
                units.add(currentDocument.getString(Nutrient.UNIT));
                quantities.add(currentDocument.getDouble(Nutrient.QUANTITY));
            }
        }

        return nutritionistNutrientMap;


    }






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
        MongoCollection<Document> foodCollection = database.getCollection(COLLECTION_FOODS);
        try(MongoCursor<Document> cursor = foodCollection.find(
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
        MongoCollection<Document> foodCollection = database.getCollection(COLLECTION_FOODS);

        try(MongoCursor<Document> cursor = foodCollection.find(
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
        // add id to EatenFood object
        // update eatenTimesCount field.
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

        mongoDB.createSimpleIndex(mongoDB.COLLECTION_DIETS, Diet.NUTRITIONIST, true); // 1st index
        mongoDB.createSimpleIndex(mongoDB.COLLECTION_USERS, User.USERTYPE, true); // 2nd index
        mongoDB.createCompoundPartialIndex(mongoDB.COLLECTION_USERS, User.USERTYPE, User.USERTYPE_NUTRITIONIST,
                                        User.COUNTRY, true);// 3rd index (OPZIONE A)
        mongoDB.createCompoundIndex(mongoDB.COLLECTION_USERS, User.USERTYPE, true, User.COUNTRY, true); //3rd index (OPZIONE B)
        mongoDB.createCompoundIndex(mongoDB.COLLECTION_FOODS, Food.CATEGORY, true,
                                        Food.EATEN_TIMES_COUNT, false); // 5th index
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