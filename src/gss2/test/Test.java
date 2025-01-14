package gss2.test;

import gss2.math.*;
import java.util.*;

public class Test
{
	public static void main(String[] args)
	{

		new Test().a();

	}
	void a()
	{
		
	}
	void test3()
	{
		System.out.println("===Test 3. Shape getScalar and getShape test ===");
		Shape s=new Shape(3, 2);
		fillR(s.data);

		for (int i=0;i < s.data.length;i++)
		{
			int[] sh=s.getShape(i);
			test(s.getScalar(sh) == s.data.data[i], "get " + i);
		}
		System.out.println("--- getScalar test done! ---");
	}
	void test2()
	{
		System.out.println("=== Default Shape test ===");

		int sh[]={2,3};
		Shape s=new Shape(2, 3);
		test(Arrays.equals(sh, s.shape), "equals shape");
		test(Arrays.equals(new int[]{3,1}, s.sum), "equal sum");
		test(s.shapeToIndex(0, 0) == 0, "access index 1");
		test(s.shapeToIndex(0, 2) == 2, "access index 2");
		test(s.shapeToIndex(1, 0) == 3, "access index 3");
		test(s.shapeToIndex(1, 2) == 5, "access index 4");
		System.out.println("--- creating default shape done! ---");
	}
	void test1()
	{
		System.out.println("=== creating data class ===");
		Data d=new Data(2, 3);
		System.out.println(d.length);
		System.out.println(Arrays.toString(d.data));
		test(d.length == 6, "data length");
		test(Arrays.equals(d.data, new float[6]), "array equals");
		System.out.println("--- data create and initzialize ---");
	}
	public static void test(boolean b, Object msg)
	{
		if (b)
			System.out.println("✓ " + msg);
		else
			throw new RuntimeException("XXXXX   est not passed.");
		// System.out.println("X " + msg);
	}
	static float[] fillR(Data d)
	{
		Random r=new Random(128);
		float[] f=new float[d.length];
		for (int i=0;i < d.length;i++)
		{
			float n=r.nextFloat();
			f[i] = n;
			d.data[i] = n;
		}
		return f;
	}
}
