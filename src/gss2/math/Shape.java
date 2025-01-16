package gss2.math;

import gss.math.*;
import java.util.*;

public class Shape
{
	/*
	 this shape is used to get and set item to and from data.

	 */
	public Data data;
	public int[] shape;
	public int[] sum;
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
	public Shape(Data d, int[]sh, int off)
	{
		init(d, sh, null, off);
	}
	public Shape(Data d, int[]sh, int[] sm, int off)
	{
		init(d, sh, sm, off);
	}
	private void init(Data d, int[]sh, int[] sm, int off)
	{
		this.data = d;
		this.shape = sh;
		this.dim = shape.length;
		if (sm == null)
			this.sum = Util.sumShapes(sh, null);
		else
			this.sum = sm;
		this.length = Util.length(sh);
		this.offset = off;
	}
	public Shape get(int...sh)
	{
		int off=shapeToIndex(sh);
		int[] nShape=Arrays.copyOfRange(shape, sh.length, shape.length);
		int[] sm=Arrays.copyOfRange(sum, sh.length, shape.length);
		Shape s=new Shape(data, nShape, sm, off);
		s.sum = sm;
		return s;
	}
	public float getScalar(int...index)
	{
		int ind=shapeToIndex(index);
		// System.out.println(".." + ind);
		return data.data[ind];
	}
	public int shapeToIndex(int...index)
	{
		if (index.length > shape.length) // change this " != " to " > " and implement the if block. or use backward loop.
			throw new IndexOutOfBoundsException();
		int newPos=0;
		for (int i=0;i < index.length;i++)
		{
			int shapeInd = index[i];// Math.min(index[i], shape[i] - 1); // uncomment to enable broadcasting.
			if (shapeInd >= shape[i])
				throw new IndexOutOfBoundsException();
			newPos += shapeInd *  sum[i];
		}
		int finalIndex=newPos + offset;
		return finalIndex;
	}
	public int[] getShape(int index)
	{
		// this function used to convert index (0-n) into shaps. by iterating all posible combination of shapes, and it returns the combination at the speciic index.
		if (index >= data.length || index < 0)
			throw new IndexOutOfBoundsException();
		int[] indShape=new int[this.shape.length];
		for (int i=this.shape.length - 1;i >= 0;i--) // count down starts from shape.length -1 down to 0.
		{
			indShape[i] = index % this.shape[i];
			index = index / this.shape[i];
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
	public Shape view(int...newShape)
	{
		return new Shape(data, newShape, offset);
	}
	public Shape transpose(int...axes)
	{
		if (axes.length != shape.length)
			throw new RuntimeException("invalid axes");
		int[] sh=new int[shape.length]; // Arrays.copyOf(shape, shape.length);
		int[] sm=new int[shape.length];
		int p=0;
		for (int i:axes)
		{
			if (i >= axes.length)
				throw new IndexOutOfBoundsException("index must not greater than the dimension o the array");
			sh[p] = shape[i];
			sm[p] = sum[i];
			p++;
		}
		return new TShape(data, sh, sm, offset);
	}
	public float[] toArray()
	{
		float[] ar=new float[length];
		for (int i=0;i < length;i++)
			ar[i] = getScalar(getShape(i));
		return ar;
	}
}
