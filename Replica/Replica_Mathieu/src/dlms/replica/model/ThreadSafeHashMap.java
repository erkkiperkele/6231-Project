package dlms.replica.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * HashMap wrapper class for threaded applications
 * 
 * @author mat
 * 
 */
public class ThreadSafeHashMap<K, V> {

	private Map<K, V> map = new HashMap<>();
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final Lock readLock = readWriteLock.readLock();
	private final Lock writeLock = readWriteLock.writeLock();

	/**
	 * Constructor
	 */
	public ThreadSafeHashMap() {

	}

	/**
	 * Check if the provided key exists in the HashMap
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(K key) {
		readLock.lock();
		try {
			return map.containsKey(key);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Get a value from the HashMap
	 * 
	 * @param key
	 * @return
	 */
	public V get(K key) {
		readLock.lock();
		try {
			return map.get(key);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 
	 * @return
	 */
	public Set<K> keySet() {
		readLock.lock();
		try {
			return map.keySet();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Add a key/value to the HashMap
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public V put(K key, V value) {
		writeLock.lock();
		try {
			return map.put(key, value);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Remove a key/value from the HashMap
	 * 
	 * @param key
	 * @return
	 */
	public V remove(K key) {
		writeLock.lock();
		try {
			return map.remove(key);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Gets the number of elements in the HashMap
	 * 
	 * @return the number of elements in the ThreadSafeHashMap
	 */
	public int size() {
		readLock.lock();
		try {
			return map.size();
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * Gets the set of values
	 * 
	 * @return the set of values in the HashMap
	 */
	public Collection<V> values() {
		readLock.lock();
		try {
			return map.values();
		} finally {
			readLock.unlock();
		}
	}
}
