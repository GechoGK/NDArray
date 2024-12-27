import gss.math.*;
import java.util.*;

import static gss.math.Util.*;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		// to be broadcast
		// if the org shape is (2,3,2)
		// and if the new shape (1,3,2) this is not broadcastable
		// because th items in shepe can be 1 , but only in original shope.
		// if the original shape items is not 1 then the new shape also must be the same items at partucular index.
		new Main().a();

	}
	void a() throws Exception
	{

		gradTet();

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
		System.out.println("rs1 & rs2 grad");
		printGrad(rs1.storage);
		System.out.println("-------");
		printGrad(rs2.storage);
		System.out.println("--- arr's gradient");
		printGrad(arr1.storage);
		System.out.println("----");
		printGrad(arr2.storage);
		System.out.println("----");
		printGrad(arr3.storage);

	}
	void tree(NDArray arr, String t)
	{
		System.out.println(t + arr);
		if (arr.childs != null && arr.childs.size() != 0)
			for (NDArray ar:arr.childs)
				tree(ar, t.replace("_", " ").replace("|", " ") + "|_____ ");
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
