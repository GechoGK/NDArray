package gss.math;

import gss.math.*;
import java.util.*;

public class BShape extends Shape
{
	/*
	 broadcast works with a little bit of hack.
	 // in broadcasting only accessing items from the array and copy is allowed.
	 // other operations performed on the base shape.

	 !!! to print broadcasted shape you need to copy it first, then print it.
	 */

	// fix shapeToIndex have problem, the clipping problem.

	private int[] baseShape;
	private int[]newBaseShape;
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
		newBaseShape = new int[brdShape.length];
		Arrays.fill(nsh, 1);
		Arrays.fill(newBaseShape, 1);
		this.stride = Util.overlap(shp, nsh);
		newBaseShape = Util.overlap(baseShape, newBaseShape);
		this.stride = Util.sumShapes(stride, null);
		this.length = Util.length(brdShape);
		this.dim = brdShape.length;
		// System.out.println(Arrays.toString(newBaseShape));
	}
	@Override
	public Shape get(int[] sh)
	{
		// needs modificatio to spport broadcast.
		/*
		 check for length of the new array and the baseShapae array.
		 if it found length greater than the base shape, it is still broadcast.
		 if it found length less than and equal to the base shape , then
		 it extracts the new base shape by subtracting the "sh" into baseShape and if it differ from original shape,
		 then it broadcast the new shape and returns it.
		 */
		// System.out.println(Arrays.toString(shape));
		// System.out.println("getting at =" + Arrays.toString(sh));
		int len=shape.length - sh.length;
		// System.out.println("new len =" + len);
		if (len > baseShape.length)
		{
			// System.out.println("still in broadcast");
			int[]nsh=Arrays.copyOfRange(shape, sh.length, shape.length);
			// System.out.println("new shape =" + Arrays.toString(nsh));
			return new BShape(base, baseShape, nsh, offset);
		}
		else
		{
			// System.out.println("length is in range checking again.");
			int[]nsh=Arrays.copyOfRange(shape, sh.length, shape.length);
			int[]bnsh=Arrays.copyOfRange(baseShape, baseShape.length - len, baseShape.length);
			// System.out.println("new shape =" + Arrays.toString(nsh));
			// System.out.println("trimmed base shape =" + Arrays.toString(bnsh));
			if (Arrays.equals(nsh, bnsh))
			{
				// System.out.println("Arrays are equal existing broadcast");
				return base.get(sh);
			}
			else
			{
				// problem. convert broadcasted into newBaseShape.
				// System.out.println("still in broadcast");
				// System.out.println("base shape " + Arrays.toString(baseShape));
				// System.out.println("new base shape " + Arrays.toString(newBaseShape));
				convShape(sh, newBaseShape);
				// System.out.println("get at after =" + Arrays.toString(sh));
				Shape s = base.get(sh);
				// System.out.println("hape type =" + s);
				s = s.broadcast(nsh);
				// System.out.println("shape type afye broadcast =" + s);
				return s;
			}
		}
		// return base.get(sh);
	}
	private int[]convShape(int[]sh, int[]nsh)
	{
		for (int i=0;i < sh.length;i++)
			sh[i] = Math.min(sh[i], nsh[i] - 1);
		return sh;
	}
//	@Override
//	public float getFloat(int[] index)
//	{
//		return base.getFloat(index);
//	}

//	@Override
//	public void setExact(int[] index, float v)
//	{
//		base.setExact(index , v);
//	}

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
		// int newPos=0;
		for (int i=0;i < index.length;i++)
		{
			int shapeInd =  Math.min(index[i], newBaseShape[i] - 1); // uncomment to enable broadcasting.
			if (shapeInd >= shape[i])
				throw new IndexOutOfBoundsException();
			index[i] = shapeInd;
			// newPos += shapeInd *  stride[i];
		}
		index = Arrays.copyOfRange(index, Math.max(0, index.length - baseShape.length), index.length);
		// System.out.println(Arrays.toString(index));
		// System.out.println(index.length+" , "+shape.length);
		// int finalIndex=newPos + offset;
		return base.shapeToIndex(index);
	}
//	@Override
//	public Shape transpose()
//	{
//		// TODO: Implement this method
//		return base.transpose();
//	}
	@Override
	public Shape transpose(int[] axes)
	{
		// TODO: Implement this method
		return base.transpose(axes);
	}
	@Override
	public Shape view(int...newShape)
	{
		getShape(newShape);
		return base.view(newShape);
	}
	@Override
	public Shape broadcast(int[] newShape)
	{
		return base.broadcast(newShape);
	}
	@Override
	public float[] toArray(float[] out, int start, int len)
	{
		if (out == null)
			out = new float[len];
		if (out.length < len)
			throw new RuntimeException("the length of the inpht array doesn't match the length specified: " + len + " > " + out.length);

		for (int l=0;l < len;l++)
			out[l] = getFlat(l);

		return out;
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
				out[i][j] = getFlat(str + pos);
				pos++;
			}
		return out;
	}
}
