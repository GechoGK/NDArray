package gss.nnet.lossfunctions;

import gss.arr.*;
import gss.nnet.*;
import gss.test.*;
import java.util.*;

import static gss.math.Util.*;

public class BCE extends LossFunc
{
	@Override
	public NDArray forward(NDArray pred, NDArray tar)
	{
		float[] prd=pred.base.data.getData();
		float[] tr=tar.base.data.getData();
		float[] bce=forward(prd, tr);
		NDArray ar=new NDArray(bce).setEnableGradient(pred.requiresGradient());
		ar.setGradientFunction(bceGrad, pred, tar);
		return ar;
	}

	float[] forward(float[] pred, float[] trueLabel)
	{
		int n = pred.length;
		float loss = 0.0f;
		final float epsilon = 1e-7f; // Avoid log(0)

		for (int i = 0; i < n; i++)
		{
			// Clip predictions to [epsilon, 1-epsilon]
			float p = Math.max(epsilon, Math.min(pred[i], 1 - epsilon));
			loss += trueLabel[i] * Math.log(p) + (1 - trueLabel[i]) * Math.log(1 - p);
		}

		// Average and negate (BCE formula)
		loss = -loss / n;
		return new float[]{loss};
	}
	// backward
	private static GradFunc bceGrad=new GradFunc("binary cross entropy"){
		@Override
		public NDArray backward(NDArray host, NDArray[] childs, Object[] params)
		{
			NDArray ch=childs[0];
			float[] grd=host.base.data.getGrads();
			float[] xv=ch.base.data.getData();
			float[] trLabel=childs[1].base.data.getData();
			float[] g=BCE.backward(grd, xv, trLabel);
			ch.base.data.setGrad(g); // don't use this method.
			return null;
		}
	};
	private static float[] backward(float[] grad, float[] x, float[] trueLabel)
	{
		int n = x.length;
		float[] gradient = new float[n];
		final float epsilon = 1e-7f; // Avoid division by 0

		for (int i = 0; i < n; i++)
		{
			// Clip predictions to [epsilon, 1-epsilon]
			float p = Math.max(epsilon, Math.min(x[i], 1 - epsilon));

			// Gradient formula: (p - y) / (p * (1 - p)) * (1/n) * grad[0]
			float grad_i = (p - trueLabel[i]) / (p * (1 - p));
			gradient[i] = (grad_i / n) * grad[0];
		}
		return gradient;
	}

	// example
	public static void test()
	{
		BCE b=new BCE();
		// Forward pass
		float[] pred = {0.2f, 0.8f};
		float[] trueLabel = {1.0f, 0.0f};
		float[] loss = b.forward(pred, trueLabel); // Returns [~1.609]

		print("loss =" + Arrays.toString(loss));
		// Backward pass (grad[0] = 1.0 for loss functions)
		float[] gradient = backward(new float[]{1.0f}, pred, trueLabel); 
		// Returns [ (0.2-1)/(0.2*0.8)/2 ≈ -2.5, (0.8-0)/(0.8*0.2)/2 ≈ 2.5 ]

		print("grad =" + Arrays.toString(gradient));

		print("==== with NDArray ====");

		NDArray pr=new NDArray(pred).setEnableGradient(true);
		NDArray tr=new NDArray(trueLabel);

		NDArray rs=b.forward(pr, tr);
		print("loss", rs);
		print("---- grad ----");
		rs.setGrad(1);
		rs.backward();
		printGrad(pr);

		Test1.test(Arrays.equals(rs.base.data.getData(), loss), "loss equals with NDArray");
		Test1.test(Arrays.equals(pr.base.data.getGrads(), gradient), "gradient equals with NDArray");
	}
}
