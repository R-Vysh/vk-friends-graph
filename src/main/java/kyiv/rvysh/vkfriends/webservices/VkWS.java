package kyiv.rvysh.vkfriends.webservices;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.services.VkService;

@RequestMapping("/vk")
// TODO remove this low-level service
public class VkWS {

	private VkService service;

	@RequestMapping(value = "/list-friends/{userId}")
	@ResponseBody
	public List<PersonInfo> listFriends(@PathVariable int userId) throws ClientProtocolException, URISyntaxException, IOException {
		return service.listFriends(userId, true);
	}

	@RequestMapping(value = "/info/{userId}")
	@ResponseBody
	public PersonInfo userInfo(@PathVariable int userId) throws ClientProtocolException, URISyntaxException, IOException {
		return service.userInfo(userId, true);
	}
	
	public void setService(VkService service) {
		this.service = service;
	}
}
