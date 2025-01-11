package test;

import gss.math.Data;
import gss.Storage;
import java.util.*;

public class Test
{
	public static void main(String[]args)
	{

//		test1();
//		test2();
//		test3();
//		test4();
		test5();

		// don't trust Storage.getStorage(...)  function
		// don't trust print function.

	}
	static void test5()
	{

		Storage s=new Storage(3, 2, 4);
		fillR(s.base);
		System.out.println(Arrays.toString(s.base.getArray()));
		
		print(s);
		for (int i=0;i < s.base.length;i++)
		{
			int[] sh=s.getShape(i);
			System.out.println(Arrays.toString(sh) + ", " + s.getScalar(sh));
		}
		s.transpose();
		print(s);
		s = s.getStorage(1);
		System.out.println(Arrays.toString(s.shape));
		System.out.println(Arrays.toString(s.bShape));
		System.out.println(Arrays.toString(s.sum));
		System.out.println(Arrays.toString(s.acc));
		System.out.println(s.position);
		print(s);
		s.transpose();
		System.out.println(Arrays.toString(s.shape));
		System.out.println(Arrays.toString(s.bShape));
		System.out.println(Arrays.toString(s.sum));
		System.out.println(Arrays.toString(s.acc));
		System.out.println(s.position);
		print(s);
		// System.out.println("== "+Arrays.toString(s.getShape(0)));
		for (int i=0;i < s.base.length;i++)
		{
			int[] sh=s.getShape(i);
			System.out.println(Arrays.toString(sh) + ", " + s.getScalar(sh));
		}

	}
	static void test4()
	{
		System.out.println("=== transpose test ===");
		Storage s=new Storage(3, 2);
		float[] dt=fillR(s.base);
		// System.out.println(Arrays.toString(dt));
		for (int i=0;i < dt.length;i++)
			if (dt[i] != s.getScalar(s.getShape(i)))
				throw new AssertionError("scalar access error ");
		System.out.println("✓ scalar access without transpose");
		// test(Arrays.equals(s.bShape, new int[]{3,2}), "base Shape equals");
		test(Arrays.equals(s.shape, new int[]{3,2}), "shape equals");
		test(Arrays.equals(s.sum, new int[]{2,1}), "sum equals");
		test(Arrays.equals(s.acc, new int[]{0,1}), "Index access equals");
		// System.out.println(Arrays.toString(s.bShape));
		// System.out.println(Arrays.toString(s.shape));
		// System.out.println(Arrays.toString(s.sum));
		// System.out.println(Arrays.toString(s.acc));
//		for (int i=0;i < s.base.length;i++)
//		{
//			int[] sh=s.getShape(i);
//			float f= s.getScalar(sh);
//			System.out.println(Arrays.toString(sh) + " = " + f);
//		}
//		print(s);
		System.out.println("... get shape for original(not transpose) ...");
		int[][] shp={
			{0,0},
			{0,1},
			{1,0},
			{1,1},
			{2,0},
			{2,1}
		};
		for (int i=0;i < shp.length;i++)
		{
			test(Arrays.equals(s.getShape(i), shp[i]), "getShape " + i);
		}
		// print(s);
		s.transpose();
		System.out.println("... after transpose ...");
		// test(Arrays.equals(s.bShape, new int[]{3,2}), "base Shape equals"); // no change.
		test(Arrays.equals(s.shape, new int[]{2,3}), "shape equals");
		test(Arrays.equals(s.sum, new int[]{2,1}), "sum equals"); // no change.
		test(Arrays.equals(s.acc, new int[]{1,0}), "Index access equals");

		System.out.println("... get shape for transpose ...");
		int[][] trShape={
			{0,0},
			{0,1},
			{0,2},
			{1,0},
			{1,1},
			{1,2}
		};
		for (int i=0;i < trShape.length;i++)
		{
			test(Arrays.equals(s.getShape(i), trShape[i]), "getShape " + i);
		}
		System.out.println("getShape for 2x3 completed");
		// print(s);
		float[] trAr={dt[0],dt[2],dt[4],dt[1],dt[3],dt[5]};
		// System.out.println(Arrays.toString(trAr));
		float[] ar=s.toArray();

		test(Arrays.equals(trAr, ar), "transpose equal");

	}
	static void test3()
	{
		System.out.println("=== Storage getScalar and getShape test ===");
		Storage s=new Storage(3, 2);
		fillR(s.base);

		for (int i=0;i < s.base.length;i++)
		{
			int[] sh=s.getShape(i);
			test(s.getScalar(sh) == s.base.getData(i), "get " + i);
		}

	}
	static void test2()
	{
		System.out.println("=== Storage shape to index test ===");
		int sh[]={2,3};
		Storage str=new Storage(sh); // shape.
		test(Arrays.equals(sh, str.shape), "equals shape");
		test(Arrays.equals(new int[]{3,1}, str.sum), "equal sum");
		test(str.shapeToIndex(0, 0) == 0, "access index 1");
		test(str.shapeToIndex(0, 2) == 2, "access index 2");
		test(str.shapeToIndex(1, 0) == 3, "access index 3");
		test(str.shapeToIndex(1, 2) == 5, "access index 4");

	}
	static void test1()
	{
		System.out.println("=== Test data ===");
		int[] sh={2,3};
		Data d=new Data(sh);
		test(d.length == sh[0] * sh[1], "data length");
		test(d.getArray().length == sh[0] * sh[1], "array length");
		test(d.requiresGrad == false, "no gradient");
		test(d.getGrads() == null, "no gradient");
		test(d.getValues() == null, "no gradient");
		test(d.dim == 2, "dimension");
		float[] fh={1,2,3,4,5,6};
		for (int i=0;i < fh.length;i++)
			d.setData(i, fh[i]);
		test(Arrays.equals(fh, d.getArray()), "fill equal");
		System.out.println("✓✓ data test passed ✓✓");
	}
	public static void test(boolean b, Object msg)
	{
		if (b)
			System.out.println("✓ " + msg);
		else System.out.println("X " + msg);
	}
	static float[] fillR(Data d)
	{
		Random r=new Random(128);
		float[] f=new float[d.length];
		for (int i=0;i < d.length;i++)
		{
			float n=r.nextFloat();
			f[i] = n;
			d.setData(i, n);
		}
		return f;
	}
	public static void print(float[] ar, int[]sh)
	{
		Storage s=new Storage(ar, sh);
		print(s);
	}
	public static void print(Storage str)
	{
		if (str.dim == 1)
		{
			System.out.print("[");
			for (int i=0;i < str.shape[str.position];i++)
				System.out.print(str.getScalar(i) + ", ");
			System.out.println("]");
		}
		else if (str.dim == 2)
		{
			System.out.print("[");
			for (int j=0;j < str.shape[str.position];j++)
			{
				System.out.print((j == 0 ?"": " ") + "[");
				for (int k=0;k < str.shape[str.position + 1];k++)
					System.out.print((k == 0 ?" ": ", ") + str.getScalar(j, k));
				System.out.print(j == str.shape[str.position] - 1 ?"]": "]\n");
			}
			System.out.println("]");
		}
		else
		{
			System.out.print("[");
			for (int i=0;i < str.shape[str.position];i++)
			{
				print(str.getStorage(i));
				if (i != str.shape[str.position] - 1)
					System.out.println();
			}
			System.out.println("]");
		}
	}
}
