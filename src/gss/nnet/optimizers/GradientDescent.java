package gss.nnet.optimizers;

import gss.arr.*;
import gss.nnet.*;
import java.util.*;

public class GradientDescent extends Optimizer
{
	public GradientDescent()
	{
		super();
	}
	public GradientDescent(NDArray...prms)//, float lr)
	{
		super();
		for (NDArray n:prms)
			params.add(n);
	}
	public GradientDescent(ArrayList<NDArray>...prms)
	{
		for (ArrayList<NDArray> ar:prms)
			params.addAll(ar);
	}
	public void setLearninfRate(float lr)
	{
		this.learningRate = lr;
	}
	@Override
	public void update()
	{
		for (NDArray p:params)
		{
			if (!p.requiresGradient())
				continue;
			float[] dt=p.base.data.data;
			float[] gr=p.base.data.grad;
			for (int i=0;i < dt.length;i++)
				dt[i] -= gr[i] * learningRate;
		}
		// super.update(params);
	}
	public void zeroGrad()
	{
		for (NDArray p:params)
			p.zeroGrad();
	}
}
