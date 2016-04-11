package kyiv.rvysh.vkfriends.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
// TODO remove public
public class PersonInfo {

	private int uid;
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
	
	public PersonInfo() {
	}

	public PersonInfo(int uid) {
		this.uid = uid;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result + city;
		result = prime * result + country;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + (hasMobile ? 1231 : 1237);
		result = prime * result + ((homePhone == null) ? 0 : homePhone.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
		result = prime * result + (online ? 1231 : 1237);
		result = prime * result + ((photo100 == null) ? 0 : photo100.hashCode());
		result = prime * result + ((photo200Orig == null) ? 0 : photo200Orig.hashCode());
		result = prime * result + ((photo50 == null) ? 0 : photo50.hashCode());
		result = prime * result + sex;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + uid;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonInfo other = (PersonInfo) obj;
		if (birthDate == null) {
			if (other.birthDate != null)
				return false;
		} else if (!birthDate.equals(other.birthDate))
			return false;
		if (city != other.city)
			return false;
		if (country != other.country)
			return false;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (hasMobile != other.hasMobile)
			return false;
		if (homePhone == null) {
			if (other.homePhone != null)
				return false;
		} else if (!homePhone.equals(other.homePhone))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (nickname == null) {
			if (other.nickname != null)
				return false;
		} else if (!nickname.equals(other.nickname))
			return false;
		if (online != other.online)
			return false;
		if (photo100 == null) {
			if (other.photo100 != null)
				return false;
		} else if (!photo100.equals(other.photo100))
			return false;
		if (photo200Orig == null) {
			if (other.photo200Orig != null)
				return false;
		} else if (!photo200Orig.equals(other.photo200Orig))
			return false;
		if (photo50 == null) {
			if (other.photo50 != null)
				return false;
		} else if (!photo50.equals(other.photo50))
			return false;
		if (sex != other.sex)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (uid != other.uid)
			return false;
		return true;
	}
}
