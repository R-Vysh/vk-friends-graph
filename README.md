# vkfriendsgraph

App for analyzing and visualising friend graphs for Vk.com.

Prerequisites: <br>
1. Install neo4j. v2.3.2 in this case. Other version will fit, but update pom.xml in that case. <br>
2. Run "CREATE CONSTRAINT ON (n:Person) ASSERT n.uid IS UNIQUE" in Neo4j console. <br>
3. Change app.properties for your neo4j user and password. <br>
4. Run via App.java. <br>
5. Visit http://localhost:8080/static/graph.html