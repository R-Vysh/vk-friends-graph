MATCH (me:Person {uid: {user_id} })-[:FRIEND]-(fr:Person) 
MATCH (fr)-[friendship:FRIEND]-() 
RETURN fr, collect(friendship)