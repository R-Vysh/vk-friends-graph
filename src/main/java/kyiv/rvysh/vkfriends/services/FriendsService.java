package kyiv.rvysh.vkfriends.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kyiv.rvysh.vkfriends.dao.PersonDao;
import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.utils.DepthLevel;
import kyiv.rvysh.vkfriends.utils.Pair;

public class FriendsService {
	private VkService vkService;
	private PersonDao personDao;

	public void upsertFriends(int userId, boolean loadInfo) {
		upsertFriends(userId, 1, loadInfo);
	}

	public void upsertFriends(int userId, int depth, boolean loadInfo) {
		// insert person for which friends are loaded
		PersonInfo info = loadInfo(userId, loadInfo);
		// create person if not exists
		personDao.insertPerson(info);
		List<Pair<Integer, Integer>> nodesToProcess = new ArrayList<>();
		nodesToProcess.add(new Pair<Integer, Integer>(userId, depth));
		List<Integer> processedNodes = new ArrayList<>();
		while (!nodesToProcess.isEmpty()) {
			Pair<Integer, Integer> node = nodesToProcess.remove(0);
			Integer id = node.getFirst();
			Integer curDepth = node.getSecond();
			if (!processedNodes.contains(id) && curDepth != 0) {
				List<PersonInfo> friends = listFriends(id, loadInfo);
				friends.forEach(value -> nodesToProcess.add(new Pair<>(value.getUid(), curDepth - 1)));
				personDao.insertFriendship(id, friends);
				processedNodes.add(id);
			}
		}
	}

	public List<PersonInfo> findFriends(int userId) {
		return findFriends(userId, 1);
	}

	public List<PersonInfo> findFriends(int userId, int depth) {
		return personDao.findFriends(userId, depth);
	}

	public Neo4jGraph<PersonInfo> findFriendsGraph(int userId, DepthLevel level) {
		return personDao.findFriendsGraph(userId, level);
	}
	
	public Map<PersonInfo, Long> findClosestPeople(int userId, int size) {
		return personDao.findClosestPeople(userId, size);
	}
	
	private List<PersonInfo> listFriends(int userId, boolean loadInfo) {
		return vkService.listFriends(userId, loadInfo);
	}

	private PersonInfo loadInfo(int userId, boolean loadInfo) {
		return vkService.userInfo(userId, loadInfo);
	}
	
	public void setVkService(VkService vkService) {
		this.vkService = vkService;
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}
}
