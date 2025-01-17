package gss2.math;

import gss.math.*;
import java.util.*;

public class TShape extends Shape
{
	/*
	 Transposed shape.
	 */

	public TShape(Data d, int[]sh, int[]sm, int offset)
	{
		this.data = d;
		this.shape = sh;
		this.stride = sm;
		this.length = Util.length(sh);
		this.offset = offset;
		this.dim = shape.length;
	}
	@Override
	public Shape get(int[] sh)
	{
		int off=shapeToIndex(sh);
		int[] nShape=Arrays.copyOfRange(shape, sh.length, shape.length);
		int[] nSum=Arrays.copyOfRange(stride, sh.length, shape.length);
		TShape s=new TShape(data, nShape, nSum, off);
		// s.offset = offset + off;
		return s;
	}
//	@Override
//	public int shapeToIndex(int[] index)
//	{
//		if (index.length > shape.length) // change this " != " to " > " and implement the if block. or use backward loop.
//			throw new IndexOutOfBoundsException();
//		int newPos=0;
//		for (int i=0;i < index.length;i++)
//		{
//			int shapeInd = index[i]; // Math.min(index[i], shape[i] - 1);
//			// baseShape[i] -1 ; because . the baseShape minimum value is 1, but 1 means it's acess index is 0, so to make it zero we need to -1;
//			// check if the index at i is not out of bound.
//			// if (shapeInd >= shape[i])
//			//	throw new IndexOutOfBoundsException();
//			newPos += shapeInd *  sum[i];
//		}
//		int finalIndex=newPos + offset;
//		// System.out.println("off = " + offset + ", newP = " + newPos + ", final pos = " + finalIndex + ", len= " + length);
//		// System.out.print("--" + finalIndex + "--" + offset + "--");
//		return finalIndex;
//		// return super.shapeToIndex(index);
//	}
	@Override
	public Shape view(int[] newShape)
	{
		TVShape ts=new TVShape(data, newShape, offset, shape, stride);
		return ts;
	}
}
