package gss.math;
import java.util.*;

public class GData extends Data
{
	public GData(float[]g)
	{
		super(g);
	}
	@Override
	public void setRequireGrad(boolean b)
	{
		// no gradient the data itswlf is a gradient.
	}
	@Override
	public void enableGrad()
	{
		// no gradient the data itswlf is a gradient.
	}
	@Override
	public void disableGrad()
	{
		// no gradient the data itswlf is a gradient.
	}
	@Override
	public Value setValue(int pos, Value v)
	{
		// no value here. just data( gradient as data)
		return null;
	}
	@Override
	public Value getValue(int pos)
	{
		// no value here.
		return null;
	}
	@Override
	public Value[] getValues()
	{
		// no values here. just use data.
		return null;
	}
	@Override
	public float getGrad(int p)
	{
		throw new RuntimeException("no gradient");
		// no gradient found.
		// return 0;
	}
	@Override
	public void setGrad(int p, float v)
	{
		throw new RuntimeException("no gradient");
		// no gradient found.
		// return 0;
	}
	@Override
	public void zeroGrad()
	{
		Arrays.fill(data, 0);
	}
	@Override
	public float[] getGrads()
	{
		// no grads here.
		return null;
	}
}
