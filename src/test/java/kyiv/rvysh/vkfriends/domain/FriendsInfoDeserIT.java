package kyiv.rvysh.vkfriends.domain;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FriendsInfoDeserIT {

	@Test
	public void testFriendInfoDeser() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String response = IOUtils.toString(new ClassPathResource("friends-list.json").getInputStream());
		mapper.readValue(response, PersonInfoResponse.class);
	}
}
