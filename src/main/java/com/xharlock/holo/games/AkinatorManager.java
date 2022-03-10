package com.xharlock.holo.games;

import java.util.HashMap;
import java.util.Map;

// TODO Implement a manager that keeps track of all the Akinator games across the different guilds and channels
public class AkinatorManager {

	private Map<Long, Akinator> instances;

	public AkinatorManager() {
		instances = new HashMap<>();
	}
	
	public Map<Long, Akinator> getInstances() {
		return instances;
	}
}