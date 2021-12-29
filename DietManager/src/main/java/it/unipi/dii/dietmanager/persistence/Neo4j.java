package it.unipi.dii.dietmanager.persistence;

import it.unipi.dii.dietmanager.entities.Diet;
import it.unipi.dii.dietmanager.entities.Nutritionist;
import it.unipi.dii.dietmanager.entities.StandardUser;
import it.unipi.dii.dietmanager.entities.User;
import org.neo4j.driver.*;

import static org.neo4j.driver.Values.parameters;

public class Neo4j implements AutoCloseable
{
    private Driver driver;
    private String uri, user, password;

    public Neo4j()
    {
        uri = "neo4j://localhost:7687";
        user = "neo4j";
        password = "root";
    }

    private void openConnection(){
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    private boolean userAlreadyExists(User user){
        openConnection();
        try ( Session session = driver.session() )
        {
            // I perform the query to understand if this user already exists
            Boolean userAlreadyExists = session.readTransaction((TransactionWork<Boolean>) tx -> {
                Result result = tx.run( "MATCH (user: User) WHERE user.username = $username RETURN user",
                        parameters( "username", user.getUsername()));
                if(result.hasNext()) {
                    return true;
                }
                return false;
            });

            close();
            return userAlreadyExists;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        return false;
    }

    private boolean dietAlreadyExists(String dietID){
        openConnection();
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

            close();
            return dietAlreadyExists;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    private boolean userAlreadyFollowedAnyDiet(StandardUser user){
        openConnection();
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
            close();
            return userAlreadyFollowedAnyDiet;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean addUser(User user)
    {
        if(userAlreadyExists(user))
            return false;
        openConnection();

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
            close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean addDiet(Diet diet)
    {
        if(dietAlreadyExists(diet.getId()))
            return false;
        openConnection();

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
                        parameters("username", diet.getNutritionist().getUsername(), "id", diet.getId()));
                return null;
            });
            close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    private boolean userAlreadyFollowedDiet(StandardUser user, Diet diet){
        openConnection();
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
            close();
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
        if(!userAlreadyExists(user) || !dietAlreadyExists(dietID))
            return false;

        // if the user already followed a diet, the operation can't be done
        if(userAlreadyFollowedAnyDiet(user))
            return false;
        openConnection();
        System.out.print(user.getUsername() + ", " + dietID + ": ");
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (user:User), (diet:Diet) WHERE user.username = $username " +
                        "AND diet.id = $id CREATE (user)-[:FOLLOWS {result: null, status: \"current\"}]->(diet) " +
                        "SET diet.followersCount = diet.followersCount + 1",
                        parameters( "username", user.getUsername(), "id", dietID));
                return null;
            });
            close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean stopDiet(StandardUser user, boolean isSucceeded){
        Diet diet = user.getCurrentDiet();
        if(!userAlreadyExists(user) || !dietAlreadyExists(diet.getId()))
            return false;

        // if user didn't follow the diet, the operation can't be done
        if(!userAlreadyFollowedDiet(user, diet))
            return false;
        openConnection();

        System.out.print(user.getUsername() + ", " + diet.getId() + ", " + isSucceeded + ": ");
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
            close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean unfollowDiet(StandardUser user){
        Diet diet = user.getCurrentDiet();
        if(!userAlreadyExists(user) || !dietAlreadyExists(diet.getId()))
            return false;

        // if user didn't follow the diet, the operation can't be done
        if(!userAlreadyFollowedDiet(user, diet))
            return false;
        openConnection();

        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (user:User)-[follows:FOLLOWS]->(diet:Diet) WHERE user.username = $username " +
                                "AND diet.id = $id SET diet.followersCount = diet.followersCount - 1 " +
                                "DELETE follows",
                        parameters( "username", user.getUsername(), "id", diet.getId()));
                return null;
            });
            close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean removeDiet(Diet diet){
        if(!dietAlreadyExists(diet.getId()))
            return false;
        openConnection();

        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (diet: Diet{id: $id}) DETACH DELETE diet",
                        parameters( "id", diet.getId()));
                return null;
            });
            close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean removeUser(User user){
        if(!userAlreadyExists(user))
            return false;

        openConnection();
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
                        parameters( "username", user.getUsername()));
                return null;
            });

            // if the user does not have relationship, then I have to delete him
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (user: User{username: $username}) DETACH DELETE user",
                        parameters( "username", user.getUsername()));
                return null;
            });

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (nutritionist: Nutritionist{username: $username}) DETACH DELETE nutritionist",
                        parameters( "username", user.getUsername()));
                return null;
            });
            close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public String lookUpMostFollowedDiet(){
        String mostFollowedDietID = null;
        openConnection();
        try ( Session session = driver.session() )
        {
            mostFollowedDietID = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run( "MATCH (diet: Diet) RETURN diet.id AS ID ORDER BY diet.followersCount DESC LIMIT 1");
                return result.next().get("ID").asString();
            });
            close();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostFollowedDietID;
    }

    public String lookUpMostFollowedDietByNutritionist(String username){
        String mostFollowedDietID = null;
        openConnection();
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
            close();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostFollowedDietID;
    }

    public String lookUpMostSucceededDiet(){
        String mostSucceededDietID = null;
        openConnection();
        try ( Session session = driver.session() )
        {
            mostSucceededDietID = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run( "MATCH (diet: Diet) RETURN diet.id AS ID ORDER BY diet.succeededCount DESC LIMIT 1");
                return result.next().get("ID").asString();
            });
            close();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostSucceededDietID;
    }

    public String lookUpMostPopularDiet(){
        String mostPopularDietID = null;
        openConnection();
        try ( Session session = driver.session() )
        {
            mostPopularDietID = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run( "MATCH (diet: Diet) RETURN diet.id AS ID " +
                        "ORDER BY diet.succeededCount+diet.followersCount DESC LIMIT 1");
                return result.next().get("ID").asString();
            });
            close();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostPopularDietID;
    }

    public String lookUpMostPopularNutritionist(){
        String mostPopularNutritionistUsername = null;
        openConnection();
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
            close();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostPopularNutritionistUsername;
    }

    public String lookUpRecommendedDiet(StandardUser user){
        // suggestions are allowed only for users who aren't currently following any diet
        if(userAlreadyFollowedAnyDiet(user))
            return null;

        openConnection();
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
            close();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostRecommendedDietID;
    }

    public void dropAll(){
        openConnection();
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (n) DETACH DELETE n");
                return null;
            });
            close();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }


    public static void main( String... args ) throws Exception
    {
        try ( Neo4j neo4j = new Neo4j() )
        {
            neo4j.dropAll();
            StandardUser user1 = new StandardUser("user1");
            StandardUser user2 = new StandardUser("user2");
            StandardUser user3 = new StandardUser("user3");
            StandardUser user4 = new StandardUser("user4");
            StandardUser user5 = new StandardUser("user5");
            StandardUser user6 = new StandardUser("user6");
            StandardUser user7 = new StandardUser("user7");
            StandardUser user8 = new StandardUser("user8");
            StandardUser user9 = new StandardUser("user9");
            StandardUser user10 = new StandardUser("user10");
            StandardUser user11 = new StandardUser("user11");
            StandardUser user12 = new StandardUser("user12");
            StandardUser user13 = new StandardUser("user13");
            StandardUser user14 = new StandardUser("user14");
            StandardUser user15 = new StandardUser("user15");
            StandardUser user16 = new StandardUser("user16");
            StandardUser user17 = new StandardUser("user17");
            StandardUser user18 = new StandardUser("user18");
            Nutritionist nutritionist1 = new Nutritionist("nut1");
            Nutritionist nutritionist2 = new Nutritionist("nut2");
            Diet diet1 = new Diet("diet1", "diet1", nutritionist1);
            Diet diet2 = new Diet("diet2", "diet2", nutritionist1);
            Diet diet3 = new Diet("diet3", "diet3", nutritionist2);
            System.out.println("****Testing addUser****");
            System.out.println(neo4j.addUser(user1));
            System.out.println(neo4j.addUser(user1));
            System.out.println(neo4j.addUser(user2));
            System.out.println(neo4j.addUser(user3));
            System.out.println(neo4j.addUser(user4));
            System.out.println(neo4j.addUser(user5));
            System.out.println(neo4j.addUser(user6));
            System.out.println(neo4j.addUser(user7));
            System.out.println(neo4j.addUser(user8));
            System.out.println(neo4j.addUser(user9));
            System.out.println(neo4j.addUser(user10));
            System.out.println(neo4j.addUser(user11));
            System.out.println(neo4j.addUser(user12));
            System.out.println(neo4j.addUser(user13));
            System.out.println(neo4j.addUser(user14));
            System.out.println(neo4j.addUser(user15));
            System.out.println(neo4j.addUser(user16));
            System.out.println(neo4j.addUser(user17));
            System.out.println(neo4j.addUser(nutritionist1));
            System.out.println(neo4j.addUser(nutritionist1));
            System.out.println(neo4j.addUser(nutritionist2));
            System.out.println(neo4j.addDiet(diet1));
            System.out.println(neo4j.addDiet(diet2));
            System.out.println(neo4j.addDiet(diet3));
            System.out.println("****Testing followDiet****");
            System.out.println(neo4j.followDiet(user1, diet1.getId()));
            System.out.println(neo4j.followDiet(user2, diet1.getId()));
            System.out.println(neo4j.followDiet(user3, diet1.getId()));
            System.out.println(neo4j.followDiet(user4, diet1.getId()));
            System.out.println(neo4j.followDiet(user5, diet2.getId()));
            System.out.println(neo4j.followDiet(user6, diet2.getId()));
            System.out.println(neo4j.followDiet(user7, diet2.getId()));
            System.out.println(neo4j.followDiet(user8, diet3.getId()));
            System.out.println(neo4j.followDiet(user9, diet3.getId()));
            System.out.println(neo4j.followDiet(user10, diet3.getId()));
            System.out.println(neo4j.followDiet(user5, diet1.getId()));
            System.out.println(neo4j.followDiet(user6, diet1.getId()));
            System.out.println(neo4j.followDiet(user8, diet1.getId()));
            System.out.println(neo4j.followDiet(user2, diet3.getId()));
            System.out.println(neo4j.followDiet(user9, diet1.getId()));
            System.out.println(neo4j.followDiet(user11, diet3.getId()));
            System.out.println(neo4j.followDiet(user12, diet3.getId()));
            System.out.println(neo4j.followDiet(user13, diet3.getId()));
            System.out.println(neo4j.followDiet(user14, diet3.getId()));
            System.out.println(neo4j.followDiet(user15, diet3.getId()));
            System.out.println(neo4j.followDiet(user16, diet3.getId()));
            System.out.println(neo4j.followDiet(user17, diet3.getId()));
            System.out.println(neo4j.followDiet(user18, diet3.getId()));
            System.out.println(neo4j.followDiet(user18, diet3.getId()));
            System.out.println(neo4j.followDiet(user9, diet2.getId()));
            System.out.println("****Testing stopDiet****");
            /*System.out.println(neo4j.stopDiet(diet1, user1, true));
            System.out.println(neo4j.stopDiet(diet1, user2, true));
            System.out.println(neo4j.stopDiet(diet2, user5, true));
            System.out.println(neo4j.stopDiet(diet2, user6, true));
            System.out.println(neo4j.stopDiet(diet2, user7, true));
            System.out.println(neo4j.stopDiet(diet1, user5, true));
            System.out.println(neo4j.stopDiet(diet1, user6, true));
            System.out.println(neo4j.stopDiet(diet1, user8, true));
            System.out.println(neo4j.stopDiet(diet3, user8, true));
            System.out.println(neo4j.stopDiet(diet3, user2, false));
            System.out.println(neo4j.stopDiet(diet1, user9, false));
            System.out.println(neo4j.stopDiet(diet1, user3, false));
            System.out.println(neo4j.followDiet(user2, diet3));
            System.out.println(neo4j.stopDiet(diet3, user2, true));
*/
/*            System.out.println(neo4j.removeDiet(diet1));
            System.out.println(neo4j.removeUser(user1));
            System.out.println(neo4j.removeUser(user4));
            System.out.println(neo4j.removeUser(nutritionist2));
*/
            System.out.println("****Testing lookup****");
            System.out.println(neo4j.lookUpMostFollowedDiet());
            System.out.println(neo4j.lookUpMostSucceededDiet());
            System.out.println(neo4j.lookUpMostPopularDiet());
            System.out.println(neo4j.lookUpRecommendedDiet(user1));
            System.out.println(neo4j.lookUpRecommendedDiet(user3));
            System.out.println(neo4j.lookUpRecommendedDiet(user11));
            System.out.println(neo4j.lookUpMostFollowedDietByNutritionist(nutritionist1.getUsername()));
            System.out.println(neo4j.lookUpMostPopularNutritionist());
        }
    }
}



