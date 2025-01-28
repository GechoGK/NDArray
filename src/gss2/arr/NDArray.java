package gss2.arr;

import gss.math.*;
import gss2.math.*;
import java.util.*;
import gss2.math.Value;
public class NDArray
{
	private Shape base;
	public List<NDArray> childs = new ArrayList<>();
	public GradFunc gradientFunction;

	public NDArray(Shape shp)
	{
		this.base = shp;
	}
	public NDArray(int...shape)
	{
		this.base = new Shape(shape);
	}
	public NDArray(float[] data)
	{
		this.base = new Shape(new int[]{data.length});
		for (int i=0;i < data.length;i++)
			base.data.data[i] = data[i]; 
		//storage.base.values = Arrays.copyOf(data, data.length);
	}
	public NDArray(float[][] data)
	{
		this.base = new Shape(new int[]{data.length, data[0].length});
		float[] dt=Util.flatten(data);
		for (int i=0;i < data.length;i++)
			base.data.data[i] = dt[i];
		// storage.base.values = Util.flatten(data);
	}
	public NDArray setEnableGradient(boolean enable)
	{
		base.setEnableGradient(enable);
		return this;
	}
	public boolean requoreGradient()
	{
		return base.data.requireGradient;
	}
	public NDArray get(int...sh)
	{
		if (sh.length == 0)
			throw new RuntimeException("index can't be empty :" + Arrays.toString(sh));
		return fromShape(base.get(sh));
	}
	public float getFloat(int...sh)
	{
		if (sh.length == 0)
			throw new RuntimeException("index can't be empty :" + Arrays.toString(sh));
		return base.getFloat(sh);
	}
	public float getFlat(int p)
	{
		return base.getFlat(p);
	}
	public void set(int...sh, float v)
	{
		base.set(sh, v);
	}
	public void setFloat(int...sh, float v)
	{
		base.setExact(sh, v);
	}
	public void setFlat(int p, float v)
	{
		base.setFlat(p, v);
	}
	// // // gradient
	// gradient set end get functions.

	public Value getExactValue(int...index)
	{
		return base.getExactValue(index);
	}
	public Value getFlatValue(int p)
	{
		return base.getFlatValue(p);
	}
	public float getExactGrad(int...index)
	{
		return base.getExactGrad(index);
	}
	public float getFlatGrad(int...index)
	{
		return base.getFlatGrad(index);
	}
	public void setExactValue(Value v, int...index)
	{
		base.setExactValue(v, index);
	}
	public void setFlatValue(Value v, int p)
	{
		base.setFlatValue(v, p);
	}
	public void setExactGrad(int[]index, float val)
	{
		base.setExactGrad(index, val);
	}
	public void setFlatGrad(int pos, float val)
	{
		base.setFlatGrad(pos, val);
	}
	// end set and get functions.
	// // // emd gradient
	// implement operators.

	// end operator implementation.
	public NDArray fromShape(Shape str)
	{
		NDArray arr=new NDArray(str);
		// arr.childs.addAll(this.childs);
		// arr.gradientFunction = this.gradientFunction;
		return arr;
	}
	public NDArray copy()
	{
		return fromShape(base.copy());
	}
}
