MATCH (me:Person {uid: {user_id} })-[ff:FRIEND*..%DEPTH%]-(fr:Person) 
WITH me + collect(distinct fr) as allnodes, ID(me) + collect(distinct ID(fr)) as allids 
MATCH (a)-[friendship:FRIEND]-(b) 
WHERE ID(a) IN allids AND ID(b) IN allids
RETURN allnodes, collect(friendship)