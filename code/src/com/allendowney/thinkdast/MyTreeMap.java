/**
 *
 */
package com.allendowney.thinkdast;

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a Map using a binary search tree.
 *
 * @param <K>
 * @param <V>
 *
 */
public class MyTreeMap<K, V> implements Map<K, V> {

	private int size = 0;
	private Node root = null;

	/**
	 * Represents a node in the tree.
	 *
	 */
	protected class Node {
		public K key;
		public V value;
		public Node left = null;
		public Node right = null;

		/**
		 * @param key
		 * @param value
		 * @param left
		 * @param right
		 */
		public Node(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}

	@Override
	public void clear() {
		size = 0;
		root = null;
	}

	@Override
	public boolean containsKey(Object target) {
		return findNode(target) != null;
	}

	/**
	 * Returns the entry that contains the target key, or null if there is none.
	 *
	 * @param target
	 */
	private Node findNode(Object target) {
		// some implementations can handle null as a key, but not this one
		if (target == null) {
			throw new IllegalArgumentException();
		}

		// something to make the compiler happy
		@SuppressWarnings("unchecked")
		Comparable<? super K> k = (Comparable<? super K>) target;

		Node node = root;
		while (node != null) {
			int c = k.compareTo(node.key);

			if (c < 0)
				node = node.left;
			else if (c > 0)
				node = node.right;
			else
				return node;
		}
		return null;
	}

	/**
	 * Compares two keys or two values, handling null correctly.
	 *
	 * @param target
	 * @param obj
	 * @return
	 */
	private boolean equals(Object target, Object obj) {
		if (target == null) {
			return obj == null;
		}
		return target.equals(obj);
	}

	@Override
	public boolean containsValue(Object target) {
		return containsValueHelper(root, target);
	}

	private boolean containsValueHelper(Node node, Object target) {
		if (node == null) {
			return false;
		}
		if (equals(node.value, target)) {
			return true;
		} else {
			if (containsValueHelper(node.left, target)) {
				return true;
			} else {
				return containsValueHelper(node.right, target);
			}
		}
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public V get(Object key) {
		Node node = findNode(key);
		if (node == null) {
			return null;
		}
		return node.value;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Set<K> keySet() {
		Set<K> set = new LinkedHashSet<K>();
		keyHelper(root, set);
		return set;
	}

	public void keyHelper(Node node, Set<K> keySet) {
		if (node == null) {
			return;
		}
		keyHelper(node.left, keySet);
		keySet.add(node.key);
		keyHelper(node.right, keySet);
	}

	@Override
	public V put(K key, V value) {
		if (key == null) {
			throw new NullPointerException();
		}
		if (root == null) {
			root = new Node(key, value);
			size++;
			return null;
		}
		return putHelper(root, key, value);
	}

	private V putHelper(Node node, K key, V value) {
		// something to make the compiler happy
		@SuppressWarnings("unchecked")
		Comparable<? super K> k = (Comparable<? super K>) key;

		int compareKey = k.compareTo(node.key);
		if (compareKey == 0) {
			V oldValue = node.value;
			node.value = value;
			return oldValue;
		} else if (compareKey > 0) {
			if (node.right == null) {
				node.right = new Node(key, value);
				size++;
			} else {
				putHelper(node.right, key, value);
			}
		} else {
			if (node.left == null) {
				node.left = new Node(key, value);
				size++;
			} else {
				putHelper(node.left, key, value);
			}
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		for (Map.Entry<? extends K, ? extends V> entry: map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public V remove(Object key) {
		root = deleteNode(root, key);
		return null;
	}

	private Node deleteNode(Node root, Object key) {
		// Base case
		if (root == null)
			return root;

		@SuppressWarnings("unchecked")
		Comparable<? super K> k = (Comparable<? super K>) key;
		int compare = k.compareTo(root.key);

		// Recursive calls for ancestors of
		// node to be deleted
		if (compare < 0) {
			root.left = deleteNode(root.left, key);
			return root;
		}
		else if (compare > 0) {
			root.right = deleteNode(root.right, key);
			return root;
		}

		// We reach here when root is the node
		// to be deleted.

		// If one of the children is empty
		if (root.left == null) {
			size--;
			return root.right;
		}
		else if (root.right == null) {
			size--;
			return root.left;
		}

		// If both children exist
		else {
			Node succParent = root;

			// Find successor
			Node succ = root.right;

			while (succ.left != null) {
				succParent = succ;
				succ = succ.left;
			}

			// Delete successor. Since successor
			// is always left child of its parent
			// we can safely make successor's right
			// right child as left of its parent.
			// If there is no succ, then assign
			// succ->right to succParent->right
			if (succParent != root)
				succParent.left = succ.right;
			else
				succParent.right = succ.right;

			// Copy Successor Data to root
			root.key = succ.key;
			root.value = succ.value;
			size--;

			return root;
		}
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Collection<V> values() {
		Set<V> set = new HashSet<V>();
		Deque<Node> stack = new LinkedList<Node>();
		stack.push(root);
		while (!stack.isEmpty()) {
			Node node = stack.pop();
			if (node == null) continue;
			set.add(node.value);
			stack.push(node.left);
			stack.push(node.right);
		}
		return set;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, Integer> map = new MyTreeMap<String, Integer>();
		map.put("Word1", 1);
		map.put("Word2", 2);
		Integer value = map.get("Word1");
		System.out.println(value);

		for (String key: map.keySet()) {
			System.out.println(key + ", " + map.get(key));
		}
	}

	/**
	 * Makes a node.
	 *
	 * This is only here for testing purposes.  Should not be used otherwise.
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public MyTreeMap<K, V>.Node makeNode(K key, V value) {
		return new Node(key, value);
	}

	/**
	 * Sets the instance variables.
	 *
	 * This is only here for testing purposes.  Should not be used otherwise.
	 *
	 * @param node
	 * @param size
	 */
	public void setTree(Node node, int size ) {
		this.root = node;
		this.size = size;
	}

	/**
	 * Returns the height of the tree.
	 *
	 * This is only here for testing purposes.  Should not be used otherwise.
	 *
	 * @return
	 */
	public int height() {
		return heightHelper(root);
	}

	private int heightHelper(Node node) {
		if (node == null) {
			return 0;
		}
		int left = heightHelper(node.left);
		int right = heightHelper(node.right);
		return Math.max(left, right) + 1;
	}
}
