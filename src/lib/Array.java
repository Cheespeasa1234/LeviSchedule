package lib;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Consumer;

/**
 * This class is meant to make using arrays easier and more readable.
 * It contains methods to keep track of items that you have in this object,
 * and methods to manage the items already in the object.
 * Make sure to check back for new versions and features.
 *
 * Changes
 * 
 * <pre>
 * 1.3
 * 
 * <pre>
 * - Implemented Iterable<T>
 * - Changed forEach(Consumer<T> cons) to forEach(Consumer<? super T> cons)
 * - Made it forEach-able (Object o : new Array<Object>)
 * 
 * @author nlevison25
 * @version 1.3 - Mar 22 2023
 *
 * @param <T> Type of objects that are held
 * 
 */
@SuppressWarnings("unchecked")
public class Array<T> implements Comparable<T>, Iterable<T> {

	/**
	 * The array holding current inventory of items
	 * 
	 * @since 1.0
	 */
	private T[] array;

	/**
	 * Standard array of number classes that are numbers (castable to double)
	 */
	@SuppressWarnings("rawtypes")
	private static final Class[] n = {
			((Byte) (byte) 1).getClass(),
			((Short) (short) 1).getClass(),
			((Integer) 1).getClass(),
			((Long) 1l).getClass(),
			((Float) 1f).getClass(),
			((Double) 1d).getClass(),
	};

	/**
	 * An array of valid Number classes. This includes:
	 * - Integer
	 * - Double
	 * - Float
	 * - Byte
	 * - Short
	 * - Long
	 * It is used to see if an object is castable to another number.
	 */
	@SuppressWarnings("rawtypes")
	public static final Array<Class> valid = new Array<Class>(n);

	/**
	 * Make a new {@code Array} object, that is fully blank.
	 * 
	 * @since 1.0
	 */
	public Array() {
		this.array = (T[]) new Object[0];
	}

	/**
	 * Make a new {@code Array} object, containing {@code arr} as elements.
	 * 
	 * @param arr The array to start with. Must be an array of classes that extend
	 *            Object.
	 * 
	 * @since 1.0
	 */
	public Array(T[] arr) {
		this.array = arr;
	}

	public Array(int l) {
		this.array = (T[]) new Object[l];
	}

	public Array(int l, T object) {
		this.array = (T[]) new Object[l];
		for (int i = 0; i < l; i++)
			this.array[i] = object;
	}

	/**
	 * Returns number representing the comparison of object lengths.
	 * If the size of a is more than this size, returns 1
	 * If the size of a is less than this size, returns -1
	 * If they are equal, returns 0
	 * Requires class equals method to be overridden.
	 * 
	 * @param a {@code Array} object to compare
	 * @return number representing the comparison of object lengths
	 * 
	 * @since 1.0
	 */
	@Override
	public int compareTo(Object o) {

		if (!(o instanceof Array)) {
			return -1;
		}

		Array<T> a = (Array<T>) o;

		if (this.equals(o))
			return 0;
		else if (a.size() < size())
			return 1;
		return -1;
	}

	/**
	 * Requires class equals method to be overridden.
	 * 
	 * @param obj Object to compare
	 * @returns boolean of equality
	 * 
	 * @since 1.0
	 */
	@Override
	public boolean equals(Object o) {
		if (!o.getClass().equals(this.getClass()))
			return false;
		T[] a = ((Array<T>) o).toArray();
		return a.equals(this.array);
	}

	/**
	 * Returns the length of elements in the array.
	 * 
	 * @return Length of this {@code Array}
	 * 
	 * @since 1.0
	 */
	public int size() {
		return this.array.length;
	}

	/**
	 * Returns the index of the element in this {@code Array}, if equal.
	 * If nothing is found, returns -1
	 * Requires class equals method to be overridden.
	 * 
	 * @param obj Object to find
	 * @return Index of first object to be equal to {@code obj}
	 * 
	 * @since 1.0
	 */
	public int indexOf(T obj) {
		for (int i = 0; i < this.array.length; i++)
			if (this.array[i].equals(obj))
				return i;
		return -1;
	}

