package it.unipi.dii.dietmanager.persistence;
import com.mongodb.ConnectionString;

import com.mongodb.client.*;
import it.unipi.dii.dietmanager.entities.Administrator;
import it.unipi.dii.dietmanager.entities.Nutritionist;
import it.unipi.dii.dietmanager.entities.StandardUser;
import it.unipi.dii.dietmanager.entities.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.json.JSONArray;
import org.json.JSONObject;


import javax.json.stream.JsonParser;

import static com.mongodb.client.model.Filters.*;
/*
Collections: users | diets | foods
user:
        _id: "username",
        password: "",
        name: "",
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

    private final Gson gson = new Gson();
    private JSONParser parser = new JSONParser();
    JSONObject json = (JSONObject) parser.parse(stringToParse);

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

    private User userFromDocument(Document userDocument)
    {
        User user;

        String password, username, name, country;
        int age;
        JSONArray jsonFoods;
        JSONObject jsonDocument = new JSONObject(userDocument.toString());
        //String userType = (String) userDocument.get("userType");
        String userType = jsonDocument.getString("userType");
        if(userType == "StandardUser"){
            //JSONObject jsonUser = new JSONObject(userDocument.toString());
            password = jsonDocument.getString("password");
            //StandardUser standardUser = jsonUser.;
            jsonFoods = jsonDocument.getJSONArray("eatenFoods");
            for(int i=0; i<jsonFoods.length(); i++){

            }
        }else if(userType == "nutritionist"){
            Nutritionist nutritionist = gson.fromJson(userDocument.toJson(), Nutritionist.class);
            user = nutritionist;
        }else if(userType == "administrator"){
            Administrator administrator = gson.fromJson(userDocument.toJson(), Administrator.class);
            user = administrator;
        } else{
            return null;
        }
        return user;
    }

    private String userToDocument(User user)
    {

        Document userDocument = Document.parse(user.toJSON().toString());

        String userString;
        String userType = (String) userDocument.get("userType");
        if(user instanceof StandardUser){
            userString = gson.toString((StandardUser)user);
        }else if(userType == "nutritionist"){
            Nutritionist nutritionist = gson.fromJson(userDocument.toJson(), Nutritionist.class);
            user = nutritionist;
        }else if(userType == "administrator"){
            Administrator administrator = gson.fromJson(userDocument.toJson(), Administrator.class);
            user = administrator;
        } else{
            return null;
        }
        return user;
    }

    public User lookUpUserByID(String username)
    {
        MongoCollection<Document> usersCollection = database.getCollection("users");
        Document userDocument = usersCollection.find(eq("_id", new ObjectId(username))).first(); // there can be at least only 1 match.
        return userFromDocument(userDocument);
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
