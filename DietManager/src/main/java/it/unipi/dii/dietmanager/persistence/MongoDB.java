package it.unipi.dii.dietmanager.persistence;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import it.unipi.dii.dietmanager.entities.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
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


public class MongoDB implements AutoCloseable{
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoDB(int localhostPort)
    {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:" + localhostPort);
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase("dietManagerDB");
    }

    @Override
    public void close() throws Exception
    {
        mongoClient.close();
    }

    private List<EatenFood> eatenFoodsFromJSONArray(JSONArray jsonEatenFoods){
        for(int i=0; i<jsonEatenFoods.length(); i++){

        }
    }

    private User getUserObjectFromJSON(JSONObject jsonUser){
        String userType = jsonUser.getString("userType");
        if(userType.equals("standardUser"))
            return StandardUser.fromJSON(jsonUser);

        if(userType.equals("nutritionist"))
            return Nutritionist.fromJSON(jsonUser);

        if(userType.equals("administrator"))
            return Administrator.fromJSON(jsonUser);

        return null;
    }

    private User userFromDocument(Document userDocument)
    {
        if(userDocument == null)
            return null;

        JSONObject jsonUser = new JSONObject(userDocument.toString());
        return getUserObjectFromJSON(jsonUser);
    }

    private Document userToDocument(User user){
        return Document.parse(user.toJSON().toString());
    }

    // for both sign in and lookUpUser
    public User lookUpUserByID(String username){
        MongoCollection<Document> usersCollection = database.getCollection("users");
        Document userDocument = usersCollection.find(eq("_id", new ObjectId(username))).first(); // there can be at least only 1 match.
        return userFromDocument(userDocument);
    }

    public boolean registerUser(User user){
        MongoCollection<Document> usersCollection = database.getCollection("users");
        Document userDocument = Document.parse(user.toJSON().toString());
        usersCollection.insertOne(userDocument);
        return lookUpUserByID(user.getUsername()) != null;
    }

    public boolean removeUser(String username){
        MongoCollection<Document> usersCollection = database.getCollection("users");
        usersCollection.deleteOne(Filters.eq("username", username));
        return lookUpUserByID(username) == null;
    }

    public User lookUpUserByUsername(String username){
        MongoCollection<Document> usersCollection = database.getCollection("users");
        JSONObject jsonUser = new JSONObject(usersCollection.find(eq("username", username)).first().toJson());
        return getUserObjectFromJSON(jsonUser);
    }

    public User lookUpUserByCountry(String country){
        MongoCollection<Document> usersCollection = database.getCollection("users");
        JSONObject jsonUser = new JSONObject(usersCollection.find(eq("country", country)).first().toJson());
        return getUserObjectFromJSON(jsonUser);
    }


    public Diet lookUpStandardUserCurrentDiet(String username){
        MongoCollection<Document> usersCollection = database.getCollection("users");
        JSONObject jsonUser = new JSONObject(usersCollection.find(eq("username", username)).first().toJson());
        return lookUpDietByID(jsonUser.getString("currentDiet"));
    }

    public List<EatenFood> lookUpStandardUserEatenFoods(String username){
        MongoCollection<Document> usersCollection = database.getCollection("users");
        JSONObject jsonUser = new JSONObject(usersCollection.find(eq("username", username)).first().toJson());
        JSONArray jsonEatenFoods = jsonUser.getJSONArray("eatenFoods");
        List<EatenFood> eatenFoods = new ArrayList<>();
        for(int i=0; i<jsonEatenFoods.length(); i++)
            eatenFoods.add(EatenFood.fromJSON(jsonEatenFoods.getJSONObject(i)));
        return eatenFoods;
    }

    /*
    MongoCollection<Document> usersCollection = database.getCollection("users");
        try(MongoCursor<Document> cursor = usersCollection.find().iterator())
        {
            while(cursor.hasNext())
            {
                System.out.println();
            }
        }
     */

    public static void main( String... args ) throws Exception {
        try( MongoDB mongoDB = new MongoDB( 7687)){

        }
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
