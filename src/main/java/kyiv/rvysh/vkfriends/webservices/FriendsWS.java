package kyiv.rvysh.vkfriends.webservices;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.services.FriendsService;

@RequestMapping("/friends")
public class FriendsWS {

	private FriendsService service;

	@RequestMapping(value = "/save/{userId}")
	public ResponseEntity<Void> saveFriends(@PathVariable int userId) {
		service.upsertFriends(userId, true);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/get/{userId}")
	@ResponseBody
	public List<PersonInfo> getFriends(@PathVariable int userId) {
		return service.findFriends(userId);
	}

	@RequestMapping(value = "/get/{userId}/{depth}")
	@ResponseBody
	public List<PersonInfo> getFriends(@PathVariable(value = "userId") int userId, @PathVariable(value = "depth") int depth) {
		return service.findFriends(userId, depth);
	}

	public void setService(FriendsService service) {
		this.service = service;
	}
}
