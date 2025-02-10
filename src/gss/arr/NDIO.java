package gss.arr;

import java.util.*;
import gss.math.*;

public class NDIO
{
	public static NDArray arange(float end)
	{
		return arange(0, end, 1);
	}
	public static NDArray arange(float str, float end)
	{
		return arange(str, end, 1);
	}
	public static NDArray arange(float str, float end, float inc)
	{
		float[] f=Util.range(str, end, inc);
		NDArray ar=new NDArray(f);
		return ar;
	}
	public static NDArray zeros(int...shape)
	{
		return value(shape, 0, false);
	}
	public static NDArray zeros(int...shape, boolean requiresGrad)
	{
		return value(shape, 0, requiresGrad);
	}
	public static NDArray zerosAlike(NDArray arr)
	{
		return value(arr.getShape(), 0, false);
	}
	public static NDArray zerosAlike(NDArray arr, boolean requiresGrad)
	{
		return value(arr.getShape(), 0, requiresGrad);
	}
	// one new array.
	public static NDArray ones(int...shape)
	{
		return value(shape, 1, false);
	}
	public static NDArray ones(int...shape, boolean requiresGrad)
	{
		return value(shape, 1, requiresGrad);
	}
	public static NDArray onesAlike(NDArray arr)
	{
		return value(arr.getShape(), 1, false);
	}
	public static NDArray onesAlike(NDArray arr, boolean requiresGrad)
	{
		return value(arr.getShape(), 1, requiresGrad);
	}
	// array with custom value.
	public static NDArray value(int[]shape, float val)
	{
		return value(shape, val, false);
	}
	public static NDArray value(int[]shape, float val, boolean requiresGrad)
	{
		NDArray arr=new NDArray(shape).setEnableGradient(requiresGrad);
		for (int i=0;i < arr.getLength();i++)
			arr.base.data.setData(i, val);
		// Arrays.fill(arr.storage.base.values, val);
		return arr;
	}
	public static NDArray fromArray(int[] shape, float...arr)
	{
		return new NDArray(shape, arr);
	}
	// the seed value can be -1.
	public static NDArray rand(int...shape)
	{
		return rand(shape, false, 128); // change 128 to -1
	}
	public static NDArray rand(int[] shape, int seed)
	{
		return rand(shape, false, seed);
	}
	public static NDArray rand(int[]shape, boolean requiresGrad)
	{
		return rand(shape, requiresGrad, 128); // change 128 to -1
	}
	public static NDArray rand(int[]shape, boolean reqiresGrad, int seed)
	{
		NDArray arr=new NDArray(shape).setEnableGradient(reqiresGrad);
		Random r=null;
		if (seed != -1)
			r = new Random(seed);
		else
			r = new Random();
		for (int i=0;i < arr.getLength();i++)
			arr.base.data.setData(i, r.nextFloat());
		return arr;
	}

}
