package gss.nnet.layers;

import gss.nnet.*;
import gss.arr.*;

public class Linear extends Module
{
	private boolean hasBiase;
	private NDArray weight;
	private NDArray biase;

	public Linear(int in, int out)
	{
		this(in, out, true);
	}
	public Linear(int in, int out, boolean hasBiase)
	{
		this.hasBiase = hasBiase;
		init(in, out);
	}
	private void init(int in, int out)
	{
		weight = newParam(NDIO.rand(in, out).setEnableGradient(true));
		if (hasBiase)
			biase = newParam(NDIO.ones(out).setEnableGradient(true));
	}
	@Override
	public NDArray forward(NDArray input)
	{
		NDArray out = input.dot(weight);
		if (hasBiase)
			out = out.add(biase);
		return out;
	}
}
