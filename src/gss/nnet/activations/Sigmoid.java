package gss.nnet.activations;

import gss.math.*;
import gss.nnet.*;
import gss.arr.*;

public class Sigmoid extends Activation
{
	/*
	 float sigmoidForward(float x) {
	 return 1.0f / (1.0f + (float) Math.exp(-x));
	 }
	 */
	@Override
	public NDArray forward(NDArray arr)
	{
		float[] dt=arr.base.data.getData();
		float[] out=new float[dt.length];
		for (int i=0;i < dt.length;i++)
		{
			out[i] = 1f / (1f + (float)Math.exp(-dt[i]));
		}
		NDArray arrOut=new NDArray(arr.getShape(), out).setEnableGradient(arr.requiresGradient());
		// gradient in progress.
		arrOut.setGradientFunction(sigmoidGradient, arr);
		return arrOut;
	}
	public static GradFunc sigmoidGradient=new GradFunc("sigmoid"){

		/*
		 float sigmoidBackward(float grad, float x) {
		 float sigmoid_x = 1.0f / (1.0f + (float) Math.exp(-x)); // Recompute σ(x)
		 return grad * sigmoid_x * (1.0f - sigmoid_x); // Chain rule: grad * σ'(x)
		 }
		 */
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object[] params)
		{
			// cache sig. for performance
			NDArray a1=childs[0];
			float[] grd=host.base.data.getGrads();
			float[] dt=a1.base.data.getData();
			for (int i=0;i < grd.length;i++)
			{
				float sig = 1f / (1f + (float)Math.exp(-dt[i]));
				a1.base.data.setGrad(i, grd[i] * sig * (1 - sig));
			}
			// childs[0].base.data.setGrad(dt);
			return null;
		}
	};
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
