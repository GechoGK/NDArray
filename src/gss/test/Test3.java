package gss.test;

import gss.arr.*;
import java.util.*;

import static gss.math.Util.*;

public class Test3
{
	public static void main(String[]args) throws Exception
	{

		new Test3().test();

	}
	void test() throws Exception
    {

		a();

	}
	void a()
    {
		print("Test 3. new dot product with backpropagation");
		NDArray a=NDIO.fromArray(new int[]{2, 5}, range(60f)).setEnableGradient(true);
		NDArray b=NDIO.fromArray(new int[]{2, 5, 3}, range(90f)).setEnableGradient(true);

		NDArray c=a.dot3(b);
		print("=== " + Arrays.toString(c.getShape()));
		print(c);

		c.setGrad(new int[]{}, 1);
		c.backward();

		print("=====  gradient  =====");
		printGrad(c);
		System.out.println("-----");
		printGrad(a);
		print("----");
		printGrad(b);

	}
	void test2()
	{
		print("Test 2. arange and range utils");
		print("float ange");
		for (float i=1;i < 10;i += .5f)
		{
			float[] f=range(0f, 20, i);
			print(i + ":= " + Arrays.toString(f));

		}
		print("int range");
		for (int i=1;i < 10;i++)
		{
			int[] ar=range(0, 20, i);
			print(i + ":= " + Arrays.toString(ar));
		}
		print("ndarray arange");
		NDArray ar=NDIO.arange(1, 10, .5f);
		print(ar.getLength() + " = " + ar);
		ar = ar.reshape(-1, 6);
		print(ar);
	}
	void test1()
	{
		// new type of dot product.
		print("Test 1. new dot product without backpropagation");
		NDArray a=NDIO.fromArray(new int[]{2, 5}, range(10f));
		NDArray b=NDIO.fromArray(new int[]{4, 5, 3}, range(60f));

		NDArray c=a.dot3(b);
		print("dot product performed");
		print(a);
		print("----");
		print(b);
		print("------");
		print("=== " + Arrays.toString(c.getShape()));
		print(c);
//		print("gradient");
//
//		printGrad(c);
//		System.out.println("-----");
//		printGrad(a);
//		print("----");
//		printGrad(b);


	}
	void drawGrad(NDArray ar, String t)
	{
		System.out.println(t + ar.gradientFunction);
		if (ar.childs != null)
			for (NDArray a:ar.childs)
				drawGrad(a, t + "   ");

	}
}