	/**
	 * Returns the array this object stores.
	 * 
	 * @return The {@code T[]} this {@code Array} stores
	 * 
	 * @since 1.0
	 */
	public T[] toArray() {
		return this.array;
	}

	/**
	 * Returns whether or not this {@code Array} object has an element.
	 *
	 * @param obj Object to search for
	 * @returns boolean, if has element
	 *
	 * @since 1.2
	 */
	public boolean has(T obj) {
		for(int i = 0; i < this.size(); i++)
			if(this.array[i] == obj)
				return true;
		return false;
	}

	/**
	 * Returns the amount of times that obj is found in this {@code Array}.
	 * Requires class equals method to be overridden.
	 * 
	 * @param obj Object to count
	 * @return Count of obj's occurences
	 * 
	 * @since 1.0
	 */
	public int countOf(T obj) {
		int count = 0;
		for (int i = 0; i < this.size(); i++) {
			System.out.println(this.array[i]);
			if (this.array[i].equals(obj))
				count++;
		}

		return count;
	}

	/**
	 * Removes the object at an index in this {@code Array}.
	 * Requires class equals method to be overridden.
	 * 
	 * @param index Index remove.
	 * @param count Maximum count of objects to remove.
	 * 
	 * @since 1.0
	 */
	public void remove(int index) {
		Array<T> aux = new Array<T>();
		for (int i = 0; i < this.size(); i++) {
			if (i != index)
				aux.add(this.get(i));
		}
		this.array = aux.toArray();
	}

	/*
	 * Removes the first equal object in this <@code Array}
	 * that is equal to obj.
	 * 
	 * @param obj Object to find and remove.
	 * 
	 * @since 1.1
	 */
	public void removeObject(T obj) {
		this.remove(this.indexOf(obj));
	}

	/*
	 * Removes the first count equal objects in this <@code Array}
	 * that is / are equal to obj.
	 * 
	 * @param obj Object to find and remove.
	 * 
	 * @param count Maximum number to remove.
	 * 
	 * @since 1.1
	 */
	public void removeObject(T obj, int count) {
		while (this.has(obj) && count > 0) {
			count--;
			this.remove(this.indexOf(obj));
		}
	}

	/**
	 * Removes any elements of this {@code Array} that are there twice.
	 * Requires class equals method to be overridden.
	 * 
	 * @since 1.0
	 */
	public void removeDuplicates() {

		Array<T> a = new Array<T>();
		for (int i = 0; i < this.size(); i++) {
			if (!a.has(array[i])) {
				a.add(array[i]);
			}
		}
		this.array = a.array;
	}

	public void clear() {
		this.array = (T[]) new Object[0];
	}

	/**
	 * Returns a new {@code Array} object with all elements from startIndex and
	 * beyond.
	 * Does not modify this object.
	 * 
	 * @param startIndex Index to start from
	 * @return Array
	 * 
	 * @since 1.1
	 */
	public Array<T> subArray(int startIndex) {

		if (startIndex < 0 || startIndex > this.size())
			throw new IllegalArgumentException("Illegal Index: " + startIndex);

		Array<T> array = new Array<T>();
		for (int i = startIndex; i < this.size(); i++)
			array.add(this.get(i));

		return array;

	}

	/**
	 * Returns a new {@code Array} object with all elements starting at startIndex,
	 * and not including endIndex and beyond.
	 * Does not modify this object.
	 * 
	 * @param startIndex Index to start from
	 * @param endIndex   Index to stop on
	 * @return Array
	 * 
	 * @since 1.1
	 */
	public Array<T> subArray(int startIndex, int endIndex) {

		if (startIndex < 0 || startIndex > this.size()) {
			throw new IllegalArgumentException("Illegal Index: " + startIndex);
		} else if (endIndex < 0 || endIndex > this.size() || endIndex < startIndex) {
			throw new IllegalArgumentException("Illegal Index: " + endIndex);
		}

		Array<T> array = new Array<T>();
		for (int i = startIndex; i < endIndex; i++)
			array.add(this.get(i));

		return array;
	}

