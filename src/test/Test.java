package test;

import gss.math.Data;
import gss.Storage;
import java.util.*;

public class Test
{
	public static void main2(String[]args)
	{

		// test();
		test6();

	}
	static void test()
	{
		test1();
		test2();
		test3();
		test4();
		test5();
		test6();

	}
	static void test6()
	{
		System.out.println("=== Test 6.view test ===");

		Storage s=new Storage(3, 2, 4);
		fillR(s.base);
		System.out.println("original");
		print(s);

		System.out.println("\nTransposed");
		s = s.transpose(2, 1, 0);
		// ...      ...(0,
		print(s);

		s.view(new int[]{8,3}, 1,0,2);

		print(s);


	}
	static void test5()
	{
		System.out.println("=== Test 5. Transpose and subdim transpose test ===");
		Storage s=new Storage(3, 2, 4);
		fillR(s.base);
		// System.out.println(Arrays.toString(s.base.getArray()));

		test(Arrays.equals(s.shape, new int[]{3,2,4}), "shape equals");
		test(Arrays.equals(s.sum, new int[]{8,4,1}), "sum equals");
		test(Arrays.equals(s.acc, new int[]{0,1,2}), "access index");

		// print(s);
		float[][][] itm=
		{
			{
				{0.74243414f, 0.39531714f, 0.53885365f, 0.06623876f},
				{ 0.63934743f, 0.22939426f, 0.41859204f, 0.75884575f}
			},
			{
				{ 0.21058547f, 0.84329474f, 0.20217377f, 0.05789256f},
				{ 0.33518922f, 0.6556215f, 0.7417879f, 0.8624616f}
			},

			{
				{0.17359579f, 0.37589586f, 0.47462994f, 0.35382342f},
				{ 0.6307474f, 0.3814925f, 0.14542317f, 0.07341051f}
			}
		};

		test(equals(itm, s), "item equals");



		System.out.println("======= after transpose =======");

		s = s.transpose(2, 1, 0);
		test(Arrays.equals(s.shape, new int[]{4,2,3}), "shape equals");
		test(Arrays.equals(s.sum, new int[]{8,4,1}), "sum equals");
		test(Arrays.equals(s.acc, new int[]{2,1,0}), "access index");
		itm = new float[][][]
		{
			{
				{ 0.74243414f, 0.21058547f, 0.17359579f},
				{0.63934743f, 0.33518922f, 0.6307474f}
			},

			{
				{ 0.39531714f, 0.84329474f, 0.37589586f},
				{0.22939426f, 0.6556215f, 0.3814925f}
			},

			{
				{ 0.53885365f, 0.20217377f, 0.47462994f},
				{ 0.41859204f, 0.7417879f, 0.14542317f}
			},

			{
				{ 0.06623876f, 0.05789256f, 0.35382342f},
				{ 0.75884575f, 0.8624616f, 0.07341051f}
			}
		};
		test(equals(itm, s), "item equals");
		// print(s);

		System.out.println("===== subdim =====");

		s = s.getStorage(1);

		test(Arrays.equals(s.shape, new int[]{4,2,3}), "shape equals"); // no change.
		test(Arrays.equals(s.sum, new int[]{8,4,1}), "sum equals"); // no chnage.
		test(Arrays.equals(s.acc, new int[]{2,1,0}), "access index"); // no change.
		test(Arrays.equals(s.getShape(), new int[]{2,3}), "shape subdim equals");

		float[][] itm2=itm[1];

		test(equals(itm2, s), "item equals");
		System.out.println("===== subdim transpose");

		s = s.transpose();

		test(Arrays.equals(s.shape, new int[]{4,3,2}), "shape equals"); // no change.
		test(Arrays.equals(s.sum, new int[]{8,4,1}), "sum equals"); // no chnage.
		test(Arrays.equals(s.acc, new int[]{2,0,1}), "access index"); // no change.
		test(Arrays.equals(s.getShape(), new int[]{3,2}), "shape subdim equals");

		itm2 = new float[][]{
			{0.39531714f,0.22939426f},
			{0.84329474f, 0.6556215f},
			{0.37589586f,0.3814925f}
		};
		test(equals(itm2, s), "subdim transpose item equals");
		System.out.println("✓✓ Transpose test completed. ✓✓");

	}
	static void test4()
	{
		System.out.println("===Test 4. transpose test ===");
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
		s = s.transpose();
		System.out.println("... after transpose ...");
		// test(Arrays.equals(s.bShape, new int[]{3,2}), "base Shape equals"); // no change.
		// System.out.println(Arrays.toString(s.shape));
		// System.out.println(Arrays.toString(s.sum));
		// System.out.println(Arrays.toString(s.acc));
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
		System.out.println("===Test 3. Storage getScalar and getShape test ===");
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
		System.out.println("===Test 2. Storage shape to index test ===");
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
		System.out.println("===Test 1. Test data ===");
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
	public static boolean equals(float[] f, Storage s)
	{
		if (s.dim != 1)
		{
			// System.out.println("dim not equals");
			return false;
		}
		int[] sh=s.getShape();
		if (f.length != sh[0])
			return false;
		for (int i=0;i < f.length;i++)
			if (f[i] != s.getScalar(i))
				return false;
		return true;
	}
	public static boolean equals(float[][] f, Storage s)
	{
		if (s.dim != 2)
		{
			// System.out.println("dim not equals");
			return false;
		}
		int[] sh=s.getShape();
		if (f.length != sh[0] || f[0].length != sh[1])
		{
			// System.out.println("shape not equals");
			return false;
		}
		for (int i=0;i < sh[0];i++)
			for (int j=0;j < sh[1];j++)
				if (f[i][j] != s.getScalar(i, j))
					return false;
		return true;
	}
	public static boolean equals(float[][][] f, Storage s)
	{
		if (s.dim != 3)
		{
			// System.out.println("dim not equals");
			return false;
		}
		for (int i=0;i < s.shape[0];i++)
			if (!equals(f[i], s.getStorage(i)))
				return false;
		return true;
	}
}
