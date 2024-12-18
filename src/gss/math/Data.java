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
	public void changeShape(int...sh)
	{
		System.out.println("merging new shape =" + Arrays.toString(shape) + ", " + Arrays.toString(sh));
		// throw new RuntimeException("illengal shape assignment.");
		int[] newSh=Arrays.copyOf(this.shape, this.shape.length);
		Util.overlap(sh, newSh);
		System.out.println("=== " + Arrays.toString(newSh));
		int len=Util.length(newSh);
		if (len != length)
			throw new RuntimeException("shape can't be changet to " + Arrays.toString(sh) + ", the length is not equal");
		this.shape = newSh;
	}
	public Data copy(int[]newShape)
	{
		Data d=new Data();
		d.values = values;
		d.setShape(newShape);
		return d;
	}
}
