MATCH (u:Person {uid: {user_id}})
UNWIND {friends} AS friend
WITH u as u, friend as friend
MATCH (f:Person {uid:friend.uid}) 
CREATE UNIQUE (u)-[:FRIEND]-(f)