package com.riverisland.app.automation.enums;

public enum SwipeElementDirection {
	
	UP("up"),
	DOWN("down"),
	LEFT("left"),
	RIGHT("right");
	
	String direction;
	
	SwipeElementDirection(String direction) {
		this.direction = direction;
	}
}
