package gss.math;

import java.util.*;

import static gss.math.Util.*;

public class NDArray
{
	/*
	 Bug ---
	 copy problem.
	 when copy method called, it doesn't copy the gradientFunction and childs.
	 so remember to include.
	 */
	public Storage storage;
	public List<NDArray> childs = new ArrayList<>();
	public GradFunc gradientFunction;

	public NDArray(Storage str)
	{
		this.storage = str;
	}
//	public NDArray(int...shape)
//	{
//		this(shape, false);
//	}
	public NDArray(int[]shape, boolean requireGrad)
	{
		this.storage = new Storage(shape, requireGrad);
		if (requireGrad)
			childs = new ArrayList<>();
	}
	public NDArray(float[]data)
	{
		this(data, false);
	}
	public NDArray(float[] data, boolean requireGrad)
	{
		this.storage = new Storage(new int[]{data.length}, requireGrad);
		for (int i=0;i < data.length;i++)
			storage.base.setData(i, data[i]); 
		//storage.base.values = Arrays.copyOf(data, data.length);
		if (requireGrad)
			childs = new ArrayList<>();
	}
	public NDArray(float[][]data)
	{
		this(data, false);
	}
	public NDArray(float[][] data, boolean requireGrad)
	{
		this.storage = new Storage(new int[]{data.length, data[0].length}, requireGrad);
		float[] dt=Util.flatten(data);
		for (int i=0;i < data.length;i++)
			storage.base.setData(i, dt[i]);
		// storage.base.values = Util.flatten(data);
		if (requireGrad)
			childs = new ArrayList<>();
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
		return fromStorage(storage.get(index));
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
	// get Value.
	public Value getValue(int...index)
	{
		return storage.getValue(index);
	}
	public Value getFlatValue(int ind)
	{
		return storage.getFlatValue(ind);
	}
	// get Gradient.
	public float getExactGrad(int...index)
	{
		return storage.getFloatGrad(index);
	}
	public float getFlatGrad(int index)
	{
		return storage.getFlatGrad(index);
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
	// set Gradient.
	public void setGrad(int[] index, float val)
	{
		storage.setGrad(index, val);
	}
	public void setExactGrad(int[] index, float val)
	{
		storage.setExactGrad(index, val);
	}
	public void setFlatGrad(int index, float val)
	{
		storage.setFlatGrad(index, val);
	}
	public void zeroGrad()
	{
		storage.zeroGrad();
	}
	// broadcast into another shape.
	public NDArray broadcast(int...newShape)
	{
		NDArray str = fromStorage(storage.broadcast(newShape));
		str.setGradientFunction(GradFunc.stepGradient, this);
		return str;
	}
	// view into another shape.
	public NDArray view(int...newShape)
	{
		return fromStorage(storage.view(newShape));
	}
	public NDArray reshape(int...newShape)
	{
		return fromStorage(storage.reshape(newShape));
	}
	public void setGradientFunction(GradFunc func, NDArray...chlds)
	{
		this.gradientFunction = func;
		this.childs.clear();
		for (NDArray ar:chlds)
			this.childs.add(ar);
	}
	public void backward()
	{
		if (gradientFunction == null)
			return;
		System.out.println("backward " + this.storage);
		// throw new RuntimeException("gradient function not found = " + gradientFunction);
		gradientFunction.backward(this, childs.toArray(new NDArray[0]));
		for (NDArray arr:childs)
			arr.backward();
	}
	// n-dimension array computation functions.
	public NDArray add(NDArray other)
	{
		int[] shp=getCommonShape(this.storage.shape, other.storage.shape);
		NDArray a1=broadcast(shp);
		NDArray a2=other.broadcast(shp);
		NDArray arrOut=new NDArray(shp, a1.requiresGradient() || a2.requiresGradient());
		arrOut.setGradientFunction(GradFunc.additionGradient, a1, a2);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't make operation with two different array length(" + a1.getLength() + " != " + a2.getLength() + ")");
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
		NDArray a1=broadcast(shp);
		NDArray a2=other.broadcast(shp);
		NDArray arrOut=new NDArray(shp, a1.requiresGradient() || a2.requiresGradient());
		arrOut.setGradientFunction(GradFunc.subtractionGradient, a1, a2);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't make operation with two different array length(" + a1.getLength() + " != " + a2.getLength() + ")");
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
		NDArray a1=broadcast(shp);
		NDArray a2=other.broadcast(shp);
		NDArray arrOut=new NDArray(shp, a1.requiresGradient() || a2.requiresGradient());
		arrOut.setGradientFunction(GradFunc.multiplicationGradient, a1, a2);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't make operation with two different array length(" + a1.getLength() + " != " + a2.getLength() + ")");
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
		NDArray a1=broadcast(shp);
		NDArray a2=other.broadcast(shp);
		NDArray arrOut=new NDArray(shp, a1.requiresGradient() || a2.requiresGradient());
		arrOut.setGradientFunction(GradFunc.divisionGradient, a1, a2);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't make operation with two different array length(" + a1.getLength() + " != " + a2.getLength() + ")");
		for (int i=0;i < a1.getLength();i++)
		{
			float v1=a1.getFlat(i);
			float v2=a2.getFlat(i);
			arrOut.setFlat(i, v1 / v2);
		}
		return arrOut;
	}
	public NDArray pow(NDArray exp)
	{
		int[] shp=getCommonShape(this.storage.shape, exp.storage.shape);
		NDArray a1=broadcast(shp);
		NDArray a2=exp.broadcast(shp);
		NDArray arrOut=new NDArray(shp, a1.requiresGradient() || a2.requiresGradient());
		arrOut.setGradientFunction(GradFunc.powGradient, a1, a2);
		// System.out.println("length " + a1.getLength() + " == " + a2.getLength());
		if (a1.getLength() != a2.getLength())
			throw new RuntimeException("can't make operation with two different array length(" + a1.getLength() + " != " + a2.getLength() + ")");
		for (int i=0;i < a1.getLength();i++)
		{
			float v1=a1.getFlat(i);
			float v2=a2.getFlat(i);
			arrOut.setFlat(i, (float)Math.pow(v1 , v2));
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
	 ..  sh2  =     [5, 4, 2, 1] taking the broadcastable shape between them.
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
	// zero new array
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
		NDArray arr=new NDArray(shape, requiresGrad);
		for (int i=0;i < arr.storage.base.getArrayLength();i++)
			arr.storage.base.setData(i, val);
		// Arrays.fill(arr.storage.base.values, val);
		return arr;
	}
	public static NDArray fromArray(int[] shape, float...arr)
	{
		throw new RuntimeException("not implemented.");
		// return null;
	}
	// the seed value can be -1.
	public static NDArray rand(int...shape)
	{
		return rand(shape, false, -1);
	}
	public static NDArray rand(int[] shape, int seed)
	{
		return rand(shape, false, seed);
	}
	public static NDArray rand(int[]shape, boolean requiresGrad)
	{
		return rand(shape, requiresGrad, -1);
	}
	public static NDArray rand(int[]shape, boolean reqiresGrad, int seed)
	{
		NDArray arr=new NDArray(shape, reqiresGrad);
		Random r=null;
		if (seed != -1)
			r = new Random(seed);
		else
			r = new Random();
		for (int i=0;i < arr.storage.length;i++)
			arr.storage.base.setData(i, r.nextFloat());
		return arr;
	}
	// new array from alrrady prepared storage.
	public NDArray fromStorage(Storage str)
	{
		NDArray arr=new NDArray(str);
		arr.childs.addAll(this.childs);
		arr.gradientFunction = this.gradientFunction;
		return arr;
	}
	public NDArray copy()
	{
		return fromStorage(storage.copy());
	}
	@Override
	public String toString()
	{
		return storage.dim + "D Array(shape =" + Arrays.toString(storage.shape) + ", requiresGradient = " + storage.requiresGradient() + ", isBroadcasted = " + storage.broadcasted + ")," + gradientFunction + "[childs = " + (childs == null ?"0": childs.size()) + "]";
	}
	public boolean isBroadcastedShape()
	{
		return storage.isBroadcastedShape();
	}
	public boolean requiresGradient()
	{
		return storage.requiresGradient();
	}
}
