package mua;

import java.util.Map;
import java.util.HashMap;

public class Variable {
	private HashMap<String, Value> map;

	public Variable() {
		map = new HashMap<String, Value>();
	}

	public Variable(HashMap<String, Value> t) {
		this.map = t;
	}
	
	public boolean isContain(String key) {
		if (this.map.containsKey(key)) {
			return true;
		}
		return false;
	}
	
	public Value getValue(String key) {
		return this.map.get(key);
	}
	
	public void addItem(String key, Value v) {
		this.map.put(key, v);
	}
	
	public void removeItem(String key) {
		this.map.remove(key);
	}

	public void replaceItem(String key, Value oldv, Value newv) {
		this.map.replace(key, newv);;
	}
	
	public HashMap<String, Value> GetMap() {
		return this.map;
	}
	
}

