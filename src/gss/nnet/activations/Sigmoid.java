package gss.nnet.activations;

import gss.math.*;
import gss.nnet.*;
import gss.arr.*;

public class Sigmoid extends Activation
{
	@Override
	public NDArray forward(NDArray arr)
	{
		float[] dt=arr.base.data.data;
		float[] out=new float[dt.length];
		for (int i=0;i < dt.length;i++)
		{
			out[i] = 1 / (1 + (float)Math.exp(-dt[i]));
		}
		NDArray arrOut=new NDArray(out).setEnableGradient(arr.requiresGradient());
		// gradient in progress.
		return arrOut;
	}
//	public static Value sigmoid(Value op1)
//	{
//		/*
//		 def sigmoid(self, x):
//		 return 1 / (1 + np.exp(-x))
//
//		 def sigmoid_derivative(self, x):
//		 return x * (1 - x)
//		 */
//		double out = 1 / (1 + Math.exp(-op1.data));
//		Value v=new Value(out, op1){
//			@Override
//			public void backward()
//			{
//				this.childs[0].setGrad(this.grad * (1 - this.grad));
//				super.backward();
//			}
//		};
//		return v;
//	}
}
