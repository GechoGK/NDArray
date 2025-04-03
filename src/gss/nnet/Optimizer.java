package gss.nnet;

import gss.arr.*;
import java.util.*;

public class Optimizer
{
	public float learningRate=0.01f;
	public ArrayList<NDArray> params=new ArrayList<>();

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
