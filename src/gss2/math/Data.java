package gss2.math;

import gss.math.*;

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
	public Value getGradValue(int pos)
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
	public void addGrad(int p, float v)
	{
		grad[p] += v;
	}
}
