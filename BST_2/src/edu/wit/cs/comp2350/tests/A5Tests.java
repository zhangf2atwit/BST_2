package edu.wit.cs.comp2350.tests;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import edu.wit.cs.comp2350.BST;
import edu.wit.cs.comp2350.DiskLocation;
import edu.wit.cs.comp2350.LocationContainer;
import edu.wit.cs.comp2350.RBTree;

@FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
public class A5Tests{

	@Rule
	public Timeout globalTimeout = Timeout.seconds(15);

	@SuppressWarnings("serial")
	private static class ExitException extends SecurityException {}

	private static class NoExitSecurityManager extends SecurityManager 
	{
		@Override
		public void checkPermission(Permission perm) {}

		@Override
		public void checkPermission(Permission perm, Object context) {}

		@Override
		public void checkExit(int status) { super.checkExit(status); throw new ExitException(); }
	}

	@Before
	public void setUp() throws Exception 
	{
		System.setSecurityManager(new NoExitSecurityManager());
	}

	@After
	public void tearDown() throws Exception 
	{
		System.setSecurityManager(null);
	}

	private void _testInsert(LocationContainer T, DiskLocation[] vals) {
		try {
			for (int i = 0; i < vals.length; i++) {
				T.insert(vals[i]);
			}
			assertEquals("Not all locations were successfully inserted", vals.length, T.size());
		} catch (ExitException e) {
			assertTrue("Program crashed", false);
		}
	}

	private void _testNext(LocationContainer T, DiskLocation target, DiskLocation expected) {
		DiskLocation actual = new DiskLocation(-1, -1);
		try {
			actual = T.next(target);
		} catch (ExitException e) {
			assertTrue("Program crashed", false);
		}
		assertNotNull("Next value is unexpectedly null, expected " + expected, actual);
		assertEquals("Location after " + target.toString() + " is incorrect", expected, actual);
	}

	private void _testPrev(LocationContainer T, DiskLocation target, DiskLocation expected) {
		DiskLocation actual = new DiskLocation(-1, -1);
		try {
			actual = T.prev(target);
		} catch (ExitException e) {
			assertTrue("Program crashed", false);
		}
		assertNotNull("Prev value is unexpectedly null, expected " + expected, actual);
		assertEquals("Location before " + target.toString() + " is incorrect", expected, actual);
	}

	private void _testHeight(LocationContainer T, int min, int max) {
		int actual = Integer.MAX_VALUE;
		try {
			actual = T.height();
		} catch (ExitException e) {
			assertTrue("Program crashed", false);
		}
		assertTrue("Height is too large", max >= actual);
		assertTrue("Height is too small", min <= actual);
	}

	private RBTree _buildSmolTree() {
		RBTree T = new RBTree();

		T.insert(new DiskLocation(6, 1));
		T.insert(new DiskLocation(4, 1));
		T.insert(new DiskLocation(9, 1));

		return T;
	}

	private RBTree _buildSmallTree() {
		RBTree T = new RBTree();

		T.insert(new DiskLocation(6, 1));
		T.insert(new DiskLocation(4, 1));
		T.insert(new DiskLocation(9, 1));
		T.insert(new DiskLocation(2, 1));
		T.insert(new DiskLocation(5, 1));
		T.insert(new DiskLocation(8, 1));
		T.insert(new DiskLocation(11, 1));

		return T;
	}

	private int _treeSize(RBTree T, DiskLocation min) {
		DiskLocation curr = T.find(min);
		int count = 0;
		try {
			while (curr != RBTree.nil && curr != null) {
				count++;
				curr = T.next(curr);
			}
		} catch (NullPointerException ex) {}

		return count;
	}

	@Test
	public void test00_InsertSmall() {
		_testInsert(new RBTree(), new DiskLocation[] {new DiskLocation(1, 2)});
		_testInsert(new RBTree(), new DiskLocation[] {new DiskLocation(1, 2), new DiskLocation(1, 3)});
		_testInsert(new RBTree(), new DiskLocation[] {new DiskLocation(1, 2), new DiskLocation(1, 1), new DiskLocation(1, 3)});
		_testInsert(new RBTree(), new DiskLocation[] {new DiskLocation(1, 3), new DiskLocation(1, 2), new DiskLocation(1, 1)});
	}

