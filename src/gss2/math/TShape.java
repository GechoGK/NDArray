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
	@Override
	public Shape view(int[] newShape)
	{
		TVShape ts=new TVShape(data, newShape, offset, shape, stride);
		return ts;
	}
}
