package impl;

import static java.lang.Math.max;

import java.util.Comparator;
import java.util.List;

public class AvlTree<T> {

	private Node<T> root;
	private Comparator<T> cmp;
	private int size;

	public AvlTree(Comparator<T> cmp) {
		this.cmp = cmp;
	}

	public AvlTree(Comparator<T> cmp, Node<T> root) {
		this(cmp);
		this.root = root;
		this.size = 1;
	}

	public Node<T> find(T value) {
		return find(value, root);
	}

	private Node<T> find(T value, Node<T> root) {
		if (null == root || cmp.compare(root.value, value) == 0) {
			return root;
		}
		if (cmp.compare(root.value, value) < 0) {
			return find(value, root.right);
		}
		return find(value, root.left);
	}

	public void traverse(Action<T> action) {
		traverse(action, root);
	}

	private void traverse(Action<T> action, Node<T> root) {
		if (null == root) {
			return;
		}
		if (null != root.left) {
			traverse(action, root.left);
		}
		action.execute(root);
		if (null != root.right) {
			traverse(action, root.right);
		}
	}

	public void insert(T value) {
		insert(value, root);
		size++;
	}

	public void insert(List<T> values) {
		for (T value : values) {
			insert(value);
		}
	}

	private void insert(T value, Node<T> root) {
		if (null == root && null == this.root) {
			this.root = new Node<T>(value);
			return;
		}
		if (null == root) {
			return;
		}
		if (cmp.compare(root.value, value) >= 0) {
			if (null == root.left) {
				root.left = new Node<T>(value);
				root.left.parent = root;
				root.height = max(getHeight(root.left), getHeight(root.right)) + 1;
				assertBalanceFactor(root);
				return;
			}
			insert(value, root.left);
		} else {
			if (null == root.right) {
				root.right = new Node<T>(value);
				root.right.parent = root;
				root.height = max(getHeight(root.left), getHeight(root.right)) + 1;
				assertBalanceFactor(root);
				return;
			}
			insert(value, root.right);
		}
		root.height = max(getHeight(root.left), getHeight(root.right)) + 1;
		assertBalanceFactor(root);
		balanceIfNecessary(root);
	}
	
	private int getHeight(Node<T> node) {
		if (null == node) {
			return 0;
		}
		return node.height;
	}

	public void delete(T value) {
		if (delete(value, this.root)) {
			size--;
		}
	}

	private boolean delete(T value, Node<T> root) {
		if (null == root) {
			return false;
		}
		if (cmp.compare(root.value, value) == 0) {
			Node<T> parent = root.parent;
			Node<T> leftChild = root.left;
			Node<T> rightChild = root.right;
			if (null == rightChild && null == leftChild) {
				root.parent = null;
				if (isLeftSubtree(root, parent)) {
					parent.left = null;
				} else {
					parent.right = null;
				}
				parent.height = max(getHeight(parent.left), getHeight(parent.right)) + 1;
				return true;
			}
			T tmp = findMin(getChild(root)).value;
			delete(tmp, root);
			root.height = max(getHeight(root.left), getHeight(root.right)) + 1;
			root.value = tmp;
			return true;
		}
		if (cmp.compare(root.value, value) > 0) {
			if (!delete(value, root.left)) {
				return false;
			}
		} else {
			if (!delete(value, root.right)) {
				return false;
			}
		}
		root.height = max(getHeight(root.left), getHeight(root.right)) + 1;
		assertBalanceFactor(root);
		balanceIfNecessary(root);
		return true;
	}

	private Node<T> getChild(Node<T> node) {
		if (null != node.left && null != node.right) {
			return node.right;
		}
		return node.left == null ? node.right : node.left;
	}

	private Node<T> findMin(Node<T> root) {
		if (null == root.left) {
			return root;
		}
		return findMin(root.left);
	}

	private boolean balanceIfNecessary(Node<T> node) {
		if (node.balanceFactor == -2 && null != node.right) {
			if (node.right.balanceFactor == 1) {
				performRightLeftRotation(node);
				return true;
			}
			if (node.right.balanceFactor == -1) {
				performDoubleRightRotation(node);
				return true;
			}
		}
		if (node.balanceFactor == 2 && null != node.left) {
			if (node.left.balanceFactor == 1) {
				performDoubleLeftRotation(node);
				return true;
			}
			if (node.left.balanceFactor == -1) {
				performLeftRightRotation(node);
				return true;
			}
		}
		return false;
	}

	private void performDoubleLeftRotation(Node<T> node) {
		Node<T> child = node.left;
		Node<T> grandchildren = child.right;
		Node<T> parent = node.parent;

		if (null != parent) {
			if (isLeftSubtree(node, parent)) {
				parent.left = child;
			} else {
				parent.right = child;
			}
			child.parent = parent;
		} else {
			child.parent = null;
			this.root = child;
		}
		child.right = node;
		node.parent = child;
		node.left = grandchildren;
		if (null != grandchildren) {
			grandchildren.parent = node;
		}
		node.height = max(getHeight(node.left), getHeight(node.right)) + 1;
		child.height = max(getHeight(child.left), getHeight(child.right)) + 1;
	}

	private void performLeftRightRotation(Node<T> node) {
		performDoubleRightRotation(node.left);
		performDoubleLeftRotation(node);
	}

	private void performDoubleRightRotation(Node<T> node) {
		Node<T> child = node.right;
		Node<T> grandchildren = child.left;
		Node<T> parent = node.parent;

		if (null != parent) {
			if (isLeftSubtree(node, parent)) {
				parent.left = child;
			} else {
				parent.right = child;
			}
			child.parent = parent;
		} else {
			child.parent = null;
			this.root = child;
		}
		child.left = node;
		node.parent = child;
		node.right = grandchildren;
		if (null != grandchildren) {
			grandchildren.parent = node;
		}
		node.height = max(getHeight(node.left), getHeight(node.right)) + 1;
		child.height = max(getHeight(child.left), getHeight(child.right)) + 1;
	}

	private void performRightLeftRotation(Node<T> node) {
		performDoubleLeftRotation(node.right);
		performDoubleRightRotation(node);
	}

	private boolean isLeftSubtree(Node<T> node, Node<T> parent) {
		if (null != parent.left) {
			return parent.left.equals(node);
		}
		return false;
	}

	private void assertBalanceFactor(Node<T> node) {
		if (null == node) {
			return;
		}
		node.balanceFactor = getHeight(node.left) - getHeight(node.right);
	}

	public int getSize() {
		return size;
	}
}
