import java.util.*;
import gss.math.*;

import static gss.math.Util.*;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		// to be broadcast
		// if the org shape is (2,3,2)
		// and if the new shape (1,3,2) thi is not broadcastable
		// because th items in shepe can be 1 , but only in original shope.
		// if the original shape items is not 1 then the new shape also must be the same items at partucular index.
		new Main().a();



	}
	void a() throws Exception
	{
		Storage str=new Storage(5, 2);
		System.out.println(str);

		int sum[]=str.sum;
		int[] tmp=new int[sum.length];
		for (int k=0;k < 20;k++)
		{
			System.out.print(k + " = ");
			int cnt=k;
			for (int i=sum.length - 1;i >= 0;i--)
			{
				System.out.println(i);
				tmp[i] = cnt % sum[i];
				cnt = cnt / sum[i];
			}
			System.out.println(Arrays.toString(tmp));
		}
		// System.out.println(i / 3 + ", " + i % 3);
	}
	int[] getSh(int count, int[]sum, int[] tmp)
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

		str.brodcast(5, 4, 3, 2); // 1 changed into 3 and added new 5 dim(wrapper);
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

		str.brodcast(3, 5, 2);
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
	void print(Storage str)
	{
		if (str.dim == 1)
		{
			System.out.print("[");
			for (int i=0;i < str.shape[0];i++)
				System.out.print(str.getFloat(i) + ", ");
			System.out.println("]");
		}
		else if (str.dim == 2)
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
			for (int i=0;i < str.shape[0];i++)
			{
				print(str.get(i));
				System.out.println();
			}
	}
	void create() throws Exception
	{
		// checking storage initialization.
		System.out.println(new Storage(2, 5, 3));
		System.out.println(new Storage(1, 1, 1, 1));
		System.out.println(new Storage(5, 5, 5, 5));

	}
}
