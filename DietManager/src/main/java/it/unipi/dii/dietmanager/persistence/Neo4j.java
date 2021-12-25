package it.unipi.dii.dietmanager.persistence;

import it.unipi.dii.dietmanager.entities.Diet;
import it.unipi.dii.dietmanager.entities.Nutritionist;
import it.unipi.dii.dietmanager.entities.StandardUser;
import it.unipi.dii.dietmanager.entities.User;
import org.neo4j.driver.*;

import static org.neo4j.driver.Values.parameters;

public class Neo4j implements AutoCloseable
{
    private final Driver driver;

    public Neo4j( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public boolean addUser(User user)
    {
        try ( Session session = driver.session() )
        {
            // I perform the query to understand if this user already exists
/*            Integer userAlreadyExists = session.readTransaction((TransactionWork<Integer>) tx -> {
                Result result = tx.run( "MATCH (user: User) WHERE EXISTS(user) AND username = $username RETURN user",
                        parameters( "username", user.getUsername()));
                if(result.hasNext()) {
                    return 1;
                }
                return 0;
            });

            if(userAlreadyExists == 1)
                return false;
*/
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
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean addDiet(Diet diet)
    {
        try ( Session session = driver.session() )
        {
            // I create the Diet node
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MERGE (:Diet {id: $id, name: $name, followersCount: 0, succededCount: 0, failedCount: 0})",
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
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean followDiet(StandardUser user, Diet diet)
    {
        try ( Session session = driver.session() )
        {
            // I perform query to understand if this user already followed this diet
            Integer userAlreadyFollowedDiet = session.readTransaction((TransactionWork<Integer>) tx -> {
                Result result = tx.run( "MATCH (user: User)-[follows:FOLLOWS]->(diet: Diet) " +
                                "WHERE user.username = $username AND diet.id = $id AND follows.status = \"current\"" +
                                "RETURN follows",
                        parameters( "username", user.getUsername(), "id", diet.getId()));
                if(result.hasNext())
                    return 1;
                return 0;
            });

            if(userAlreadyFollowedDiet == 1)
                return false;

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (user:User), (diet:Diet) WHERE user.username = $username " +
                        "AND diet.id = $id CREATE (user)-[:FOLLOWS {result: null, status: \"current\"}]->(diet) " +
                        "SET diet.followersCount = diet.followersCount + 1",
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

    public boolean stopDiet(Diet diet, StandardUser user, boolean isSucceded){
        try ( Session session = driver.session() )
        {
            // I perform query to understand if this user already followed this diet
            Integer userAlreadyFollowedDiet = session.readTransaction((TransactionWork<Integer>) tx -> {
                Result result = tx.run( "MATCH (user: User)-[follows:FOLLOWS]->(diet: Diet) " +
                                "WHERE user.username = $username AND diet.id = $id AND follows.status = \"current\"" +
                                "RETURN follows",
                        parameters( "username", user.getUsername(), "id", diet.getId()));
                if(result.hasNext())
                    return 1;
                return 0;
            });

            if(userAlreadyFollowedDiet == 0)
                return false;

            String dietResult = isSucceded == true ? "succeded" : "failed";
            int failedCountIncrement = isSucceded == true? 0 : 1;
            int succededCountIncrement = isSucceded == true? 1 : 0;

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (user:User)-[follows:FOLLOWS]->(diet:Diet) WHERE user.username = $username " +
                                "AND diet.id = $id SET follows.status = $status, diet.followersCount = diet.followersCount - 1, " +
                                "diet.failedCount = diet.failedCount + $failedCountIncrement, " +
                                "diet.succededCount = diet.succededCount + $succededCountIncrement",
                        parameters( "username", user.getUsername(), "id", diet.getId(), "status", dietResult,
                                    "failedCountIncrement", failedCountIncrement, "succededCountIncrement", succededCountIncrement));
                return null;
            });
            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean removeDiet(Diet diet){
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (diet: Diet{id: $id}) DETACH DELETE diet",
                        parameters( "id", diet.getId()));
                return null;
            });

            return true;
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean removeUser(User user){
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (user: User{username: $username})-[follows:FOLLOWS]->(diet: Diet)" +
                                "WITH diet, user, (CASE WHEN follows.status = \"current\" THEN 1 ELSE 0 END) AS followersCountDecrement, " +
                                "(CASE WHEN follows.status = \"succeded\" THEN 1 ELSE 0 END) AS succededCountDecrement, " +
                                "(CASE WHEN follows.status = \"failed\" THEN 1 ELSE 0 END) AS failedCountDecrement " +
                                "SET diet.followersCount = diet.followersCount - followersCountDecrement, " +
                                "diet.failedCount = diet.failedCount - failedCountDecrement, " +
                                "diet.succededCount = diet.succededCount - succededCountDecrement DETACH DELETE user",
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
                return result.next().get("ID").asString();
            });
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostFollowedDietID;
    }

    public String lookUpMostSuccededDiet(){
        String mostSuccededDietID = null;
        try ( Session session = driver.session() )
        {
            mostSuccededDietID = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run( "MATCH (diet: Diet) RETURN diet.id AS ID ORDER BY diet.succededCount DESC LIMIT 1");
                return result.next().get("ID").asString();
            });
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostSuccededDietID;
    }

    public String lookUpMostPopularDiet(){
        String mostPopularDietID = null;
        try ( Session session = driver.session() )
        {
            mostPopularDietID = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run( "MATCH (diet: Diet) RETURN diet.id AS ID ORDER BY diet.succededCount+diet.followersCount DESC LIMIT 1");
                return result.next().get("ID").asString();
            });
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostPopularDietID;
    }


