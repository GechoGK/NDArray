package gss2.math;

import gss.math.*;

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

	public Shape(int...sh)
	{
		this.data = new Data(sh);
		this.shape = sh;
		this.sum = Util.sumShapes(sh, null);
		this.length = Util.length(sh);
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
}
