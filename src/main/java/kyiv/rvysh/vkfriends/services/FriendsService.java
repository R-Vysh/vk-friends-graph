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
		upsertFriendsGraph(userId, 1, loadInfo);
	}

	public void upsertFriendsGraph(int userId, int depth, boolean loadInfo) {
		depth++;
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
				if (curDepth == 1) {
					List<PersonInfo> filteredFriends = new ArrayList<>();
					for (PersonInfo f : friends) {
						if (processedNodes.contains(f.getUid())) {
							filteredFriends.add(f);
						}
					}
					personDao.insertLinksOnly(id, filteredFriends);
				} else {
					personDao.insertFriendship(id, friends);
				}
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

	public Map<PersonInfo, Long> findClosestFriends(int userId, int size) {
		return personDao.findClosestFriends(userId, size);
	}

	public Map<PersonInfo, Long> recommendPeople(int userId, int size) {
		PersonInfo person = personDao.loadPerson(userId);
		List<PersonInfo> possibleFriends = personDao.findFriends(userId, 2);
		Map<PersonInfo, Long> scores = personDao.findClosestPeople(userId, size);
		for (PersonInfo friend : possibleFriends) {
			Long addScore = Long.valueOf(0);
			if (person.country == friend.country) {
				addScore += 1L;
			}
			if (person.city == friend.city) {
				addScore += 3L;
			}
			Long finalScore = (scores.get(friend) == null ? 0 : scores.get(friend)) + addScore;
			scores.put(person, finalScore);
		}
		scores.remove(person);
		return scores;
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
