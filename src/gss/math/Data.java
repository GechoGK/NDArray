package gss.math;

import java.util.*;

public class Data
{
	public float[] values;
	public int[] shape;
	public int length;
	public int dim;

	public Data(int...sh)
	{
		this.shape = Arrays.copyOf(sh, sh.length);
		this.length = Util.sum(shape, 0);
		this.values = new float[length];
		this.dim = shape.length;
	}
}
