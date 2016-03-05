package kyiv.rvysh.vkfriends.utils;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;

public class CustomCharacterEscapes extends CharacterEscapes {
	private static final long serialVersionUID = 1807880804044451077L;
	private final int[] asciiEscapes;

	public CustomCharacterEscapes() {
		int[] esc = CharacterEscapes.standardAsciiEscapesForJSON();
		esc['\''] = CharacterEscapes.ESCAPE_STANDARD;
		esc['\"'] = CharacterEscapes.ESCAPE_STANDARD;
		asciiEscapes = esc;
	}

	@Override
	public int[] getEscapeCodesForAscii() {
		return asciiEscapes;
	}

	@Override
	public SerializableString getEscapeSequence(int ch) {
		return null;
	}
}