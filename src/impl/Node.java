package impl;

public class Node<T> {

	public Node<T> left;
	public Node<T> right;
	public Node<T> parent;
	public T value;
	public int balanceFactor;
	
	public Node(T value) {
		this.value = value;
	}
}
