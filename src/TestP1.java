import gss.math.*;
import java.util.*;

import static gss.math.Util.*;

public class TestP1
{
	void a() throws Exception
	{

		testBrodSubDim();

	}
	void testOverlap() throws Exception
	{

		int[] i1={2,2}; // orig shape
		int[] i2={2}; // get(0); 2[2] baseShape =[1,2]
		int[] i3={4,5,2};

		Util.overlap(i2, i3);

		System.out.println(Arrays.toString(i1));
		System.out.println(Arrays.toString(i2));
		System.out.println(Arrays.toString(i3));

	}
	void testBrodSubDim()throws Exception
	{
		// isBroadcasted shape. subdim faild.
		Storage str=new Storage(2, 1);
		fillRand(str);
		System.out.println(str);
		print(str);
		System.out.println("--------");
		str = str.get(1);
		System.out.println(str);
		print(str);
		System.out.println("--------");
		str = str.broadcast(3, 4, 2);
		System.out.println(str);
		print(str);
		System.out.println("------");
		System.out.println("[");
		for (int i=0;i < str.length;i++)
			System.out.print(str.getFlat(i) + ", ");
		System.out.println("]");

	}
	void errorView() throws Exception
	{
		// ✓
		Storage str=new Storage(4, 2, 3);
		fillRand(str);
		// System.out.println(str);
		// System.out.println(str.length);
		// print(str);
		System.out.println("---------");
		Storage strd1= str.get(1);
		print(strd1);
		System.out.print("== ");
		for (int i=0;i < strd1.length;i++)
			System.out.print(strd1.getFlat(i) + ", ");
		System.out.println();
		Storage str2=strd1.view(strd1.length);
		System.out.println(str2);
		System.out.println(str2.length);
		print(str2);
		System.out.print("== ");
		for (int i=0;i < str2.length;i++)
			System.out.print(str2.getFlat(i) + ", ");
		System.out.println();
		// strd1 and str2  should be equal, but they are not.

	}
	void testReshape() throws Exception
	{
		// reshape lazy fixed. it may have problems. for now ✓
		Storage str=new Storage(3, 1, 2);

		fillRand(str);

		System.out.println(str);
		print(str);
		System.out.println("-----");
		str = str.broadcast(2, 3, 4, 2);
		System.out.println(str);
		print(str);
		System.out.println("----");
		str = str.get(1, 2);
		System.out.println(str);
		print(str);

		str = str.reshape(2, 4);
		System.out.println(str);
		print(str);


	}
	void testOverlap2() throws Exception
	{
		int[] sh={1,2,3,4,5};
		int[] sh2={1,1};

		Util.overlap(sh2, sh);

		System.out.println(Arrays.toString(sh));
		System.out.println(Arrays.toString(sh2));
	}
	void testView() throws Exception
	{
		Storage str2=new Storage(3, 1, 2);

		Storage str = str2.get(2);
		fillRand(str);
		System.out.println(str);
		print(str);
		str = str.view(2, 1);
		System.out.println(str);
		print(str);
		System.out.println("----");
		System.out.println(str2);
		print(str2);
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
	void reshapeTest() throws Exception
	{
		Storage str=new Storage(3, 2, 5);

		fillRand(str);

		System.out.println(str);
		print(str);

		str.view(2, 5, 3);
		System.out.println(str);
		print(str);


	}
	void copyTest() throws Exception
	{
		Storage str=new Storage(3, 1, 2);
		fillRand(str);
		System.out.println(str);
		print(str);
		System.out.println("------");
		str.broadcast(3, 5, 2);
		System.out.println(str);
		print(str);

		str = str.get(1);
		str = str.copy();
		System.out.println(str);
		print(str);

	}
	void getStrAndGetFloatTest() throws Exception
	{
		// works
		tstBrdGet();

		System.out.println("--------------------");

		Storage str=new Storage(3, 2, 1);
		fillRand(str);
		// System.out.println("== " + Arrays.toString(str.base.values));
		System.out.println(str);
		print(str);
		str.broadcast(2, 3, 2, 5);	
		System.out.println(str);
		print(str);
		System.out.println("=======");
		str = str.get(0, 2, 0);
		System.out.println(str);
		print(str);


	}
	void tstBrdGet()
	{
		// problem.

		Storage str=new Storage(3, 3, 1);
		fillRand(str);
		// System.out.println(Arrays.toString(str.base.values));
		System.out.println(str);
		print(str);
		System.out.println("=======");
		for (int i=0;i < str.length;i++)
			System.out.println(str.get(str.getShape(i)).getFlat(0));

		System.out.println("==.... ");
		str = str.broadcast(3, 3, 3);
		print(str);
		System.out.println(str);
		// System.out.println(Arrays.toString(str.base.values));
		for (int i=0;i < str.length;i++)
			System.out.println(i + " = " + str.get(str.getShape(i)).getFlat(0));
	}
	void storageViewTest() throws Exception
	{
		Storage str=new Storage(3, 3, 2);
		fillRand(str);
		System.out.println(str);
		print(str);
		System.out.println("-------------");
		str.view(3,  2 , 3); // by modifieng the base class we can achieve view. but modifieng the base is not neccesary.
		System.out.println(str);
		print(str);

	}
	void arrTest() throws Exception
	{
		// Util.getAt(arr,index) and getAtR(arr,index) test.
		float[] arr={1,2};
		System.out.println("forward access");
		System.out.println(0 + ", " + getAt(arr,  0));
		System.out.println(1 + ", " + getAt(arr,  1));
		System.out.println("backward access");
		System.out.println(-1 + " " + getAt(arr,  -1));
		System.out.println(-2 + ", " + getAt(arr,  -2));
		System.out.println("backward access method 2");
		System.out.println(0 + ", " + getAtR(arr,  0));
		System.out.println(1 + ", " + getAtR(arr,  1));

	}
	void getFlatBench() throws Exception
	{
		System.out.println("getFlat() benchmark.");

		Storage str=new Storage(3, 10, 5);
		for (;;)
		{
			long c=0;
			for (int k=0;k < 20;k++)
			{
				long l=System.currentTimeMillis();
				for (int i=0;i < 10000;i++)
				{
					str.getFlat(i % str.length);
				}
				l = System.currentTimeMillis() - l;
				c += l;
			}
			c = c / 10;
			System.out.println(c + " millis in average");
		}

	}
	void storageGetFlatTest() throws Exception
	{
		// works good.
		Storage str=new Storage(3, 1, 2);
		System.out.println(str);
		// System.out.println("== " + str.base.values.length);
		// fill data to the array.
//		for (int i=0;i < str.base.values.length;i++)
//			str.base.values[i] = i + 1;

		str.broadcast(2, 3, 5, 2);
		System.out.println(str);

		// getting value at index.
		int ps=0;
		for (int i=0;i < str.shape[0];i++)
			for (int j=0;j < str.shape[1];j++)
				for (int k=0;k < str.shape[2];k++)
					for (int l=0;l < str.shape[3];l++)
					{
						float v1=str.getFlat(ps);
						float v2=str.getFloat(i, j, k, l);
						System.out.println(">> " + ps + " = " + v1 + " (" + i + ", " + j + ", " + k + ") = " + v2 + " === " + (v1 == v2));
						ps++;
					}

	}
	void shapeGenUsingIndexTest() throws Exception
	{
		Storage str=new Storage(5, 2);
		System.out.println(str);

		int sum[]=str.shape;
		int[] tmp=new int[sum.length];
		for (int k=0;k < 20;k++)
		{
			System.out.print(k + " = ");
			int cnt=k;
			for (int i=sum.length - 1;i >= 0;i--)
			{
				System.out.print(sum[i] + ", ");
				tmp[i] = cnt % sum[i];
				cnt = cnt / sum[i];
			}
			System.out.println(Arrays.toString(tmp));
		}
		// System.out.println(i / 3 + ", " + i % 3);
	}
	int[] getShapeFromIndex(int count, int[]sum, int[] tmp)
	{
		if (tmp == null)
			tmp = new int[sum.length];
		System.out.println("getting for " + count);
		for (int i= 0;i < tmp.length;i++)
		{
			tmp[i] = count / sum[i];
			System.out.println(count + "/" + sum[i] + "== " + tmp[i]);
			count = count % sum[i];
		}
		return tmp;
	}
	void brodTestNew() throws Exception
	{
		// works well. some optim needed.
		Storage str=new Storage(4, 1, 2);
		// for (int i=0;i < str.base.values.length;i++)
		// 	str.base.values[i] = i + 1;
		System.out.println(str);
		System.out.println("original data ");
		// print(str);
		System.out.println("--v  after broadcasting..");

		str.broadcast(5, 4, 3, 2); // 1 changed into 3 and added new 5 dim(wrapper);
		// print(str);
		System.out.println("=== " + str);
		int i=1;//for (int i=0;i < str.shape[0];i++)
		{
			System.out.println(str.get(i));
			print(str);
		}

	}
	void storageBrodGetTest() throws Exception
	{
		/*
		 error. in the following example the shape [3,1,2]; is brodcasted to [3,5,2];
		 so the length which changed was 1. that means the children of the this array (outer array) must be the the same.
		 but, they are not the same.
		 becauss the offset changed according to it's length
		 1st  offset = 0;
		 2nd  offset = 10;  // must be 0
		 3rd  offset = 20;  // must be 0
		 total length = 30;
		 .:. fix...me
		 */
		Storage str=new Storage(3, 1, 2);
		System.out.println("==== " + str.base.length);
		// for (int i=0;i < str.base.length;i++)
		// 	str.base.values[i] = i + 1;
		System.out.println("++ before brodcast");
		System.out.println(str);
		// System.out.println("== " + Arrays.toString(str.base.values));
		System.out.println();
		for (int i=0;i < str.length;i++)
			System.out.print(str.getFlat(i) + ", ");
		System.out.println();

		str.broadcast(3, 5, 2);
		System.out.println("++ after brodcast");
		System.out.println(str);

		System.out.println();
		for (int i=0;i < str.length;i++)
			System.out.print(str.getFlat(i) + ", ");
		System.out.println();

		for (int i=0;i < str.shape[0];i++)
		{
			Storage str2=str.get(i);
			System.out.println(str2);
			print(str2);
			System.out.println("...");
			System.out.print("size =" + str2.length + " [");
			for (int k=0;k < str2.length;k++)
				System.out.print(str2.getFlat(k) + ", ");
			System.out.println("]\n=== ");
		}

		System.out.println();
		System.out.println("++ each individual item after brodcast");
		for (int i=0;i < 3;i++)
			System.out.println(str.get(i));




	}
	void fakeStrGetBench() throws Exception
	{

		Storage str=new Storage(3, 3, 3, 2, 2, 3);
		// FakeStorage str=new FakeStorage(st.data, new int[]{3,3,3,2,2,3});
		System.out.println(str);
		for (int i=0;i < 2000;i++)
			System.out.println();
		for (;;)
		{
			long cnt=0;
			for (int c=0;c < 10;c++)
			{
				long l=System.currentTimeMillis();
				for (int i=0;i < 10000;i++)
					str.get(2, 2, 2, 1 , 1);
				l = System.currentTimeMillis() - l;
				cnt += l;
			}
			cnt = cnt / 10;
			System.out.println(cnt + " millis in average");
		}

	}
	void StorageGetStr() throws Exception
	{
		Storage str=new Storage(2, 2, 3, 4);
		// for (int i=0;i < str.base.length;i++)
		// 	str.base.values[i] = i + 1;
		System.out.println(str);
		str = str.get(0);
		System.out.println(str);
		str = str.get(0);
		System.out.println(str);
		System.out.println();
		// print2(str);
	}
	void StorageGetFloat() throws Exception
	{
		Storage str=new Storage(2, 3, 4);
		// for (int i=0;i < str.base.length;i++)
		// 	str.base.values[i] = i + 1;
		str = str.get(1);
		System.out.println(str);
		System.out.println();
		// print2(str);
	}
	void create() throws Exception
	{
		// checking storage initialization.
		System.out.println(new Storage(2, 5, 3));
		System.out.println(new Storage(1, 1, 1, 1));
		System.out.println(new Storage(5, 5, 5, 5));

	}
}
