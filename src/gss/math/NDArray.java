package gss.math;

import java.util.*;

import static gss.math.Util.*;

public class NDArray
{
	public Storage storage;

	public NDArray(Storage str)
	{
		this.storage = str;
	}
	public NDArray(int...shape)
	{
		this.storage = new Storage(shape);
	}
	public NDArray(float[] data)
	{
		this.storage = new Storage(data.length);
		storage.base.values = Arrays.copyOf(data, data.length);
	}
	public NDArray(float[][] data)
	{
		this.storage = new Storage(data.length, data[0].length);
		storage.base.values = Util.flatten(data);
	}
	public int[] getShape()
	{
		return storage.shape;
	}
	public int[] getDataShape()
	{
		return storage.base.shape;
	}
	public int getDim()
	{
		return storage.dim;
	}
	public int getLength()
	{
		// it return the length of storage.
		// for broadcasted storage, returns the overall length of storage.
		// caution not data length.
		return storage.length;
	}
	// static methods.
	public static NDArray zeros(int...shape)
	{
		return value(shape, 0);
	}
	public static NDArray zerosAlike(NDArray arr)
	{
		return value(arr.getShape(), 0);
	}
	public static NDArray ones(int...shape)
	{
	 	return value(shape, 1);
	}
	public static NDArray onesAlike(NDArray arr)
	{
		return value(arr.getShape(), 1);
	}
	public static NDArray value(int[]shape, float val)
	{
		NDArray arr=new NDArray(shape);
		Arrays.fill(arr.storage.base.values, val);
		return arr;
	}
	// the seed value can be empty.
	public static NDArray rand(int[]shape, int...seed)
	{
		NDArray arr=new NDArray(shape);
		Random r=null;
		if (seed.length > 0)
			r = new Random(seed[0]);
		for (int i=0;i < arr.storage.length;i++)
			arr.storage.base.values[i] = r.nextFloat();
		return arr;
	}
}