	/**
	 * Requires class equals method to be overridden.
	 * Requires class compareTo method.
	 * 
	 * Sorts this Array with a bubble sort algorithm.
	 * 
	 * This algorithm works by going through the array
	 * multiple times, seeing if two objects in the array,
	 * next to each other, and not in the right order.
	 * If they are not, they get swapped. The pointer then
	 * moves on and does another swap, repeating this forever
	 * until the array is sorted.
	 * 
	 * This is much faster than selectionSort, but the
	 * downside is that it has O(n^2) time complexity in the
	 * worst case, (most unsorted) but can have O(n) time
	 * complexity in best case. (This means the time it takes
	 * to sort exponentially increases, as the length of the
	 * array increases.)
	 * 
	 * @see {@link #selectionSort() Selection Sort} for more info on time
	 *      complexity.
	 * 
	 * @since 1.1
	 */
	public void bubbleSort() {
		if (this.size() < 1 || !(array[0] instanceof Comparable))
			return;
		int n = this.size();
		for (int i = 0; i < n - 1; i++)
			for (int j = 0; j < n - i - 1; j++) {
				Comparable<T> obj = ((Comparable<T>) this.array[j]);
				if (obj.compareTo(this.array[j + 1]) == 1) {
					T temp = array[j];
					this.array[j] = this.array[j + 1];
					this.array[j + 1] = temp;
				}
			}
	}

	/**
	 * Requires class equals method to be overridden.
	 * Requires class compareTo method.
	 * 
	 * Sorts this Array with a selection sort algorithm.
	 * 
	 * This algorithm works by going through the array, and
	 * finding the current smallest item. It adds the smallest
	 * to the final array, and adds the smallest to objects to
	 * ignore in the future.
	 * 
	 * This is technically slower than selectionSort, but always
	 * has O(n) time. Meaning, the length of the array times the
	 * time to compare and add an element is the final time.
	 * 
	 * The symbol O means the amount of time per element. This
	 * includes comparing, adding, subtracting, casting, and
	 * anything else that takes time. Multiply O by n, the
	 * amount of elements you need to do this for. This means
	 * the time complexity is O(n).
	 * 
	 * @see {@link #bubbleSort() Bubble Sort}
	 * 
	 * @since 1.1
	 */
	public void selectionSort() {
		if (this.size() < 1 || !(this.array[0] instanceof Comparable))
			return;

		int n = this.size();

		// One by one move boundary of unsorted subarray
		for (int i = 0; i < n - 1; i++) {
			// Find the minimum element in unsorted array
			int min_idx = i;
			for (int j = i + 1; j < n; j++)
				if (((Comparable<T>) this.array[j]).compareTo(this.array[min_idx]) == -1)
					min_idx = j;

			// Swap the found minimum element with the first
			// element
			T temp = this.array[min_idx];
			this.array[min_idx] = this.array[i];
			this.array[i] = temp;
		}
	}

	/**
	 * Runs a VOID method on every element of this Array.
	 * This is called a LAMBDA, an anonymous function.
	 * This means you give this method a function, but it doesn't
	 * know what it does. However, it will run it.
	 * 
	 * Here is how to make a consumer (lambda):
	 * 
	 * Consumer{@literal<Integer>} lambda;
	 * 
	 * lambda = (param) {@literal ->} {@literal {
	 * System.out.println(param +"");
	 * };
	 * 
	 * Then, just do arr.forEach(lambda); and all of the items of the list are
	 * printed!
	 * 
	 * Remember, the method you make MUST be VOID, or it will not work.
	 * 
	 * @param cons Lambda to run
	 * 
	 * @since 1.2
	 */
	@Override
	public void forEach(Consumer<? super T> cons) {

		for (int i = 0; i < this.size(); i++) {
			cons.accept(this.get(i));
		}

	}

