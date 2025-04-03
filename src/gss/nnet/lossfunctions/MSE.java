package gss.nnet.lossfunctions;

import gss.arr.*;
import gss.nnet.*;
import java.util.*;

import static gss.math.Util.*;
import gss.test.*;

public class MSE extends LossFunc
{
	/*
	 problem when converting to NDArray.
	 */

	private float[] forward(float[] pred, float[] trueLabel)
	{
		int n = pred.length;
		float loss = 0.0f;

		// Compute sum of squared errors
		for (int i = 0; i < n; i++)
		{
			float diff = pred[i] - trueLabel[i];
			loss += diff * diff;
		}

		// Average the sum
		loss /= n;

		// Return loss as a 1-element array
		return new float[]{loss};
	}

	@Override
	public NDArray forward(NDArray pred, NDArray tar)
	{
//		if (pred.getLength() != tar.getLength())
//			throw new RuntimeException("unable to compute loss function with different array lengths");
		float[] prd=pred.base.data.getData();
		float[] tr=tar.base.data.getData();
		float[] mse=forward(prd, tr);
		NDArray ar=new NDArray(mse).setEnableGradient(pred.requiresGradient());
		ar.setGradientFunction(mseGrad, pred, tar);
		return ar;
	}
	private static GradFunc mseGrad=new GradFunc("mean squared error"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object[] params)
		{
			NDArray ch=childs[0];
			float[] grd=host.base.data.getGrads();
			float[] xv=ch.base.data.getData();
			float[] trLabel=childs[1].base.data.getData();
			float[] g=MSE.backward(grd, xv, trLabel);
			ch.base.data.setGrad(g); // don't use this method.
			return null;
		}
	};
	// Class-level variable to store true labels from forward pass

	private static float[] backward(float[] grad, float[] x, float[] trueLabel)
	{
		int n = x.length;
		float[] gradient = new float[n];

		// Compute gradient for each prediction
		for (int i = 0; i < n; i++)
		{
			// Gradient formula: 2 * (x[i] - trueLabel[i]) / n
			gradient[i] = 2 * (x[i] - trueLabel[i]) / n;

			// Multiply by upstream gradient (grad[0] = 1 for loss functions)
			gradient[i] *= grad[0];
		}

		return gradient;
	}

	/// example

	public static void test()
	{
		// Forward pass
		MSE m=new MSE();
		float[] pred = {3.0f, 5.0f};
		float[] trueLabel = {1.0f, 2.0f};
		float[] loss = m.forward(pred, trueLabel); // Returns [6.5]
		print("loss =" + Arrays.toString(loss));
		
		// Backward pass
		float[] gradFromAbove = {1.0f}; // Upstream gradient (dL/dL = 1)
		float[] gradient = backward(gradFromAbove, pred, trueLabel); // Returns [2.0, 3.0]

		print("grad =" + Arrays.toString(gradient));

		print("==== with NDArray ====");

		NDArray pr=new NDArray(pred).setEnableGradient(true);
		NDArray tr=new NDArray(trueLabel);

		NDArray rs=m.forward(pr, tr);
		print("loss", rs);
		print("---- grad ----");
		rs.setGrad(1);
		rs.backward();
		printGrad(pr);

		Test1.test(Arrays.equals(rs.base.data.getData(), loss), "loss equals with NDArray");
		Test1.test(Arrays.equals(pr.base.data.getGrads(), gradient), "gradient equals with NDArray");
	}
}
