package kyiv.rvysh.vkfriends.domain.graph;

public class Neo4jError {
	private String code;
	private String message;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Neo4jError [code=" + code + ", message=" + message + "]";
	}
}
