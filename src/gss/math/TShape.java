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
	public Shape view(int...newShape)
	{
		getShape(newShape);
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
