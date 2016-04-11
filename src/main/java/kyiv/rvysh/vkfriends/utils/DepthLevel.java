package kyiv.rvysh.vkfriends.utils;

public enum DepthLevel {
	ONE(1), TWO(2);
	
	private final int value;
    
	private DepthLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
