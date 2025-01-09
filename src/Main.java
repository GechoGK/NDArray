import gss.math.*;
import java.util.*;

import static gss.math.Util.*;

public class Main
{
	public static void main2(String[] args) throws Exception
	{
		// to be broadcastable
		// if the org shape is (2,3,2)
		// and if the new shape (1,3,2) this is not broadcastable
		// because the items in shepe can be 1 , but only in original shope.
		// if the original shape items is not 1 then the new shape also must be the same items at partucular index.
		new Main().a();

	}
	void a() throws Exception
	{

		
	}
	void calcWithValueForwardAndBackward() throws Exception
	{
		// works.
		NDArray a1=NDArray.rand(new int[]{2, 5}, true); 
		NDArray a2=NDArray.rand(new int[]{5}, true);

		NDArray out=a1.mul(a2);

		out.setGrad(new int[]{}, 1);
		out.backward();

		printGrad(a1);
		System.out.println("-------------");
		printGrad(a2);
		System.out.println("-------------");
		printGrad(out);

		NDArray b1=NDArray.rand(new int[]{2,5}, true);
		NDArray b2=NDArray.rand(new int[]{5}, true);

		NDArray out2=b1.mul2(b2);

		out2.setGrad(new int[]{}, 1);
		out2.backward();
		// backTree(out2.storage.base.getValues());

		System.out.println("====== value equals =====");

		System.out.println(Util.equals(a1, b1));
		System.out.println(Util.equals(a2, b2));

		System.out.println(Util.equals(out, out2));

		System.out.println("------ grad equals ------");

		System.out.println(Util.equals(a1, b1, true));
		System.out.println(Util.equals(a2, b2, true));

		System.out.println(Util.equals(out, out2, true));


		System.out.println("==================");

		printGrad(b1);
		System.out.println("-------------");
		printGrad(b2);
		System.out.println("-------------");
		printGrad(out2);


	}
	void gradTet() throws Exception
	{
		NDArray arr1 = NDArray.rand(new int[]{3, 1, 2}, true);
		NDArray arr2 = NDArray.rand(new int[]{2, 2}, true);
		NDArray arr3= NDArray.value(new int[]{1}, 100);

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
		printGrad(rs.storage);
		System.out.println("rs1 & rs2 grad == rs1");
		printGrad(rs1.storage);
		System.out.println("------- == rs2");
		printGrad(rs2.storage);
		System.out.println("--- arr's gradient  == arr1");
		printGrad(arr1.storage);
		System.out.println("---- == arr2");
		printGrad(arr2.storage);
		System.out.println("---- == arr3");
		printGrad(arr3.storage);

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
			Value[] vls=arr.storage.base.getValues();
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
	void testBasicMathGrad() throws Exception
	{
		// gradient. adding methods ....
		NDArray arr1 = NDArray.rand(new int[]{3, 1, 2}, true);
		NDArray arr2 = NDArray.rand(new int[]{2, 2}, true);
		// uncomment the line below to check for errors.
		// arr2 = NDArray.rand(new int[]{4,2}, false);

		// add,sub,mul,div gradients added.
		// all basic operations have the same implementation.
		NDArray res=arr1.add(arr2);

		System.out.println(arr1);
		System.out.println(arr2);
		System.out.println(res);
		print(arr1.storage);
		System.out.println("---------");
		print(arr2.storage);
		System.out.println("---------");
		print(res.storage);
		System.out.println("-----");
		// System.out.println(Arrays.toString(res.storage.base.grads));

//		System.out.println(Arrays.toString(res.storage.base.grads));
//		System.out.println(Arrays.toString(arr1.storage.base.grads));
//		System.out.println(Arrays.toString(arr2.storage.base.grads));
//		System.out.println("after backward method called.");
		res.setGrad(new int[]{}, 2); // set the result gradient to 2.
		res.backward();

//		System.out.println(Arrays.toString(res.storage.base.grads));
//		System.out.println(Arrays.toString(arr1.storage.base.grads));
//		System.out.println(Arrays.toString(arr2.storage.base.grads));

	}
	void testArrayMathBasic() throws Exception
	{

		NDArray a1=new NDArray(new float[]{4,8,3,9,20,50,8,5,3,7});
		NDArray a2=new NDArray(new float[][]{{9},{5},{2},{3}});
		System.out.println("adding two arrays");
		print(a1.storage);
		System.out.println("  ::::: ");
		print(a2.storage);
		System.out.println("  ===== ");

		// addition.
		System.out.println("\naddition result");
		NDArray rs = a1.add(a2);
		print(rs.storage);

		// subtraction.
		System.out.println("\nsubtraction result");
		rs = a1.sub(a2);
		print(rs.storage);

		// division.
		System.out.println("\ndivision result");
		rs = a1.div(a2);
		print(rs.storage);

		// multiplucation.
		System.out.println("\nmultiplication result");
		rs = a1.mul(a2);
		print(rs.storage);

	}
	void testCommonBroadcastableShape() throws Exception
	{
		int[] sh1={6,2,3};
		sh1 = new int[]{1,4,2,8};
		sh1 = new int[]{9,6,3,4,5,7,2,120};
		int[] sh2={5,1,2,1}; // 1 in sh1 changes into 4.
		int[] newShape=NDArray.getCommonShape(sh1, sh2);

		/*
		 sh1  =        [1, 2, 3]
		 sh2  =     [5, 4, 2, 1] taking the broadcastable shape bdtween them.
		 newShape = [5, 4, 2, 3]
		 */

		System.out.println(" ==== " + Arrays.toString(newShape));
	}
}
