package com.allendowney.thinkdast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jfree.data.xy.XYSeries;

import com.allendowney.thinkdast.Profiler.Timeable;

public class ProfileListAdd {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		profileArrayListAddEnd();
		profileArrayListAddBeginning();
		profileLinkedListAddBeginning();
		profileLinkedListAddEnd();
	}

	/**
	 * Characterize the run time of adding to the end of an ArrayList
	 */
	public static void profileArrayListAddEnd() {
		Timeable timeable = new Timeable() {
			List<String> list;

			public void setup(int n) {
				list = new ArrayList<String>();
			}

			public void timeMe(int n) {
				for (int i=0; i<n; i++) {
					list.add("a string");
				}
			}
		};
		int startN = 32000;
		int endMillis = 2000;
		runProfiler("ArrayList add end", timeable, startN, endMillis);
	}
	
	/**
	 * Characterize the run time of adding to the beginning of an ArrayList
	 */
	public static void profileArrayListAddBeginning() {
		Timeable timeable = new Timeable() {
			List<String> list;

			public void setup(int n) {
				list = new ArrayList<String>();
			}

			public void timeMe(int n) {
				for (int i=0; i<n; i++) {
					list.add(0, "a string");
				}
			}
		};
		int startN = 32000;
		int endMillis = 1000;
		runProfiler("ArrayList add end", timeable, startN, endMillis);
	}

	/**
	 * Characterize the run time of adding to the beginning of a LinkedList
	 */
	public static void profileLinkedListAddBeginning() {
		Timeable timeable = new Timeable() {
			List<String> list;

			public void setup(int n) {
				list = new LinkedList<>();
			}

			public void timeMe(int n) {
				for (int i=0; i<n; i++) {
					list.add(0, "a string");
				}
			}
		};
		int startN = 32000;
		int endMillis = 2000;
		runProfiler("ArrayList add end", timeable, startN, endMillis);
	}

	/**
	 * Characterize the run time of adding to the end of a LinkedList
	 */
	public static void profileLinkedListAddEnd() {
		Timeable timeable = new Timeable() {
			List<String> list;

			public void setup(int n) {
				list = new LinkedList<>();
			}

			public void timeMe(int n) {
				for (int i=0; i<n; i++) {
					list.add("a string");
				}
			}
		};
		int startN = 4000;
		int endMillis = 2000;
		runProfiler("ArrayList add end", timeable, startN, endMillis);
	}

	/**
	 * Runs the profiles and displays results.
	 * 
	 * @param timeable
	 * @param startN
	 * @param endMillis
	 */
	private static void runProfiler(String title, Timeable timeable, int startN, int endMillis) {
		Profiler profiler = new Profiler(title, timeable);
		XYSeries series = profiler.timingLoop(startN, endMillis);
		profiler.plotResults(series);
	}

	private static void findParameters(String title, Timeable timeable, int startN, int endMillis) {
		int[] arrStartN = new int[4];
		int[] arrEndMillis = new int[3];
		for (int i = 0; i < arrStartN.length; i++) {
			for (int j = 0; j < arrEndMillis.length;j++) {
				arrEndMillis[j] = endMillis;
				endMillis = endMillis + 1000;
			}
			arrStartN[i] = startN;
			startN = startN * 2;
			endMillis = 1000;
		}

		Profiler profiler = new Profiler(title, timeable);
		double[][] slope = new double[arrStartN.length][arrEndMillis.length];
		for (int i = 0; i < arrStartN.length; i++) {
			for (int j = 0; j < arrEndMillis.length; j++) {
				XYSeries series = profiler.timingLoop(arrStartN[i], arrEndMillis[j], false);
				slope[i][j] = profiler.estimateSlope(series);
			}
		}

		System.out.println("================");
		for (int i = 0; i < arrStartN.length; i++) {
			for (int j = 0; j < arrEndMillis.length; j++) {
				System.out.println("Estimated slope= " + slope[i][j] + "for startN= " + arrStartN[i] + " end endMillis=" + arrEndMillis[j]);
			}
		}
	}
}
