package gss.nnet.lossfunctions;

import gss.arr.*;
import gss.nnet.*;
import gss.test.*;
import java.util.*;

import static gss.math.Util.*;

public class MAE extends LossFunc
{
	@Override
	public NDArray forward(NDArray pred, NDArray tar)
	{
		float[] prd=pred.base.data.getData();
		float[] tr=tar.base.data.getData();
		float[] mae=forward(prd, tr);
		NDArray ar=new NDArray(mae).setEnableGradient(pred.requiresGradient());
		ar.setGradientFunction(maeGrad, pred, tar);
		return ar;
	}
	private float[] forward(float[] pred, float[] trueLabel)
	{
		int n = pred.length;
		float loss = 0.0f;

		// Sum of absolute differences
		for (int i = 0; i < n; i++)
		{
			loss += Math.abs(pred[i] - trueLabel[i]);
		}

		// Average the sum
		loss /= n;

		// Return loss as a 1-element array
		return new float[]{loss};
	}
	private static GradFunc maeGrad=new GradFunc("mean absolute error"){

		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object[] params)
		{
			NDArray ch=childs[0];
			float[] grd=host.base.data.getGrads();
			float[] xv=ch.base.data.getData();
			float[] trLabel=childs[1].base.data.getData();
			float[] g=MAE.backward(grd, xv, trLabel);
			ch.base.data.setGrad(g); // don't use this method.
			return null;
		}
	};
	// backward
	private static float[] backward(float[]grad, float[] x, float[] trueLabel)
	{
		int n = x.length;
		float[] gradient = new float[n];

		// Compute gradient for each prediction
		for (int i = 0; i < n; i++)
		{
			// Gradient formula: sign(x[i] - trueLabel[i]) / n
			float diff = x[i] - trueLabel[i];
			gradient[i] = (float) (Math.signum(diff) / n) * grad[0];
		}

		return gradient;
	}

	// example

	public static void test()
	{
		// Forward pass
		MAE m=new MAE();
		float[] pred = {3.0f, 5.0f};
		float[] trueLabel = {1.0f, 2.0f};
		float[] loss = m.forward(pred, trueLabel); // Returns [2.5]
		print("loss =" + Arrays.toString(loss));
		// Backward pass
		float[] gradFromAbove = {1.0f};
		float[] gradient = backward(gradFromAbove, pred, trueLabel); // Returns [1.0/2=0.5, 1.0/2=0.5]

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
