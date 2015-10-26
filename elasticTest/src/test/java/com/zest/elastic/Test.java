package com.zest.elastic;

import java.util.ArrayList;
import java.util.List;

public class Test {

	@org.junit.Test
	public void testT() {

		System.out.println(fibonacciTailRecursion(11, 1, 0));
	}

	public static int fibonacciTailRecursion(int n, int cached_1, int cached_2) {
		if (n < 2)
			return n * cached_1;
		int tmp = cached_1;
		cached_1 += cached_2;
		cached_2 = tmp;
		return fibonacciTailRecursion(n - 1, cached_1, cached_2);
	}

	@org.junit.Test
	public void aaaa() {
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(1);
		a.add(2);
		a.add(3);
		System.out.println(func(a,10));
	}

	public int func(List<Integer> list, int number) {
		int count = 0;
		if (number == 0) {
			count = 1;
		} else if (number > 0) {
			for (int i = 0; i < list.size(); i++) {
				count += func(list.subList(i, list.size()),
						number - list.get(i));
			}
		}
		return count;
	}

}
