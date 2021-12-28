package it.unipi.dii.dietmanager.persistence;

import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import it.unipi.dii.dietmanager.entities.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


abstract class DietSchema{

}

abstract class FoodSchema{

}
/*
Collections: users | diets | foods

user:
        _id: "username",
        password: "",
        fullName: "",
        sex: "",
        age: "",
        country: "",
        userType: "standardUser" || "nutritionist" || "administrator",
        --------- standardUser ---------
        eatenFoods: [   {
                        eatenFoodID: "00112364",
                        foodID: "92378562",
                        quantity: int,	 (sempre grammi)
                        timestamp: Timestamp
                        },
                    ],
        currentDiet: "stringID"
        ---------------------------------

food:
        _id: "StringName",
        category: "",
        nutrients:   [	{           // Per100g
                        name: "Energy",
                        unit: "KCAL",
                        quantity: double
                        }
                    ],
        eatenTimesCount: ""

diet:
        _id: "454784791",
        name: "",
        nutrients:   [	{           // Per100g
                    name: "Energy",
                    unit: "KCAL",
                    quantity: double
                    }
                ],
	    nutritionist: "username"
*/


public class MongoDB implements AutoCloseable{  // FUNZIONA SOLO CON TRY CATCH  ???

    private MongoClient mongoClient;
    private MongoDatabase database;

    private final int localhostPort;

