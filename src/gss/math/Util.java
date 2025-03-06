package gss.math;

import gss.math.*;
import gss.arr.*;
import java.util.*;

import gss.arr.NDArray;

public class Util
{
	public static float[] flatten(float[][] data)
	{
		int rw=data.length;
		int cl=data[0].length;
		int pos=0;
		float[] dt=new float[rw * cl];
		for (int r=0;r < rw;r++)
			for (int c=0;c < cl;c++)
				dt[pos++] = data[r][c];
		return dt;
	}
	public static float[] flatten(float[][][] data)
	{
		int dp=data.length;
		int rw=data[0].length;
		int cl=data[0][0].length;
		int pos=0;
		float[] dt=new float[dp * rw * cl];
		for (int d=0;d < dp;d++)
			for (int r=0;r < rw;r++)
				for (int c=0;c < cl;c++)
					dt[pos++] = data[d][r][c];
		return dt;
	}
	public static float[] flatten(float[][][][] data)
	{
		int dp=data.length;
		int rw=data[0].length;
		int cl=data[0][0].length;
		int dd=data[0][0][0].length;
		int pos=0;
		float[] dt=new float[dp * rw * cl * dd];
		for (int d=0;d < dp;d++)
			for (int r=0;r < rw;r++)
				for (int c=0;c < cl;c++)
					for (int i=0;i < dd;i++)
						dt[pos++] = data[d][r][c][i];
		return dt;
	}
	public static int length(int...sh)
	{
		return length(-2, sh);
	}
	public static int length(int ignore, int...sh)
	{
		int size=1;
		for (int s:sh)
		{
			if (s == ignore)
				continue;
			if (s <= 0)
				throw new RuntimeException("shape of \"0\" doesn't allowed (" + Arrays.toString(sh) + ")");
			size *= s;
		}
		return size;
	}
	public static int sum(int[] sh, int start)
	{
		int sm=1;
		for (int i=start;i < sh.length;i++)
			sm *= sh[i];
		return sm;
	}
	public static int[] sumShapes(int[] shape, int[] sum)
	{
		if (sum == null || sum.length != shape.length)
			sum = new int[shape.length];
		sum[sum.length - 1] = 1;
		int sm=1;
		for (int i=shape.length - 1;i >= 1;i--)
		{
			sm *= shape[i];
			sum[i - 1] = sm;
		}
		return sum;
	}
	public static int index(int val, int...arr)
	{
		for (int i=0;i < arr.length;i++)
		{
			if (val == arr[i])
				return i;
		}
		return -1; // -1 means the value you are looking for doesn't exist in this array(arr).
	}
	public static int occurance(int val, int...arr)
	{
		int count=0; // starting pos
		for (int i=0;i < arr.length;i++)
			if (val == arr[i])
				count++;
		return count; // returning value (0) means the value not found in array.
	}
	public static int[] overlap(int[] sh, int length)
	{
		int[] arr=new int[length];
		int diff= length - sh.length;
		for (int i=0;i < Math.min(length, sh.length);i++)
		{
			arr[Math.max(0, length - sh.length) + i] = sh[i + Math.max(0, diff * -1)];
		}
		return arr;
	}
	public static int[] overlap(int[] sh, int[] into)
	{
		if (into == null)
			return null;
		int length=into.length;
		// int[] into=new int[length];
		int diff= length - sh.length;
		for (int i=0;i < Math.min(length, sh.length);i++)
		{
			into[Math.max(0, length - sh.length) + i] = sh[i + Math.max(0, diff * -1)];
		}
		return into;
	}
	/*
	 usage.
	 float[] arr={1,2,3};
	 // forward access.
	 float v1=getAt(arr,0); //  1,
	 float v1=getAt(arr,1); //  2,
	 float v1=getAt(arr,2); //  3,
	 // backward access.
	 float v1=getAt(arr,-1); //  3,
	 float v1=getAt(arr,-2); //  2,
	 float v1=getAt(arr,-3); //  1,
	 */
	public static float getAt(float[] arr, int index)
	{
		if (index < 0)
			return arr[arr.length + index]; // accessing backward array.
		return arr[index]; // the default array access.
	}
	public static int getAt(int[] arr, int index)
	{
		if (index < 0)
			return arr[arr.length + index]; // accessing backward array.
		return arr[index]; // the default array access.
	}
	/*
	 float[] arr={1,2,3};
	 float v1=getAtR(arr,0); //  3,
	 float v1=getAtR(arr,1); //  2,
	 float v1=getAtR(arr,2); //  1,
	 */
	public static float getAtR(float[] arr, int index)
	{
		return arr[arr.length - index - 1]; // 
	}
	public static int getAtR(int[] arr, int index)
	{
		return arr[arr.length - index - 1]; // 
	}
	public static void print(NDArray ar)
	{
		if (ar == null)
			System.out.println("null array");
		else
			print(ar.base);
	}
	public static void print(Shape str)
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
	public static void printGrad(NDArray ar)
	{
		printGrad(ar.base);
	}
	public static void printGrad(Shape str)
	{
		if (!str.requiresGradient())
		{
			System.out.println("null gradient");
			return;
		}
		if (str.dim == 1)
		{
			System.out.print("[");
			for (int i=0;i < str.shape[0];i++)
				System.out.print(str.getExactGrad(i) + ", ");
			System.out.println("]");
		}
		else if (str.dim == 2)
		{
			System.out.print("[");
			for (int j=0;j < str.shape[0];j++)
			{
				System.out.print((j == 0 ?"": " ") + "[");
				for (int k=0;k < str.shape[1];k++)
					System.out.print((k == 0 ?" ": ", ") + str.getExactGrad(j, k));
				System.out.print(j == str.shape[0] - 1 ?"]": "]\n");
			}
			System.out.println("]");
		}
		else
			for (int i=0;i < str.shape[0];i++)
			{
				printGrad(str.get(i));
				System.out.println();
			}
	}
	public static void print(float[] f)
	{
		if (f == null)
		{
			System.out.println("null array");
			return;
		}
		System.out.print("[ ");
		for (int i=0;i < f.length;i++)
			System.out.print(f[i] + (i == f.length - 1 ?"": ", "));
		System.out.println(" ]");
	}
	public static void print(float[][] f)
	{
		if (f == null)
		{
			System.out.println("null array");
			return;
		}
		System.out.print("[");
		for (int j=0;j < f.length;j++)
		{
			System.out.print((j == 0 ?"": " ") + "[ ");
			for (int i=0;i < f[0].length;i++)
				System.out.print(f[j][i] + (i == f[0].length - 1 ?"": ", "));
			System.out.println(" ]" + (j == f.length - 1 ?"]": ""));
		}
	}
	public static void print(Object o)
	{
		System.out.println(o);
	}
	public static void fill(Shape str, float val)
	{
		for (int i=0;i < str.data.getLength();i++)
			str.data.setData(i, val);
	}
	public static void fillRand(Shape str)
	{
		Random r=new Random(128);
		for (int i=0;i < str.data.getLength();i++)
			str.data.setData(i, r.nextFloat());
	}
	public static boolean equals(int[] s1, int[] s2)
	{
		if (s1.length != s2.length)
			return false;
		for (int i=0;i < s1.length;i++)
			if (s1[i] != s2[i])
				return false;
		return true;
	}
	public static boolean equals(float[] s1, float[] s2)
	{
		if (s1 == null || s2 == null)
			return false;
		if (s1.length != s2.length)
			return false;
		for (int i=0;i < s1.length;i++)
			if (s1[i] != s2[i])
				return false;
		return true;
	}
	public static boolean equals(NDArray a1, NDArray a2, boolean...checkGrad)
	{
		if (a1 == null || a2 == null)
			return false;
		if (!equals(a1.getShape(), a2.getShape()))
			return false;
		if (!equals(a1.base.data.getData(), a2.base.data.getData()))
			return false;
		if (checkGrad.length != 0 && checkGrad[0])
			if (!equals(a1.base.data.getGrads(), a2.base.data.getGrads()))
				return false;
		return true;
	}
	public static int[] range(int len)
	{
		return range(0, len, 1);
	}
	public static int[] range(int str, int end)
	{
		return range(str, end, 1);
	}
	public static int[] range(int str, int end, int inc)
	{
		int cnt=(end - str);
		int[] arr=new int[(cnt / inc) + (cnt % inc == 0 ?0: 1)];
		int p=0;
		for (int i=str;i < end;i += inc)
		{
			arr[p] = i;
			p++;
		}
		return arr;
	}
	public static float[] range(float len)
	{
		return range(0, len, 1);
	}
	public static float[] range(float str, float end)
	{
		return range(str, end, 1);
	}
	public static float[] range(float str, float end, float inc)
	{
		float cnt=(end - str);
		float[] f=new float[(int)(cnt / inc) + (cnt % inc == 0 ?0: 1)];
		int p=0;
		for (float i=str;i < end;i += inc)
		{
			f[p] = i;
			p++;
		}
		return f;
	}
	public static String getString(String s, int times)
	{
		StringBuilder sb=new StringBuilder();
		for (int i=0;i < times;i++)
			sb.append(s);
		return sb.toString();
	}
}
