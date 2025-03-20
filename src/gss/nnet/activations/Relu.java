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
		NDArray arrOut=new NDArray(arr.getShape(), out).setEnableGradient(arr.requiresGradient());
		// gradient in progress.
		arrOut.setGradientFunction(reluGradient, arr);
		return arrOut;
	}
	// how gradient works by this.grad or this.data ?
	public static GradFunc reluGradient=new GradFunc("relu"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object[] params)
		{
			NDArray arr=childs[0];
			float[] gd=host.base.data.getGrads();
			float[] dt=arr.base.data.getData();
			for (int i=0;i < dt.length;i++)
			{
				if (dt[i] > 0)
					arr.base.data.setGrad(i, gd[i]);
			}
			return null;
		}
	};
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
