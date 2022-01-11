package it.unipi.dii.dietmanager.persistence;

import it.unipi.dii.dietmanager.entities.Diet;
import it.unipi.dii.dietmanager.entities.StandardUser;
import it.unipi.dii.dietmanager.entities.User;
import org.neo4j.driver.*;
import org.neo4j.driver.internal.logging.JULogging;

import java.util.logging.Level;

import static org.neo4j.driver.Values.parameters;

public class Neo4jManager implements AutoCloseable
{
    private Driver driver;

    public Neo4jManager(String connectionMode, String ipAddress, int port, String user, String password)
    {
        String uri = connectionMode+"://"+ipAddress+":"+port;
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ),
                Config.builder().withLogging(new JULogging(Level.OFF)).build());
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    // for future usage
    public boolean userAlreadyExists(String username){
        try ( Session session = driver.session() )
        {
            // I perform the query to understand if this user already exists
            Boolean userAlreadyExists = session.readTransaction((TransactionWork<Boolean>) tx -> {
                Result result = tx.run( "MATCH (user: User) WHERE user.username = $username RETURN user",
                        parameters( "username", username));
                if(result.hasNext()) {
                    return true;
                }
                return false;
            });
            return userAlreadyExists;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        return false;
    }

    // for future usage
    public boolean dietAlreadyExists(String dietID){
        try ( Session session = driver.session() )
        {
            // I perform the query to understand if this user already exists
            Boolean dietAlreadyExists = session.readTransaction((TransactionWork<Boolean>) tx -> {
                Result result = tx.run( "MATCH (diet: Diet) WHERE diet.id = $id RETURN diet",
                        parameters( "id", dietID));
                if(result.hasNext()) {
                    return true;
                }
                return false;
            });

            return dietAlreadyExists;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    // for future usage
    private boolean userAlreadyFollowedAnyDiet(StandardUser user){
        try ( Session session = driver.session() )
        {
            // I perform query to understand if this user already followed this diet
            Boolean userAlreadyFollowedAnyDiet = session.readTransaction((TransactionWork<Boolean>) tx -> {
                Result result = tx.run( "MATCH (user: User)-[follows:FOLLOWS]->(diet: Diet) " +
                                "WHERE user.username = $username AND follows.status = \"current\"" +
                                "RETURN follows",
                        parameters( "username", user.getUsername()));
                if(result.hasNext())
                    return true;
                return false;
            });
            return userAlreadyFollowedAnyDiet;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean addUser(User user)
    {
        boolean isSuccessful = false;
        try ( Session session = driver.session() )
        {
            if(user instanceof StandardUser)
                session.writeTransaction((TransactionWork<Void>) tx -> {
                    tx.run( "MERGE (:User {username: $username})", parameters( "username", user.getUsername()));
                    return null;
                });
            else
                session.writeTransaction((TransactionWork<Void>) tx -> {
                    tx.run( "MERGE (:Nutritionist {username: $username})", parameters( "username", user.getUsername()));
                    return null;
                });
            isSuccessful = true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return isSuccessful;
    }

    public boolean addDiet(Diet diet)
    {
        boolean isSuccessful = false;
        try ( Session session = driver.session() )
        {
            // I create the Diet node
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MERGE (:Diet {id: $id, name: $name, followersCount: 0, succeededCount: 0, failedCount: 0})",
                        parameters( "id", diet.getId(), "name", diet.getName()));
                return null;
            });

            // I create the relationship between the diet and the nutritionist who created it
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (nutritionist:Nutritionist), (diet:Diet) WHERE nutritionist.username = $username " +
                            "AND diet.id = $id CREATE (nutritionist)-[:PROVIDES]->(diet)",
                        parameters("username", diet.getNutritionist(), "id", diet.getId()));
                return null;
            });
            isSuccessful = true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return isSuccessful;
    }

    // for future usage
    private boolean userAlreadyFollowedDiet(StandardUser user, Diet diet){
        try ( Session session = driver.session() )
        {
            // I perform query to understand if this user already followed this diet
            Boolean userAlreadyFollowedDiet = session.readTransaction((TransactionWork<Boolean>) tx -> {
                Result result = tx.run( "MATCH (user: User)-[follows:FOLLOWS]->(diet: Diet) " +
                                "WHERE user.username = $username AND diet.id = $id AND follows.status = \"current\"" +
                                "RETURN follows",
                        parameters( "username", user.getUsername(), "id", diet.getId()));
                if(result.hasNext())
                    return true;
                return false;
            });
            return userAlreadyFollowedDiet;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    //NOTE: if user succeeded in a diet and then restart the same diet, the fact that he finish successfully that diet
    // will be forgotten
    public boolean followDiet(StandardUser user, String dietID)
    {
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (user:User), (diet:Diet) WHERE user.username = $username " +
                        "AND diet.id = $id CREATE (user)-[:FOLLOWS {result: null, status: \"current\"}]->(diet) " +
                        "SET diet.followersCount = diet.followersCount + 1",
                        parameters( "username", user.getUsername(), "id", dietID));
                return null;
            });
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean stopDiet(StandardUser user, boolean isSucceeded){
        Diet diet = user.getCurrentDiet();

        try ( Session session = driver.session() )
        {
            String dietResult = isSucceeded == true ? "succeeded" : "failed";
            int failedCountIncrement = isSucceeded == true? 0 : 1;
            int succeededCountIncrement = isSucceeded == true? 1 : 0;

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (user:User)-[follows:FOLLOWS]->(diet:Diet) WHERE user.username = $username " +
                                "AND diet.id = $id SET follows.status = $status, diet.followersCount = diet.followersCount - 1, " +
                                "diet.failedCount = diet.failedCount + $failedCountIncrement, " +
                                "diet.succeededCount = diet.succeededCount + $succeededCountIncrement",
                        parameters( "username", user.getUsername(), "id", diet.getId(), "status", dietResult,
                                    "failedCountIncrement", failedCountIncrement, "succeededCountIncrement", succeededCountIncrement));
                return null;
            });
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean unfollowDiet(StandardUser user){
        Diet diet = user.getCurrentDiet();

        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (user:User)-[follows:FOLLOWS]->(diet:Diet) WHERE user.username = $username " +
                                "AND diet.id = $id SET diet.followersCount = diet.followersCount - 1 " +
                                "DELETE follows",
                        parameters( "username", user.getUsername(), "id", diet.getId()));
                return null;
            });
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean removeDiet(String dietID){
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (diet: Diet{id: $id}) DETACH DELETE diet",
                        parameters( "id", dietID));
                return null;
            });
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean removeUser(String username){
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (user: User{username: $username})-[follows:FOLLOWS]->(diet: Diet)" +
                                "WITH diet, user, (CASE WHEN follows.status = \"current\" THEN 1 ELSE 0 END) AS followersCountDecrement, " +
                                "(CASE WHEN follows.status = \"succeeded\" THEN 1 ELSE 0 END) AS succeededCountDecrement, " +
                                "(CASE WHEN follows.status = \"failed\" THEN 1 ELSE 0 END) AS failedCountDecrement " +
                                "SET diet.followersCount = diet.followersCount - followersCountDecrement, " +
                                "diet.failedCount = diet.failedCount - failedCountDecrement, " +
                                "diet.succeededCount = diet.succeededCount - succeededCountDecrement DETACH DELETE user",
                        parameters( "username", username));
                return null;
            });

            // if the user does not have relationship, then I have to delete him
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (user: User{username: $username}) DETACH DELETE user",
                        parameters( "username", username));
                return null;
            });

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (nutritionist: Nutritionist{username: $username}) DETACH DELETE nutritionist",
                        parameters( "username", username));
                return null;
            });
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public String lookUpMostFollowedDiet(){
        String mostFollowedDietID = null;
        try ( Session session = driver.session() )
        {
            mostFollowedDietID = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run( "MATCH (diet: Diet) RETURN diet.id AS ID ORDER BY diet.followersCount DESC LIMIT 1");
                if(result.hasNext())
                    return result.next().get("ID").asString();
                return null;
            });
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostFollowedDietID;
    }

    public String lookUpMostFollowedDietByNutritionist(String username){
        String mostFollowedDietID = null;
        try ( Session session = driver.session() )
        {
            mostFollowedDietID = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run( "MATCH (nutritionist: Nutritionist)-[:PROVIDES]->(diet: Diet) " +
                                        "WHERE nutritionist.username = $username " +
                                        "RETURN diet.id AS ID ORDER BY diet.followersCount DESC LIMIT 1",
                                        parameters("username", username));
                if(result.hasNext())
                    return result.next().get("ID").asString();
                return null;
            });
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostFollowedDietID;
    }

    public String lookUpMostSucceededDiet(){
        String mostSucceededDietID = null;
        try ( Session session = driver.session() )
        {
            mostSucceededDietID = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run( "MATCH (diet: Diet) RETURN diet.id AS ID ORDER BY diet.succeededCount DESC LIMIT 1");
                if(result.hasNext())
                    return result.next().get("ID").asString();
                return null;
            });
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostSucceededDietID;
    }

    public String lookUpMostPopularDiet(){
        String mostPopularDietID = null;
        try ( Session session = driver.session() )
        {
            mostPopularDietID = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run( "MATCH (diet: Diet) RETURN diet.id AS ID " +
                        "ORDER BY diet.succeededCount+diet.followersCount DESC LIMIT 1");
                if(result.hasNext())
                    return result.next().get("ID").asString();
                return null;
            });
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostPopularDietID;
    }

    public String lookUpMostPopularNutritionist(){
        String mostPopularNutritionistUsername = null;
        try ( Session session = driver.session() )
        {
            mostPopularNutritionistUsername = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run( "MATCH (nutritionist: Nutritionist)-[:PROVIDES]->(diet: Diet) " +
                                            "RETURN nutritionist.username AS username " +
                                            "ORDER BY diet.succeededCount+diet.followersCount DESC LIMIT 1");
                if(result.hasNext())
                    return result.next().get("username").asString();
                return null;
            });
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostPopularNutritionistUsername;
    }

    public String lookUpRecommendedDiet(StandardUser user){
        String mostRecommendedDietID = null;
        try ( Session session = driver.session() )
        {
            mostRecommendedDietID = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run(
                "MATCH (user1: User)-[f1:FOLLOWS]->(diet1: Diet)<-[f2:FOLLOWS]-(user2: User)-[f3:FOLLOWS]->(diet2: Diet) " +
                    "WHERE f1.status = f2.status AND user1.username = $username " +
                    "AND f3.status = \"succeeded\" WITH diet2, count(*) AS validArrowCount " +
                    "RETURN diet2.id AS ID ORDER BY validArrowCount DESC LIMIT 1",
                    parameters("username", user.getUsername()));
                if(result.hasNext())
                    return result.next().get("ID").asString();
                // if the target user doesn't have completed diets or if no one of the users who completed/failed the same diet
                // of the target user don't have completed diets, then I return the most succeeded diet (in general)
                return lookUpMostSucceededDiet();
            });
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostRecommendedDietID;
    }

    public void dropDatabase(){
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (n) DETACH DELETE n");
                return null;
            });
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}