	@Test
	public void test01_InsertMedium() {
		_testInsert(new RBTree(), new DiskLocation[] {new DiskLocation(1, 3), new DiskLocation(1, 1), new DiskLocation(1, 2), new DiskLocation(1, 4)});
		_testInsert(new RBTree(), new DiskLocation[] {new DiskLocation(1, 3), new DiskLocation(1, 1), new DiskLocation(1, 2), new DiskLocation(1, 5), new DiskLocation(1, 4)});
		_testInsert(new RBTree(), new DiskLocation[] {new DiskLocation(1, 2), new DiskLocation(2, 1), new DiskLocation(2, 3), new DiskLocation(1, 4), new DiskLocation(1, 1), new DiskLocation(2, 4)});
		_testInsert(new RBTree(), new DiskLocation[] {new DiskLocation(2, 3), new DiskLocation(1, 3), new DiskLocation(2, 2), new DiskLocation(1, 2), new DiskLocation(1, 1), new DiskLocation(2, 1)});
	}

	@Test
	public void test02_InsertSeq() {
		for (int i = 1; i < 1000000; i*=10) {
			DiskLocation[] d = new DiskLocation[i];
			for (int j = 0; j < i; j++)
				d[j] = new DiskLocation(1, j);
			_testInsert(new RBTree(), d);
		}
	}

	@Test
	public void test03_InsertRand() {

		for (int track = 10; track <= 200; track += 10) {
			for (int sector = 10; sector <= 200; sector += 10) {
				ArrayList<DiskLocation> arrD = new ArrayList<DiskLocation>(track*sector);
				for (int i = 0; i < track; i++) {
					for (int j = 0; j < sector; j++) 
						arrD.add(new DiskLocation(i, j));
				}
				DiskLocation[] d = new DiskLocation[track*sector];
				Collections.shuffle(arrD);		// randomize array
				_testInsert(new RBTree(), arrD.toArray(d));
			}
		}
	}

	@Test
	public void test04_NextSmall() {
		RBTree T = _buildSmolTree();

		_testNext(T, T.find(new DiskLocation(4, 1)), new DiskLocation(6, 1));
		_testNext(T, T.find(new DiskLocation(6, 1)), new DiskLocation(9, 1));

		T = _buildSmallTree();
		_testNext(T, T.find(new DiskLocation(9, 1)), new DiskLocation(11, 1));
		_testNext(T, T.find(new DiskLocation(4, 1)), new DiskLocation(5, 1));
	}

	@Test
	public void test05_NextLarge() {
		RBTree T = new RBTree();

		for (int i = 0; i < 300; i++)
			T.insert(new DiskLocation(i, 1));
		_testNext(T, T.find(new DiskLocation(1, 1)), new DiskLocation(2, 1));
		_testNext(T, T.find(new DiskLocation(2, 1)), new DiskLocation(3, 1));
		_testNext(T, T.find(new DiskLocation(3, 1)), new DiskLocation(4, 1));
		_testNext(T, T.find(new DiskLocation(91, 1)), new DiskLocation(92, 1));
		_testNext(T, T.find(new DiskLocation(140, 1)), new DiskLocation(141, 1));
	}

	@Test
	public void test06_NextNil() {
		RBTree T = _buildSmolTree();
		_testNext(T, T.find(new DiskLocation(9, 1)), LocationContainer.nil);

		T = _buildSmallTree();
		_testNext(T, T.find(new DiskLocation(11, 1)), LocationContainer.nil);
	}

	@Test
	public void test07_PrevSmall() {

		RBTree T = _buildSmolTree();
		_testPrev(T, T.find(new DiskLocation(9, 1)), new DiskLocation(6, 1));
		_testPrev(T, T.find(new DiskLocation(6, 1)), new DiskLocation(4, 1));

		T = _buildSmallTree();
		_testPrev(T, T.find(new DiskLocation(11, 1)), new DiskLocation(9, 1));
		_testPrev(T, T.find(new DiskLocation(5, 1)), new DiskLocation(4, 1));
	}

