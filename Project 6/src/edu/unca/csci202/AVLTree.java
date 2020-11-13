package edu.unca.csci202;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class AVLTree<T extends Comparable<T>> implements BinarySearchTreeADT<T> {

	// Node Class
	private class AVLNode<T>{
		private T data;
		private AVLNode<T> left;
		private AVLNode<T> right;
		private AVLNode<T> parent;
		private int height;
		
		public void updateHeight() {
			int leftHeight = 0;
			int rightHeight = 0;
			
			if(this.left != null) leftHeight = this.left.height;
			if(this.right != null) rightHeight = this.right.height;
			this.height = 1 + Math.max(leftHeight, rightHeight);
		}
		
		public int balanceFactor() {
			int leftHeight = 0;
			int rightHeight = 0;
			
			if(this.left != null) leftHeight = this.left.height;
			if(this.right != null) rightHeight = this.right.height;
		
			return (rightHeight - leftHeight);
		}
		
	}
	
	// AVL Tree Instance Variable
	private int size;
	private AVLNode<T> root;
	
	// Constructor
	public AVLTree() {
		super();
		this.size = 0;
		this.root = null;
	}
	
	public T getRootElement() {
		if(this.root == null) return null;
		return this.root.data;
	}

	public boolean isEmpty() {
		return this.root == null;
	}

	public Iterator<T> iterator() {
		return this.iteratorInOrder();
	}

	public Iterator<T> iteratorInOrder() {
		LinkedList<T> list = new LinkedList<T>();
		traverseInOrder(this.root, list);
		return list.iterator();
	}

	private void traverseInOrder(AVLNode<T> node, LinkedList<T> list) {
		if(node == null) return;
			// Recurse left
			traverseInOrder(node.left,	list);
			// Visit node
			list.add(node.data);
			// Recurse right
			traverseInOrder(node.right, list);
	}
	
	public Iterator<T> iteratorPreOrder() {
		LinkedList<T> list = new LinkedList<T>();
		traversePreOrder(this.root, list);
		return list.iterator();
	}
	
	private void traversePreOrder(AVLNode<T> node, LinkedList<T> list) {
		if(node == null) return;
		// Visit node
		list.add(node.data);
		// Recurse left
		traversePreOrder(node.left, list);
		// Recurse right
		traversePreOrder(node.right, list);
	}

	public Iterator<T> iteratorPostOrder() {
		LinkedList<T> list = new LinkedList<T>();
		traversePostOrder(this.root, list);
		return list.iterator();
	}

	private void traversePostOrder(AVLNode<T> node, LinkedList<T> list) {
		if(node == null) return;
		// Recurse left
		traversePostOrder(node.left, list);
		// Recurse right
		traversePostOrder(node.right, list);
		// Visit node
		list.add(node.data);
	}
	
	public Iterator<T> iteratorLevelOrder() {
		LinkedList<T> list = new LinkedList<T>();
		traverseLevelOrder(this.root, list);
		return list.iterator();
	}

	private void traverseLevelOrder(AVLNode<T> node, LinkedList<T> list) {
		Queue<AVLNode<T>> q = new LinkedList<>();
		
		q.add(this.root);
		
		while(!q.isEmpty()) {
			
			list.add(q.peek().data);
			
			if(q.peek().left != null) {
				q.add(q.peek().left);
			}
			if(q.peek().right != null) {
				q.add(q.peek().right);
			}
			
			q.remove();
		}
	}
	
	public int insert(T element) {
		this.size++;
		AVLNode<T> z = new AVLNode<T>();
		z.data = element;
		z.parent = null;
		z.left = null;
		z.right = null;
		z.height = 0;
		
		AVLNode<T> x = this.root;
		AVLNode<T> y = null;
		
		int traversals = 0;
		
		/**
		 * Find where to put the new node
		 */
		while(x != null) {
			traversals++;
			y = x;				// Assign the trailing pointer
			if (z.data.compareTo(x.data) < 0) {
				x = x.left;		// Traverse left
			} else {
				x = x.right;	// Traverse right
			}
		}
		
		// Found where to insert
		z.parent = y;	// Assign trailing pointer as parent
		
		if(y == null) {
			this.root = z;
		} else if(z.data.compareTo(y.data) < 0) {
			y.left = z;
		} else {
			y.right = z;
		}
		
		// Now that we have inserted, re-balance the tree
		this.insertFix(z);
		
		return traversals;
	}

	/**
	 * Fix the tree balance after an insert
	 * @param x Node that was just inserted into the tree
	 */
	private void insertFix(AVLNode<T> x) {
		x.updateHeight();
		while(x != null) {
			x.updateHeight();
			int currBal = x.balanceFactor();
			if(currBal == -2) {
				int leftBal = x.left.balanceFactor();
				if(leftBal == -1 || leftBal == 0) { // Right Rotations
					this.rightRotate(x);
					x.updateHeight();
					x.parent.updateHeight();
					return; // Done Re-balancing
				} else if (leftBal == 1) {	// Left-Right Double Rotation
					this.leftRotate(x.left);
					this.rightRotate(x);
					x.updateHeight();
					x.parent.left.updateHeight();
					x.parent.updateHeight();
					return; // Done
				}
			} else if (currBal == 2) {
				int rightBal = x.right.balanceFactor();
				if(rightBal == 1 || rightBal == 0) { // Left Rotation
					this.leftRotate(x);
					x.updateHeight();
					x.parent.updateHeight();
					return; // Done
				} else if (rightBal == -1) { // Right-Left Double Rotation
					this.rightRotate(x.right);
					this.leftRotate(x);
					x.updateHeight();
					x.parent.right.updateHeight();
					x.parent.updateHeight();
					return; // Done
				}
			}
			// Loop update
			x = x.parent;
		}
	}
	
	private void leftRotate(AVLNode<T> x) {
		AVLNode<T> y = x.right;
		
		// Step 1
		x.right = y.left;
		if(y.left != null) y.left.parent = x;
		
		// Step 2
		y.parent = x.parent;
		if(x.parent == null) {
			this.root = y;
		} else if (x == x.parent.left) {
			x.parent.left = y;
		} else {
			x.parent.right = y;
		}
		
		// Step 3
		y.left = x;
		x.parent = y;
	}
	
	private void rightRotate(AVLNode<T> x) {
		AVLNode<T> y = x.left;
		
		// Step 1
		x.left = y.right;
		if(y.right != null) y.right.parent = x;
		
		// Step 2
		y.parent = x.parent;
		if(x.parent == null) {
			this.root = y;
		} else if (x == x.parent.right) {
			x.parent.right  = y;
		} else {
			x.parent.left = y;
		}
		
		// Step 3
		y.right = x;
		x.parent = y;
	}
	
	public int height() {
		if(this.root == null) return 0;
		return this.root.height;
	}

	/**
	 * Return the largest element in the tree
	 * @return the largest element in the tree
	 */
	public T maximum() {
		if(this.root == null) return null;
		AVLNode<T> node = maximum(this.root);
		if(node != null) return node.data;
		return null;
	}
	private AVLNode<T> maximum(AVLNode<T> node){
		while(node.right != null) {
			node = node.right;
		}
		return node;
	}

	/**
	 * Return the smallest element in the tree
	 * @return the smallest element in the tree
	 */
	public T minimum() {
		if(this.root == null) return null;
		AVLNode<T> node = minimum(this.root);
		if(node != null) return node.data;
		return null;
	}
	private AVLNode<T> minimum(AVLNode<T> node) {
		while(node.left != null) {
			node = node.left;
		}
		return node;
	}

	public void delete(T element) {
		AVLNode<T> z = find(this.root, element);
		if(z == null) System.out.println("ERROR: Node not found in the tree.");
		else {
			this.size--;
			delete(z);
		}
	}
	
	public void delete(AVLNode<T> z) {
		AVLNode<T> fixNode = null;
		
		if(z.left == null) {
			transplant(z, z.right);
			fixNode = z.parent;
		}
		else if (z.right == null) {
			transplant(z, z.left);
			fixNode = z.parent;
		}
		else {
			AVLNode<T> y = minimum(z.right);
			if(y.parent != z) {
				transplant(y, y.right);
				y.right = z.right;
				y.right.parent = y;
				fixNode = y.parent;
			} else fixNode = y.right;
			transplant(z,y);
			y.left = z.left;
			y.left.parent = y;
		}
		if(fixNode != null) {
			deleteFix(fixNode);
		}
	}
	
	private void deleteFix(AVLNode<T> x) {
		x.updateHeight();
		while(x != null) {
			x.updateHeight();
			int currBal = x.balanceFactor();
			if(currBal == -2) {
				int leftBal = x.left.balanceFactor();
				if(leftBal == -1 || leftBal == 0) { // Right Rotations
					this.rightRotate(x);
					x.updateHeight();
					x.parent.updateHeight();
				} else if (leftBal == 1) {	// Left-Right Double Rotation
					this.leftRotate(x.left);
					this.rightRotate(x);
					x.updateHeight();
					x.parent.left.updateHeight();
					x.parent.updateHeight();
				}
			} else if (currBal == 2) {
				int rightBal = x.right.balanceFactor();
				if(rightBal == 1 || rightBal == 0) { // Left Rotation
					this.leftRotate(x);
					x.updateHeight();
					x.parent.updateHeight();
				} else if (rightBal == -1) { // Right-Left Double Rotation
					this.rightRotate(x.right);
					this.leftRotate(x);
					x.updateHeight();
					x.parent.right.updateHeight();
					x.parent.updateHeight();
				}
			}
			// Loop update
			x = x.parent;
		}
	}
	
	private void transplant(AVLNode<T> u, AVLNode<T> v) {
		if (u.parent == null) this.root = v;
		else if (u == u.parent.left) u.parent.left = v;
		else u.parent.right = v;
		if (v != null) v.parent = u.parent;
	}

	public T find(T element) {
		AVLNode<T> x = find(this.root, element);
		if (x != null) return x.data;
		else return null;
	}
	
	public AVLNode<T> find(AVLNode<T> x, T element){
		if (x == null) return null;
		if (element.compareTo(x.data) == 0) return x;
		if (element.compareTo(x.data) < 0) return find(x.left, element);
		else return find(x.right, element);
	}

	public boolean contains(T element) {
		if(find(element) != null) return true;
		return false;
	}

	public int size() {
		return this.size;
	}

	public String toString() {
		return print(this.root, 0);
	}
	private String print(AVLNode<T> node, int level) {
		String ret = "";
		if(node != null) {
			for(int i = 0; i < level; i++) {
				ret += "\t";
			}
			ret += node.data;
			ret += "\n";
			// Recurse right
			ret += print(node.right, level+1);
			// Recurse left
			ret += print(node.left, level+1);
		}
		return ret;
	}
	
}
