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
	void a()
	{
		// gradient. adding methods ....
		NDArray arr=new NDArray(new int[]{3, 1, 2}, true);

		System.out.println(arr);
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

		// sibtraction.
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
