package edu.mbhs.madubozhi.touhou.game.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * HashMap with Keys mapped to Arrays of Values.
 * @author bowenzhi
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class HashArray<K, V> {
	private HashMap<K, ArrayList<V>> hashArray;
	private ArrayList<K> keys;
	private int initSize;
	/**
	 * Initializes a HashArray of initial array size 10 per key
	 */
	public HashArray(){
		this(10);
	}
	/**
	 * Initializes a HashArray of initial array size initSize per key
	 * @param initSize
	 */
	public HashArray(int initSize){
		hashArray = new HashMap<K, ArrayList<V>>();
		keys = new ArrayList<K>();
		this.initSize = initSize;
	}
	/**
	 * Adds a value to the HashArray, mapped from key
	 * @param key Key mapping to the Value
	 * @param value Value to add
	 */
	public void put(K key, V value){
		if (hashArray.get(key) == null){
			hashArray.put(key, new ArrayList<V>(initSize));
			keys.add(key);
		}
		hashArray.get(key).add(value);
	}
	/**
	 * Removes the key
	 * @param key Key to remove
	 * @return All values removed that were mapped from the key
	 */
	public ArrayList<V> remove(K key){
		keys.remove(key);
		return hashArray.remove(key);
	}
	/**
	 * Removes the first instance of the given value
	 * @param value Value to remove
	 * @return The key that mapped to the removed value
	 */
	public K removeValue(V value){
		K toReturn = null;
		for (K key:keys){
			if (hashArray.get(key) == null){
				continue;
			}
			if (hashArray.get(key).remove(value)){
				toReturn = key;
				break;
			}
		}
		return toReturn;
	}
	/**
	 * Appends another HashArray to this one, all clashing keys are merged
	 * @param other HashArray to combine
	 */
	public void combine(HashArray<K, V> other){
		for(K key:other.getKeys()){
			for(V value:other.get(key)){
				this.put(key, value);
			}
		}
	}
	/**
	 * @param key
	 * @return Value mapped from key
	 */
	public ArrayList<V> get(K key){
		return hashArray.get(key);
	}
	/**
	 * @return All keys with mappings in this HashArray
	 */
	public ArrayList<K> getKeys(){
		return keys;
	}
	/**
	 * @return All values mapped in this HashArray
	 */
	public ArrayList<V> getAll(){
		ArrayList<V> toReturn = new ArrayList<V>();
		for (K key:keys){
			for (V value:hashArray.get(key)){
				toReturn.add(value);
			}
		}
		return toReturn;
	}
	/**
	 * @return number of values existent in all mappings
	 */
	public int size() {
		int size = 0;
		for (K key:keys){
			size += hashArray.get(key).size();
		}
		return size;
	}
}