	/**
	 * Runs a VOID method on every element between start and end of this Array.
	 * This is called a LAMBDA, an anonymous function.
	 * This means you give this method a function, but it doesn't
	 * know what it does. However, it will run it.
	 * 
	 * Here is how to make a consumer (lambda):
	 * 
	 * Consumer{@literal<Integer>} lambda;
	 * 
	 * lambda = (param) {@literal ->} {@literal {
	 * System.out.println(param +"");
	 * };
	 * 
	 * Then, just do arr.forEach(lambda); and all of the items of the list are
	 * printed!
	 * 
	 * Remember, the method you make MUST be VOID, or it will not work.
	 * 
	 * @param cons Lambda to run
	 * 
	 * @param start start of running
	 * @param end   end of running
	 * 
	 * @since 1.2
	 */
	public void forEach(Consumer<T> cons, int start, int end) {

		for (int i = start; i < end; i++) {
			cons.accept(this.get(i));
		}

	}

	/**
	 * Swaps items at two indexes.
	 * 
	 * @param a Index 1
	 * @param b Index 2
	 */
	public void swap(int a, int b) {
		T temp = this.get(a);
		this.set(a, this.get(b));
		this.set(b, temp);
	}

	/**
	 * Randomly shuffles the elements of this array.
	 * It chooses a random number of elements to shuffle,
	 * and for all of these elements, swaps its position
	 * with another element. This generates a psuedo-randomly
	 * shuffled list. This is NOT cryptographically safe!
	 * Make sure to use the crypto library for better randomness.
	 */
	public void shuffle() {

		Random rand = new Random();

		int timesToShuffle = rand.nextInt(20, 50);
		for (int i = 0; i < timesToShuffle; i++) {
			int a = rand.nextInt(0, this.size());
			int b;
			do {
				b = rand.nextInt(0, this.size());
			} while (b == a);
			this.swap(a, b);
		}

	}

	/**
	 * Randomly shuffles the elements of this array.
	 * It chooses a random number of elements to shuffle,
	 * and for all of these elements, swaps its position
	 * with another element. This generates a psuedo-randomly
	 * shuffled list. This is NOT cryptographically safe!
	 * Make sure to use the crypto library for better randomness.
	 * 
	 * This uses a seed that you give, so if you give the same
	 * seed, you will always get the same shuffle. Only do this
	 * if your seed is part of your program as random generation
	 * as a constant.
	 * 
	 * @param seed Seed number for randomness.
	 */
	public void shuffle(int seed) {
		Random rand = new Random(seed);

		int timesToShuffle = rand.nextInt(20, 50);
		for (int i = 0; i < timesToShuffle; i++) {
			int a = rand.nextInt(0, this.size());
			int b;
			do {
				b = rand.nextInt(0, this.size());
			} while (b == a);
			this.swap(a, b);
		}

	}

	/**
	 * Reverses the order of elements in this array.
	 * For example, if this array is [1, 2, 3, 4] it
	 * will become [4, 3, 2, 1]
	 * 
	 * @since 1.1
	 */
	public void reverse() {

		for (int i = 0; i < this.size() / 2; i++) {
			this.swap(i, this.size() - i - 1);
		}

	}

	/**
	 * Adds elements to the smallest list until it is
	 * the same length as the other array.
	 * 
	 * @param arr1   Array to buffer
	 * @param arr2   Array to buffer
	 * @param buffer object to buffer with
	 * 
	 * @since 1.2
	 */
	@SuppressWarnings("rawtypes")
	public static void buffer(Array arr1, Array arr2, Object buffer) {

		int ct = arr1.compareTo(arr2);
		int bu = arr2.size() - arr1.size();
		if (ct == -1) {
			for (int i = 0; i < bu; i++)
				arr1.add(buffer);

		} else if (ct == 1) {
			bu = -bu;
			for (int i = 0; i < bu; i++)
				arr2.add(buffer);

		}

	}

