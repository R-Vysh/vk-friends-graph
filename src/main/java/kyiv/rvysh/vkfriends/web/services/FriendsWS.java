package kyiv.rvysh.vkfriends.web.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.services.FriendsService;
import kyiv.rvysh.vkfriends.utils.DepthLevel;

@RequestMapping("/friends")
public class FriendsWS {

	private FriendsService service;
	private ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(value = "/save/{userId}", method = RequestMethod.POST)
	public ResponseEntity<Void> saveFriends(@PathVariable int userId) {
		service.upsertFriendsGraph(userId, 1, true);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/get/{userId}")
	@ResponseBody
	public List<PersonInfo> getFriends(@PathVariable int userId) {
		return service.findFriends(userId);
	}

	@RequestMapping(value = "/get/{userId}/{depth}")
	@ResponseBody
	public List<PersonInfo> getFriends(@PathVariable(value = "userId") int userId,
			@PathVariable(value = "depth") int depth) {
		return service.findFriends(userId, depth);
	}

	@RequestMapping(value = "/get-closest-friends/{userId}/{size}")
	@ResponseBody
	// TODO avoid using mapper
	public Map<String, Long> getClosestFriends(@PathVariable(value = "userId") int userId,
			@PathVariable(value = "size") int size) throws JsonProcessingException {
		Map<PersonInfo, Long> map = service.findClosestFriends(userId, size);
		Map<String, Long> result = new HashMap<String, Long>();
		for (Map.Entry<PersonInfo, Long> entry : map.entrySet()) {
			result.put(mapper.writeValueAsString(entry.getKey()), entry.getValue());
		}
		return result;
	}
	
	@RequestMapping(value = "/get-closest-people/{userId}/{size}")
	@ResponseBody
	// TODO avoid using mapper
	public Map<String, Long> getClosestPeople(@PathVariable(value = "userId") int userId,
			@PathVariable(value = "size") int size) throws JsonProcessingException {
		Map<PersonInfo, Long> map = service.recommendPeople(userId, size);
		Map<String, Long> result = new HashMap<String, Long>();
		for (Map.Entry<PersonInfo, Long> entry : map.entrySet()) {
			result.put(mapper.writeValueAsString(entry.getKey()), entry.getValue());
		}
		return result;
	}

	@RequestMapping(value = "/get-graph/{userId}")
	@ResponseBody
	public Neo4jGraph<PersonInfo> getFriendsGraph(@PathVariable int userId) {
		return service.findFriendsGraph(userId, DepthLevel.ONE);
	}

	@RequestMapping(value = "/get-graph-2/{userId}")
	@ResponseBody
	public Neo4jGraph<PersonInfo> getFriendsGraph2(@PathVariable int userId) {
		return service.findFriendsGraph(userId, DepthLevel.TWO);
	}

	public void setService(FriendsService service) {
		this.service = service;
	}
}
