package edu.wit.cs.comp2350;

// TODO Document class
/*
 * A red–black tree is a kind of self-balancing binary search tree. 
 * Each node stores an extra bit representing "color" ("red" or "black"), 
 * used to ensure that the tree remains balanced during insertions and deletions.
 * 
 * This Red-Black Tree (RBST) is extends an abstract LocationContainer.
 * Class DiskLocation, the node has parent, left, and right references for binary trees. 
 * Every time it pass the DiskLocation object, and all the functions below are help it to build the Binary Search Tree.
 * This is red black tree also make the program run time more efficient.
 */
public class RBTree extends LocationContainer {
	/**
	 * sets a disk location's color to red.
	 * A setRed method that demonstrates how to assign a color to a DiskLocation.
	 * whenever you are setting a node to red to guarantee never change nil to the wrong color
	 */
	private void setRed(DiskLocation z) {
		if (z != nil)
			z.color = RB.RED;
	}

	@Override
	public DiskLocation find(DiskLocation d) {
		// TODO implement method
		if (root == nil) {
			return nil;
		}
		DiskLocation curr = root;                                 // For find the target value, it search from the root.
		while (curr != null && curr != nil) {
			if (d.equals(curr)) {
				return curr;
			}

			if (d.isGreaterThan(curr)) {
				curr = curr.right;
			} else {
				curr = curr.left;
			}
		}
		return nil;                                              // If didn't find the same value, return the nil.
	}

	@Override
	public DiskLocation next(DiskLocation d) {
		// TODO implement method
		DiskLocation curr = find(d);                             // find location of itself.
		if (curr.right != nil) {
			DiskLocation min = findMin_right(curr.right);
			return min;
		} else {
			return up(curr);
		}
	}

	// Function to find the minimum on the right sutree
	// this value will between the next element of the object b.
	private DiskLocation findMin_right(DiskLocation d) {
		while (d.left != nil) {
			d = d.left;                                           // track down to the most left element on right side
		}
		return d;
	}

	// call up function to help the value get the right place
	private DiskLocation up(DiskLocation x) {
		DiskLocation p = x.parent;
		if (p == nil)
			return p;
		else if (x == p.left) {
			return p;
		} else
			return up(p);
	}

	@Override
	public DiskLocation prev(DiskLocation d) {
		// TODO implement method
		DiskLocation curr = find(d);
		if (curr.left != nil) {
			DiskLocation max = findMax_left(curr.left);
			return max;
		} else {
			return prevUp(d);
		}
	}

	private DiskLocation findMax_left(DiskLocation d) {
		while (d.right != nil) {
			d = d.right;                                           // track down to the most right element on left side
		}
		return d;
	}

	// This is a surport functionfor prev function,if right child not excits
	// preUp will help current to the it parent's postion in the right subtree.
	private DiskLocation prevUp(DiskLocation d) {
		DiskLocation curr = d.parent;
		if (curr == nil || d == curr.right) {
			return curr;
		} else
			return prevUp(curr);
	}

	// Insert a node into an n-node red-black tree in O(lgn) time p315 presuducode
	@Override
	public void insert(DiskLocation d) {
		// TODO implement method
		d.left = nil;
		d.right = nil;
		d.parent = nil;

		if (root == null) {
			root = nil;
		}
		d.parent = findParent(d, root, nil);
		if (d.parent == nil) {
			root = d;
		} else {
			if (d.parent.isGreaterThan(d)) {                        // Equals (d < d.parent)
				d.parent.left = d;
			} else {
				d.parent.right = d;
			}
		}
		d.left = nil;
		d.right = nil;

		setRed(d);
		fixInsert(d);
	}

	// find parent is a recursion function to help the value find their parent
	private DiskLocation findParent(DiskLocation n, DiskLocation curr, DiskLocation parent) {
		if (curr == nil) {
			return parent;
		} else {
			if (n.isGreaterThan(curr)) {
				return findParent(n, curr.right, curr);
			} else {
				return findParent(n, curr.left, curr);
			}
		}
	}

	// Use this method on fix-insert instead of directly coloring nodes red to avoid setting nil as red.
	private void fixInsert(DiskLocation d) {
		while (d.parent.color == RB.RED) {
			if (d.parent == d.parent.parent.left) {
				DiskLocation y = d.parent.parent.right;

				if (y.color == RB.RED) {
					d.parent.color = RB.BLACK;
					y.color = RB.BLACK;

					setRed(d.parent.parent);                            // d.parent.parent.color = RB.RED;
					d = d.parent.parent;
				} else {
					if (d == d.parent.right) {
						d = d.parent;
						rotateLeft(d);                                  // rotate-left(n)
					}
					d.parent.color = RB.BLACK;	
					setRed(d.parent.parent);                            // d.parent.parent.color = RB.RED;
					rotateRight(d.parent.parent);                       // rotate-right(d’s grandparent)
				}
			}
			else {
				// else 3 symmetric cases (switch left↔right)
				DiskLocation y = d.parent.parent.left;

				if (y.color == RB.RED) {
					d.parent.color = RB.BLACK;
					y.color = RB.BLACK;

					setRed(d.parent.parent);                            // d.parent.parent.color = RB.RED;
					d = d.parent.parent;
				}
				else {
					if (d == d.parent.left) {
						d = d.parent;
						rotateRight(d);                                 // rotate-left(z)
					}
					d.parent.color = RB.BLACK;	                               
					setRed(d.parent.parent);                            // d.parent.parent.color = RB.RED;
					rotateLeft(d.parent.parent);                        // rotate-right(z’s grandparent)
				}
			}
		}
		root.color = RB.BLACK;
	}

	private void rotateLeft(DiskLocation d) {
		DiskLocation y = d.right;
		d.right = y.left;

		if (y.left != nil) {
			y.left.parent = d;
		}
		y.parent = d.parent;
		if (d.parent == nil) {                                         // d is root
			root = y;
		} else if (d == d.parent.left) {                               // d is left child ?
			d.parent.left = y;
		} else {                                                       // d is right child ?
			d.parent.right = y;
		}

		y.left = d;
		d.parent = y;
	}

	private void rotateRight(DiskLocation d) {
		DiskLocation y = d.left;
		d.left = y.right;

		if (y.right != nil) {
			y.right.parent = d;
		}
		y.parent = d.parent;
		if (d.parent == nil) {                                        // d is root
			root = y;
		} else if (d == d.parent.right) {                             // d is right child ?
			d.parent.right = y;
		} else {                                                      // d is left child  ?
			d.parent.left = y;
		}

		y.right = d;
		d.parent = y;
	}

	@Override
	public int height() {
		// TODO implement method
		if (root == nil) {
			return 0;
		}
		return findHeight(root) - 1;
	}
	// find the number of Max between the left size and right size
	private int findHeight(DiskLocation d) {
		if (d == nil) {
			return 0;
		}
		// math function to find the max height of the left tree and right tree
		int max_size = Math.max(findHeight(d.left), findHeight(d.right)) + 1;
		return max_size;
	}
}
