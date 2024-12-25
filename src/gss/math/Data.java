package gss.math;

import java.util.*;

public class Data
{
	private float[] values;
	private float[] grads;
	public int[] shape; // this shape can be changed using view method in Storage class.
	public int length;
	public int dim;
	public boolean requiresGrad;

	public Data(int[]sh)
	{
		this(sh, false);
	}
	public Data(int[]sh,  boolean requireGrad)
	{
		this.requiresGrad   = requireGrad;
		setShape(sh);
		this.values = new float[length];
		if (requireGrad)
			this.grads  = new float[length];
		else
			this.grads = null;
	}
	public void setShape(int[] sh)
	{
		this.shape = Arrays.copyOf(sh, sh.length);
		this.length = Util.sum(shape, 0);
		this.dim = shape.length;
	}
	public void changeShape(int[]newShape, int[]oldShape)
	{
		System.out.println("merging new shape =" + Arrays.toString(shape) + ", " + Arrays.toString(newShape));
		// throw new RuntimeException("illengal shape assignment.");
		int ln=oldShape.length - newShape.length;
		int[] newSh=Arrays.copyOf(this.shape, this.shape.length - ln);
		Util.overlap(newShape, newSh);
		System.out.println("=== " + Arrays.toString(newSh));
		int len=Util.length(newSh);
		if (len != length)
			throw new RuntimeException("shape can't be changed to " + Arrays.toString(newShape) + ", the length is not equal");
		this.shape = newSh;
	}
	public Data copy(int[]newShape)
	{
		Data d=new Data(newShape, requiresGrad);
		d.values = values;
		d.grads = grads;
		// d.setShape(newShape);
		return d;
	}
	public Data enableGradient()
	{
		requiresGrad = true;
		grads = new float[values.length];
		return this;
	}
	public Data disableGradient()
	{
		requiresGrad = false;
		grads = null;
		return this;
	}
	public void zeroGrad()
	{
		Arrays.fill(grads, 0);
	}
	public int getArrayLength()
	{
		return values.length;
	}
	public float getData(int ind)
	{
		return values[ind];
	}
	public void setData(int ind, float val)
	{
		values[ind] = val;
	}
	public void addData(int ind, float val)
	{
		values[ind] += val;
	}
	public float getGrad(int ind)
	{
		return grads[ind];
	}
	public void setGrad(int ind, float val)
	{
		grads[ind] = val;
	}
	public void addGrad(int ind, float val)
	{
		grads[ind] += val;
	}
}