	/**
	 * Represents an Array as a point in size()D space
	 * and thus calculates the length.
	 * 
	 * Represents each entry in a list as a position in a dimension.
	 * Using the formula sqrt ( (x1 - x2)^2 + (y1 - y2)^2 + ... ) you can
	 * calculate the distance of each point.
	 * 
	 * If the sizes of either array is different, the smallest one is
	 * buffered to mimic the values of the bigger array. So, if array1
	 * is [1, 2, 3, 4, 5] and array2 is [2, 3, 4] than array2 will be
	 * used as [2, 3, 4, 4, 5]. The object passed into this function is not
	 * modified.
	 * 
	 * @param array1 Array of number type values that represent a point
	 * @param array2 Array of number type values that represent a point
	 * @throws ClassCastException
	 * 
	 * @since 1.2
	 */
	public static double dist(Array<Number> array1, Array<Number> array2) {

		if (valid.has(array1.getClass()))
			throw new ClassCastException("Arrays must be a number (" + array1.getClass() + ")");
		if (valid.has(array2.getClass()))
			throw new ClassCastException("Arrays must be a number (" + array2.getClass() + ")");

		Array<Number> arr1 = new Array<Number>();
		for (int i = 0; i < array1.size(); i++) {
			arr1.add((Double) array1.get(i));
		}

		Array<Number> arr2 = new Array<Number>();
		for (int i = 0; i < array1.size(); i++) {
			arr1.add((Double) array1.get(i));
		}

		// Buffer lengths
		buffer(arr1, arr2, 0);
		System.out.println(arr1.toString() + arr2.toString());

		double total = 0;
		for (int i = 0; i < arr1.size(); i++) {
			// casting SUCKSSSSS
			// Integer >> double >> String >> Integer

			double d = Math.pow(arr1.get(i).doubleValue() - arr2.get(i).doubleValue(), 2);
			total += d;
		}

		return Math.sqrt(total);

	}

	/**
	 * Adds obj to the end of this {@code Array}. Increases the size of this
	 * {@code Array} by 1
	 * 
	 * @param obj Object to add to end of array.
	 * 
	 * @since 1.0
	 */
	public void add(T obj) {
		T[] aux = (T[]) new Object[this.array.length + 1];
		for (int i = 0; i < this.array.length; i++)
			aux[i] = get(i);
		aux[aux.length - 1] = obj;
		array = aux;
	}

	/**
	 * Returns the object at index i.
	 * 
	 * @param i index in this {@code Array} to get object from
	 * @return object found
	 * 
	 * @since 1.0
	 */
	public T get(int i) {
		if (i < 0 || i > this.size())
			throw new IllegalArgumentException("Illegal Index: " + i);
		return this.array[i];
	}

	/**
	 * Sets the value at i to obj. Overwrites existing object.
	 * 
	 * @param i   index in this array to overwrite
	 * @param obj object to set
	 * 
	 * @since 1.0
	 */
	public void set(int i, T obj) {
		if (i < 0 || i > this.size())
			throw new IllegalArgumentException("Illegal Index: " + i);
		if (obj == null || !(obj instanceof T))
			throw new IllegalArgumentException("Illegal obj: " + obj);
		this.array[i] = obj;
	}

	/**
	 * Returns a string representation of this {@code Array}'s contents.
	 * 
	 * @return String representation
	 * 
	 * @since 1.0
	 */
	public String toString() {
		if (size() < 1)
			return "[]";
		String s = "[";
		for (int i = 0; i < this.array.length - 1; i++)
			s += this.array[i] + ", ";
		s += this.array[this.array.length - 1] + "]";
		return s;
	}

	/**
	 * Returns a new Array object with the same elements as this.
	 * 
	 * @return new {@code Array} object
	 * 
	 * @since 1.0
	 */
	@Override
	public Array<T> clone() {
		T[] ar = (T[]) new Object[this.size()];
		System.arraycopy(this.array, 0, ar, 0, this.size());
		Array<T> a = new Array<T>(ar);
		return a;
	}

	@Override
	public Iterator<T> iterator() {
		return Arrays.asList(array).iterator();
	}
}
