package gss.math;

import java.util.*;

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
	public static int length(int...sh)
	{
		return length(0, sh);
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
		int sm=1;
		for (int i=shape.length - 1;i >= 0;i--)
		{
			sm *= shape[i];
			sum[i] = sm;
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
	 float v1=getAt(arr,0); //  3,
	 float v1=getAt(arr,1); //  2,
	 float v1=getAt(arr,2); //  1,
	 */
	public static float getAtR(float[] arr, int index)
	{
		return arr[arr.length - index - 1]; // 
	}
	public static int getAtR(int[] arr, int index)
	{
		return arr[arr.length - index - 1]; // 
	}
}
