package it.unipi.dii.dietmanager.persistence;

import it.unipi.dii.dietmanager.entities.Diet;
import it.unipi.dii.dietmanager.entities.Nutritionist;
import it.unipi.dii.dietmanager.entities.StandardUser;
import it.unipi.dii.dietmanager.entities.User;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;

import java.util.ArrayList;
import java.util.List;

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
            // I perform the query to understand if this diet already exists
/*            Integer dietAlreadyExists = session.readTransaction((TransactionWork<Integer>) tx -> {
                Result result = tx.run( "MATCH (diet: Diet) WHERE id = $id RETURN diet",
                        parameters( "id", diet.getId()));
                if(result.hasNext()) {
                    return 1;
                }
                return 0;
            });

            if(dietAlreadyExists == 1)
                return false;
*/
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

    private void printAge(String name)
    {
        try ( Session session = driver.session() )
        {
            Integer age = session.readTransaction((TransactionWork<Integer>) tx -> {
                Result result = tx.run( "MATCH (p:Person) WHERE p.name = $name RETURN p.age",
                        parameters( "name", name) );
                return result.single().get(0).asInt();
            });
            System.out.println(age);
        }
    }

    //Find and show all the movie titles an actor acted in
    private void printActorMovies( final String actorName)
    {
        try ( Session session = driver.session() )
        {
            List<String> movieTitles = session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run( "MATCH (p:Person)-[:ACTED_IN]->(m) WHERE p.name = $name" +
                                " RETURN m.title as Title",
                        parameters( "name", actorName) );
                ArrayList<String> movies = new ArrayList<>();
                while(result.hasNext())
                {
                    Record r = result.next();
                    movies.add(r.get("Title").asString());
                }
                return movies;
            });
            System.out.println(movieTitles);
        }
    }

    //Find and show all the co-actors of an actor
    private void printCoActors( final String actorName)
    {
        try ( Session session = driver.session() )
        {
            List<String> coActorsNames = session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run( "MATCH (p:Person)-[:ACTED_IN]->(m)<-[:ACTED_IN]-(others) " +
                                "WHERE p.name = $name " +
                                "RETURN others.name as Coactors",
                        parameters( "name", actorName) );
                ArrayList<String> coActors = new ArrayList<>();
                while(result.hasNext())
                {
                    Record r = result.next();
                    coActors.add(r.get("Coactors").asString());
                }
                return coActors;
            });
            System.out.println(coActorsNames);
        }
    }

    //Find the actor with the maximum number of movies who acted in
    private void printMostFamousActor()
    {
        try ( Session session = driver.session() )
        {
            String mostFamousActor = session.readTransaction((TransactionWork<String>) tx -> {

                String query = "MATCH (p:Person)-[:ACTED_IN]->(m:Movie) " +
                        "RETURN p.name AS Name, count(*) as NumMovies " +
                        "ORDER BY NumMovies DESC " +
                        "LIMIT 1";
                Result result = tx.run( query );
                return result.single().get("Name").asString();
            });
            System.out.println(mostFamousActor);
        }
    }

    //Find actors with at least n movies
    private void printActorsWithAtLeastNMovies(final int n)
    {
        try ( Session session = driver.session() )
        {
            session.readTransaction((TransactionWork<Void>) tx -> {

                String query = "MATCH (p:Person)-[:ACTED_IN]->(m:Movie) " +
                        "WITH p.name AS Name, count(*) AS NumMovies " +
                        "WHERE NumMovies >= $numMovies " +
                        "RETURN Name, NumMovies";
                Result result = tx.run(query, parameters("numMovies", n));
                while (result.hasNext()) {
                    Record r = result.next();
                    String name = r.get("Name").asString();
                    int nMovies = r.get("NumMovies").asInt();
                    System.out.println(name + " with " + nMovies + " movies");
                }
                return null;
            });
        }
    }

    //Find all the movies where the average age of actors is lower than the youngest director by 20 years
    private void printMoviesWithVeryOldDirectors()
    {
        try ( Session session = driver.session() )
        {
            session.readTransaction((TransactionWork<Void>) tx -> {

                String query = "MATCH (p:Person)-[:ACTED_IN]->(m:Movie) " +
                        "WITH m.title AS Title, avg(m.released - p.born) AS AvgActorsAge " +
                        "MATCH (p2:Person)-[:DIRECTED]->(m2:Movie) WHERE m2.title = Title " +
                        "WITH m2.title AS Title, min(m2.released - p2.born) AS YoungestDirectorAge, AvgActorsAge " +
                        "WHERE AvgActorsAge < YoungestDirectorAge - 20 " +
                        "RETURN Title, YoungestDirectorAge, AvgActorsAge";
                Result result = tx.run(query);
                List<Record> records = result.list();
                for (Record r: records) {
                    String title = r.get("Title").asString();
                    int youngestDirectorAge = r.get("YoungestDirectorAge").asInt();
                    double avgActorsAge = r.get("AvgActorsAge").asDouble();
                    System.out.println("\"" + title + "\". Youngest director is " + youngestDirectorAge + " years old" +
                            ", and the average actors' age is " + avgActorsAge);
                }
                return null;
            });
        }
    }

    //Find and show the shortest path between two actor which passes through a third actor
    private void printShortestPathWithIntermediaryActor(final String firstActor, final String lastActor,
                                                        final String middleActor)
    {
        try ( Session session = driver.session() )
        {
            session.readTransaction((TransactionWork<Void>) tx -> {

                String query = "MATCH path = shortestPath((:Person {name: $name1})-[*]-(:Person {name: $name2})) " +
                        "WHERE ANY(x IN nodes(path) WHERE x.name = $name3) " +
                        "RETURN path";
                Result result = tx.run( query,
                        parameters("name1", firstActor, "name2", lastActor, "name3", middleActor));
                Path path = result.single().get("path").asPath();
                String pathString = "--";
                for(Node n : path.nodes())
                {
                    if(n.hasLabel("Person"))
                    {
                        pathString += n.get("name").asString() + "--";
                    }
                    else if(n.hasLabel("Movie"))
                    {
                        pathString += n.get("title").asString() + "--";
                    }
                }
                System.out.println("The length of the path is " + path.length());
                System.out.println(pathString);
                return null;
            });
        }
    }

    public static void main( String... args ) throws Exception
    {
        try ( Neo4j neo4j = new Neo4j( "neo4j://localhost:7687", "neo4j", "root" ) )
        {
            StandardUser user1 = new StandardUser("user1");
            StandardUser user2 = new StandardUser("user2");
            StandardUser user3 = new StandardUser("user3");
            Nutritionist nutritionist1 = new Nutritionist("nut1");
            Nutritionist nutritionist2 = new Nutritionist("nut2");
            Diet diet1 = new Diet("diet1", "vegan", nutritionist1);
            Diet diet2 = new Diet("diet2", "vegan", nutritionist1);
            Diet diet3 = new Diet("diet3", "vegetarian", nutritionist2);
            System.out.println(neo4j.addUser(user1));
            System.out.println(neo4j.addUser(user1));
            System.out.println(neo4j.addUser(user2));
            System.out.println(neo4j.addUser(user3));
            System.out.println(neo4j.addUser(nutritionist1));
            System.out.println(neo4j.addUser(nutritionist2));
            System.out.println(neo4j.addDiet(diet1));
            System.out.println(neo4j.addDiet(diet2));
            System.out.println(neo4j.addDiet(diet3));
            System.out.println(neo4j.followDiet(user1, diet1));
            System.out.println(neo4j.followDiet(user2, diet2));
            System.out.println(neo4j.followDiet(user1, diet1));
            System.out.println(neo4j.followDiet(user3, diet1));
            System.out.println(neo4j.stopDiet(diet1, user1, true));
            System.out.println(neo4j.stopDiet(diet1, user1, true));
            System.out.println(neo4j.stopDiet(diet2, user2, false));
/*            neo4j.addPerson( "Mario");
            System.out.println("---------------------------");
            neo4j.printAge( "Gionatan");
            System.out.println("===========================");

            neo4j.printActorMovies( "Tom Hanks");
            System.out.println("---------------------------");
            neo4j.printCoActors( "Tom Hanks");
            System.out.println("---------------------------");
            neo4j.printMostFamousActor();
            System.out.println("---------------------------");
            neo4j.printActorsWithAtLeastNMovies(5);
            System.out.println("---------------------------");
            neo4j.printMoviesWithVeryOldDirectors();
            System.out.println("---------------------------");
            neo4j.printShortestPathWithIntermediaryActor("Tom Hanks",
                    "Kevin Bacon", "Meg Ryan");*/
        }
    }
}


