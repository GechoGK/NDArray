import gss.math.*;
import java.util.*;

import static gss.math.Util.*;

public class TestP1
{
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
		System.out.println("== " + Arrays.toString(str.base.values));
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
		System.out.println(Arrays.toString(str.base.values));
		System.out.println(str);
		print(str);
		System.out.println("=======");
		for (int i=0;i < str.length;i++)
			System.out.println(str.get(str.getShape(i)).getFlat(0));

		System.out.println("==.... ");
		str = str.broadcast(3, 3, 3);
		print(str);
		System.out.println(str);
		System.out.println(Arrays.toString(str.base.values));
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
		System.out.println("== " + str.base.values.length);
		// fill data to the array.
		for (int i=0;i < str.base.values.length;i++)
			str.base.values[i] = i + 1;

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
		for (int i=0;i < str.base.values.length;i++)
			str.base.values[i] = i + 1;
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
		for (int i=0;i < str.base.length;i++)
			str.base.values[i] = i + 1;
		System.out.println("++ before brodcast");
		System.out.println(str);
		System.out.println("== " + Arrays.toString(str.base.values));
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
		for (int i=0;i < str.base.length;i++)
			str.base.values[i] = i + 1;
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
		for (int i=0;i < str.base.length;i++)
			str.base.values[i] = i + 1;
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
