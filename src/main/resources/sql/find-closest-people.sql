MATCH (me:Person {uid: {user_id} })-[:FRIEND]-(:Person)-[rel:FRIEND]-(people:Person)
return people, count(DISTINCT rel)
ORDER BY count(DISTINCT rel) DESC
limit {size}