MATCH (me:Person {uid: {user_id} })-[:FRIEND]-(:Person)-[rel:FRIEND]-(people:Person)
WHERE (me)-[:FRIEND]-(people)
RETURN people, count(DISTINCT rel)
ORDER BY count(DISTINCT rel) DESC
limit {size}