    public static void main( String... args ) throws Exception
    {
        try ( Neo4j neo4j = new Neo4j( "neo4j://localhost:7687", "neo4j", "root" ) )
        {
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
            Nutritionist nutritionist1 = new Nutritionist("nut1");
            Nutritionist nutritionist2 = new Nutritionist("nut2");
            Diet diet1 = new Diet("diet1", "vegan1", nutritionist1);
            Diet diet2 = new Diet("diet2", "vegan2", nutritionist1);
            Diet diet3 = new Diet("diet3", "vegetarian", nutritionist2);
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
            System.out.println(neo4j.addUser(nutritionist1));
            System.out.println(neo4j.addUser(nutritionist2));
            System.out.println(neo4j.addDiet(diet1));
            System.out.println(neo4j.addDiet(diet2));
            System.out.println(neo4j.addDiet(diet3));
            System.out.println(neo4j.followDiet(user1, diet1));
            System.out.println(neo4j.followDiet(user2, diet1));
            System.out.println(neo4j.followDiet(user3, diet1));
            System.out.println(neo4j.followDiet(user4, diet1));
            System.out.println(neo4j.followDiet(user5, diet2));
            System.out.println(neo4j.followDiet(user6, diet2));
            System.out.println(neo4j.followDiet(user7, diet2));
            System.out.println(neo4j.followDiet(user8, diet3));
            System.out.println(neo4j.followDiet(user9, diet3));
            System.out.println(neo4j.followDiet(user10, diet3));
            System.out.println(neo4j.stopDiet(diet1, user1, true));
            System.out.println(neo4j.stopDiet(diet1, user2, true));
            System.out.println(neo4j.stopDiet(diet2, user5, true));
            System.out.println(neo4j.stopDiet(diet2, user6, true));
            System.out.println(neo4j.stopDiet(diet2, user7, true));
/*            System.out.println(neo4j.removeDiet(diet1));
            System.out.println(neo4j.removeUser(user1));
            System.out.println(neo4j.removeUser(user4));
            System.out.println(neo4j.removeUser(nutritionist2));
*/
            System.out.println(neo4j.lookUpMostFollowedDiet());
            System.out.println(neo4j.lookUpMostSuccededDiet());
            System.out.println(neo4j.lookUpMostPopularDiet());
        }
    }
}



