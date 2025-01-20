package gss2.math;

import gss.math.*;
import java.util.*;

public class BShape extends Shape
{
	/*
	 broadcast works with a little bit of hack.
	 */
	private int[] baseShape;
	private Shape base;

	public BShape(Shape base, int[]shp, int[]brdShape)
	{
		this(base, shp, brdShape, 0);
	}
	public BShape(Shape base, int[]shp, int[]brdShape, int off)
	{
		this.base = base;
		this.data = base.data;
		this.baseShape = shp;
		this.shape = brdShape;
		this.offset = off;
		int[] nsh=new int[brdShape.length];
		Arrays.fill(nsh, 1);
		this.stride = Util.overlap(shp, nsh);
		this.stride = Util.sumShapes(stride, null);
		this.length = Util.length(brdShape);
		// System.out.println(Arrays.toString(stride));
	}
	@Override
	public Shape get(int[] sh)
	{
		return base.get(sh);
	}

//	@Override
//	public float getScalar(int[] index)
//	{
//		int ind=shapeToIndex(index);
//		// System.out.println(".." + ind);
//		return base.data.data[ind];
//	}
	@Override
	public int shapeToIndex(int[] index)
	{
		if (index.length > shape.length) // change this " != " to " > " and implement the if block. or use backward loop.
			throw new IndexOutOfBoundsException();
		int newPos=0;
		for (int i=0;i < index.length;i++)
		{
			int shapeInd =  Math.min(index[i], baseShape[i] - 1); // uncomment to enable broadcasting.
			if (shapeInd >= shape[i])
				throw new IndexOutOfBoundsException();
			newPos += shapeInd *  stride[i];
		}
		int finalIndex=newPos + offset;
		return finalIndex;
		// return super.shapeToIndex(index);
	}
	@Override
	public Shape transpose()
	{
		// TODO: Implement this method
		return base.transpose();
	}
	@Override
	public Shape transpose(int[] axes)
	{
		// TODO: Implement this method
		return base.transpose(axes);
	}
	@Override
	public Shape view(int[] newShape)
	{
		return base.view(newShape);
	}
	@Override
	public Shape broadcast(int[] newShape)
	{
		return base.broadcast(newShape);
	}

}
