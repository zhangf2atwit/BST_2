package edu.wit.cs.comp2350;

/**
 * An abstract class to insert and retrieve some info about disk locations
 */
public abstract class LocationContainer {

	public static DiskLocation nil = new DiskLocation(-1, -1);
	protected DiskLocation root;

	public abstract DiskLocation find(DiskLocation d);	// returns the object or nil
	public abstract DiskLocation next(DiskLocation d);	// returns the next object or nil
	public abstract DiskLocation prev(DiskLocation d);	// returns the previous object or nil
	public abstract void insert(DiskLocation d);		// inserts the object
	public abstract int height();						// returns the largest tree height


	public int size() {
		return _size(root);
	}

	private int _size(DiskLocation D) {
		if (D == null || D == nil)
			return 0;

		return 1 + _size(D.left) + _size(D.right);
	}
}
