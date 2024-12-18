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

		// test text.

	}
	void a() throws Exception
	{

	}
	void testReshape() throws Exception
	{
		// reshape lazy fixed. it may have problems.
		Storage str=new Storage(3, 1, 2);

		fillRand(str);

		System.out.println(str);
		print(str);
		System.out.println("-----");
		str = str.broadcast(4, 3, 4, 2);
		str = str.get(2, 2);
		System.out.println(str);
		print(str);

		str = str.reshape(3, 1);
		System.out.println(str);
		print(str);


	}
	void testView() throws Exception
	{
		int[] sh={1,2,3,4,5};
		int[] sh2={1,1};

		Util.overlap(sh2, sh);

		System.out.println(Arrays.toString(sh));
		System.out.println(Arrays.toString(sh2));

		Storage str=new Storage(3, 1, 2);
		str = str.get(2);
		fillRand(str);
		System.out.println(str);
		print(str);
		str = str.view(2, 1);
		System.out.println(str);
		print(str);
	}
	void testCopyBroadcast() throws Exception
	{
		// changing broadcast to copy storage, rather than returning itself.
		Storage str1=new Storage(3, 1, 2);
		Storage str2=new Storage(3, 1, 2);

		fillRand(str1);
		fillRand(str2);

		System.out.println(str1);
		System.out.println(str2);
		System.out.println("-----");

		Storage st1=str1.broadcast(3, 5, 2);
		Storage st2=str2.broadcast(3, 5, 2); // str2.broadcast2(3, 5, 2);

		System.out.println(st1);
		System.out.println(st2);
		System.out.println("-----");
		System.out.println(str1);
		System.out.println(str2);
		System.out.println("----- equal with original -----");
		System.out.println("str1 == st1 " + (str1 == st1));
		System.out.println("str2 == st2 " + (str2 == st2));

		if (st1.length != st2.length)
			throw new Exception("changing broadcast method faild");
		for (int i=0;i < st1.length;i++)
		{
			if (st1.getFlat(i) != st2.getFlat(i))
				throw new Exception("data access on broadcasted shape error");
		}
		System.out.println("passed ✓");

	}
	void testSetSubDim() throws Exception
	{
		// ✓
		Storage str2=new Storage(3, 2);
		fillRand(str2);
		System.out.println(str2);
		print(str2);
		Storage str = str2.get(1);
		System.out.println(str);
		print(str);
		str.set(new int[]{}, 2);
		print(str);
		System.out.println("------");
		print(str2);

	}
	void testSetExact()
	{
		// ✓
		// setFlat() works // trust me :-)
		// if
		// set(int[],Storage) works
		// set(int[],float) works,
		// then setFlat work, because the above methods works because of setFlat();
		// .
		Storage str=new Storage(1, 2);
		print(str);
		str.setExact(new int[]{0,0}, 3);
		print(str);
		str.setExact(new int[]{0,1}, 5);
		print(str);


	}
	void testSetStr()
	{
		// the Storage view, reshape, broadcast  have umdetermined results.
		// so. it needs additional test.
		// atleast for now. ✓
		Storage str1=new Storage(4, 3, 5, 2); // or (3, 5, 2); also works.
		Storage str2=new Storage(3, 1, 2); // broadcasted from str1
		System.out.println("befor set");
		fillRand(str2);
		print(str1);

		System.out.println("after set");
		str1.set(new int[]{}, str2);
		print(str1);

	}
	void testSetBroad()
	{
		// ✓
		// remember set value on broadcasted shape have ba behaviour.
		// because when broadcasting we see small dims as bigger ones.
		// so changing one also make the smaller dims to change, that also chnage the overall array value.
		// for example see below.
		Storage str=new Storage(3, 1, 2);
		System.out.println("before set");
		print(str);
		System.out.println("-----");
		str.broadcast(3, 5, 2);
		str.set(new int[]{1,0}, 3); // this is the base set value. below set values are the same as this.
		// str.set(new int[]{1,1}, 3);  // same as new int[]{1,0}
		// str.set(new int[]{1,2}, 3);  // same as new int[]{1,0}
		// str.set(new int[]{1,3}, 3);  // same as new int[]{1,0}
		// str.set(new int[]{1,4}, 3);  // same as new int[]{1,0}
		// because of the broadcasted shape undrline value is (1) setting the shape to other value has no effect it is the same as setting the shape (1) at broadcasted index.
		// !! recomendation
		// copy before set value. it will fix.
		print(str);
		System.out.println("after set");

	}
	void testSetNoBrod()
	{
		// ✓
		Storage str=new Storage(3, 2, 5);
		str.set(new int[]{1,0}, 5);

		System.out.println(str);
		print(str);

	}
	void getFlatTest() throws Exception
	{
		// ✓
		Storage str=new Storage(3, 2, 1);

		fillRand(str);
		System.out.println(str);
		print(str);

		str = str.get(2);

		System.out.println(str);
		print(str);

		System.out.println(str);
		for (int i=0;i < str.length;i++)
			System.out.println(i + " = " + str.getFlat(i));

	}
}