    private final String USERTYPE_STANDARD_USER = "standardUser";
    private final String USERTYPE_NUTRITIONIST = "nutritionists";
    private final String USERTYPE_ADMINISTRATOR = "administrator";

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
        close();
    }

    @Override
    public void close()
    {
        try{
            mongoClient.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*************************** Users related methods **********************************/

    private User userFromJSON(JSONObject jsonUser){
        String userType = jsonUser.getString(UserString.USER_TYPE);
        if(userType.equals(USERTYPE_STANDARD_USER))
            return StandardUser.fromJSON(jsonUser);

        if(userType.equals(USERTYPE_NUTRITIONIST))
            return Nutritionist.fromJSON(jsonUser);

        if(userType.equals(USERTYPE_ADMINISTRATOR))
            return Administrator.fromJSON(jsonUser);

        return null;
    }

    private User userFromDocument(Document userDocument){
        if(userDocument == null)
            return null;
        JSONObject jsonUser = new JSONObject(userDocument.toString());
        return userFromJSON(jsonUser);
    }

    private Document userToDocument(User user){
        return Document.parse(user.toJSON().toString());
    }

    // for both sign in and lookUpUser
    public User lookUpUserByID(String username){
        openConnection();
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        Document userDocument = usersCollection.find(eq(UserString.USERNAME, new ObjectId(username))).first(); // there can be at least only 1 match.
        closeConnection();
        return userFromDocument(userDocument);
    }

    public boolean registerUser(User user){
        openConnection();
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        usersCollection.insertOne(userToDocument(user));
        closeConnection();
        return lookUpUserByID(user.getUsername()) != null;
    }

    public boolean removeUser(String username){
        openConnection();
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        usersCollection.deleteOne(Filters.eq(UserString.USERNAME, username));
        closeConnection();
        return lookUpUserByID(username) == null;
    }
    /*  ALREADY DEFINED IN LOOKUP-USER-BY-ID
    public User lookUpUserByUsername(String username){
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        JSONObject jsonUser = new JSONObject(usersCollection.find(eq("username", username)).first().toJson());
        return getUserObjectFromJSON(jsonUser);
    }
     */

    public List<Nutritionist> lookUpNutritionistByCountry(String country){
        openConnection();
        List<Nutritionist> nutritionists = new ArrayList<>();
        MongoCollection<Document> usersCollection = database.getCollection(COLLECTION_USERS);
        try(MongoCursor<Document> cursor = usersCollection.find(
                and(eq(UserString.COUNTRY, country),eq(UserString.USERNAME, USERTYPE_NUTRITIONIST))).iterator()){
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
                eq(UserString.COUNTRY, country)).iterator()){
            while(cursor.hasNext()){
                users.add(userFromDocument(cursor.next()));
            }
        }
        closeConnection();
        return users;
    }

    /*  useless: StandardUser Current diet is already read at log in.
                 If he does not have one, whenever he add a diet Logic Manager add it also into logic User object.
                 therefore it does not belong to a MongoDB query.

    public Diet lookUpStandardUserCurrentDiet(String username){
        MongoCollection<Document> usersCollection = database.getCollection("users");
        JSONObject jsonUser = new JSONObject(usersCollection.find(eq("username", username)).first().toJson());
        return lookUpDietByID(jsonUser.getString("currentDiet"));
    }
     */

    /* useless: as above.

    public List<EatenFood> lookUpStandardUserEatenFoods(String username){
        MongoCollection<Document> usersCollection = database.getCollection("users");
        JSONObject jsonUser = new JSONObject(usersCollection.find(eq("username", username)).first().toJson());
        JSONArray jsonEatenFoods = jsonUser.getJSONArray("eatenFoods");
        List<EatenFood> eatenFoods = new ArrayList<>();
        for(int i=0; i<jsonEatenFoods.length(); i++)
            eatenFoods.add(EatenFood.fromJSON(jsonEatenFoods.getJSONObject(i)));
        return eatenFoods;
    }
     */

    /************************************************************************************/
    /*************************** Diets related methods **********************************/

    private Diet dietFromDocument(Document dietDocument) {
        if(dietDocument == null)
            return null;
        JSONObject jsonDiet = new JSONObject(dietDocument.toString());
        return Diet.fromJSON(jsonDiet);
    }

    private Document dietToDocument(Diet diet){
        return Document.parse(diet.toJSON().toString());
    }

    public Diet lookUpDietByID(String id){
        openConnection();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        Document dietDocument = dietCollection.find(eq(DietString.ID, new ObjectId(id))).first(); // there can be at least only 1 match.
        closeConnection();
        return dietFromDocument(dietDocument);
    }

    public List<Diet> lookUpDietByName(String name){
        openConnection();
        List<Diet> diets = new ArrayList<>();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        try(MongoCursor<Document> cursor = dietCollection.find(
                eq(DietString.NAME, name)).iterator()){
            while(cursor.hasNext()){
                diets.add(dietFromDocument(cursor.next()));
            }
        }
        closeConnection();
        return diets;
    }

    public List<Diet> lookUpDietByNutritionist(Nutritionist nutritionist){
        openConnection();
        List<Diet> diets = new ArrayList<>();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_DIETS);
        try(MongoCursor<Document> cursor = dietCollection.find(
                eq(DietString.NUTRITIONIST, nutritionist.getUsername())).iterator()){
            while(cursor.hasNext()){
                diets.add(dietFromDocument(cursor.next()));
            }
        }
        closeConnection();
        return diets;
    }

    /************************************************************************************/
    /*************************** Foods related methods **********************************/


    private Food foodFromDocument(Document foodDocument) {
        if(foodDocument == null)
            return null;
        JSONObject jsonFood = new JSONObject(foodDocument.toString());
        return Food.fromJSON(jsonFood);
    }

    private Document foodToDocument(Food food){
        return Document.parse(food.toJSON().toString());
    }

    public List<Food> lookUpFoodsByName(String name){
        openConnection();
        String regex = "\\.\\*"+name+"\\.\\*";

        List<Food> foods = new ArrayList<>();
        MongoCollection<Document> dietCollection = database.getCollection(COLLECTION_FOODS);
        try(MongoCursor<Document> cursor = dietCollection.find(
                eq(FoodString.NAME, regex)).iterator()){
            while(cursor.hasNext()){
                foods.add(foodFromDocument(cursor.next()));
            }
        }
        closeConnection();
        return foods;
    }

    /************************************************************************************/


    public static void main( String... args ) throws Exception {
        try( MongoDB mongoDB = new MongoDB( 7687)){

        }
    }

    abstract class UserString{
        public static final String USERNAME = "_id";
        public static final String PASSWORD = "password";
        public static final String FULLNAME = "fullName";
        public static final String SEX = "sex";
        public static final String AGE = "age";
        public static final String COUNTRY = "country";
        public static final String USER_TYPE = "userType";
        public static final String EATENFOODS = "eatenFoods";
        public static final String CURRENT_DIET = "currentDiet";
    }
    abstract class FoodString{
        public static final String NAME = "_id";
        public static final String CATEGORY = "category";
        public static final String NUTRIENTS = "nutrients";
        public static final String EATEN_TIMES_COUNT = "eatenTimesCount";
    }
    abstract class DietString{
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String NUTRIENTS = "nutrients";
        public static final String NUTRITIONIST = "nutritionist";
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
