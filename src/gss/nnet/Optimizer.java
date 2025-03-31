package gss.nnet;

import gss.arr.*;

public class Optimizer
{
	public float learningRate=0.001f;

	public Optimizer()
	{
		this(0.001f);
	}
	public Optimizer(float lr)
	{
		this.learningRate = lr;
	}
	public void update(NDArray...params)
	{
		// update the parameters based on their gradient.
	}
	public void zeroGrad(NDArray...params)
	{
		// zero each parameter's gradient.
	}
}
