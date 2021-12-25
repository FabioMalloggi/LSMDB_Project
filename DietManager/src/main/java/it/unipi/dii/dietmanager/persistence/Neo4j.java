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

    public String lookUpMostFollowedDietByNutritionist(Nutritionist nutritionist){
        String mostFollowedDietID = null;
        try ( Session session = driver.session() )
        {
            mostFollowedDietID = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run( "MATCH (nutritionist: Nutritionist)-[:PROVIDES]->(diet: Diet) " +
                                        "WHERE nutritionist.username = $username " +
                                        "RETURN diet.id AS ID ORDER BY diet.followersCount DESC LIMIT 1",
                                        parameters("username", nutritionist.getUsername()));
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
                Result result = tx.run( "MATCH (diet: Diet) RETURN diet.id AS ID " +
                        "ORDER BY diet.succededCount+diet.followersCount DESC LIMIT 1");
                return result.next().get("ID").asString();
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
                                            "ORDER BY diet.succededCount+diet.followersCount DESC LIMIT 1");
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

    public String lookUpMostRecommendedDiet(StandardUser user){
        String mostRecommendedDietID = null;
        try ( Session session = driver.session() )
        {
            mostRecommendedDietID = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run(
                "MATCH (user1: User)-[f1:FOLLOWS]->(diet1: Diet)<-[f2:FOLLOWS]-(user2: User)-[f3:FOLLOWS]->(diet2: Diet) " +
                    "WHERE f1.status = f2.status AND f1.status <> \"current\" AND user1.username = $username " +
                    "AND f3.status = \"succeded\" WITH diet2, count(*) AS validArrowCount " +
                    "RETURN diet2.id AS ID ORDER BY validArrowCount DESC LIMIT 1",
                    parameters("username", user.getUsername()));
                if(result.hasNext())
                    return result.next().get("ID").asString();
                // if the target user doesn't have completed diets or if no one of the users who completed/failed the same diet
                // of the target user don't have completed diets, then I return the most succeded diet (in general)
                return lookUpMostSuccededDiet();
            });
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return mostRecommendedDietID;
    }

    public void dropAll(){
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


    public static void main( String... args ) throws Exception
    {
        try ( Neo4j neo4j = new Neo4j( "neo4j://localhost:7687", "neo4j", "root" ) )
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
            Nutritionist nutritionist1 = new Nutritionist("nut1");
            Nutritionist nutritionist2 = new Nutritionist("nut2");
            Diet diet1 = new Diet("diet1", "diet1", nutritionist1);
            Diet diet2 = new Diet("diet2", "diet2", nutritionist1);
            Diet diet3 = new Diet("diet3", "diet3", nutritionist2);
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
            System.out.println(neo4j.followDiet(user5, diet1));
            System.out.println(neo4j.followDiet(user6, diet1));
            System.out.println(neo4j.followDiet(user8, diet1));
            System.out.println(neo4j.followDiet(user2, diet3));
            System.out.println(neo4j.followDiet(user9, diet1));
            System.out.println(neo4j.followDiet(user11, diet3));
            System.out.println(neo4j.followDiet(user12, diet3));
            System.out.println(neo4j.followDiet(user13, diet3));
            System.out.println(neo4j.followDiet(user14, diet3));
            System.out.println(neo4j.followDiet(user15, diet3));
            System.out.println(neo4j.followDiet(user16, diet3));
            System.out.println(neo4j.followDiet(user17, diet3));
            System.out.println(neo4j.stopDiet(diet1, user1, true));
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

/*            System.out.println(neo4j.removeDiet(diet1));
            System.out.println(neo4j.removeUser(user1));
            System.out.println(neo4j.removeUser(user4));
            System.out.println(neo4j.removeUser(nutritionist2));
*/
            System.out.println(neo4j.lookUpMostFollowedDiet());
            System.out.println(neo4j.lookUpMostSuccededDiet());
            System.out.println(neo4j.lookUpMostPopularDiet());
            System.out.println(neo4j.lookUpMostRecommendedDiet(user1));
            System.out.println(neo4j.lookUpMostRecommendedDiet(user3));
            System.out.println(neo4j.lookUpMostRecommendedDiet(user11));
            System.out.println(neo4j.lookUpMostFollowedDietByNutritionist(nutritionist1));
            System.out.println(neo4j.lookUpMostPopularNutritionist());
        }
    }
}



