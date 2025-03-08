package gss.nnet;

import gss.arr.*;

public abstract class LossFunc
{
	
	public abstract NDArray forward(NDArray pred, NDArray tar);
}
