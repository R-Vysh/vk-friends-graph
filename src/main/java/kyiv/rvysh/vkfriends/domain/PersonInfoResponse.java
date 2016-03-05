package kyiv.rvysh.vkfriends.domain;

import java.util.List;

public class PersonInfoResponse {
	private List<PersonInfo> response;

	public List<PersonInfo> getResponse() {
		return response;
	}
	
	public void setResponse(List<PersonInfo> response) {
		this.response = response;
	}
}
