package gss.nnet.optimizers;

import gss.nnet.*;
import gss.arr.*;

public class GradientDescent extends Optimizer
{
	public GradientDescent()
	{
		super();
	}
	public GradientDescent(float lr)
	{
		super(lr);
	}
	@Override
	public void update(NDArray...params)
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
	public void zeroGrad(NDArray...prms)
	{
		for (NDArray p:prms)
			p.zeroGrad();
	}
}
