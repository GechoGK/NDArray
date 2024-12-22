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
		this(shape, false);
	}
	public NDArray(int[]shape, boolean requireGrad)
	{
		this.storage = new Storage(shape, requireGrad);
	}
	public NDArray(float[]data)
	{
		this(data, false);
	}
	public NDArray(float[] data, boolean requireGrad)
	{
		this.storage = new Storage(new int[]{data.length}, requireGrad);
		storage.base.values = Arrays.copyOf(data, data.length);
	}
	public NDArray(float[][]data)
	{
		this(data, false);
	}
	public NDArray(float[][] data, boolean requireGrad)
	{
		this.storage = new Storage(new int[]{data.length, data[0].length}, requireGrad);
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
	// get and set methods
	// returns the new  NDArray from storage at index @index.
	public NDArray get(int...index)
	{
		return new NDArray(storage.get(index));
	}
	// return float value from data at index @index.
	public float getExact(int...index)
	{
		return storage.getFloat(index);
	}
	// return float value by converting the array into single dimension array.
	public float getFlat(int index)
	{
		return storage.getFlat(index);
	}
	// set aray value at specific index.
	public void set(int[]index, NDArray arr)
	{
		storage.set(index, arr.storage);
	}
	// set float value at specific index
	public void set(int[] index, float val)
	{
		storage.set(index, val);
	}
	// flatten the array then set value at index @index.
	public void setFlat(int index, float val)
	{
		storage.setFlat(index, val);
	}
	// set exactly one value into the array.
	public void setExact(int[] index, float val)
	{
		storage.setExact(index, val);
	}
	// broadcast into another shape.
	public NDArray broadcast(int[] newShape)
	{
		return new NDArray(storage.broadcast(newShape));
	}
	public NDArray view(int...newShape)
	{
		return new NDArray(storage.view(newShape));
	}
	public NDArray reshape(int...newShape)
	{
		return new NDArray(storage.reshape(newShape));
	}
	// n-dimension array computation functions.
	public NDArray add(NDArray other)
	{
		int[] shp=getCommonShape(this.storage.shape, other.storage.shape);
		NDArray arrOut=new NDArray(shp);
		NDArray a1=broadcast(shp);
		NDArray a2=other.broadcast(shp);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't add two different length arrays (" + a1.getLength() + " != " + a2.getLength() + ")");
		for (int i=0;i < a1.getLength();i++)
		{
			float v1=a1.getFlat(i);
			float v2=a2.getFlat(i);
			arrOut.setFlat(i, v1 + v2);
		}
		return arrOut;
	}
	public NDArray sub(NDArray other)
	{
		int[] shp=getCommonShape(this.storage.shape, other.storage.shape);
		NDArray arrOut=new NDArray(shp);
		NDArray a1=broadcast(shp);
		NDArray a2=other.broadcast(shp);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't subtract two different length arrays (" + a1.getLength() + " != " + a2.getLength() + ")");
		for (int i=0;i < a1.getLength();i++)
		{
			float v1=a1.getFlat(i);
			float v2=a2.getFlat(i);
			arrOut.setFlat(i, v1 - v2);
		}
		return arrOut;
	}
	public NDArray mul(NDArray other)
	{
		int[] shp=getCommonShape(this.storage.shape, other.storage.shape);
		NDArray arrOut=new NDArray(shp);
		NDArray a1=broadcast(shp);
		NDArray a2=other.broadcast(shp);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't multiply two different length arrays (" + a1.getLength() + " != " + a2.getLength() + ")");
		for (int i=0;i < a1.getLength();i++)
		{
			float v1=a1.getFlat(i);
			float v2=a2.getFlat(i);
			arrOut.setFlat(i, v1 * v2);
		}
		return arrOut;
	}
	public NDArray div(NDArray other)
	{
		int[] shp=getCommonShape(this.storage.shape, other.storage.shape);
		NDArray arrOut=new NDArray(shp);
		NDArray a1=broadcast(shp);
		NDArray a2=other.broadcast(shp);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't devide two different length arrays (" + a1.getLength() + " != " + a2.getLength() + ")");
		for (int i=0;i < a1.getLength();i++)
		{
			float v1=a1.getFlat(i);
			float v2=a2.getFlat(i);
			arrOut.setFlat(i, v1 / v2);
		}
		return arrOut;
	}
	// special functions.
	/*
	 // this function returns the broadcasted shape of the two array.
	 // the commom shape for two arrays.
	 // the functions take the longer array as a result.
	 example.
	 1.  shape.1 = [2,1,4,3]
	 ..  shape.2 = [2,5,4,1]  these shapes can be broadcasted. one into another.
	 ==== result = [2,5,4,3]

	 2.  shape.1 = [4,3,2,6,1,6]
	 ..  shape.2 =         [3,6] also the se two shapes can be broadcast one into another.
	 ===  result = [4,3,2,6,3,6] ...

	 3.  sh1  =        [1, 2, 3]
	 ..  sh2  =     [5, 4, 2, 1] taking the broadcastable shape bdtween them.
	 ..  newShape = [5, 4, 2, 3]
	 // when computing a loop start from the end and down to 0.
	 */
	public static int[] getCommonShape(int[] shape1, int[] shape2)
	{
		// System.out.println("finding common broadcastable shape for");
		// System.out.println(Arrays.toString(shape1) + ", " + Arrays.toString(shape2));
		int[] newShape1=Arrays.copyOf(shape1.length > shape2.length ?shape1: shape2, Math.max(shape1.length, shape2.length));
		int[] newShape2=shape1.length > shape2.length ?shape2: shape1;
		// System.out.println("temporary result shape ");
		// System.out.println("== " + Arrays.toString(newShape1));
		// System.out.println("== " + Arrays.toString(newShape2));
		for (int i=0;i < Math.min(shape1.length, shape2.length);i++)
		{
			int sh1=getAtR(newShape1, i);
			int sh2=getAtR(newShape2, i);
			// System.out.println("... [" + sh1 + ", " + sh2 + "]");
			if (sh1 != sh2 && (sh1 != 1 && sh2 != 1))
				throw new RuntimeException("not broadcastable shape at. ( " + sh1 + " != " + sh2 + " )");
			newShape1[newShape1.length - 1 - i] = sh1 == 1 ?sh2: sh1;
		}
		return newShape1;
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
	public static NDArray fromArray(int[] shape, float...arr)
	{
		return null;
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
	public NDArray copy()
	{
		return new NDArray(storage.copy());
	}
	@Override
	public String toString()
	{
		return storage.dim + "D Array(shape =" + Arrays.toString(storage.shape) + ", requiresGradient = " + storage.requiresGradient() + ")";
	}
}
