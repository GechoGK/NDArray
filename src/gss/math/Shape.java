package gss.math;

import java.util.Arrays;

public class Shape implements Cloneable
{
	/*
	 this shape is used to get and set item to and from data.

	 */
	public Data data;
	public int[] shape;
	public int[] stride;
	public int length;
	public int offset=0;
	public int dim=0;

	public Shape()
	{}
	public Shape(int...sh)
	{
		this.data = new Data(sh);
		init(data, sh, null, 0);
	}
	public Shape(int[]sh, float[]dt)
	{
		this.data = new Data(dt);
		init(data, sh, null, 0);
	}
	public Shape(Data d, int[]sh, int off)
	{
		init(d, sh, null, off);
	}
	public Shape(Data d, int[]sh, int[] strd, int off)
	{
		init(d, sh, strd, off);
	}
	public void setEnableGradient(boolean b)
	{
		data.setRequireGrad(b);
	}
	public void resetGradient()
	{
		data.enableGrad();
	}
	private void init(Data d, int[]sh, int[] strd, int off)
	{
		this.data = d;
		this.shape = sh; // copy this shape is a better idea b/c it may be come from another shape class through NDIO....alike(..). if not copied modifying shape would also change the other unknown shape.
		this.dim = shape.length;
		if (stride == null)
			this.stride = Util.sumShapes(sh, null);
		else
			this.stride = strd;
		this.length = Util.length(sh);
		this.offset = off;
	}
	public Shape get(int...sh)
	{
		int off=shapeToIndex(sh);
		int[] nShape=Arrays.copyOfRange(shape, sh.length, shape.length);
		int[] strd=Arrays.copyOfRange(stride, sh.length, shape.length);
		Shape s=new Shape(data, nShape, strd, off);
		// s.stride = strd;
		return s;
	}
	public void set(int[]index, float val)
	{
		if (index.length > shape.length)
			throw new RuntimeException("index out of bound exception " + Arrays.toString(index));
		get(index).fill(val);
	}
	public void setGrad(int[]index, float val)
	{
		if (index.length > shape.length)
			throw new RuntimeException("index out of bound exception " + Arrays.toString(index));
		get(index).fillGrad(val);
	}
	public void setExact(int[]index, float v)
	{
		// if (index.length != shape.length)
		// 	throw new RuntimeException("ivalid index size :" + Arrays.toString(index) + " >> the index length should be equal to the shape length of the array.");
		int ps=shapeToIndex(index);
		data.setData(ps, v);
	}
	// set float value, assuming the array is flat.
	public void setFlat(int p, float v)
	{
		setExact(getShape(p), v);
	}
	// fills the scalar value tot the array data.
	public void fill(float v)
	{
		for (int i=0;i < length;i++)
		{
			int ind=shapeToIndex(getShape(i));
			data.setData(ind, v);
		}
	}
	// fills the scalar value to the gradient.
	public void fillGrad(float v)
	{
		if (!requiresGradient())
			throw new RuntimeException("gradient not found :: requiresGradient = " + requiresGradient() + " ?? try enabling it by .setEnableGradient(true);");
		for (int i=0;i < length;i++)
		{
			int ind=shapeToIndex(getShape(i));
			data.setGrad(ind, v);
		}
	}
	public float getFloat(int...index)
	{
		int ind=shapeToIndex(index);
		// System.out.println(".." + ind);
		return data.getData(ind);
	}
	public float getFlat(int p)
	{
		return getFloat(getShape(p));
	}
	// gradient set and get functions.
	public Value getExactValue(int...index)
	{
		int ind=shapeToIndex(index);
		// System.out.println(".." + ind);
		return data.getValue(ind);
	}
	public Value getFlatValue(int p)
	{
		return getExactValue(getShape(p));
	}
	public float getExactGrad(int...index)
	{
		int ind=shapeToIndex(index);
		// System.out.println(".." + ind);
		return data.getGrad(ind);
	}
	public float getFlatGrad(int index)
	{
		return getExactGrad(getShape(index));
	}
	public void setExactValue(Value v, int...index)
	{
		int ps=shapeToIndex(index);
		data.setValue(ps, v);
	}
	public void setFlatValue(Value v, int p)
	{
		setExactValue(v, getShape(p));
	}
	public void setExactGrad(int[]index, float val)
	{
		int ps=shapeToIndex(index);
		data.setGrad(ps, val);
	}
	public void setFlatGrad(int pos, float val)
	{
		setExactGrad(getShape(pos), val);
	}
	/*
	 // TO-DO for performance.

	 public void setFlatGrad(int startPosition,float[]array){
	 // it assign array value starts from "startPosition" to the length of an array.
	 // it aims to achieve by calulating indexToShape with multiple values,
	 // since these values are consecutive, we can find the first index then increment on that value.
	 }
	 */
	// end set and get functions.
	public int shapeToIndex(int...index)
	{
		if (index.length > shape.length) // change this " != " to " > " and implement the if block. or use backward loop.
			throw new IndexOutOfBoundsException();
		// System.out.println("finding index =" + Arrays.toString(shape) + ",  " + Arrays.toString(index));
		int newPos=0;
		for (int i=0;i < index.length;i++)
		{
			int shapeInd = index[i];// Math.min(index[i], shape[i] - 1); // uncomment to enable broadcasting.
			// System.out.println("...."+shape[i]);
			if (shapeInd >= shape[i])
				throw new IndexOutOfBoundsException(">> " + shapeInd + ", " + shape[i]);
			newPos += shapeInd *  stride[i];
		}
		int finalIndex=newPos + offset;
		return finalIndex;
	}
	public int[] getShape(int index)
	{
		// this function used to convert index (0-n) into shaps. by iterating all posible combination of shapes, and it returns the combination at the speciic index.
		if (index >= length || index < 0)
			throw new IndexOutOfBoundsException();
		int[] sh=this.shape;
		int[] indShape=new int[sh.length];
		for (int i=sh.length - 1;i >= 0;i--) // count down starts from shape.length -1 down to 0.
		{
			indShape[i] = index % sh[i];
			index = index / sh[i];
		}
		return indShape;
	}
	public Shape transpose()
	{
		int[] ax=new int[shape.length];
		for (int i=0;i < ax.length;i++)
			ax[i] = ax.length - i - 1;
		return transpose(ax);
	}
	public Shape transpose(int...axes)
	{
		if (axes.length != shape.length)
			throw new RuntimeException("invalid axes");
		int[] sh=new int[shape.length]; // Arrays.copyOf(shape, shape.length);
		int[] strd=new int[shape.length];
		int p=0;
		for (int i:axes)
		{
			if (i >= axes.length)
				throw new IndexOutOfBoundsException("index must not greater than the dimension o the array");
			sh[p] = shape[i];
			strd[p] = stride[i];
			p++;
		}
		return new TShape(data, shape, sh, strd, offset);
	}
	public Shape view(int...newShape)
	{
		getShape(newShape);
		if (length != Util.length(newShape))
			throw new RuntimeException("can't view this array into " + Arrays.toString(newShape) + " because the length is not equal");
		// if newShape length != length
		//		can't view this array into new shape.
		return new Shape(data, newShape, offset);
	}
	public Shape reshape(int...newShape)
	{
		// !!! the returning value must be a new copy of storage.
		// TO-DO  fix return this. instead create a new Storage(...);

		// try to broadcast if posible, if not copy the array.
		// reshape chnges the shape, also the underlaying data shape.
		getShape(newShape);
		int len=Util.length(newShape);
		if (length != len)
			throw new RuntimeException("different type of shape is not allowed.");
		if (this instanceof BShape || this instanceof TShape || this instanceof TVShape)
		{
			// System.out.println("reshaping broadcasted shape");
			Shape str=view(newShape).copy();
			return str;
		}
//		// Storage str=copy();
//		base.changeShape(newShape, this.shape); 
//		init(base, newShape, offset, null);
//		return this;
		return view(newShape);
	}
	// this functiom is used to calculate the index if it have -1 in their item.
	public int[] getShape(int...shp)
	{
		int nIndex=-1;
		for (int i=0;i < shp.length;i++)
		{
			if (shp[i] == -1 && nIndex != -1)
				throw new RuntimeException("the shape can't have multiple -1 values.");
			else if (shp[i] == -1)
				nIndex = i;
		}
		if (nIndex != -1)
		{
			shp[nIndex] = 1;
			int len=Util.length(shp);
			int remSize=length / len;
			if (length % len != 0)
				throw new RuntimeException("choose an appropriate array size: unable to fill the missing value.");
			shp[nIndex] = remSize;
		}
		return shp;
	}
	public Shape broadcast(int...newShape)
	{
		// check for broadcastable shape
		if (!isBrodcastable(this.shape, newShape))
			throw new RuntimeException("the shape " + Arrays.toString(newShape) + " can't be broadcast to " + Arrays.toString(this.shape));
		// broadcastable shapes are used only to get scalar value. for now.
		if (shape.length == newShape.length && Arrays.equals(newShape, shape))
			return this;
		BShape sh = new BShape(this, shape, newShape);
		return sh;
	}
	public static boolean isBrodcastable(int[]orgShape, int[]newShape)
	{
		if (newShape.length < orgShape.length)
			return false;
		int len=newShape.length - orgShape.length;
		// System.out.println("checking... len =" + len);
		for (int i=0;i < orgShape.length;i++)
		{
			if (!(orgShape[i] == newShape[len + i] || (orgShape[i] == 1 && newShape[len + i] > 0)))
				return false;
		}
		// System.out.println("brodcastable shape " + Arrays.toString(tarShape) + " with " + Arrays.toString(orgShape));
		return true;
	}
	// to array methods.
	// for other shapes, implement only this one: te others workoutby themself.
	public float[] toArray(float[]out, int start, int len) // lazy collect.
	{
		if (out == null)
			out = new float[len];
		if (out.length < len)
			throw new RuntimeException("the length of the input array doesn't match the length specified: " + len + " > " + out.length);
		int str=offset + start;
		for (int i=0;i < len;i++)
			out[i] = data.getData(str + i);
		return out;
	}
	public Value[] toValueArray()
	{
		Value[] out = new Value[length];
		int str=offset;
		for (int i=0;i < length;i++)
			out[i] = data.getValue(str + i);
		return out;
	}
	public float[]toArray()
	{
		return toArray(null, 0, length);
	}
	public float[] toArray(float[]out)
	{
		return toArray(out, 0, length);
	}
	public float[] toArray(float[]out, int start)
	{
		return toArray(out, start, out.length);
	}
	public float[]toArray(int start, int end)
	{
		return toArray(null, start, end);
	}
	public float[]toArray(int start)
	{
		return toArray(null, start, length);
	}
	public float[][] to2DArray(float[][]out) // lazy collect.
	{
		Shape sh=view(-1, shape[shape.length - 1]);
		if (out == null)
			out = new float[sh.shape[0]][sh.shape[1]];
		if (out.length < sh.shape[0] || out[0].length < sh.shape[1])
			throw new RuntimeException("the length of the input array doesn't match the length specified:");
		int str=offset;
		int pos=0;
		for (int i=0;i < out.length;i++)
			for (int j=0;j < out[0].length;j++)
			{
				out[i][j] = data.getData(str + pos);
				pos++;
			}
		return out;
	}
	public Value[][] to2DValueArray() // lazy collect.
	{
		Shape sh=view(-1, shape[shape.length - 1]);
		Value[][]out = new Value[sh.shape[0]][sh.shape[1]];
		int str=offset;
		int pos=0;
		for (int i=0;i < out.length;i++)
			for (int j=0;j < out[0].length;j++)
			{
				out[i][j] = data.getValue(str + pos);
				pos++;
			}
		return out;
	}
	public Shape copy()
	{
		Shape sh=new Shape(this.shape);
		sh.setEnableGradient(data.requireGradient);
		for (int i=0;i < sh.length;i++)
		{
			sh.data.setData(i, getFloat(getShape(i)));
			if (requiresGradient())
				sh.data.setGrad(i, getExactGrad(getShape(i)));
		}
		return sh;
	}
	@Override
	protected Shape clone() 
	{
		Shape s=new Shape(this.shape);
		s.data = this.data;
		s.shape = this.shape;
		s.stride = this.stride;
		s.length = this.length;
		s. offset = this.offset;
		s.dim = this.dim;
		return s;
	}
	public boolean requiresGradient()
	{
		return data.requireGradient;
	}
	public Shape getGradient()
	{
		if (!requiresGradient())
			throw new RuntimeException("gradient not enabled : try enabling it.");
		GData data=new GData(this.data.grad);
		Shape s=this.clone();
		s.data = data;
		return s;
	}
	public Shape detachGradient()
	{
		if (!requiresGradient())
			throw new RuntimeException("gradient not enabled : try enabling it.");
		float[] f=new float[this.data.length];
		for (int i=0;i < f.length;i++)
			f[i] = this.data.grad[i];
		Data data=new Data(f);
		Shape s=new Shape(this.shape);
		s.data = data;
		return s;
	}

}
