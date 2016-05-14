MATCH (u:Person {uid: {user_id}})
FOREACH (friend in {friends} | 
    MERGE (f:Person {uid:friend.uid}) 
    SET f += friend
    CREATE UNIQUE (u)-[:FRIEND]-(f)
)