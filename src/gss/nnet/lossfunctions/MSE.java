package gss.nnet.lossfunctions;

import gss.nnet.*;
import gss.arr.*;

public class MSE extends LossFunc
{
	@Override
	public NDArray forward(NDArray pred, NDArray tar)
	{
		if (pred.getLength() != tar.getLength())
			throw new RuntimeException("unable to compute loss function with different array lengths");

		NDArray sqd=pred.sub(tar).pow(2);
		sqd = sqd.view(-1);

		NDArray mse=sqd.sum().div(sqd.getShape()[0]);

		return mse;
	}


}
