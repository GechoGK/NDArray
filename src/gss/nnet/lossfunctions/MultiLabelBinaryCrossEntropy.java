package gss.nnet.lossfunctions;

import gss.arr.*;
import gss.nnet.*;

import static gss.math.Util.*;

/*
 // not tested.
 // gradient not implemented.
 */

public class MultiLabelBinaryCrossEntropy extends LossFunc
{
	// there are parameters to determine the output length.

	@Override
	public NDArray forward(NDArray pred, NDArray tar)
	{
		float[][] predData=pred.base.to2DArray(null);
		float[][] tarData=tar.base.to2DArray(null);

		if (predData.length != tarData.length)
			throw new RuntimeException("predicted and tergeted arrays have different length");

		int dataLength=predData.length;
		int labelLength=predData[0].length;

		// float[]outData=new float[predData.length];
		float loss=0;
		for (int ne=0;ne < dataLength;ne++) // number of examples.
		{
			if (predData[ne].length != tarData[ne].length)
				new RuntimeException("predicted and tergeted arrays have different length");
			for (int lb=0;lb < labelLength;lb++) // number of labels.
			{
				loss += binaryCrossEntropy(predData[ne][lb], tarData[ne][lb]);
			}
		}
		loss = loss / (labelLength * dataLength);
		// outData[ne] = loss;
		NDArray arrOut=new NDArray(new float[]{loss}).setEnableGradient(pred.requiresGradient());
		// gradient in progress.
		return arrOut;
	}
    private double binaryCrossEntropy(double p, double y)
	{
        // Add a small epsilon to avoid log(0) errors
        double epsilon = 1e-15;
        p = Math.max(epsilon, Math.min(1 - epsilon, p)); //Clip p to avoid log(0)
        return - (y * Math.log(p) + (1 - y) * Math.log(1 - p));
    }
    public static void main2(String[] args)
	{
		new MultiLabelBinaryCrossEntropy().a();
	}
	void a()
	{
        // Example usage:
        float[][] predictions = {
			{0.8f, 0.2f, 0.9f},
			{0.1f, 0.7f, 0.3f}
        };

        float[][] trueLabels = {
			{1.0f, 0.0f, 1.0f},
			{0.0f, 1.0f, 0.0f}
        };

        NDArray loss = forward(new NDArray(new int[]{2,3}, predictions), new NDArray(new int[]{2,3}, trueLabels));
        print("Multi-label loss: " , loss);


        //Example with inconsistent dimensions
        float[][] predictions2 = {
			{0.8f, 0.2f, 0.9f},
			{0.1f, 0.7f, 0.3f}
        };

        float[][] trueLabels2 = {
			{1.0f, 0.0f, 1.0f},
			{0.0f, 1.0f, 0.0f}
        };


		try
		{
			NDArray loss2 = forward(new NDArray(new int[]{2,3}, predictions2), new NDArray(new int[]{2,3}, trueLabels2));
			print("Multi-label loss: ", loss2);
		}
		catch (IllegalArgumentException e)
		{
            System.out.println("Exception caught: " + e.getMessage());
        }
    }
}

