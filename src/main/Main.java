package main;

import java.util.Comparator;

import impl.Action;
import impl.AvlTree;
import impl.Node;

public class Main {

	public static void main(String[] args) {
		AvlTree<Integer> tree = new AvlTree<>(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return Integer.compare(o1, o2);
			}
		});
		tree.traverse(new Action<Integer>() {
			@Override
			public void execute(Node<Integer> node) {
				System.out.println(String.format("node: %s, factor: %s", node.value, node.height));
			}
		});
	}

}
