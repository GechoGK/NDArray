package gss.math;

import gss.math.*;
import java.util.*;

public class TShape extends Shape
{
	/*
	 Transposed shape.
	 */
	public int[] baseShape;

	public TShape(Data d, int[]bShape, int[]sh, int[]sm, int offset)
	{
		this.data = d;
		this.baseShape = bShape;
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
		TShape s=new TShape(data, baseShape, nShape, nSum, off);
		// s.offset = offset + off;
		return s;
	}
	@Override
	public Shape view(int[] newShape)
	{
		TVShape ts=new TVShape(data, newShape, offset, shape, stride);
		return ts;
	}
	@Override
	public float[] toArray(float[] out, int start, int len)
	{
		if (out == null)
			out = new float[len];
		if (out.length < len)
			throw new RuntimeException("the length of the inpht array doesn't match the length specified: " + len + " > " + out.length);

		// System.out.println("..." + Arrays.toString(shape));
		// System.out.println("..." + Arrays.toString(stride));

		for (int l=0;l < len;l++)
			out[l] = getFlat(l);

		return out;
	}
}
