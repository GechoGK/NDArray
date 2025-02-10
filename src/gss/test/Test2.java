package gss.test;

import gss.arr.*;
import gss.math.*;
import java.util.*;

import static gss.math.Util.*;

public class Test2
{
	public static void main2(String[]args) throws Exception
    {

		new Test2().test();

	}
	void test() throws Exception
    {

		Test1.test(null);
		test1();
		test2();
		test3(); // !!!!!!  works.
		test4(); // !!!!!!  works.
		test5(); // !!!!!!  works.
		test6();
		test7();
		test8();
		test9();
		test10();
		test11();
		a();

	}
	void a()
    {
		System.out.println("Test finished.");
	}
	void test11()
	{
		System.out.println("=== Test 11. test dot product with backpropagation ===");

		NDArray a1=NDIO.fromArray(new int[]{2,4}, new float[]{1,2,3,4,5,6,7,8}).setEnableGradient(true);
		NDArray a2=NDIO.fromArray(new int[]{4,3}, new float[]{10,11,12,13,14,15,16,17,18,19,20,21}).setEnableGradient(true);

		NDArray a3=a1.dot(a2);

		NDArray b1=a1.copy();
		NDArray b2=a2.copy();

		NDArray b3=b1.dot(b2);

		Test1.test(Util.equals(a1, b1), "dot product with float and value");

		a3.setGrad(new int[]{}, 1);
		b3.setGrad(new int[]{}, 1);

		a3.backward();
		b3.backward();

		// printGrad(a1);
		// printGrad(a2);
		// printGrad(b1);
		// printGrad(b2);

		Test1.test(Util.equals(a1, b1, true), "dot product gradient with float and value");

	}
	void test10()
    {
		System.out.println("=== Test 10. test dot product (no backpropagation)===");
		System.out.println("this test is only for Value class, may be it doesn't give accurate results");
		System.out.println("preparing...");
		NDArray a1=NDIO.rand(new int[]{5,10}).setEnableGradient(true);
		NDArray a2=NDIO.rand(new int[]{10,8}).setEnableGradient(true);
		System.out.println("prepared");
		NDArray a3=null;
		// while (new Boolean(true))
		{
			float tm=0;
			int cnt=10;
			// for (int i=0;i < cnt;i++)
			{
				long l=System.currentTimeMillis();
				a3 = a1.dot(a2);
				l = System.currentTimeMillis() - l;
				tm += l;
			}
			System.out.println((tm / cnt) + " millis to calculate dot product");
		}
		if (1 == 1)
        {
			print(a1);
			System.out.println("-----");
			print(a2);
			System.out.println("---- dot product ----");
			print(a3);
			System.out.println("----- Gradients before backward pass.-----");
			System.out.println("====  out  ====");
			printGrad(a3);
			System.out.println("====  a  ====");
			printGrad(a1);
			System.out.println("====  b  ====");
			printGrad(a2);
			System.out.println("-----------------------");
			a3.setGrad(new int[]{}, 1);
			// drawTree(a3);
			a3.backward();
			System.out.println("----- Gradients after backward pass.-----");
			System.out.println("====  out  ====");
			printGrad(a3);
			System.out.println("====  a  ====");
			printGrad(a1);
			System.out.println("====  b  ====");
			printGrad(a2);
			System.out.println("-----------------------");
		}
	}
	void drawTree(NDArray ar)
    {
		Value[] vs=ar.base.data.getValues();
		if (vs == null)
        {
			System.out.println("no tree to display");
			return;
		}
		for (Value v:vs)
			draw(v, "");
	}
	void draw(Value v, String t)
    {
		System.out.println(t + v);
		if (v.args != null)
			for (Value vv:v.args)
				draw(vv, t + "    ");
	}
	void test9()
    {
		System.out.println("=== Test 9. test dot product (no backpropagation)===");

		NDArray a1=NDIO.fromArray(new int[]{2, 4}, new float[]{1,2,3,4,5,6,7,8});
		NDArray a2=NDIO.fromArray(new int[]{4,2}, new float[]{3,4,5,6,7,8,9,10});

		NDArray a3=a1.dot(a2);
		// print(a3);
		// System.out.println(Arrays.toString(a3.getShape()));
		float[][]r=
		{
			{70.0f, 80.0f},
			{166.0f, 192.0f}
		};
		Test1.test(Test1.equals(r, a3.base), " dot product item equals");
	}
	void test8()
    {
		System.out.println("=== Test 8. test to2DArray. ===");
		Shape s=new Shape(3, 2, 4);
		s = s.view(-1, 4);
		fillRand(s);
		float[][] f=s.to2DArray(null);
		Test1.test(Test1.equals(f, s), "to2DArray item equals.");
	}
	void test7()
    {
		System.out.println("=== Test 7. shape fill -1 values. ===");
		Shape s=new Shape(3, 2, 4);

		// System.out.println("shape =" + Arrays.toString(s.shape));

		int[] sh=s.getShape(3, 2, 4);
		// System.out.println(Arrays.toString(sh));
		Test1.test(Arrays.equals(sh, new int[]{3,2,4}), "array fill equals 1");

		sh = s.getShape(4, -1); // or new int[]{4,-1}
		// System.out.println(Arrays.toString(sh));
		Test1.test(Arrays.equals(sh, new int[]{4,6}), "array fill equals 2");

		sh = s.getShape(2, 2, 2, -1); // or new int[]{2,2,2,-1}
		// System.out.println(Arrays.toString(sh));
		Test1.test(Arrays.equals(sh, new int[]{2,2,2,3}), "array fill equals 3");

		sh = s.getShape(new int[]{-1});
		// System.out.println(Arrays.toString(sh));
		Test1.test(Arrays.equals(sh, new int[]{24}), "array fill equals 4");


	}
	void test6() throws Exception
    {
		System.out.println("=== Test 6. Gradient calcator for hStack and vStack. ===");

		NDArray arr2=NDIO.rand(6, 2, 4).setEnableGradient(true);

		// print(arr2);
		// System.out.println("------------");
		// printGrad(arr2);
		NDArray arr = arr2.vStack();
		// System.out.println("gradient =" + arr.requiresGradient());
		// print(arr);
		arr.setGrad(new int[]{}, 1);
		arr.backward();
		// printGrad(arr);
		// printGrad(arr2);
		Test1.test(Arrays.equals(arr.base.data.getGrads(), arr2.base.data.getGrads()), "vStack backward gradient");
		arr2.base.data.zeroGrad();
		arr = arr2.hStack();
		arr.setGrad(new int[]{}, 1);
		arr.backward();
		// printGrad(arr2);

		Test1.test(Arrays.equals(arr.base.data.getGrads(), arr2.base.data.getGrads()), "hStack backward gradient");


	}
	void test5() throws Exception
    {
		System.out.println("=== Test 5. Gradient calculator on multiplication using raw Values. ===");
		// works.
		NDArray a1=NDIO.rand(new int[]{2, 5}, true); 
		NDArray a2=NDIO.rand(new int[]{5}, true);

		NDArray out=a1.mul(a2);

		out.setGrad(new int[]{}, 1);
		out.backward();

		printGrad(a1);
		System.out.println("-------------");
		printGrad(a2);
		System.out.println("-------------");
		printGrad(out);

		NDArray b1=NDIO.rand(new int[]{2,5}, true);
		NDArray b2=NDIO.rand(new int[]{5}, true);

//		NDArray out2=b1.mul2(b2);
//
//		out2.setGrad(new int[]{}, 1);
//		out2.backward();
//		// backTree(out2.storage.base.getValues());
//
//		System.out.println("====== value equals =====");
//
//		System.out.println(Util.equals(a1, b1));
//		System.out.println(Util.equals(a2, b2));
//
//		System.out.println(Util.equals(out, out2));
//
//		System.out.println("------ grad equals ------");
//
//		System.out.println(Util.equals(a1, b1, true));
//		System.out.println(Util.equals(a2, b2, true));
//
//		System.out.println(Util.equals(out, out2, true));
//
//
//		System.out.println("==================");
//
//		printGrad(b1);
//		System.out.println("-------------");
//		printGrad(b2);
//		System.out.println("-------------");
//		printGrad(out2);
//
//
	}
	void test4() throws Exception
    {
		System.out.println("=== Test 4. Gradient calculator 2 on addition ===");

		NDArray arr1 = NDIO.rand(new int[]{3, 1, 2}, true);
		NDArray arr2 = NDIO.rand(new int[]{2, 2}, true);
		NDArray arr3= NDIO.value(new int[]{1}, 100);

		NDArray rs1=arr1.add(arr2);
		NDArray rs2=arr3.add(arr2);

		NDArray rs=rs1.add(rs2);

		System.out.println(arr1);
		System.out.println(arr2);
		System.out.println(arr3);
		System.out.println(rs1);
		System.out.println("------- gradients -------");

		rs.setGrad(new int[]{}, 2);
		rs.backward();
		System.out.println("result grad");
		printGrad(rs.base);
		System.out.println("rs1 & rs2 grad == rs1");
		printGrad(rs1.base);
		System.out.println("------- == rs2");
		printGrad(rs2.base);
		System.out.println("--- arr's gradient  == arr1");
		printGrad(arr1.base);
		System.out.println("---- == arr2");
		printGrad(arr2.base);
		System.out.println("---- == arr3");
		printGrad(arr3.base);

	}
	void backTree(Value[] vls)
    {
		System.out.println("backing tree");
		if (vls == null || vls.length == 0)
        {
			System.out.println("empty values");
			return;
		}
		HashSet<Value> tmpLst=new HashSet<>();
		HashSet<Value> lst=new HashSet<>();
		lst.addAll(Arrays.asList(vls));
		while (lst.size() != 0)
        {
			System.out.println(lst.size() + " items to backward");
			for (Value v:lst)
            {
				v.backward();
				tmpLst.addAll(v.args);
			}
			lst.clear();
			lst.addAll(tmpLst);
			tmpLst.clear();
		}
	}
	void tree(NDArray arr, String t)
    {
		System.out.println(t + arr);
		if (arr.gradientFunction == GradFunc.itemGradient)
        {
			System.out.println(t + "listing child gradient");
			Value[] vls=arr.base.data.getValues();
			for (Value v:vls)
				treeV(v, t);
		}
		if (arr.childs != null && arr.childs.size() != 0)
			for (NDArray ar:arr.childs)
				tree(ar, t.replace("_", " ").replace("|", " ") + "|_____ ");
	}
	void treeV(Value vl, String t)
    {
		System.out.println(t + vl);
		if (vl.args != null && vl.args.size() != 0)
			for (Value  vv:vl.args)
				treeV(vv, t.replace("_", " ").replace("|", " ") + "|_____ ");
	}
	void test3() throws Exception
    {
		System.out.println("=== Test 3. Gradient calculator on addition ===");
		// gradient. adding methods ....
		NDArray arr1 = NDIO.rand(new int[]{3, 1, 2}, true);
		NDArray arr2 = NDIO.rand(new int[]{2, 2}, true);
		// uncomment the line below to check for errors.
		// arr2 = NDArray.rand(new int[]{4,2}, false);

		// add,sub,mul,div gradients added.
		// all basic operations have the same implementation.
		NDArray res=arr1.add(arr2);

		System.out.println(arr1);
		print(arr1.base);
		System.out.println("---------");
		System.out.println(arr2);
		print(arr2.base);
		System.out.println("---------");
		System.out.println(res);
		print(res.base);
		System.out.println("-----");
		// System.out.println(Arrays.toString(res.storage.base.grads));

//		System.out.println(Arrays.toString(res.storage.base.grads));
//		System.out.println(Arrays.toString(arr1.storage.base.grads));
//		System.out.println(Arrays.toString(arr2.storage.base.grads));
//		System.out.println("after backward method called.");
		res.setGrad(new int[]{}, 2); // set the result gradient to 2.
		res.backward();

		System.out.println(Arrays.toString(res.base.data.getGrads()));
		System.out.println(Arrays.toString(arr1.base.data.getGrads()));
		System.out.println(Arrays.toString(arr2.base.data.getGrads()));

	}
	void test2() throws Exception
    {
		System.out.println("=== Test 2. NDArray basic math ===");

		NDArray a1=new NDArray(new float[]{4,8,3,9,20,50,8,5,3,7});
		NDArray a2=new NDArray(new float[][]{{9},{5},{2},{3}});
		System.out.println("adding two arrays");
		print(a1.base);
		System.out.println("  ::::: ");
		print(a2.base);
		System.out.println("  ===== ");

		// addition.
		System.out.println("\naddition result");
		NDArray rs = a1.add(a2);
		print(rs.base);

		// subtraction.
		System.out.println("\nsubtraction result");
		rs = a1.sub(a2);
		print(rs.base);

		// division.
		System.out.println("\ndivision result");
		rs = a1.div(a2);
		print(rs.base);

		// multiplucation.
		System.out.println("\nmultiplication result");
		rs = a1.mul(a2);
		print(rs.base);

	}
	void test1() throws Exception
    {
		System.out.println("=== Test 1. test common broadcast shape ===");

		int[] sh2={5,1,2,1}; // 1 in sh1 changes into 4.
		int[] sh1={6,2,3};
		int[] newShape=NDArray.getCommonShape(sh1, sh2);
		Test1.test(Arrays.equals(newShape, new int[]{5,6,2,3}), "common shape 1 equals");
		sh1 = new int[]{1,4,2,8};
		newShape = NDArray.getCommonShape(sh1, sh2);
		Test1.test(Arrays.equals(newShape, new int[]{5,4,2,8}), "common shape 2 equals");
		sh1 = new int[]{9,6,3,4,5,7,2,120};
		newShape = NDArray.getCommonShape(sh1, sh2);
		Test1.test(Arrays.equals(newShape, new int[]{9,6,3,4,5,7,2,120}), "common shape 3 equals");

		/*
		 sh1  =        [1, 2, 3]
		 sh2  =     [5, 4, 2, 1] taking the broadcastable shape bdtween them.
		 newShape = [5, 4, 2, 3]
		 */

	}
}
