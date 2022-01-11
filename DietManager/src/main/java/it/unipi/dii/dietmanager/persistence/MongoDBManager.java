package it.unipi.dii.dietmanager.persistence;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.dii.dietmanager.entities.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.inc;

public class MongoDBManager {
    private ConnectionString connectionUri;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private final String DIET_MANAGER_DATABASE = "dietManagerDB";
    private final String COLLECTION_USERS = "users";
    private final String COLLECTION_DIETS = "diets";
    private final String COLLECTION_FOODS = "foods";
    
    private boolean onlyOneConnection = false;
    
    private final int EATEN_FOOD_SLOTS_SIZE = 70;

    public MongoDBManager(String ipAddress, int port){
        String uri = "mongodb://" + ipAddress + ":" + port;
        connectionUri = new ConnectionString(uri);
        MongoClientSettings mcs = MongoClientSettings.builder()
                .applyConnectionString(connectionUri)
                .readPreference(ReadPreference.nearest())
                .retryWrites(true)
                .writeConcern(WriteConcern.ACKNOWLEDGED)  // Write Concern to wait for acknowledgement according to Server configuration.
                .build();
        mongoClient = MongoClients.create(mcs);
        disableLogger();
    }
    
    public void openOnlyOneConnection(){
        this.onlyOneConnection = true; 
        openConnection();
    }

    public void closeOnlyOneConnection(){
        closeConnection();
    }

    private void openConnection()
    {
        mongoClient = MongoClients.create(connectionUri);
        database = mongoClient.getDatabase(DIET_MANAGER_DATABASE);
    }

