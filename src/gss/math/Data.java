package gss.math;

import java.util.*;

public class Data
{
	public float[] values;
	public int[] shape; // this shape can be changed using view method in Storage class.
	public int length;
	public int dim;

	public Data(int...sh)
	{
		setShape(sh);
		this.values = new float[length];
	}
	public void setShape(int[] sh)
	{
		this.shape = Arrays.copyOf(sh, sh.length);
		this.length = Util.sum(shape, 0);
		this.dim = shape.length;
	}
}
