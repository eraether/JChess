package com.eraether.jchess;
import java.util.ArrayList;
import java.util.List;


public class MultiMap<K, V> {
	private ArrayList<K> keys;
	private ArrayList<V> values;

	public MultiMap() {
		keys = new ArrayList<K>();
		values = new ArrayList<V>();
	}

	public void put(K k, V v) {
		keys.add(k);
		values.add(v);
	}

	public int getSize() {
		return keys.size();
	}

	public List<V> get(K k) {
		List<V> list = new ArrayList<V>();
		for (int x = 0; x < keys.size(); x++) {
			if (keys.get(x).equals(k))
				list.add(values.get(x));
		}
		return list;
	}

	public boolean isEmpty() {
		return getSize() == 0;
	}

	public List<K> keySet() {
		return keys;
	}
}