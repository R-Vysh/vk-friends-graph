MATCH (u:Person {uid: {userid}})
FOREACH (friend in {friends} | 
    MERGE (f:Person {uid:friend.uid}) 
    SET f += friend
    MERGE (u)-[:FRIEND]->(f)
)