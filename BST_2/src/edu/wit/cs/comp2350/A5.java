package edu.wit.cs.comp2350;

import java.util.Scanner;

/** Provides next/previous/height info on (int, int) disk locations 
 * 
 * Wentworth Institute of Technology
 * COMP 2350
 * Assignment 5
 * 
 */

public class A5 {

	// read pairs of ints from scanner
	// returns an array of DiskLocations
	private static DiskLocation getLoc(Scanner s) {
		int track, sector = 0;
		track = s.nextInt();
		if (s.hasNextInt())
			sector = s.nextInt();
		else {
			System.err.println("Track/sector mismatch on input " + s.next());
			System.exit(0);
		}

		if (track < 0 || sector < 0) {
			System.err.println("Track and sector values must be non-negative");
			System.exit(0);
		}
		DiskLocation d = new DiskLocation(track, sector);
		return d;
	}

	// prints the next/prev n items after a specific location
	// the location must be a valid location in l
	private static void printIter(LocationContainer l, DiskLocation d, char direction, int number) {

		DiskLocation temp = l.find(d);
		for (int i = 0; i < number; i++) {
			if (temp == LocationContainer.nil)
				return;
			if (direction == 'n')
				temp = l.next(temp);
			else if (direction == 'p')
				temp = l.prev(temp);
			if (temp.toString().length() == 0)
				break;
			System.out.println(temp.toString());
		}
	}

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		s.useDelimiter("(\\s|,)+");	//delimit with commas
		LocationContainer l = new BST();

		System.out.printf("Enter the data structure to use ([l]ist, [b]inary tree, [r]ed-black tree): ");
		char algo = s.next().charAt(0);

		switch (algo) {
		case 'l':
			l = new LinkedList();
			break;
		case 'b':
			l = new BST();
			break;
		case 'r':
			l = new RBTree();
			break;
		default:
			System.out.println("Invalid data structure");
			System.exit(0);
			break;
		}

		System.out.printf("Enter non-negative track/sector pairs separated by commas.\nTerminate the list with one of the following options:\n");
		System.out.printf("Enter [n] to print the next values after a location (must be valid location).\n");
		System.out.printf("Enter [p] to print the previous values before a location (must be valid location).\n");
		System.out.printf("Enter [h] to print the height of the data structure\n");
		System.out.printf("Enter [q] to quit\n");
		System.out.printf("***\nExample (inserts three locations and asks for the previous 2 before (2, 1):\n0 1, 1 1, 2 1 p\n2 1\n2\n***\n");

		char nextAction = ',';

		while (nextAction == ',') {
			l.insert(getLoc(s));
			if (!s.hasNextInt())
				nextAction = s.next().charAt(0); // read in option
		}

		switch (nextAction) {
		case 'n':
		case 'p':
			int track = -1;
			int sector = -1;
			int number = -1;
			System.out.printf("Enter starting <track> <sector>: ");
			if (s.hasNextInt())
				track = s.nextInt();
			if (s.hasNextInt())
				sector = s.nextInt();
			System.out.printf("Enter number of locations to seek: ");
			if (s.hasNextInt())
				number = s.nextInt();
			else {
				System.err.println("Couldn't read track/sector and number");
				System.exit(0);
			}
			printIter(l, new DiskLocation(track, sector), nextAction, number);
			break;
		case 'h':
			System.out.printf("height: %d\n", l.height());
			break;
		case 'q':
			System.exit(0);
		default:
			System.out.println("Invalid action");
			System.exit(0);
			break;
		}
		s.close();

	}

}
