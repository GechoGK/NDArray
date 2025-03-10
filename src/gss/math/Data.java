package gss.math;

import gss.math.*;
import java.util.*;

public class Data
{
	// it just holds the flatten array that are used by many shape utils.
	public float[] data;
	public float[] grad;
	public Value[] gradValues;
	public int length;
	public boolean requireGradient=false;

	public Data(int...shape)
	{
		this.data = new float[Util.length(shape)];
		this.length = data.length;
	}
	public Data(float[]data)
	{
		this.data = data;
		this.length = data.length;
	}
	public void setRequireGrad(boolean b)
	{
		if (b && grad == null)
		{
			grad = new float[length];
			// gradValue = new Value[length];
			requireGradient = true;
		}
		else if (!b)
		{
			grad = null;
			// gradValue = null;
			requireGradient = false;
		}
	}
	public void enableGrad()
	{
		// gradValue = new Value[length];
		grad = new float[length];
		requireGradient = true;
	}
	public void disableGrad()
	{
		grad = null;
		// gradValue = null;
		requireGradient = false;
	}
	public Value getValue(int pos)
	{
		if (gradValues == null)
			gradValues = new Value[data.length];
		Value v=gradValues[pos];
		if (v == null)	
		{
			v = new DValue(this, pos);
			gradValues[pos] = v;
		}
		return v;
	}
	public Value setValue(int pos, Value v)
	{
		// System.out.println("setting flat " + ind + " = " + v);
		if (gradValues == null)
		 	gradValues = new Value[data.length];
		DValue dv=(DValue)gradValues[pos];
		if (dv == null)
		{
			dv = new DValue(this, pos);
			gradValues[pos] = dv;
		}
		dv.set(v);
		// System.out.println(dv);
		return dv;
	}
	public void setData(int p, float v)
	{
		data[p] = v;
	}
	public float getData(int p)
	{
		return data[p];
	}
	public float getGrad(int p)
	{
		return grad[p];
	}
	public void setGrad(int p, float v)
	{
		grad[p] += v;
	}
	public void setGrad(float[]d)
	{
		if (!requireGradient)
			throw new RuntimeException("gradient not enabled!, try enable gradient before set value on it.");
		if (grad.length != d.length)
			throw new RuntimeException("the lemgth of the gradient is not equals to the array you trying to assign.");
		grad = d;
	}
	public void setGrad(float[][]d)
	{
		float[]dd=Util.flatten(d);
		setGrad(dd);
	}
	public void zeroGrad()
	{
		Arrays.fill(grad, 0);
	}
	public Value[] getValues()
	{
		return gradValues;
	}
	public float[] getData()
	{
		return data;
	}
	public float[] getGrads()
	{
		return grad;
	}
	public int getLength()
	{
		return data.length;
	}
}
