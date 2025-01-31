package gss2.test;

import gss2.math.*;
import java.util.*;

import gss2.math.Data;

public class Test1
{
	public static void test(String[] args)
	{

		new Test1().test();

	}
	void test()
	{

		test1();
		test2();
		test3();
		test4();
		test5();
		test6();
		test7();
		test8();
		test9();
		test10();
		test11();
		test12();
		test13();

	}
	void test13()
	{
		System.out.println("=== 13. Shape toArray test. ===");
		Shape s=new Shape(3, 1, 4);
		fillR(s.data);

		Shape s2=s.transpose();
		s2 = s2.broadcast(4, 2, 3);
		// System.out.println(s2.getFloat(0, 1));

		float[][][] itm=
		{
			{
				{ 0.74243414f, 0.39531714f, 0.53885365f, 0.06623876f}
			},

			{
				{ 0.63934743f, 0.22939426f, 0.41859204f, 0.75884575f}
			},

			{
				{ 0.21058547f, 0.84329474f, 0.20217377f, 0.05789256f}
			}
		};
		test(equals(itm, s), "Item equals");
		float[][][] itm2=
		{
			{
				{ 0.74243414f, 0.63934743f, 0.21058547f},
				{ 0.74243414f, 0.63934743f, 0.21058547f}
			},
			{
				{ 0.39531714f, 0.22939426f, 0.84329474f},
				{ 0.39531714f, 0.22939426f, 0.84329474f}
			},

			{
				{ 0.53885365f, 0.41859204f, 0.20217377f},
				{ 0.53885365f, 0.41859204f, 0.20217377f}
			},

			{
				{ 0.06623876f, 0.75884575f, 0.05789256f},
				{ 0.06623876f, 0.75884575f, 0.05789256f}
			}
		};
		// print(s2);
		test(equals(itm2, s2), "broadcasted item equals");
		float[][][][] itm3=
		{
			{
				{
					{ 0.74243414f, 0.63934743f, 0.21058547f},
					{ 0.74243414f, 0.63934743f, 0.21058547f}
				},
				{
					{ 0.39531714f, 0.22939426f, 0.84329474f},
					{ 0.39531714f, 0.22939426f, 0.84329474f}
				},

				{
					{ 0.53885365f, 0.41859204f, 0.20217377f},
					{ 0.53885365f, 0.41859204f, 0.20217377f}
				},

				{
					{ 0.06623876f, 0.75884575f, 0.05789256f},
					{ 0.06623876f, 0.75884575f, 0.05789256f}
				}
			},
			{}
		};
		itm3[1] = itm3[0];
		s2 = s2.broadcast(2, 4, 2, 3);
		test(equals(itm3, s2), "broadcasted item equals 2");
	}
	void test12()
	{
		System.out.println("=== 12. Shape get test. ===");
		Shape s=new Shape(3, 2, 4);
		fillR(s.data);

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

		test(s.getFloat(2) == 0.17359579f, "getFloat 1");
		test(s.getFloat(0, 1) == 0.63934743f, "getFloat 2");
		test(s.getFloat(1, 0, 2) == 0.20217377f, "getFloat 3");

		test(s.getFlat(1) == 0.39531714f, "getFlat 1");
		test(s.getFlat(15) == 0.8624616f, "getFlat 2");
		test(s.getFlat(22) == 0.14542317f, "getFlat 3");


	}
	void test11()
	{
		System.out.println("=== 11. Shape set test. ===");
		Shape s=new Shape(3, 2, 4);
		fillR(s.data);
		// print(s);
		// System.out.println("---------");
		s.set(new int[]{0}, 100);
		s.setExact(new int[]{1,1}, 50);
		s.setFlat(22, 90);
		// print(s);
		float[][][] itm=
		{
			{
				{100,100,100,100},
				{100,100,100,100}  // set(new int[]{0}, 100);
			},
			{
				{ 0.21058547f, 0.84329474f, 0.20217377f, 0.05789256f},
				{ 50, 0.6556215f, 0.7417879f, 0.8624616f} // s.setExact(new int[]{1,1}, 50)
			},

			{
				{0.17359579f, 0.37589586f, 0.47462994f, 0.35382342f},
				{ 0.6307474f, 0.3814925f, 90, 0.07341051f} // setFlat(22,90)
			}
		};
		test(equals(itm, s), "set methods");
	}
	void test10()
	{
		System.out.println("=== Test 10. broadcast. ===");
		Shape s=new Shape(3, 1, 1);
		fillR(s.data);

		float[][][] itm=
		{
			{
				{ 0.74243414f}
			},

			{
				{ 0.39531714f}
			},

			{
				{ 0.53885365f}
			}
		};
		test(equals(itm, s), "original item equals");

		// print(s);
		System.out.println("--------");
		s = s.broadcast(3, 2, 2);
		// System.out.println(s.getFloat(2, 1, 1));
		itm = new float[][][]
		{
			{
				{ 0.74243414f,0.74243414f },
				{ 0.74243414f,0.74243414f }
			},

			{
				{ 0.39531714f,0.39531714f },
				{ 0.39531714f,0.39531714f }
			},

			{
				{ 0.53885365f, 0.53885365f},
				{ 0.53885365f, 0.53885365f}
			}
		};
		// print(s);
		test(equals(itm, s.copy()), "broadcasted item equals");
		// s = s.broadcast(3, 2, 3);
		// s.fill(10);
		// print(s);
		// System.out.println(Arrays.toString(s.shape));
		float[]fl=Util.flatten(itm);
		for (int i=0;i < s.length;i++)
		{
			int[]sh=s.getShape(i);
			float t=fl[i];
			float v= s.getFloat(sh);
			float fv=s.getFlat(i);
			// System.out.println(t + ", " + v + ", " + fv);

			if (t != v || v != fv)
				throw new RuntimeException("test not passed, item not equals with getFlat");
		}
		System.out.println("✓ broadcasted item equals");
		System.out.println("------");
		// s = s.copy();
		// print(s);

	}
	void test9()
	{
		System.out.println("=== Test 9. get type inference. ===");
		Shape s=new Shape(3, 2, 4);
		fillR(s.data);

		test(s.getClass().equals(Shape.class), "first class test");
		Shape ss=s.get(0);
		test(ss.getClass().equals(Shape.class), "get class test");

		ss = s.view(6, 4);
		test(ss.getClass().equals(Shape.class), "view class test");


		ss = s.transpose();
		test(ss.getClass().equals(TShape.class), "transpose class test");

		ss = ss.view(8, 3);
		test(ss.getClass().equals(TVShape.class), "view transposed class test");

	}
	void test8()
	{
		System.out.println("=== Test 8. transposedView shape and sub transposedView test with subdim.===");

		Shape s=new Shape(3, 2, 4);
		fillR(s.data);

		test(Arrays.equals(s.shape, new int[]{3,2,4}), "shape equals");
		test(Arrays.equals(s.stride, new int[]{8,4,1}), "stride equals");

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

		System.out.println("======= after subdim transpose =======");
		// System.out.println(Arrays.toString(s.shape));
		// print(s);
		s = s.get(1);
		// print(s);
		// System.out.println(Arrays.toString(s.shape));
		s = s.transpose();
		// print(s);

		// System.out.println(Arrays.toString(s.shape));

		test(Arrays.equals(s.shape, new int[]{4,2}), "shape equals");
		test(Arrays.equals(s.stride, new int[]{1,4}), "stride equals");

		float[][]itm2= new float[][]
		{
			{0.21058547f,0.33518922f},
			{0.84329474f,0.6556215f},
			{0.20217377f, 0.7417879f},
			{0.05789256f,0.8624616f}
		};
		test(equals(itm2, s), "item equals");

		System.out.println("======= after subdim transpose and view =======");
		s = s.view(8);

		test(Arrays.equals(s.shape, new int[]{8}), "shape equals");
		test(Arrays.equals(s.stride, new int[]{1}), "stride equals");

		float[]itm3=
		{
			0.21058547f,0.33518922f,
			0.84329474f,0.6556215f,
			0.20217377f, 0.7417879f,
			0.05789256f,0.8624616f
		};
		test(equals(itm3, s), "item equals");

		// print(s);
		System.out.println("--- subdim transpose view completed! ---");

	}
	void test7()
	{
		System.out.println("=== Test 7. transposedView shape and sub transposedView test ===");
		Shape s=new Shape(3, 2, 4);
		fillR(s.data);

		test(Arrays.equals(s.shape, new int[]{3,2,4}), "shape equals");
		test(Arrays.equals(s.stride, new int[]{8,4,1}), "stride equals");

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

		// print(s);

		System.out.println("======= after transpose =======");
		s = s.transpose();

		// print(s);

		test(Arrays.equals(s.shape, new int[]{4,2,3}), "shape equals");
		test(Arrays.equals(s.stride, new int[]{1,4,8}), "stride equals");

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

		System.out.println("======= after transposed And view =======");

		TVShape tv = (TVShape)s.view(4, 6);

		test(Arrays.equals(tv.shape, new int[]{4,6}), "shape equals");
		test(Arrays.equals(tv.stride, new int[]{6,1}), "stride equals");
		test(Arrays.equals(tv.baseShape, new int[]{4,2,3}), "base shape equals");
		test(Arrays.equals(tv.baseStride, new int[]{1,4,8}), "base stride equals");

		float[][] itm2=
		{
			{
				0.74243414f, 0.21058547f, 0.17359579f,
				0.63934743f, 0.33518922f, 0.6307474f
			},
			{
				0.39531714f, 0.84329474f, 0.37589586f,
				0.22939426f, 0.6556215f, 0.3814925f
			},
			{
				0.53885365f, 0.20217377f, 0.47462994f,
				0.41859204f, 0.7417879f, 0.14542317f
			},
			{
				0.06623876f, 0.05789256f, 0.35382342f,
				0.75884575f, 0.8624616f, 0.07341051f
			}
		};

		// print(tv);

		test(equals(itm2, tv), "item equals");


	}
	void test6()
	{
		System.out.println("=== Test 6. view and sub view test ===");
		Shape s=new Shape(3, 2, 4);
		fillR(s.data);

		test(Arrays.equals(s.shape, new int[]{3,2,4}), "shape equals");
		test(Arrays.equals(s.stride, new int[]{8,4,1}), "stride equals");

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

		System.out.println("======= after view =======");

		System.out.println("------------ test 1 -----------");

		Shape o = s.view(6, 4);

		test(Arrays.equals(o.shape, new int[]{6,4}), "shape equals");
		test(Arrays.equals(o.stride, new int[]{4,1}), "stride equals");

		// print(s);
		float[][] itm2=
		{
			{0.74243414f, 0.39531714f, 0.53885365f, 0.06623876f},
			{ 0.63934743f, 0.22939426f, 0.41859204f, 0.75884575f},
			{ 0.21058547f, 0.84329474f, 0.20217377f, 0.05789256f},
			{ 0.33518922f, 0.6556215f, 0.7417879f, 0.8624616f},
			{0.17359579f, 0.37589586f, 0.47462994f, 0.35382342f},
			{ 0.6307474f, 0.3814925f, 0.14542317f, 0.07341051f}
		};

		test(equals(itm2, o), "item equals");

		System.out.println("------------ test 2 -----------");
		o = s.view(3, 8);

		test(Arrays.equals(o.shape, new int[]{3,8}), "shape equals");
		test(Arrays.equals(o.stride, new int[]{8,1}), "stride equals");

		// print(s);
		itm2 = new float[][] // 2d float array.
		{
			{0.74243414f, 0.39531714f, 0.53885365f, 0.06623876f,
				0.63934743f, 0.22939426f, 0.41859204f, 0.75884575f},
			{ 0.21058547f, 0.84329474f, 0.20217377f, 0.05789256f,
				0.33518922f, 0.6556215f, 0.7417879f, 0.8624616f},
			{0.17359579f, 0.37589586f, 0.47462994f, 0.35382342f,
				0.6307474f, 0.3814925f, 0.14542317f, 0.07341051f}
		};

		test(equals(itm2, o), "item equals");
		System.out.println("------------ test 3 -----------");
		o = s.view(24);

		test(Arrays.equals(o.shape, new int[]{24}), "shape equals");
		test(Arrays.equals(o.stride, new int[]{1}), "stride equals");

		// print(s);
		float[]itm3 = // 1d float array.
		{0.74243414f, 0.39531714f, 0.53885365f, 0.06623876f,
			0.63934743f, 0.22939426f, 0.41859204f, 0.75884575f,
			0.21058547f, 0.84329474f, 0.20217377f, 0.05789256f,
			0.33518922f, 0.6556215f, 0.7417879f, 0.8624616f,
			0.17359579f, 0.37589586f, 0.47462994f, 0.35382342f,
			0.6307474f, 0.3814925f, 0.14542317f, 0.07341051f
		};

		test(equals(itm3, o), "item equals");

		System.out.println("======= after view =======");

		s = s.get(1);

		o = s.view(8);

		test(Arrays.equals(o.shape, new int[]{8}), "shape equals");
		test(Arrays.equals(o.stride, new int[]{1}), "stride equals");

		// print(s);
		itm3 = new float[]
		{
			0.21058547f, 0.84329474f, 0.20217377f, 0.05789256f,
			0.33518922f, 0.6556215f, 0.7417879f, 0.8624616f
		};

		test(equals(itm3, o), "item equals");

	}
	void test5()
	{
		System.out.println("=== Test 5. Transpose and subdim transpose test ===");
		Shape s=new Shape(3, 2, 4);

		fillR(s.data);
		// System.out.println(Arrays.toString(s.base.getArray()));

		test(Arrays.equals(s.shape, new int[]{3,2,4}), "shape equals");
		test(Arrays.equals(s.stride, new int[]{8,4,1}), "stride equals");
		// test(Arrays.equals(s.acc, new int[]{0,1,2}), "access index");

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
		test(Arrays.equals(s.stride, new int[]{1,4,8}), "stride equals");
		// test(Arrays.equals(s.acc, new int[]{2,1,0}), "access index");
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

		s = s.get(1);

		test(Arrays.equals(s.shape, new int[]{2,3}), "shape equals"); // no change.
		test(Arrays.equals(s.stride, new int[]{4,8}), "stride equals"); // no chnage.
		// test(Arrays.equals(s.acc, new int[]{2,1,0}), "access index"); // no change.
		// test(Arrays.equals(s.shape, new int[]{2,3}), "shape subdim equals");

		float[][] itm2=itm[1];

		test(equals(itm2, s), "item equals");
		System.out.println("===== subdim transpose");

		s = s.transpose();

		test(Arrays.equals(s.shape, new int[]{3,2}), "shape equals");
		test(Arrays.equals(s.stride, new int[]{8,4}), "stride equals");
		// test(Arrays.equals(s.acc, new int[]{2,0,1}), "access index");
		// test(Arrays.equals(s.shape, new int[]{3,2}), "shape subdim equals");

		itm2 = new float[][]{
			{0.39531714f,0.22939426f},
			{0.84329474f, 0.6556215f},
			{0.37589586f,0.3814925f}
		};
		test(equals(itm2, s), "subdim transpose item equals");
		System.out.println("✓✓ Transpose test completed. ✓✓");

	}
	void test4()
	{
		System.out.println("===Test 4. transpose test ===");
		Shape s=new Shape(3, 2);
		float[] dt=fillR(s.data);
		// System.out.println(Arrays.toString(dt));
		for (int i=0;i < dt.length;i++)
			if (dt[i] != s.getFloat(s.getShape(i)))
				throw new AssertionError("scalar access error ");
		System.out.println("✓ scalar access without transpose");
		// test(Arrays.equals(s.bShape, new int[]{3,2}), "base Shape equals");
		test(Arrays.equals(s.shape, new int[]{3,2}), "shape equals");
		test(Arrays.equals(s.stride, new int[]{2,1}), "stride equals");
		// test(Arrays.equals(s.acc, new int[]{0,1}), "Index access equals");

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
		// System.out.println(Arrays.toString(s.stride));
		// System.out.println(Arrays.toString(s.acc));
		test(Arrays.equals(s.shape, new int[]{2,3}), "shape equals");
		test(Arrays.equals(s.stride, new int[]{1,2}), "stride equals"); // no change.
		// test(Arrays.equals(s.acc, new int[]{1,0}), "Index access equals");

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
	void transposeDisp()
	{
		Shape s=new Shape(3, 2);
		fillR(s.data);

		print(s);
		for (int i=0;i < s.length;i++)
			System.out.println("== " + Arrays.toString(s.getShape(i)));

		s = s.transpose();

		print(s);
		for (int i=0;i < s.length;i++)
			System.out.println("== " + Arrays.toString(s.getShape(i)));

		s = s.transpose();

		print(s);
		for (int i=0;i < s.length;i++)
			System.out.println("== " + Arrays.toString(s.getShape(i)));


	}
	void test3()
	{
		System.out.println("===Test 3. Shape getScalar and getShape test ===");
		Shape s=new Shape(3, 2);
		fillR(s.data);

		for (int i=0;i < s.length;i++)
		{
			int[] sh=s.getShape(i);
			test(s.getFloat(sh) == s.data.data[i], "get " + i);
		}
		System.out.println("--- getScalar test done! ---");
	}
	void test2()
	{
		System.out.println("=== Test 2. Default Shape test ===");

		int sh[]={2,3};
		Shape s=new Shape(2, 3);
		test(Arrays.equals(sh, s.shape), "equals shape");
		test(Arrays.equals(new int[]{3,1}, s.stride), "equal stride");
		test(s.shapeToIndex(0, 0) == 0, "access index 1");
		test(s.shapeToIndex(0, 2) == 2, "access index 2");
		test(s.shapeToIndex(1, 0) == 3, "access index 3");
		test(s.shapeToIndex(1, 2) == 5, "access index 4");
		System.out.println("--- creating default shape done! ---");
	}
	void test1()
	{
		System.out.println("=== Test 1. creating data class ===");
		Data d=new Data(2, 3);
		// System.out.println(d.length);
		// System.out.println(Arrays.toString(d.data));
		test(d.length == 6, "data length");
		test(Arrays.equals(d.data, new float[6]), "array equals");
		System.out.println("--- data create and initzialize ---");
	}
	public static void test(boolean b, Object msg)
	{
		if (b)
			System.out.println("✓ " + msg);
		else
			throw new RuntimeException("XXXXX  test not passed. >> " + msg);
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
	public static void print(Shape str)
	{
		// System.out.println("== "+str.shape.length);
		if (str.shape.length == 1)
		{
			System.out.print("[");
			for (int i=0;i < str.shape[0];i++)
				System.out.print(str.getFloat(i) + ", ");
			System.out.println("]");
		}
		else if (str.shape.length == 2)
		{
			System.out.print("[");
			for (int j=0;j < str.shape[0];j++)
			{
				System.out.print((j == 0 ?"": " ") + "[");
				for (int k=0;k < str.shape[1];k++)
					System.out.print((k == 0 ?" ": ", ") + str.getFloat(j, k));
				System.out.print(j == str.shape[0] - 1 ?"]": "]\n");
			}
			System.out.println("]");
		}
		else
		{
			System.out.print("[");
			for (int i=0;i < str.shape[0];i++)
			{
				print(str.get(i));
				if (i != str.shape[0] - 1)
					System.out.println();
			}
			System.out.println("]");
		}
	}
	public static boolean equals(float[] f, Shape s)
	{
		if (s.dim != 1)
		{
			// System.out.println("dim not equals");
			return false;
		}
		int[] sh=s.shape;
		if (f.length != sh[0])
			return false;
		for (int i=0;i < f.length;i++)
			if (f[i] != s.getFloat(i))
				return false;
		return true;
	}
	public static boolean equals(float[][] f, Shape s)
	{
		if (s.dim != 2)
		{
			// System.out.println("dim not equals");
			return false;
		}
		int[] sh=s.shape;
		if (f.length != sh[0] || f[0].length != sh[1])
		{
			// System.out.println("shape not equals");
			return false;
		}
		float[] a=Util.flatten(f);
		for (int i=0;i < s.length;i++)
			if (a[i] != s.getFlat(i))
				return false;
		return true;
	}
	public static boolean equals(float[][][] f, Shape s)
	{
		if (s.dim != 3)
		{
			System.out.println("dim not equals");
			return false;
		}
		int[] sh=s.shape;
		if (f.length != sh[0] || f[0].length != sh[1] || f[0][0].length != sh[2])
		{
			System.out.println(Arrays.toString(s.shape) + " != (" + f.length + ", " + f[0].length + ", " + f[0][0].length + ")");
			System.out.println("shape not equals");
			return false;
		}
		float[] a=Util.flatten(f);
		for (int i=0;i < s.length;i++)
			if (a[i] != s.getFlat(i))
				return false;
		return true;
	}
	public static boolean equals(float[][][][]f, Shape s)
	{
		if (s.dim != 4)
		{
			System.out.println("dim not equals");
			return false;
		}
		int[] sh=s.shape;
		if (f.length != sh[0] || f[0].length != sh[1] || f[0][0].length != sh[2] || f[0][0][0].length  != sh[3])
		{
			System.out.println(Arrays.toString(s.shape) + " != (" + f.length + ", " + f[0].length + ", " + f[0][0].length + ", " + f[0][0][0].length + ")");
			System.out.println("shape not equals");
			return false;
		}
		float[] a=Util.flatten(f);
		for (int i=0;i < s.length;i++)
			if (a[i] != s.getFlat(i))
				return false;
		return true;
	}
}
