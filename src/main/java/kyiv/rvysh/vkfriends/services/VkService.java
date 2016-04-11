package kyiv.rvysh.vkfriends.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.domain.PersonInfoResponse;
import kyiv.rvysh.vkfriends.utils.HttpRequestUtils;

public class VkService {
	private static final Logger LOGGER = LoggerFactory.getLogger(VkService.class);
	private static final String VK_URL = "api.vk.com";
	private static final String FRIENDS_GET_URL = "/method/friends.get";
	private static final String USERS_GET_URL = "/method/users.get";
	private static final Joiner JOINER = Joiner.on(",");

	private ObjectMapper mapper;
	private List<String> fields;

	public VkService() {
		mapper = new ObjectMapper();
	}

	public List<PersonInfo> listFriends(int userId, boolean loadInfo) {
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		if (loadInfo) {
			params.put("fields", JOINER.join(fields));
		}
		try {
			LOGGER.info("Getting friends for {}", userId);
			String resp = HttpRequestUtils.sendGet(VK_URL, FRIENDS_GET_URL, params);
			if (!loadInfo) {
				@SuppressWarnings("unchecked")
				Map<String, List<Integer>> m = (Map<String, List<Integer>>) mapper.readValue(resp, Map.class);
				List<Integer> ids = m.get("response");
				List<PersonInfo> result = new ArrayList<>();
				for (Integer id : ids) {
					PersonInfo info = new PersonInfo();
					info.setUid(id);
					result.add(info);
				}
				return result;
			} else {
				return mapper.readValue(resp, PersonInfoResponse.class).getResponse();
			}
		} catch (URISyntaxException | IOException e) {
			LOGGER.error("Could not load from vk", e);
		}
		return new ArrayList<>();
	}

	public PersonInfo userInfo(int userId, boolean loadInfo) {
		if (!loadInfo) {
			return new PersonInfo(userId);
		}
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		params.put("fields", JOINER.join(fields));
		try {
			LOGGER.info("Getting user info for {}", userId);
			String resp = HttpRequestUtils.sendGet(VK_URL, USERS_GET_URL, params);
			return mapper.readValue(resp, PersonInfoResponse.class).getResponse().get(0);
		} catch (URISyntaxException | IOException e) {
			LOGGER.error("Could not load from vk", e);
		}
		return new PersonInfo();
	}

	public List<PersonInfo> usersInfo(List<Integer> userIds) {
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", JOINER.join(userIds));
		params.put("fields", JOINER.join(fields));
		try {
			LOGGER.info("Getting users info for {}", userIds);
			String resp = HttpRequestUtils.sendGet(VK_URL, USERS_GET_URL, params);
			return mapper.readValue(resp, PersonInfoResponse.class).getResponse();
		} catch (URISyntaxException | IOException e) {
			LOGGER.error("Could not load from vk", e);
		}
		return new ArrayList<>();
	}

	@Required
	public void setFields(List<String> fields) {
		this.fields = fields;
	}
}
