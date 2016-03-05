package kyiv.rvysh.vkfriends.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
// TODO remove public
public class PersonInfo {

	public PersonInfo() {
	}

	public PersonInfo(int uid) {
		this.uid = uid;
	}

	public int uid;
	@JsonProperty("first_name")
	public String firstName;
	@JsonProperty("last_name")
	public String lastName;
	public short sex;
	public String nickname;
	public String domain;
	// String because may be missing year
	@JsonProperty("bdate")
	public String birthDate;
	public int city;
	public int country;
	@JsonProperty("photo_50")
	public String photo50;
	@JsonProperty("photo_100")
	public String photo100;
	@JsonProperty("photo_200_orig")
	public String photo200Orig;
	public boolean hasMobile;
	public boolean online;
	public String homePhone;
	public String status;

}
