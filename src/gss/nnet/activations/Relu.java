package gss.nnet.activations;

import gss.nnet.*;
import gss.arr.*;

public class Relu extends Activation
{
	@Override
	public NDArray forward(NDArray arr)
	{
		float[] dt=arr.base.data.data;
		float[] out=new float[dt.length];
		for (int i=0;i < dt.length;i++)
		{
			out[i] = Math.max(0, dt[i]);
		}
		NDArray arrOut=new NDArray(out).setEnableGradient(arr.requiresGradient());
		// gradient in progress.
		return arrOut;
	}
//	public static Value relu(Value op1)
//	{
//		/*
//		 def relu(self, x):
//		 return Math.max(0,x);
//		 def relu_derivative(self, x):
//		 return x<0?0:1;
//		 */
//		double out = Math.max(0, op1.data);
//		Value v=new Value(out, op1){
//			@Override
//			public void backward()
//			{
//				this.childs[0].setGrad(this.grad < 0 ? 0 : 1);
//				super.backward();
//			}
//		};
//		return v;
//	}
}