	@Test
	public void test08_PrevLarge() {
		RBTree T = new RBTree();

		for (int i = 0; i < 300; i++)
			T.insert(new DiskLocation(i, 1));
		_testPrev(T, T.find(new DiskLocation(2, 1)), new DiskLocation(1, 1));
		_testPrev(T, T.find(new DiskLocation(3, 1)), new DiskLocation(2, 1));
		_testPrev(T, T.find(new DiskLocation(4, 1)), new DiskLocation(3, 1));
		_testPrev(T, T.find(new DiskLocation(92, 1)), new DiskLocation(91, 1));
		_testPrev(T, T.find(new DiskLocation(141, 1)), new DiskLocation(140, 1));
	}

	@Test
	public void test09_PrevNil() {
		RBTree T = _buildSmolTree();
		_testPrev(T, T.find(new DiskLocation(4, 1)), LocationContainer.nil);

		T = _buildSmallTree();
		_testPrev(T, T.find(new DiskLocation(2, 1)), LocationContainer.nil);
	}

	@Test
	public void test10_Find() {
		RBTree T = _buildSmolTree();
		DiskLocation d;

		d = T.find(new DiskLocation(9, 1));
		assertNotNull("Find value is unexpectedly null", d);
		assertTrue("Find is not working correctly", d.equals(new DiskLocation(9, 1)));
		assertNotNull("Find should return corresponding DiskLocation in tree", d.parent);

		d = T.find(new DiskLocation(4, 1));
		assertNotNull("Find value is unexpectedly null", d);
		assertTrue("Find is not working correctly", d.equals(new DiskLocation(4, 1)));
		assertNotNull("Find should return corresponding DiskLocation in tree", d.parent);

		T = _buildSmallTree();

		d = T.find(new DiskLocation(4, 1));
		assertNotNull("Find value is unexpectedly null", d);
		assertTrue("Find is not working correctly", d.equals(new DiskLocation(4, 1)));
		assertNotNull("Find should return corresponding DiskLocation in tree", d.parent);

		d = T.find(new DiskLocation(11, 1));
		assertNotNull("Find value is unexpectedly null", d);
		assertTrue("Find is not working correctly", d.equals(new DiskLocation(11, 1)));
		assertNotNull("Find should return corresponding DiskLocation in tree", d.parent);
	}

	@Test
	public void test11_Size() {
		RBTree T = _buildSmolTree();
		assertEquals("Tree has wrong number of nodes", 3, _treeSize(T, new DiskLocation(4, 1)));

		T = _buildSmallTree();
		assertEquals("Tree has wrong number of nodes", 7, _treeSize(T, new DiskLocation(2, 1)));


		T = new RBTree();
		ArrayList<DiskLocation> arrD = new ArrayList<DiskLocation>(200);
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 10; j++) 
				arrD.add(new DiskLocation(i, j));
		}
		DiskLocation[] d = new DiskLocation[200];
		Collections.shuffle(arrD);		// randomize array
		_testInsert(T, arrD.toArray(d));

		assertEquals("Tree has wrong number of nodes (or next is failing)", 200, _treeSize(T, new DiskLocation(0, 0)));
	}

	@Test
	public void test12_HeightSmall() {
		RBTree T = _buildSmolTree();
		_testHeight(T, 1, 2);

		T = _buildSmallTree();
		_testHeight(T, 2, 4);
	}

	@Test
	public void test13_PublicMethods() {
		List<String> mNames = Arrays.asList("find", "next", "prev", "insert", "height", "size",
				"wait", "equals", "toString", "hashCode", "getClass", "notify", "notifyAll");

		for (Method m: BST.class.getMethods())
			assertTrue("method named " + m.getName() + " should be private.",
					Modifier.isPrivate(m.getModifiers()) || mNames.contains(m.getName()));		
	}

}
