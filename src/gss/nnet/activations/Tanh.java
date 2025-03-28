package gss.nnet.activations;

import gss.nnet.*;
import gss.arr.*;

public class Tanh extends Activation
{
	@Override
	public NDArray forward(NDArray arr)
	{
		float[] dt=arr.base.data.data;
		float[] out=new float[dt.length];
		for (int i=0;i < dt.length;i++)
		{
			out[i] = (float)Math.tanh(dt[i]);
		}
		NDArray arrOut=new NDArray(arr.getShape(), out).setEnableGradient(arr.requiresGradient());
		arrOut.setGradientFunction(tanhGradient, arr);
		return arrOut;
	}
	public static GradFunc tanhGradient=new GradFunc("tanh"){
		/*
		 float tanhBackward(float grad, float x) {
		 float tanh_x = (float) Math.tanh(x); // Recompute tanh(x)
		 return grad * (1.0f - tanh_x * tanh_x); // Chain rule: grad * (1 - tanh²(x))
		 }
		 */
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object[] params)
		{
			NDArray a1=childs[0];
			float[] grd=host.base.data.getGrads();
			float[] dt=childs[0].base.toArray();
			for (int i=0;i < dt.length;i++)
			{
				float th=(float)Math.tanh(dt[i]);
				a1.base.data.setGrad(i, grd[i] * (1f - th * th));
			}
			// childs[0].base.data.setGrad(dt);
			return null;
		}
	};
//	public static Value tanh(Value op1)
//	{
//		/*
//		 def tanh(self, x):
//		 return Math.tanh(x);
//		 def tanh_derivative(self, x):
//		 return 1 - x ^ 2;
//		 */
//		double out = Math.tanh(op1.data);
//		Value v=new Value(out, op1){
//			@Override
//			public void backward()
//			{
//				this.childs[0].setGrad(1 - this.grad * this.grad);
//				super.backward();
//			}
//		};
//		return v;
//	}

}
