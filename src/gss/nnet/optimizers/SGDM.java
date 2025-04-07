package gss.nnet.optimizers;

import gss.arr.*;
import gss.nnet.*;
import java.util.*;
import gss.math.*;

public class SGDM extends Optimizer
{
    private float momentum;
    private float[][] velocity;  // Tracks accumulated velocity for momentum

    // Constructor: Initialize learning rate and momentum factor
    public SGDM(List<NDArray> prms, float learningRate, float momentum)
	{
        super(learningRate);
		this.params.addAll(prms);
        this.momentum = momentum;
        this.velocity = null;  // Velocity initialized lazily on first `forward` call
    }
	public SGDM(List<NDArray>...prms)
	{
		this(new ArrayList<>(), 0.01f, 0.85f);
		for (List<NDArray> p:prms)
			params.addAll(p);
	}
	@Override
	public void update()
	{
		if (velocity == null)
			velocity = new float[params.size()][];
		for (int i=0;i < params.size();i++)
		{
			float[] grad=params.get(i).base.data.getGrads();
			float[] data=params.get(i).base.data.getData();
			float[] vl=velocity[i];
			if (vl == null)
			{
				velocity[i] = new float[data.length];
				vl = velocity[i];
			}
			update(vl, grad, data);
		}
	}
    // Update parameters using momentum
    private float[] update(float[]vel, float[] grad, float[] data)
	{
        // Apply momentum update rule:
        // velocity = momentum * velocity + grad
        // data = data - learningRate * velocity
        for (int i = 0; i < data.length; i++)
		{
            vel[i] = momentum * vel[i] + grad[i];
            data[i] -= learningRate * vel[i];
        }

        return data;  // Updated parameters (modified in-place)
    }
	public static void test()
	{
		// Example data (parameters) and gradient
		float[] data = {1.0f, 2.0f, 3.0f};
		float[] grad = {0.1f, 0.2f, 0.3f};

		NDArray ar=new NDArray(data).setEnableGradient(true);
		ar.base.data.setGrad(grad);

		Util.print(ar);
		Util.printGrad(ar);

		// Initialize optimizer with learning rate 0.01 and momentum 0.9
		SGDM optimizer = new SGDM(Arrays.asList(ar), 0.01f, 0.9f);
		// Update parameters using momentum
		optimizer.update();

		Util.print(ar);
		Util.printGrad(ar);
		// System.out.println(Arrays.toString(data));  
		// Output: [0.999, 1.998, 2.997] (after first step)
	}
}
