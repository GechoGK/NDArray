package gss.nnet.optimizers;

import gss.arr.*;
import gss.nnet.*;
import java.util.*;

public class Adam extends Optimizer
{
    // Hyperparameters
    private final float beta1;    // Exponential decay rate for 1st moment (e.g., 0.9)
    private final float beta2;    // Exponential decay rate for 2nd moment (e.g., 0.999)
    private final float epsilon;  // Small constant for numerical stability (e.g., 1e-8)

    // Internal states (moving averages)
    private float[][] mm;  // First moment vector (mean)
    private float[][] vv;  // Second moment vector (uncentered variance)
    private int t;      // Timestep counter

    // Constructor with hyperparameters
    public Adam(List<NDArray> prms, float lr, float beta1, float beta2, float epsilon)
	{
		this.params.clear();
        this.params.addAll(prms);
		this.learningRate = lr;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.epsilon = epsilon;
        this.mm = null;
        this.vv = null;
        this.t = 0;
    }
	public Adam(NDArray...prms, float lr, float beta1, float beta2, float epsilon)
	{
		this(Arrays.asList(prms), lr, beta1, beta2, epsilon);
	}
    // Default constructor with common hyperparameters
    public Adam(List<NDArray>...prms)
	{
        this(new ArrayList<>(), 0.001f, 0.8f, 0.999f, 1e-8f);
		for (List<NDArray>n:prms)
			params.addAll(n);
    }
	public Adam(NDArray...prms)
	{
		this(Arrays.asList(prms), 0.001f, 0.8f, 0.999f, 1e-8f);
	}
	@Override
	public void update()
	{
		if (mm == null || vv == null)
		{
            mm = new float[params.size()][]; // already 0.0f
            vv = new float[params.size()][]; // already 0.0f;
		}
		t++;
		for (int i=0;i < params.size();i++)
		{
			float[] m=mm[i];
			float[] v=vv[i];
			NDArray ar=params.get(i);
			float[] grd=ar.base.data.getGrads();
			float[] dat=ar.base.data.getData();
			if (m == null || v == null)
			{
				mm[i] = new float[dat.length];
				vv[i] = new float[dat.length];
				Arrays.fill(mm[i], 0.0f);
				Arrays.fill(vv[i], 0.0f);
				m = mm[i];
				v = vv[i];
			}
			update(grd, dat, m, v);
		}
	}
    // Update parameters using Adam algorithm
    public float[] update(float[] grad, float[] data, float[] m, float[] v)
	{
        // Precompute bias correction terms
        float beta1_t = (float) Math.pow(beta1, t);
        float beta2_t = (float) Math.pow(beta2, t);
        float mBiasCorrection = 1.0f / (1.0f - beta1_t);
        float vBiasCorrection = 1.0f / (1.0f - beta2_t);

        // Update each parameter
        for (int i = 0; i < data.length; i++)
		{
            // Update moving averages
            m[i] = beta1 * m[i] + (1.0f - beta1) * grad[i];
            v[i] = beta2 * v[i] + (1.0f - beta2) * grad[i] * grad[i];

            // Compute bias-corrected estimates
            float mHat = m[i] * mBiasCorrection;
            float vHat = v[i] * vBiasCorrection;

            // Update parameter: data = data - lr * mHat / (sqrt(vHat) + epsilon)
            data[i] -= learningRate * mHat / ((float) Math.sqrt(vHat) + epsilon);
        }

        return data;
    }
	public static void test()
	{
		// Sample data and gradient
		float[] data = {1.0f, 2.0f, 3.0f};  // Parameters to update
		float[] grad = {0.5f, -0.3f, 1.2f};  // Gradients

		NDArray ar=new NDArray(data).setEnableGradient(true);
		ar.base.data.setGrad(grad); // not recomended.

		Adam adam = new Adam(ar);

		// Perform Adam update
		System.out.println("Updated data: " + Arrays.toString(ar.base.data.getData()));
		for (int i=0;i < 10;i++)
		{
			adam.update();
			System.out.println("Updated data: " + Arrays.toString(ar.base.data.getData()));
		}
	}
}
