package gss.nnet;

import gss.arr.*;

public class Optimizer
{
	public float learningRate=0.01f;

	public Optimizer()
	{
		this(0.01f);
	}
	public Optimizer(float lr)
	{
		this.learningRate = lr;
	}
	public void update()
	{
		// update the parameters based on their gradient.
	}
	public void zeroGrad()
	{
		// zero each parameter's gradient.
	}
}
