MATCH (me:Person {uid: {user_id} })-[:FRIEND*..%DEPTH% ]-(fr:Person) 
return collect(distinct fr)