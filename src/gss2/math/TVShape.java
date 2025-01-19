package gss2.math;

import gss.math.*;
import java.util.*;

public class TVShape extends Shape
{
	// this is transposed view shape.
	public int[]baseShape;
	public int[]baseStride;

	public TVShape(Data d, int[]sh, int offset, int[]bShape, int[]bStride)
	{
		this.data = d;
		this.shape = sh;
		this.stride = Util.sumShapes(sh, null);
		this.length = Util.length(sh);
		this.offset = offset;
		this.dim = shape.length;
		this.baseShape = bShape;
		this.baseStride = bStride;
	}
	@Override
	public Shape get(int[] sh)
	{
		int off=shapeToIndex(sh);
		int[] nShape=Arrays.copyOfRange(shape, sh.length, shape.length);
		// int[] nSum=Arrays.copyOfRange(stride, sh.length, shape.length);
		TVShape s=new TVShape(data, nShape, off, baseShape, baseStride);
		// s.offset = offset + off;
		return s;
	}
	@Override
	public int shapeToIndex(int[] index)
	{
		// convert local shape to local index.
		// the old shapeToIndex can do it.
		int ind= super.shapeToIndex(index);
		// convert localIndex to globalShape, using stored baseShape. arrays.
		int[]nShape=getShape(ind, baseShape);
		// then find the global index if the globalShape using stored baseStride.
		int position=shapeToIndex(nShape, baseShape, baseStride);
		return position;
	}
	private int shapeToIndex(int[]index, int[]bShape, int[]bStride)
	{
		if (index.length > bShape.length) // change this " != " to " > " and implement the if block. or use backward loop.
			throw new IndexOutOfBoundsException();
		int newPos=0;
		for (int i=0;i < index.length;i++)
		{
			int shapeInd = index[i];// Math.min(index[i], shape[i] - 1); // uncomment to enable broadcasting.
			if (shapeInd >= bShape[i])
				throw new IndexOutOfBoundsException();
			newPos += shapeInd *  bStride[i];
		}
		int finalIndex=newPos + offset;
		return finalIndex;
	}
	private int[] getShape(int index, int[]baseShape)
	{
		int[] indShape=new int[baseShape.length];
		for (int i=baseShape.length - 1;i >= 0;i--) // count down starts from shape.length -1 down to 0.
		{
			indShape[i] = index % baseShape[i];
			index = index / baseShape[i];
		}
		return indShape;
	}
}