    private void closeConnection() {
        try{
            mongoClient.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void disableLogger(){
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        rootLogger.setLevel(Level.OFF);
    }

    public void dropDatabase(){
        if( ! onlyOneConnection) openConnection();
        database.drop();
        if( ! onlyOneConnection) closeConnection();
    }

    public void createDietManagerIndexes(){
        createSimpleIndex(COLLECTION_DIETS, Diet.NUTRITIONIST, true);
        createSimpleIndex(COLLECTION_USERS, User.COUNTRY, true);
        createCompoundIndex(COLLECTION_FOODS, Food.CATEGORY, true,
                Food.EATEN_TIMES_COUNT, false);
    }

    private void createSimpleIndex(String collectionName, String attributeName, boolean isAttributeAscending){
        if( ! onlyOneConnection) openConnection();
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
        if( ! onlyOneConnection) closeConnection();
    }

    private void createCompoundIndex(String collectionName, String firstAttributeName, boolean isFirstAttributeAscending,
                                    String secondAttributeName, boolean isSecondAttributeAscending){
        if( ! onlyOneConnection) openConnection();
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
        if( ! onlyOneConnection) closeConnection();
    }

    // for future usage: create compound partial index
    private void createCompoundPartialIndex(String collectionName, String firstAttributeName, String firstAttributeValue,
                                           String secondAttributeName, boolean isSecondAttributeAscending){
        if( ! onlyOneConnection) openConnection();
        int secondAttributeSortingOrder = isSecondAttributeAscending ? 1:-1;
        MongoCollection<Document> collection = database.getCollection(collectionName);
        IndexOptions partialFilterIndexOptions = new IndexOptions()
                .partialFilterExpression(Filters.eq(firstAttributeName, firstAttributeValue));
        collection.createIndex(new Document(secondAttributeName, secondAttributeSortingOrder), partialFilterIndexOptions);
        if( ! onlyOneConnection) closeConnection();
    }

    /*************************** Users related methods **********************************/

    private User userFromJSONObject(JSONObject jsonUser){
        String userType = jsonUser.getString(User.USERTYPE);
        if(userType.equals(User.USERTYPE_STANDARDUSER)){
            StandardUser mongoStandardUser = StandardUser.fromJSONObject(jsonUser);
            return userFromEatenFoodUserMongoAllocation(mongoStandardUser);
        }
        if(userType.equals(User.USERTYPE_NUTRITIONIST))
            return Nutritionist.fromJSONObject(jsonUser);

        if(userType.equals(User.USERTYPE_ADMINISTRATOR))
            return Administrator.fromJSONObject(jsonUser);

        return null;
    }

    private User userFromDocument(Document userDocument){
        if(userDocument == null)
            return null;
        JSONObject jsonUser = new JSONObject(userDocument);
        return userFromJSONObject(jsonUser);
    }

    private Document userToDocument(User user){

        if(user instanceof StandardUser){
            StandardUser mongoStandardUser = userToUserEatenFoodMongoAllocation((StandardUser) user);
            return Document.parse(mongoStandardUser.toJSONObject().toString());
        }else
            return Document.parse(user.toJSONObject().toString());
    }

    // for both signIn and lookUpUserbyUsername
    public User lookUpUserByUsername(String username){
        if( ! onlyOneConnection) openConnection();
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        Document userDocument = usersCollection.find(eq(User.USERNAME, username)).first();
        if( ! onlyOneConnection) closeConnection();
        return userFromDocument(userDocument);
    }

    // for future usage
    public List<Nutritionist> lookUpNutritionistsByCountry(String country){
        if( ! onlyOneConnection) openConnection();
        List<Nutritionist> nutritionists = new ArrayList<>();
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        try(MongoCursor<Document> cursor = usersCollection.find(
                and(eq(User.COUNTRY, country),eq(User.USERNAME, User.USERTYPE_NUTRITIONIST))).iterator()){
            while(cursor.hasNext())
                nutritionists.add((Nutritionist) userFromDocument(cursor.next()));
        }
        if( ! onlyOneConnection) closeConnection();
        return nutritionists;
    }

    public List<User> lookUpAllUsersByCountry(String country){
        if( ! onlyOneConnection) openConnection();
        List<User> users = new ArrayList<>();
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        try(MongoCursor<Document> cursor = usersCollection.find(
                eq(User.COUNTRY, country)).iterator()){
            while(cursor.hasNext()){
                users.add(userFromDocument(cursor.next()));
            }
        }
        if( ! onlyOneConnection) closeConnection();
        return users;
    }

    public boolean addUser(User user){
        if( ! onlyOneConnection) openConnection();
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        InsertOneResult insertOneResult = usersCollection.insertOne(userToDocument(user));
        if( ! onlyOneConnection) closeConnection();
        return insertOneResult.wasAcknowledged();
    }

    public boolean removeUser(User user){
        if( ! onlyOneConnection) openConnection();
        boolean isSuccessful = true, wasAcknowledged = false;
        if(user != null) {
            // if the user to be removed is a Nutritionist, all his diets must be deleted altogether.
            if(user instanceof Nutritionist){
                isSuccessful = removeDietsByNutritionist(user.getUsername());
            }
            // deleting user given his username
            if(isSuccessful) {
                MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
                DeleteResult deleteResult = usersCollection.deleteOne(eq(User.USERNAME, user.getUsername()));
                isSuccessful = deleteResult.wasAcknowledged();
            }
        } else {
            isSuccessful = false;
        }
        if( ! onlyOneConnection) closeConnection();
        return isSuccessful;
    }

    /************************************************************************************/
    /*************************** Diets related methods **********************************/

    private Diet dietFromDocument(Document dietDocument) {
        if(dietDocument == null)
            return null;
        JSONObject jsonDiet = new JSONObject(dietDocument);
        String _id = dietDocument.get(Diet.ID).toString();
        jsonDiet.put(Diet.ID, _id);
        return Diet.fromJSONObject(jsonDiet);
    }

    private Document dietToDocument(Diet diet){
        return Document.parse(diet.toJSONObject().toString());
    }

    // for both lookUpDietByID and lookUpStandardUserCurrentDiet
    public Diet lookUpDietByID(String id){
        if( ! onlyOneConnection) openConnection();
        Document dietDocument = null;
        try {
            MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
            dietDocument = dietCollection.find(eq(Diet.ID, new ObjectId(id))).first(); // there can be at least only 1 match.
        }catch(Exception e){}
        if( ! onlyOneConnection) closeConnection();
        return dietFromDocument(dietDocument);
    }

    // return all diets whose name include substring searched.
    public List<Diet> lookUpDietByName(String subname){
        if( ! onlyOneConnection) openConnection();
        //String regex = "\\.\\*"+subname+"\\.\\*";
        Pattern pattern = Pattern.compile(subname, Pattern.CASE_INSENSITIVE);
        Bson filter = Filters.regex(Diet.NAME, pattern);

        List<Diet> diets = new ArrayList<>();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        try(MongoCursor<Document> cursor = dietCollection.find(
                filter).iterator()){
            while(cursor.hasNext()){
                diets.add(dietFromDocument(cursor.next()));
            }
        }
        if( ! onlyOneConnection) closeConnection();
        return diets;
    }

    public List<Diet> lookUpDietByNutritionist(String username){
        if( ! onlyOneConnection) openConnection();
        List<Diet> diets = new ArrayList<>();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        try(MongoCursor<Document> cursor = dietCollection.find(
                eq(Diet.NUTRITIONIST, username)).iterator()){
            while(cursor.hasNext()){
                diets.add(dietFromDocument(cursor.next()));
            }
        }
        if( ! onlyOneConnection) closeConnection();
        return diets;
    }

    public boolean followDiet(StandardUser standardUser, String dietID){
        if( ! onlyOneConnection) openConnection();
        MongoCollection<Document> userCollection = database.getCollection(COLLECTION_USERS);
        Bson userFilter = Filters.eq( User.USERNAME, standardUser.getUsername() );
        Bson insertCurrentDietField = Updates.set(StandardUser.CURRENT_DIET, dietID);
        UpdateResult updateResult = userCollection.updateOne(userFilter, insertCurrentDietField);
        if( ! onlyOneConnection) closeConnection();
        return updateResult.wasAcknowledged();
    }

    // used for both 'unfollowDiet' and 'stopDiet'
    public boolean unfollowDiet(StandardUser standardUser){
        if( ! onlyOneConnection) openConnection();
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
        if( ! onlyOneConnection) closeConnection();
        return bulkWriteResult.wasAcknowledged();
    }

    public boolean addDiet(Diet diet){
        if(diet.getId() != null)
            return false;
        if( ! onlyOneConnection) openConnection();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        Document dietDocument = dietToDocument(diet);
        InsertOneResult insertOneResult = dietCollection.insertOne(dietDocument);
        // adding '_id' to diet object passed as argument
        diet.setId(dietDocument.getObjectId("_id").toString());
        if( ! onlyOneConnection) closeConnection();
        return insertOneResult.wasAcknowledged();
    }

    public boolean removeDiet(String dietID){
        return removeDiet(Arrays.asList(dietID));
    }

    public boolean removeDiet(List<String> dietIDs){
        if( ! onlyOneConnection) openConnection();
        // deleting 'diet' document from 'diets' collection
        boolean isSuccessful;

        List<ObjectId> dietObjectIds = new ArrayList<>();
        for (String dietId: dietIDs){
            dietObjectIds.add(new ObjectId(dietId));
        }
        // deleting 'currentDiet' field and consequently 'EatenFoods' field from each 'user' document whose current diet value
        // correspond to one of the diets to be removed
        MongoCollection<Document> userCollection = database.getCollection(COLLECTION_USERS);

        Bson userFilter = Filters.in( StandardUser.CURRENT_DIET, dietIDs );
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

        // if the previous operation completed successfully, all diets are removed from 'diets' collection
        if(isSuccessful){
            MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
            DeleteResult deleteDietsResult = dietCollection.deleteMany(in(Diet.ID, dietObjectIds));
            isSuccessful = deleteDietsResult.wasAcknowledged();
        }
        if( ! onlyOneConnection) closeConnection();
        return isSuccessful;
    }

    private boolean removeDietsByNutritionist(String username){
        List<Diet> dietsToRemove = lookUpDietByNutritionist(username);
        List<String> dietsToRemoveIDs = new ArrayList<>();
        for(Diet diet: dietsToRemove)
            dietsToRemoveIDs.add(diet.getId());
        return removeDiet(dietsToRemoveIDs);
    }

    private HashMap<String, Integer> lookUpDietsCountForEachNutritionist(){
        if( ! onlyOneConnection) openConnection();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        HashMap<String, Integer> nutritionistDietsCountMap = new HashMap<>();

        Document currentDocument;
        try(MongoCursor<Document> cursor = dietCollection.aggregate(Arrays.asList(
                new Document("$group",
                    new Document("_id", "$"+Diet.NUTRITIONIST)
                            .append("numDiets", new Document("$sum", 1L)))) ).iterator()){

            while(cursor.hasNext()){
                currentDocument = cursor.next();
                nutritionistDietsCountMap.put(currentDocument.getString(Nutritionist.USERNAME), (int)(long)currentDocument.getLong("numDiets"));
            }
        }
        if( ! onlyOneConnection) closeConnection();
        return nutritionistDietsCountMap;
    }

    public HashMap<String, Nutrient> lookUpMostSuggestedNutrientForEachNutritionist(){
        if( ! onlyOneConnection) openConnection();

        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        HashMap<String, Integer> nutritionistDietsCountMap = new HashMap<>();
        HashMap<String, Nutrient> nutritionistNutrientMap = new HashMap<>();

        // first we need to know how many diets each nutritionist has published
        nutritionistDietsCountMap = lookUpDietsCountForEachNutritionist();

        if( ! nutritionistDietsCountMap.isEmpty()){

            List<String> nutritionists = new ArrayList<>();
            List<Nutrient> nutrients = new ArrayList<>();

            Bson unwindDocument = new Document("$unwind",
                    new Document("path", "$"+Diet.NUTRIENTS)
                            .append("preserveNullAndEmptyArrays", false));

            Bson matchDocument = new Document("$match",
                    new Document(Diet.NUTRIENTS+"."+Nutrient.NAME,
                            new Document("$ne", "Energy")));

            Bson group1Document = new Document("$group",
                    new Document("_id",
                            new Document(Diet.NUTRITIONIST, "$"+Diet.NUTRITIONIST)
                                    .append("nutrient", "$"+Diet.NUTRIENTS+"."+Nutrient.NAME)
                                    .append(Nutrient.UNIT, "$"+Diet.NUTRIENTS+"."+Nutrient.UNIT))
                            .append("totalQuantity",
                                    new Document("$sum", "$"+Diet.NUTRIENTS+"."+Nutrient.QUANTITY)));

            Bson project1Document = new Document("$project",
                    new Document("_id", 0L)
                            .append(Diet.NUTRITIONIST, "$_id."+Diet.NUTRITIONIST)
                            .append("nutrient", "$_id.nutrient")
                            .append("totalQuantity",
                                    new Document("$cond",
                                        new Document("if",
                                                new Document("$eq", Arrays.asList("$_id."+Nutrient.UNIT, "UG")))
                                                .append("then",
                                                        new Document("$divide", Arrays.asList("$totalQuantity", 1000000L)))
                                                .append("else",
                                                        new Document("$cond",
                                                                new Document("if",
                                                                        new Document("$eq", Arrays.asList("$_id"+Nutrient.UNIT, "MG")))
                                                                        .append("then",
                                                                                new Document("$divide", Arrays.asList("$totalQuantity", 1000L)))
                                                                        .append("else", "$totalQuantity"))))));

            try(MongoCursor<Document> cursor = dietCollection.aggregate(Arrays.asList(
                    unwindDocument,
                    matchDocument,
                    group1Document,
                    project1Document )).iterator()){

                Document currentDocument;
                while(cursor.hasNext()){
                    currentDocument = cursor.next();

                    nutritionists.add(
                            currentDocument.getString(Diet.NUTRITIONIST));
                    nutrients.add(new Nutrient(
                            currentDocument.getString("nutrient"),
                            "G",
                            currentDocument.getDouble("totalQuantity")
                    ));
                }
            }
            // end of connection: next steps are performed in local
            if( ! onlyOneConnection) closeConnection();

            String lastAnalyzedNutritionist = "", currentNutritionist;
            Nutrient lastMostNutrient = null, currentNutrient;

            for(int i=0; i < nutrients.size(); i++){
                currentNutritionist = nutritionists.get(i);
                currentNutrient = nutrients.get(i);

                // computing currentNutrient.avgQuantity from currentNutrient.totalQuantity
                // by dividing it for the number of diets the currentNutritionist has published.
                currentNutrient.setQuantity( currentNutrient.getQuantity() /
                                            nutritionistDietsCountMap.get(currentNutritionist) );

                if(i==0){
                    lastAnalyzedNutritionist = currentNutritionist;
                    lastMostNutrient = currentNutrient;

                    if(i == nutrients.size()-1){
                        nutritionistNutrientMap.put(lastAnalyzedNutritionist,lastMostNutrient);
                    }

                }else if(i!=0 && currentNutritionist == lastAnalyzedNutritionist){
                    if(currentNutrient.getQuantity() > lastMostNutrient.getQuantity()) {
                        lastMostNutrient = currentNutrient;
                    }
                    // if last 'record' to be analyzed:
                    if(i == nutrients.size()-1){
                        nutritionistNutrientMap.put(lastAnalyzedNutritionist,lastMostNutrient);
                    }
                }else if(i!=0 && currentNutritionist != lastAnalyzedNutritionist){
                    // add lastAnalyzedNutritionist to return list
                    nutritionistNutrientMap.put(lastAnalyzedNutritionist,lastMostNutrient);
                    // start analyzing new nutritionist
                    lastAnalyzedNutritionist = currentNutritionist;
                    lastMostNutrient = currentNutrient;

                    // if last 'record' to be analyzed:
                    if(i == nutrients.size()-1){
                        nutritionistNutrientMap.put(lastAnalyzedNutritionist,lastMostNutrient);
                    }
                }
            }
        }
        return nutritionistNutrientMap;
    }

    /************************************************************************************/
    /*************************** Foods related methods **********************************/


    private Food foodFromDocument(Document foodDocument) {
        if(foodDocument == null)
            return null;
        JSONObject jsonFood = new JSONObject(foodDocument);
        String name = foodDocument.get(Food.NAME).toString();
        jsonFood.put(Food.NAME, name);
        return Food.fromJSONObject(jsonFood);
    }

    private Document foodToDocument(Food food){
        return Document.parse(food.toJSONObject().toString());
    }

    public List<Food> lookUpFoodsByName(String subname){
        if( ! onlyOneConnection) openConnection();
        String regex = "^.*"+subname+".*$";
//        Pattern pattern = Pattern.compile(subname, Pattern.CASE_INSENSITIVE);
//        Bson filter = Filters.regex(Food.NAME, pattern);

        //Pattern pattern = Pattern.compile(".*" + subname + ".*");
        //Bson filter = Filters.regex(Food.NAME, pattern);
        List<Food> foods = new ArrayList<>();
        MongoCollection<Document> foodCollection = database.getCollection(COLLECTION_FOODS);
        try(MongoCursor<Document> cursor = foodCollection.find(
                eq(Food.NAME, subname)).iterator()){
            while(cursor.hasNext()){
                foods.add(foodFromDocument(cursor.next()));
            }
        }
        if( ! onlyOneConnection) closeConnection();
        return foods;
    }

    public Food lookUpMostEatenFoodByCategory(String category){
        if( ! onlyOneConnection) openConnection();
        MongoCollection<Document> foodCollection = database.getCollection(COLLECTION_FOODS);

        Bson matchFoodsByCategory = match(Filters.eq(Food.CATEGORY, category));
        Bson sortFoodsByEatenTimesCount = sort(descending(Food.EATEN_TIMES_COUNT));

        Document maxEatenTimesFoodDocument = foodCollection.aggregate(Arrays.asList(
                matchFoodsByCategory,
                sortFoodsByEatenTimesCount)).first();

        Food maxEatenTimesFood = foodFromDocument(maxEatenTimesFoodDocument);
        if( ! onlyOneConnection) closeConnection();
        return maxEatenTimesFood;
    }

    public boolean addFood(Food food){
        if( ! onlyOneConnection) openConnection();
        MongoCollection<Document> foodCollection = database.getCollection(COLLECTION_FOODS);
        InsertOneResult insertOneResult = foodCollection.insertOne(foodToDocument(food));
        if( ! onlyOneConnection) closeConnection();
        return insertOneResult.wasAcknowledged();
    }

    public boolean removeFood(String foodName){
        if( ! onlyOneConnection) openConnection();
        boolean isSuccessful = false;

        EatenFood eatenFood = new EatenFood();

        // Replace all instances of EatenFoods referring to the food to be removed with empty slot of eatenfoods
        String elementIdentifier = "elem";
        MongoCollection<Document> userCollection = database.getCollection(COLLECTION_USERS);

        UpdateResult updateResult = userCollection.updateMany(
                new Document(StandardUser.EATENFOODS+"."+EatenFood.FOOD_NAME, foodName),
                new Document("$set",
                        new Document(StandardUser.EATENFOODS+".$["+elementIdentifier+"]",
                                new Document(EatenFood.ID, eatenFood.getId())
                                        .append(EatenFood.FOOD_NAME, eatenFood.getFoodName())
                                        .append(EatenFood.QUANTITY, eatenFood.getQuantity())
                                        .append(EatenFood.TIMESTAMP, eatenFood.getTimestamp().toString())
                        )
                ),
                new UpdateOptions().arrayFilters(
                        Arrays.asList(new Document(elementIdentifier+"."+EatenFood.FOOD_NAME, foodName))
                )
        );
        if( ! onlyOneConnection) closeConnection();

        // Consistency issue: only if the precedent operation completed successfully then we can delete the food
        if(updateResult.wasAcknowledged()){
            if( ! onlyOneConnection) openConnection();
            MongoCollection<Document> foodCollection = database.getCollection(COLLECTION_FOODS);
            DeleteResult deleteResult = foodCollection.deleteOne(eq(Food.NAME, foodName));
            isSuccessful = deleteResult.wasAcknowledged();
            if( ! onlyOneConnection) closeConnection();
        }
        return isSuccessful;
    }

    private StandardUser userToUserEatenFoodMongoAllocation(StandardUser user){
        StandardUser mongoUser = new StandardUser(user);
        int eatenFoodsCount = 0;
        if(user.getEatenFoods() != null && user.getCurrentDiet() != null){
            eatenFoodsCount = user.getEatenFoods().size();
            // padding of empty eatenFoods into the user according to the defined constant.
            for(int i=0; i < EATEN_FOOD_SLOTS_SIZE - eatenFoodsCount % EATEN_FOOD_SLOTS_SIZE; i++){
                mongoUser.getEatenFoods().add(new EatenFood());
            }
        }
        return mongoUser;
    }

    private StandardUser userFromEatenFoodUserMongoAllocation(StandardUser mongoUser){
        StandardUser user = new StandardUser(mongoUser);
        // remove padding of eatenFoods from user eatenFood list
        if(user.getCurrentDiet() != null) {
            if(user.getEatenFoods() != null) {
                int index;
                while ((index = user.getEatenFoods().indexOf(new EatenFood())) >= 0) {
                    user.getEatenFoods().remove(index);
                }
                // DA ELIMINARE
                while ((index = user.getEatenFoods().indexOf(new EatenFood(EatenFood.generateEatenFoodFormatID(0),
                        String.format(Food.foodNameFieldFormat, ""), -1, new Timestamp(0)))) >= 0) {
                    user.getEatenFoods().remove(index);
                }
            }
        } else { // user is not currently following any diet
            user.getEatenFoods().clear();
        }
        return user;
    }

    public boolean incrementEatenTimesCount(String foodName){
        if( ! onlyOneConnection) openConnection();
        MongoCollection<Document> foodCollection = database.getCollection(COLLECTION_FOODS);
        Bson foodFilter = Filters.eq( Food.NAME, foodName );
        UpdateResult updateResult = foodCollection.updateOne(foodFilter, inc(Food.EATEN_TIMES_COUNT, 1));
        if( ! onlyOneConnection) closeConnection();
        return updateResult.wasAcknowledged();
    }

    public boolean updateEatenFood(StandardUser standardUser){
        if( ! onlyOneConnection) openConnection();
        MongoCollection<Document> userCollection = database.getCollection(COLLECTION_USERS);

        Bson userFilter = Filters.eq( User.USERNAME, standardUser.getUsername() );
        UpdateResult updateResult = userCollection.replaceOne(userFilter, userToDocument(standardUser));
        if( ! onlyOneConnection) closeConnection();
        return updateResult.wasAcknowledged();
    }
    /************************************************************************************/
}