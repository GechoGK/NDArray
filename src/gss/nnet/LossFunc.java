package gss.nnet;

import gss.arr.*;

public abstract class LossFunc extends Module
{
	public abstract NDArray forward(NDArray pred, NDArray tar);
	@Override
	public NDArray forward(NDArray arr)
	{
		throw new RuntimeException("this forward method is not implemented.");
	}
}
